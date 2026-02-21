import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { useAuthStore } from './authStore'
import { cartApi, CartItemResponse } from '../api/cart'

// Guest cart item (stored in localStorage)
export interface GuestCartItem {
    productId: number
    productName: string
    quantity: number
    unitPrice: number
}

// Unified cart item for UI
export interface CartItem {
    productId: number
    productName: string
    quantity: number
    unitPrice: number
    totalPrice: number
}

// Product info needed to add to cart
export interface ProductForCart {
    id: number
    name: string
    price: number
}

interface CartState {
    // State
    items: CartItem[]
    totalPrice: number
    itemCount: number
    loading: boolean
    error: string | null

    // Guest cart (persisted to localStorage)
    guestItems: GuestCartItem[]

    // Actions
    fetchCart: () => Promise<void>
    addItem: (product: ProductForCart, quantity?: number) => Promise<void>
    updateQuantity: (productId: number, quantity: number) => Promise<void>
    removeItem: (productId: number) => Promise<void>
    clearCart: () => Promise<void>
    mergeGuestCart: () => Promise<void>
    clearError: () => void
}

// Helper to convert API response to CartItem
const apiItemToCartItem = (item: CartItemResponse): CartItem => ({
    productId: item.productId,
    productName: item.productName,
    quantity: item.quantity,
    unitPrice: item.unitPrice,
    totalPrice: item.lineTotal,
})

// Helper to convert guest item to CartItem
const guestItemToCartItem = (item: GuestCartItem): CartItem => ({
    productId: item.productId,
    productName: item.productName,
    quantity: item.quantity,
    unitPrice: item.unitPrice,
    totalPrice: item.unitPrice * item.quantity,
})

// Calculate totals from items
const calculateTotals = (items: CartItem[]) => ({
    totalPrice: items.reduce((sum, item) => sum + item.totalPrice, 0),
    itemCount: items.reduce((sum, item) => sum + item.quantity, 0),
})

