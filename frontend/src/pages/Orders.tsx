import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { apiClient, ApiResponse } from '../api/client'
import { useAuthStore } from '../stores/authStore'
import { Icons } from '../components/Icons'

interface Order {
    id: number
    totalAmount: number
    status: string
    createdAt: string
    items: Array<{
        productName: string
        quantity: number
        price: number
    }>
}

export function Orders() {
    const user = useAuthStore((state) => state.user)
    const [orders, setOrders] = useState<Order[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const isAdmin = user?.role === 'ADMIN'

    useEffect(() => {
        fetchOrders()
    }, [isAdmin])

    const fetchOrders = async () => {
        setLoading(true)
        setError(null)
        try {
            const endpoint = isAdmin ? '/admin/orders' : '/orders'
            const response = await apiClient.get<ApiResponse<Order[] | { content: Order[] }>>(endpoint)

            if (response.data.success) {
                const data = response.data.data
                setOrders(Array.isArray(data) ? data : data.content)
            }
        } catch (err) {
            console.error('Failed to fetch orders:', err)
            setError('Could not connect to server. Is the backend running?')
        } finally {
            setLoading(false)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
    }

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        })
    }

    const getStatusBadge = (status: string) => {
        const statusMap: Record<string, string> = {
            'COMPLETED': 'badge-success',
            'DELIVERED': 'badge-success',
            'PENDING': 'badge-warning',
            'PROCESSING': 'badge-info',
            'CANCELLED': 'badge-crimson',
            'FAILED': 'badge-crimson',
        }
        return statusMap[status] || ''
    }

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}
        >
            <header className="page-header">
                <h1 className="page-title">
                    <span className="page-title-icon" style={{ background: 'linear-gradient(135deg, var(--orders-color) 0%, #7986CB 100%)' }}>
                        <Icons.Orders />
                    </span>
                    Orders
                </h1>
                <p className="page-subtitle">
                    {isAdmin ? 'All store orders' : 'Your order history'} ({orders.length})
                </p>
            </header>

            {error ? (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon text-error">
                        <Icons.AlertCircle />
                    </div>
                    <span className="empty-state-title">Connection Error</span>
                    <span className="empty-state-message">{error}</span>
                    <button className="btn btn-secondary mt-4" onClick={fetchOrders}>
                        <Icons.Refresh /> Try Again
                    </button>
                </motion.div>
            ) : loading ? (
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Order</th>
                                <th>Status</th>
                                <th>Amount</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {[1, 2, 3].map((i) => (
                                <tr key={i}>
                                    <td><div className="skeleton skeleton-text" style={{ width: '80px' }} /></td>
                                    <td><div className="skeleton skeleton-text" style={{ width: '100px' }} /></td>
                                    <td><div className="skeleton skeleton-text" style={{ width: '80px' }} /></td>
                                    <td><div className="skeleton skeleton-text" style={{ width: '100px' }} /></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : orders.length > 0 ? (
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Order</th>
                                <th>Status</th>
                                <th>Amount</th>
                                <th>Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((order, index) => (
                                <motion.tr
                                    key={order.id}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: index * 0.03 }}
                                >
                                    <td>
                                        <div className="flex items-center gap-3">
                                            <div style={{
                                                width: '40px',
                                                height: '40px',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                background: 'var(--cream)',
                                                borderRadius: 'var(--radius-md)',
                                                color: 'var(--orders-color)'
                                            }}>
                                                <Icons.Orders />
                                            </div>
                                            <span style={{ fontFamily: 'var(--font-display)', fontWeight: 600 }}>
                                                Order #{order.id}
                                            </span>
                                        </div>
                                    </td>
                                    <td>
                                        <span className={`badge ${getStatusBadge(order.status)}`}>
                                            {order.status.toLowerCase()}
                                        </span>
                                    </td>
                                    <td>
                                        <span className="price-tag">{formatCurrency(order.totalAmount)}</span>
                                    </td>
                                    <td style={{ color: 'var(--text-secondary)', fontSize: 'var(--text-sm)' }}>
                                        {formatDate(order.createdAt)}
                                    </td>
                                    <td>
                                        <button className="btn-icon hover-wobble">
                                            <Icons.ArrowRight />
                                        </button>
                                    </td>
                                </motion.tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon" style={{ color: 'var(--orders-color)' }}>
                        <Icons.Orders />
                    </div>
                    <span className="empty-state-title">
                        {isAdmin ? 'No orders yet' : 'No orders yet'}
                    </span>
                    <span className="empty-state-message">
                        {isAdmin
                            ? 'Orders will appear here when customers start buying'
                            : 'Start shopping and your order history will appear here!'}
                    </span>
                    {!isAdmin && (
                        <a href="/products" className="btn btn-primary mt-4">
                            <Icons.Products /> Browse Products
                        </a>
                    )}
                </motion.div>
            )}
        </motion.div>
    )
}
