import { apiClient, ApiResponse } from './client'

interface OrderItem {
    productId: number
    productName: string
    quantity: number
    price: number
}

interface Order {
    id: number
    userId: number
    totalAmount: number
    status: string
    items: OrderItem[]
    createdAt: string
}

interface CreateOrderRequest {
    addressId: number
    paymentRequest: {
        amount: number
        cardNumber: string
        cardHolder: string
        cvv: string
        expireDate: string
    }
}

export const ordersApi = {
    getMyOrders: async (): Promise<ApiResponse<Order[]>> => {
        const response = await apiClient.get<ApiResponse<Order[]>>('/orders')
        return response.data
    },

    getById: async (id: number): Promise<ApiResponse<Order>> => {
        const response = await apiClient.get<ApiResponse<Order>>(`/orders/${id}`)
        return response.data
    },

    checkout: async (data: CreateOrderRequest): Promise<ApiResponse<Order>> => {
        const response = await apiClient.post<ApiResponse<Order>>('/orders', data)
        return response.data
    },

    // Admin endpoints
    getAllOrders: async (params?: { page?: number; size?: number }): Promise<ApiResponse<{ content: Order[]; page: { totalElements: number } }>> => {
        const searchParams = new URLSearchParams()
        if (params?.page !== undefined) searchParams.append('page', params.page.toString())
        if (params?.size !== undefined) searchParams.append('size', params.size.toString())

        const response = await apiClient.get<ApiResponse<{ content: Order[]; page: { totalElements: number } }>>(
            `/admin/orders?${searchParams.toString()}`
        )
        return response.data
    },

    updateStatus: async (id: number, status: string): Promise<ApiResponse<Order>> => {
        const response = await apiClient.patch<ApiResponse<Order>>(`/admin/orders/${id}/status`, { status })
        return response.data
    },
}