export const useCartStore = create<CartState>()(
    persist(
        (set, get) => ({
            // Initial state
            items: [],
            totalPrice: 0,
            itemCount: 0,
            loading: false,
            error: null,
            guestItems: [],

            fetchCart: async () => {
                const isAuthenticated = useAuthStore.getState().isAuthenticated

                if (isAuthenticated) {
                    // Fetch from API
                    set({ loading: true, error: null })
                    try {
                        const response = await cartApi.getCart()
                        if (response.success) {
                            const items = response.data.items.map(apiItemToCartItem)
                            const totals = calculateTotals(items)
                            set({ items, ...totals, loading: false })
                        }
                    } catch (err) {
                        const error = err as { response?: { status?: number } }
                        if (error.response?.status === 401 || error.response?.status === 403) {
                            set({ error: 'Please sign in to view your saved cart', loading: false })
                        } else {
                            set({ error: 'Could not connect to server', loading: false })
                        }
                    }
                } else {
                    // Use guest cart from localStorage
                    const { guestItems } = get()
                    const items = guestItems.map(guestItemToCartItem)
                    const totals = calculateTotals(items)
                    set({ items, ...totals, loading: false, error: null })
                }
            },

            addItem: async (product: ProductForCart, quantity = 1) => {
                const isAuthenticated = useAuthStore.getState().isAuthenticated

                if (isAuthenticated) {
                    // Add via API
                    set({ loading: true, error: null })
                    try {
                        const response = await cartApi.addItem({
                            productId: product.id,
                            quantity,
                        })
                        if (response.success) {
                            const items = response.data.items.map(apiItemToCartItem)
                            const totals = calculateTotals(items)
                            set({ items, ...totals, loading: false })
                        }
                    } catch (err) {
                        set({ error: 'Failed to add item to cart', loading: false })
                        throw err
                    }
                } else {
                    // Add to guest cart
                    const { guestItems } = get()
                    const existingIndex = guestItems.findIndex(
                        (item) => item.productId === product.id
                    )

                    let newGuestItems: GuestCartItem[]
                    if (existingIndex >= 0) {
                        // Update existing item
                        newGuestItems = guestItems.map((item, index) =>
                            index === existingIndex
                                ? { ...item, quantity: item.quantity + quantity }
                                : item
                        )
                    } else {
                        // Add new item
                        newGuestItems = [
                            ...guestItems,
                            {
                                productId: product.id,
                                productName: product.name,
                                quantity,
                                unitPrice: product.price,
                            },
                        ]
                    }

                    const items = newGuestItems.map(guestItemToCartItem)
                    const totals = calculateTotals(items)
                    set({ guestItems: newGuestItems, items, ...totals })
                }
            },

            updateQuantity: async (productId: number, quantity: number) => {
                const isAuthenticated = useAuthStore.getState().isAuthenticated

                if (quantity <= 0) {
                    // Remove item if quantity is 0 or less
                    return get().removeItem(productId)
                }

                if (isAuthenticated) {
                    set({ loading: true, error: null })
                    try {
                        const response = await cartApi.updateQuantity(productId, { quantity })
                        if (response.success) {
                            const items = response.data.items.map(apiItemToCartItem)
                            const totals = calculateTotals(items)
                            set({ items, ...totals, loading: false })
                        }
                    } catch (err) {
                        set({ error: 'Failed to update quantity', loading: false })
                        throw err
                    }
                } else {
                    // Update guest cart
                    const { guestItems } = get()
                    const newGuestItems = guestItems.map((item) =>
                        item.productId === productId ? { ...item, quantity } : item
                    )
                    const items = newGuestItems.map(guestItemToCartItem)
                    const totals = calculateTotals(items)
                    set({ guestItems: newGuestItems, items, ...totals })
                }
            },

            removeItem: async (productId: number) => {
                const isAuthenticated = useAuthStore.getState().isAuthenticated

                if (isAuthenticated) {
                    set({ loading: true, error: null })
                    try {
                        const response = await cartApi.removeItem(productId)
                        if (response.success) {
                            const items = response.data.items.map(apiItemToCartItem)
                            const totals = calculateTotals(items)
                            set({ items, ...totals, loading: false })
                        }
                    } catch (err) {
                        set({ error: 'Failed to remove item', loading: false })
                        throw err
                    }
                } else {
                    // Remove from guest cart
                    const { guestItems } = get()
                    const newGuestItems = guestItems.filter(
                        (item) => item.productId !== productId
                    )
                    const items = newGuestItems.map(guestItemToCartItem)
                    const totals = calculateTotals(items)
                    set({ guestItems: newGuestItems, items, ...totals })
                }
            },

            clearCart: async () => {
                const isAuthenticated = useAuthStore.getState().isAuthenticated

                if (isAuthenticated) {
                    set({ loading: true, error: null })
                    try {
                        const response = await cartApi.clearCart()
                        if (response.success) {
                            set({ items: [], totalPrice: 0, itemCount: 0, loading: false })
                        }
                    } catch (err) {
                        set({ error: 'Failed to clear cart', loading: false })
                        throw err
                    }
                } else {
                    // Clear guest cart
                    set({ guestItems: [], items: [], totalPrice: 0, itemCount: 0 })
                }
            },

            mergeGuestCart: async () => {
                const { guestItems } = get()

                if (guestItems.length === 0) {
                    // No guest items to merge, just fetch the server cart
                    await get().fetchCart()
                    return
                }

                set({ loading: true, error: null })

                try {
                    // Add each guest item to the server cart
                    for (const item of guestItems) {
                        await cartApi.addItem({
                            productId: item.productId,
                            quantity: item.quantity,
                        })
                    }

                    // Clear guest cart after successful merge
                    set({ guestItems: [] })

                    // Fetch the updated cart from server
                    const response = await cartApi.getCart()
                    if (response.success) {
                        const items = response.data.items.map(apiItemToCartItem)
                        const totals = calculateTotals(items)
                        set({ items, ...totals, loading: false })
                    }
                } catch (err) {
                    console.error('Failed to merge guest cart:', err)
                    // Still clear guest cart to avoid duplicate attempts
                    set({ guestItems: [], loading: false })
                    // Fetch whatever is on the server
                    await get().fetchCart()
                }
            },

            clearError: () => set({ error: null }),
        }),
        {
            name: 'shophub-cart-storage',
            // Only persist guestItems to localStorage
            partialize: (state) => ({ guestItems: state.guestItems }),
        }
    )
)
