import { apiClient, ApiResponse } from './client'

interface Product {
    id: number
    name: string
    description: string
    price: number
    categoryId: number
    categoryName: string
}

interface PaginationMeta {
    page: number
    size: number
    totalElements: number
    totalPages: number
    hasNext: boolean
    hasPrevious: boolean
}

interface ProductsResponse {
    items: Product[]
    meta: PaginationMeta
}

interface CreateProductRequest {
    name: string
    description: string
    price: number
    categoryId: number
}

export const productsApi = {
    getAll: async (params?: {
        page?: number
        size?: number
        search?: string
        categoryId?: number
        sort?: string
    }): Promise<ApiResponse<ProductsResponse>> => {
        const searchParams = new URLSearchParams()
        if (params?.page !== undefined) searchParams.append('page', params.page.toString())
        if (params?.size !== undefined) searchParams.append('size', params.size.toString())
        if (params?.search) searchParams.append('search', params.search)
        if (params?.categoryId) searchParams.append('categoryId', params.categoryId.toString())
        if (params?.sort) searchParams.append('sort', params.sort)

        const response = await apiClient.get<ApiResponse<ProductsResponse>>(
            `/products?${searchParams.toString()}`
        )
        return response.data
    },

    getById: async (id: number): Promise<ApiResponse<Product>> => {
        const response = await apiClient.get<ApiResponse<Product>>(`/products/${id}`)
        return response.data
    },

    create: async (data: CreateProductRequest): Promise<ApiResponse<Product>> => {
        const response = await apiClient.post<ApiResponse<Product>>('/admin/products', data)
        return response.data
    },

    update: async (id: number, data: Partial<CreateProductRequest>): Promise<ApiResponse<Product>> => {
        const response = await apiClient.put<ApiResponse<Product>>(`/admin/products/${id}`, data)
        return response.data
    },

    delete: async (id: number): Promise<void> => {
        await apiClient.delete(`/admin/products/${id}`)
    },
}
