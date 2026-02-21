import { apiClient, ApiResponse } from './client'

// =====================================================
// Admin API - User Management & Cart Management
// =====================================================

interface User {
    id: number
    name: string
    email: string
    role: 'USER' | 'ADMIN'
    createdAt: string
}

interface CartItem {
    id: number
    productId: number
    productName: string
    quantity: number
    price: number
    subtotal: number
}

interface Cart {
    id: number
    userId: number
    items: CartItem[]
    totalAmount: number
}

interface UpdateRoleRequest {
    role: 'USER' | 'ADMIN'
}

// =====================================================
// User Management Endpoints
// =====================================================

export const adminUsersApi = {
    /**
     * Get all users
     */
    getAll: async (): Promise<ApiResponse<User[]>> => {
        const response = await apiClient.get<ApiResponse<User[]>>('/admin/users')
        return response.data
    },

    /**
     * Get user by ID
     */
    getById: async (id: number): Promise<ApiResponse<User>> => {
        const response = await apiClient.get<ApiResponse<User>>(`/admin/users/${id}`)
        return response.data
    },

    /**
     * Update user role
     */
    updateRole: async (id: number, role: 'USER' | 'ADMIN'): Promise<ApiResponse<User>> => {
        const request: UpdateRoleRequest = { role }
        const response = await apiClient.patch<ApiResponse<User>>(`/admin/users/${id}/role`, request)
        return response.data
    },

    /**
     * Delete user
     */
    delete: async (id: number): Promise<void> => {
        await apiClient.delete(`/admin/users/${id}`)
    },
}

// =====================================================
// Cart Management Endpoints
// =====================================================

export const adminCartsApi = {
    /**
     * Get cart by user ID
     */
    getByUserId: async (userId: number): Promise<ApiResponse<Cart>> => {
        const response = await apiClient.get<ApiResponse<Cart>>(`/admin/carts/users/${userId}`)
        return response.data
    },

    /**
     * Clear cart by user ID
     */
    clearByUserId: async (userId: number): Promise<ApiResponse<Cart>> => {
        const response = await apiClient.delete<ApiResponse<Cart>>(`/admin/carts/users/${userId}/clear`)
        return response.data
    },

    /**
     * Delete cart by user ID
     */
    deleteByUserId: async (userId: number): Promise<void> => {
        await apiClient.delete(`/admin/carts/users/${userId}`)
    },
}

export type { User, Cart, CartItem }
