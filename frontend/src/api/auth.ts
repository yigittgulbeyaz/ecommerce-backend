import { apiClient, ApiResponse } from './client'
import axios from 'axios'

interface LoginRequest {
    email: string
    password: string
}

interface RegisterRequest {
    name: string
    email: string
    password: string
}

interface AuthResponse {
    id: number
    name: string
    email: string
    accessToken: string
    refreshToken: string
}

interface LoginResponse {
    accessToken: string
    refreshToken: string
}

interface UserProfile {
    id: number
    name: string
    email: string
    role: 'USER' | 'ADMIN'
}

export const authApi = {
    login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
        const response = await apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data)
        return response.data
    },

    register: async (data: RegisterRequest): Promise<ApiResponse<AuthResponse>> => {
        const response = await apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data)
        return response.data
    },

    getMe: async (token?: string): Promise<ApiResponse<UserProfile>> => {
        // If token is provided, use it directly (for login flow before token is stored)
        if (token) {
            const response = await axios.get<ApiResponse<UserProfile>>('/api/v1/users/me', {
                headers: { Authorization: `Bearer ${token}` }
            })
            return response.data
        }
        // Otherwise use the stored token via apiClient
        const response = await apiClient.get<ApiResponse<UserProfile>>('/users/me')
        return response.data
    },
}
