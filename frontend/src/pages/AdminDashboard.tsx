import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useAuthStore } from '../stores/authStore'
import { apiClient, ApiResponse } from '../api/client'
import { Icons } from '../components/Icons'
import './AdminDashboard.css'

interface DashboardStats {
    totalProducts: number
    totalOrders: number
    totalCategories: number
    totalUsers: number
    revenue: number
}

interface RecentOrder {
    id: number
    totalAmount: number
    status: string
    createdAt: string
}

export function AdminDashboard() {
    const user = useAuthStore((state) => state.user)
    const [stats, setStats] = useState<DashboardStats | null>(null)
    const [recentOrders, setRecentOrders] = useState<RecentOrder[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        fetchDashboardData()
    }, [])

    const fetchDashboardData = async () => {
        try {
            const [statsRes, ordersRes] = await Promise.all([
                apiClient.get<ApiResponse<DashboardStats>>('/admin/stats'),
                apiClient.get<ApiResponse<RecentOrder[]>>('/admin/orders?size=5')
            ])

            if (statsRes.data.success) {
                setStats(statsRes.data.data)
            }
            if (ordersRes.data.success) {
                const data = ordersRes.data.data
                setRecentOrders(Array.isArray(data) ? data.slice(0, 5) : [])
            }
        } catch (error) {
            console.error('Failed to fetch dashboard data:', error)
        } finally {
            setLoading(false)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            maximumFractionDigits: 0
        }).format(amount)
    }

    const getStatusColor = (status: string) => {
        const colors: Record<string, string> = {
            'COMPLETED': '#10b981',
            'DELIVERED': '#10b981',
            'PENDING': '#f59e0b',
            'PROCESSING': '#3b82f6',
            'CANCELLED': '#ef4444',
            'FAILED': '#ef4444',
        }
        return colors[status] || '#6b7280'
    }

    const statCards = [
        {
            label: 'Total Products',
            value: stats?.totalProducts || 0,
            icon: Icons.Products,
            color: '#f97316',
            bgColor: 'rgba(249, 115, 22, 0.15)',
        },
        {
            label: 'Total Orders',
            value: stats?.totalOrders || 0,
            icon: Icons.Orders,
            color: '#3b82f6',
            bgColor: 'rgba(59, 130, 246, 0.15)',
        },
        {
            label: 'Categories',
            value: stats?.totalCategories || 0,
            icon: Icons.Categories,
            color: '#10b981',
            bgColor: 'rgba(16, 185, 129, 0.15)',
        },
        {
            label: 'Total Users',
            value: stats?.totalUsers || 0,
            icon: Icons.User,
            color: '#8b5cf6',
            bgColor: 'rgba(139, 92, 246, 0.15)',
        },
    ]

    const quickActions = [
        { label: 'Manage Products', icon: Icons.Products, path: '/products', color: '#f97316' },
        { label: 'Manage Orders', icon: Icons.Orders, path: '/orders', color: '#3b82f6' },
        { label: 'Manage Categories', icon: Icons.Categories, path: '/categories', color: '#10b981' },
        { label: 'Manage Users', icon: Icons.User, path: '/admin/users', color: '#8b5cf6' },
    ]

    return (
        <div className="admin-dashboard">
            {/* Header */}
            <motion.header
                className="admin-header"
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
            >
                <div className="admin-header-content">
                    <div className="admin-greeting">
                        <div className="admin-badge">🛠️ Admin Control Center</div>
                        <h1 className="admin-title">Welcome back, {user?.name}!</h1>
                        <p className="admin-subtitle">Here's what's happening with your store today</p>
                    </div>
                    <div className="admin-revenue-card">
                        <span className="revenue-label">Total Revenue</span>
                        <span className="revenue-value">{formatCurrency(stats?.revenue || 0)}</span>
                        <span className="revenue-change">↑ 12.5% from last month</span>
                    </div>
                </div>
            </motion.header>

            {/* Stats Grid */}
            <div className="admin-stats-grid">
                {loading ? (
                    Array.from({ length: 4 }).map((_, i) => (
                        <div key={i} className="admin-stat-card skeleton-card">
                            <div className="skeleton" style={{ width: '48px', height: '48px', borderRadius: '12px' }} />
                            <div style={{ flex: 1 }}>
                                <div className="skeleton" style={{ width: '60px', height: '28px', marginBottom: '8px' }} />
                                <div className="skeleton" style={{ width: '80px', height: '14px' }} />
                            </div>
                        </div>
                    ))
                ) : (
                    statCards.map((stat, index) => (
                        <motion.div
                            key={stat.label}
                            className="admin-stat-card"
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: index * 0.1 }}
                            whileHover={{ scale: 1.02, y: -4 }}
                        >
                            <div className="stat-icon-wrapper" style={{ background: stat.bgColor }}>
                                <stat.icon />
                            </div>
                            <div className="stat-info">
                                <span className="stat-value" style={{ color: stat.color }}>
                                    {stat.value.toLocaleString()}
                                </span>
                                <span className="stat-label">{stat.label}</span>
                            </div>
                            <div className="stat-glow" style={{ background: stat.color }} />
                        </motion.div>
                    ))
                )}
            </div>

            {/* Main Content Grid */}
            <div className="admin-content-grid">
                {/* Recent Orders */}
                <motion.div
                    className="admin-card orders-card"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.3 }}
                >
                    <div className="card-header">
                        <h2 className="card-title">
                            <Icons.Orders />
                            Recent Orders
                        </h2>
                        <Link to="/orders" className="view-all-link">
                            View All →
                        </Link>
                    </div>
                    <div className="orders-list">
                        {loading ? (
                            Array.from({ length: 4 }).map((_, i) => (
                                <div key={i} className="order-item skeleton-order">
                                    <div className="skeleton" style={{ width: '100%', height: '52px' }} />
                                </div>
                            ))
                        ) : recentOrders.length > 0 ? (
                            recentOrders.map((order, index) => (
                                <motion.div
                                    key={order.id}
                                    className="order-item"
                                    initial={{ opacity: 0, x: -10 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    transition={{ delay: 0.4 + index * 0.05 }}
                                >
                                    <div className="order-info">
                                        <span className="order-id">Order #{order.id}</span>
                                        <span className="order-amount">{formatCurrency(order.totalAmount)}</span>
                                    </div>
                                    <span
                                        className="order-status"
                                        style={{
                                            background: `${getStatusColor(order.status)}20`,
                                            color: getStatusColor(order.status)
                                        }}
                                    >
                                        {order.status.toLowerCase()}
                                    </span>
                                </motion.div>
                            ))
                        ) : (
                            <div className="empty-orders">
                                <Icons.Orders />
                                <p>No orders yet</p>
                            </div>
                        )}
                    </div>
                </motion.div>

                {/* Quick Actions */}
                <motion.div
                    className="admin-card actions-card"
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.4 }}
                >
                    <div className="card-header">
                        <h2 className="card-title">
                            <Icons.Settings />
                            Quick Actions
                        </h2>
                    </div>
                    <div className="quick-actions-grid">
                        {quickActions.map((action, index) => (
                            <motion.div
                                key={action.label}
                                initial={{ opacity: 0, scale: 0.9 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ delay: 0.5 + index * 0.1 }}
                            >
                                <Link to={action.path} className="quick-action-btn">
                                    <div
                                        className="action-icon"
                                        style={{
                                            background: `${action.color}20`,
                                            color: action.color
                                        }}
                                    >
                                        <action.icon />
                                    </div>
                                    <span className="action-label">{action.label}</span>
                                    <Icons.ArrowRight />
                                </Link>
                            </motion.div>
                        ))}
                    </div>
                </motion.div>
            </div>

            {/* Activity Feed (placeholder for future) */}
            <motion.div
                className="admin-card activity-card"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.6 }}
            >
                <div className="card-header">
                    <h2 className="card-title">
                        <Icons.Info />
                        System Status
                    </h2>
                </div>
                <div className="system-status">
                    <div className="status-item">
                        <div className="status-indicator online" />
                        <span className="status-text">All systems operational</span>
                    </div>
                    <div className="status-item">
                        <div className="status-indicator online" />
                        <span className="status-text">Database connected</span>
                    </div>
                    <div className="status-item">
                        <div className="status-indicator online" />
                        <span className="status-text">Payment gateway active</span>
                    </div>
                </div>
            </motion.div>
        </div>
    )
}
