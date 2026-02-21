import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuthStore } from '../stores/authStore'
import { useCartStore } from '../stores/cartStore'
import { apiClient, ApiResponse } from '../api/client'
import { Icons } from '../components/Icons'
import './Dashboard.css'

interface RecentOrder {
    id: number
    totalAmount: number
    status: string
    createdAt: string
}

interface Product {
    id: number
    name: string
    description: string
    price: number
    categoryName: string
}

interface PagedProducts {
    items: Product[]
    meta: { totalElements: number }
}

export function Dashboard() {
    const user = useAuthStore((state) => state.user)
    const addToCart = useCartStore((state) => state.addItem)
    const [recentOrders, setRecentOrders] = useState<RecentOrder[]>([])
    const [featuredProducts, setFeaturedProducts] = useState<Product[]>([])
    const [loading, setLoading] = useState(true)
    const [addingToCart, setAddingToCart] = useState<number | null>(null)
    const [hoveredProduct, setHoveredProduct] = useState<number | null>(null)

    useEffect(() => {
        fetchDashboardData()
    }, [])

    const fetchDashboardData = async () => {
        try {
            // Fetch user orders
            const ordersResponse = await apiClient.get<ApiResponse<RecentOrder[]>>('/orders')
            if (ordersResponse.data.success) {
                const data = ordersResponse.data.data
                setRecentOrders(Array.isArray(data) ? data.slice(0, 5) : [])
            }

            // Fetch featured products
            const productsResponse = await apiClient.get<ApiResponse<PagedProducts>>('/products?size=8')
            if (productsResponse.data.success) {
                setFeaturedProducts(productsResponse.data.data.items || [])
            }
        } catch (error) {
            console.error('Failed to fetch dashboard data:', error)
        } finally {
            setLoading(false)
        }
    }

    const handleAddToCart = async (product: Product) => {
        setAddingToCart(product.id)
        try {
            await addToCart({
                id: product.id,
                name: product.name,
                price: product.price,
            })
        } catch (err) {
            console.error('Failed to add to cart:', err)
        } finally {
            setAddingToCart(null)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
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

    const getGreeting = () => {
        const hour = new Date().getHours()
        if (hour < 12) return 'Good morning'
        if (hour < 18) return 'Good afternoon'
        return 'Good evening'
    }

    const quickActions = [
        { label: 'Browse Products', icon: Icons.Products, path: '/products', color: '#f97316', bgColor: 'rgba(249, 115, 22, 0.12)' },
        { label: 'My Orders', icon: Icons.Orders, path: '/orders', color: '#3b82f6', bgColor: 'rgba(59, 130, 246, 0.12)' },
        { label: 'Categories', icon: Icons.Categories, path: '/categories', color: '#10b981', bgColor: 'rgba(16, 185, 129, 0.12)' },
        { label: 'My Cart', icon: Icons.Cart, path: '/cart', color: '#ec4899', bgColor: 'rgba(236, 72, 153, 0.12)' },
    ]

    return (
        <div className="user-dashboard">
            {/* Welcome Hero */}
            <motion.section
                className="welcome-hero"
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
            >
                {/* Animated mesh background */}
                <div className="hero-mesh">
                    <div className="mesh-blob mesh-1" />
                    <div className="mesh-blob mesh-2" />
                    <div className="mesh-blob mesh-3" />
                </div>

                <div className="welcome-content">
                    <div className="welcome-left">
                        <motion.div
                            className="hero-avatar"
                            initial={{ scale: 0 }}
                            animate={{ scale: 1 }}
                            transition={{ type: "spring", stiffness: 200, delay: 0.2 }}
                        >
                            <div className="hero-avatar-ring">
                                <span className="hero-avatar-letter">
                                    {user?.name?.charAt(0).toUpperCase() || 'U'}
                                </span>
                            </div>
                            <div className="avatar-status-dot" />
                        </motion.div>

                        <div className="welcome-text">
                            <motion.p
                                className="welcome-eyebrow"
                                initial={{ opacity: 0, x: -10 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: 0.3 }}
                            >
                                {getGreeting()} 👋
                            </motion.p>
                            <motion.h1
                                className="welcome-title"
                                initial={{ opacity: 0, x: -10 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: 0.4 }}
                            >
                                {user?.name}
                            </motion.h1>
                            <motion.p
                                className="welcome-subtitle"
                                initial={{ opacity: 0, x: -10 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: 0.5 }}
                            >
                                Let's find something amazing today ✨
                            </motion.p>
                        </div>
                    </div>

                    <div className="welcome-right">
                        <motion.div
                            className="hero-stat-card"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.5 }}
                        >
                            <span className="hero-stat-icon">🛒</span>
                            <div className="hero-stat-info">
                                <span className="hero-stat-value">{useCartStore.getState().items.length}</span>
                                <span className="hero-stat-label">In Cart</span>
                            </div>
                        </motion.div>
                        <motion.div
                            className="hero-stat-card"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.6 }}
                        >
                            <span className="hero-stat-icon">📦</span>
                            <div className="hero-stat-info">
                                <span className="hero-stat-value">{recentOrders.length}</span>
                                <span className="hero-stat-label">Orders</span>
                            </div>
                        </motion.div>
                    </div>
                </div>

                {/* Floating sparkles */}
                <div className="hero-sparkles">
                    <span className="sparkle-dot s1" />
                    <span className="sparkle-dot s2" />
                    <span className="sparkle-dot s3" />
                    <span className="sparkle-dot s4" />
                    <span className="sparkle-dot s5" />
                </div>
            </motion.section>

            {/* Quick Actions */}
            <section className="quick-actions-section">
                <h2 className="section-title">Quick Actions</h2>
                <div className="quick-actions-grid">
                    {quickActions.map((action, index) => (
                        <motion.div
                            key={action.label}
                            initial={{ opacity: 0, scale: 0.9 }}
                            animate={{ opacity: 1, scale: 1 }}
                            transition={{ delay: index * 0.1 }}
                        >
                            <Link to={action.path} className="quick-action-card">
                                <div
                                    className="action-icon-container"
                                    style={{ background: action.bgColor }}
                                >
                                    <action.icon />
                                </div>
                                <span className="action-label">{action.label}</span>
                                <span className="action-arrow"><Icons.ArrowRight /></span>
                            </Link>
                        </motion.div>
                    ))}
                </div>
            </section>

            {/* Featured Products */}
            <section className="featured-products-section">
                <div className="section-header">
                    <h2 className="section-title">✨ Magical Picks For You</h2>
                    <Link to="/products" className="see-all-link">
                        See All <Icons.ArrowRight />
                    </Link>
                </div>

                <div className="products-carousel">
                    {loading ? (
                        <div className="products-grid">
                            {[...Array(4)].map((_, i) => (
                                <div key={i} className="product-card-skeleton">
                                    <div className="skeleton-image" />
                                    <div className="skeleton-text" />
                                    <div className="skeleton-text short" />
                                </div>
                            ))}
                        </div>
                    ) : featuredProducts.length > 0 ? (
                        <div className="products-grid">
                            <AnimatePresence>
                                {featuredProducts.slice(0, 8).map((product, index) => (
                                    <motion.div
                                        key={product.id}
                                        className="product-card-featured"
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: index * 0.05 }}
                                        onMouseEnter={() => setHoveredProduct(product.id)}
                                        onMouseLeave={() => setHoveredProduct(null)}
                                        whileHover={{ y: -8 }}
                                    >
                                        <Link to={`/products/${product.id}`} className="product-link">
                                            <div className="product-image-placeholder">
                                                <Icons.Products />
                                                <span className="category-tag">{product.categoryName}</span>
                                            </div>
                                            <div className="product-info">
                                                <h3 className="product-name">{product.name}</h3>
                                                <p className="product-description">
                                                    {product.description?.slice(0, 60)}...
                                                </p>
                                                <div className="product-footer">
                                                    <span className="product-price">{formatCurrency(product.price)}</span>
                                                    {hoveredProduct === product.id && (
                                                        <motion.button
                                                            className="add-to-cart-btn"
                                                            initial={{ scale: 0 }}
                                                            animate={{ scale: 1 }}
                                                            onClick={(e) => {
                                                                e.preventDefault()
                                                                handleAddToCart(product)
                                                            }}
                                                            disabled={addingToCart === product.id}
                                                        >
                                                            {addingToCart === product.id ? (
                                                                <span className="mini-spinner" />
                                                            ) : (
                                                                <Icons.Plus />
                                                            )}
                                                        </motion.button>
                                                    )}
                                                </div>
                                            </div>
                                        </Link>
                                    </motion.div>
                                ))}
                            </AnimatePresence>
                        </div>
                    ) : (
                        <div className="empty-products">
                            <Icons.Products />
                            <p>No products available yet</p>
                            <Link to="/products" className="btn btn-primary">
                                Browse All Products
                            </Link>
                        </div>
                    )}
                </div>
            </section>

            {/* Recent Orders */}
            <section className="recent-orders-section">
                <div className="section-header">
                    <h2 className="section-title">📦 Recent Orders</h2>
                    <Link to="/orders" className="see-all-link">
                        View All <Icons.ArrowRight />
                    </Link>
                </div>

                <div className="orders-container">
                    {loading ? (
                        <div className="orders-loading">
                            {[...Array(3)].map((_, i) => (
                                <div key={i} className="order-skeleton" />
                            ))}
                        </div>
                    ) : recentOrders.length > 0 ? (
                        <div className="orders-list">
                            {recentOrders.map((order, index) => (
                                <motion.div
                                    key={order.id}
                                    className="order-card"
                                    initial={{ opacity: 0, x: -20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    transition={{ delay: index * 0.1 }}
                                >
                                    <div className="order-main">
                                        <div className="order-info">
                                            <span className="order-id">Order #{order.id}</span>
                                            <span className="order-date">
                                                {new Date(order.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <span className="order-amount">{formatCurrency(order.totalAmount)}</span>
                                    </div>
                                    <div className="order-status-row">
                                        <span
                                            className="status-badge"
                                            style={{
                                                background: `${getStatusColor(order.status)}15`,
                                                color: getStatusColor(order.status)
                                            }}
                                        >
                                            {order.status.toLowerCase()}
                                        </span>
                                        <Link to={`/orders/${order.id}`} className="view-order-link">
                                            View Details →
                                        </Link>
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    ) : (
                        <div className="empty-orders">
                            <Icons.Orders />
                            <p>No orders yet</p>
                            <Link to="/products" className="btn btn-primary">
                                Start Shopping
                            </Link>
                        </div>
                    )}
                </div>
            </section>

            {/* Continue Shopping CTA */}
            <motion.section
                className="cta-section"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.5 }}
            >
                <div className="cta-content">
                    <h2 className="cta-title">Continue Your Magical Journey 🪄</h2>
                    <p className="cta-subtitle">Discover new enchanting items added daily</p>
                    <Link to="/products" className="btn btn-cta">
                        Explore Products
                        <Icons.ArrowRight />
                    </Link>
                </div>
                <div className="cta-decorations">
                    <span className="sparkle">✨</span>
                    <span className="sparkle">🌟</span>
                    <span className="sparkle">⭐</span>
                </div>
            </motion.section>
        </div>
    )
}
