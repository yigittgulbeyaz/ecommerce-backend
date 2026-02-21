import { apiClient, ApiResponse } from './client'

// Types matching backend DTOs
export interface CartItemResponse {
    productId: number
    productName: string
    quantity: number
    unitPrice: number
    lineTotal: number
}

export interface CartResponse {
    cartId: number
    items: CartItemResponse[]
    total: number
}

export interface AddCartItemRequest {
    productId: number
    quantity: number
}

export interface UpdateCartItemQuantityRequest {
    quantity: number
}

export const cartApi = {
    getCart: async (): Promise<ApiResponse<CartResponse>> => {
        const response = await apiClient.get<ApiResponse<CartResponse>>('/cart')
        return response.data
    },

    addItem: async (request: AddCartItemRequest): Promise<ApiResponse<CartResponse>> => {
        const response = await apiClient.post<ApiResponse<CartResponse>>('/cart/items', request)
        return response.data
    },

    updateQuantity: async (productId: number, request: UpdateCartItemQuantityRequest): Promise<ApiResponse<CartResponse>> => {
        const response = await apiClient.patch<ApiResponse<CartResponse>>(`/cart/items/${productId}`, request)
        return response.data
    },

    removeItem: async (productId: number): Promise<ApiResponse<CartResponse>> => {
        const response = await apiClient.delete<ApiResponse<CartResponse>>(`/cart/items/${productId}`)
        return response.data
    },

    clearCart: async (): Promise<ApiResponse<CartResponse>> => {
        const response = await apiClient.delete<ApiResponse<CartResponse>>('/cart/clear')
        return response.data
    },
}
