import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useCartStore } from '../stores/cartStore'
import { useAuthStore } from '../stores/authStore'
import { Icons } from '../components/Icons'

export function Cart() {
    const { items, totalPrice, itemCount, loading, error, fetchCart, updateQuantity, removeItem, clearCart, clearError } = useCartStore()
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

    useEffect(() => {
        fetchCart()
    }, [fetchCart, isAuthenticated])

    const handleUpdateQuantity = async (productId: number, newQuantity: number) => {
        try {
            await updateQuantity(productId, newQuantity)
        } catch (err) {
            console.error('Failed to update quantity:', err)
        }
    }

    const handleRemoveItem = async (productId: number) => {
        try {
            await removeItem(productId)
        } catch (err) {
            console.error('Failed to remove item:', err)
        }
    }

    const handleClearCart = async () => {
        if (!confirm('Clear entire cart?')) return
        try {
            await clearCart()
        } catch (err) {
            console.error('Failed to clear cart:', err)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
    }

    // Determine error type for better messaging
    const isAuthError = error?.includes('sign in')
    const isConnectionError = error?.includes('connect')

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}
        >
            <header className="page-header">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div>
                        <h1 className="page-title">
                            <span className="page-title-icon" style={{ background: 'linear-gradient(135deg, var(--cart-color) 0%, #FFB74D 100%)' }}>
                                <Icons.Cart />
                            </span>
                            Your Cart
                        </h1>
                        <p className="page-subtitle">
                            {itemCount} {itemCount === 1 ? 'item' : 'items'} in your cart
                            {!isAuthenticated && items.length > 0 && (
                                <span style={{ color: 'var(--text-muted)', marginLeft: '8px' }}>
                                    • Guest cart
                                </span>
                            )}
                        </p>
                    </div>
                    {items.length > 0 && (
                        <button className="btn btn-ghost text-error" onClick={handleClearCart}>
                            <Icons.Trash /> Clear Cart
                        </button>
                    )}
                </div>
            </header>

            {error && isConnectionError ? (
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
                    <button className="btn btn-secondary mt-4" onClick={() => { clearError(); fetchCart(); }}>
                        <Icons.Refresh /> Try Again
                    </button>
                </motion.div>
            ) : error && isAuthError ? (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon" style={{ color: 'var(--warning-color, #f59e0b)' }}>
                        <Icons.User />
                    </div>
                    <span className="empty-state-title">Sign In Required</span>
                    <span className="empty-state-message">{error}</span>
                    <Link to="/login" className="btn btn-primary mt-4">
                        Sign In
                    </Link>
                </motion.div>
            ) : loading ? (
                <div className="card" style={{ minHeight: '200px' }}>
                    <div className="skeleton skeleton-title" />
                    <div className="skeleton skeleton-text mt-4" />
                    <div className="skeleton skeleton-text mt-2" />
                </div>
            ) : items.length > 0 ? (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 'var(--space-6)' }}>
                    {/* Cart Items */}
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">Items</h3>
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            {items.map((item, index) => (
                                <motion.div
                                    key={item.productId}
                                    initial={{ opacity: 0, x: -10 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    transition={{ delay: index * 0.05 }}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: 'var(--space-4)',
                                        padding: 'var(--space-4)',
                                        borderBottom: index < items.length - 1 ? '1px solid rgba(0,0,0,0.05)' : 'none',
                                    }}
                                >
                                    <div style={{
                                        width: '48px',
                                        height: '48px',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        background: 'var(--cream)',
                                        borderRadius: 'var(--radius-md)',
                                        color: 'var(--cart-color)'
                                    }}>
                                        <Icons.Products />
                                    </div>
                                    <div style={{ flex: 1 }}>
                                        <div style={{ fontFamily: 'var(--font-display)', fontWeight: 600 }}>{item.productName}</div>
                                        <div style={{ fontSize: 'var(--text-sm)', color: 'var(--text-muted)' }}>
                                            {formatCurrency(item.unitPrice)} each
                                        </div>
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                                        <button
                                            className="btn-icon"
                                            onClick={() => handleUpdateQuantity(item.productId, item.quantity - 1)}
                                            disabled={item.quantity <= 1}
                                        >
                                            −
                                        </button>
                                        <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, width: '36px', textAlign: 'center' }}>
                                            {item.quantity}
                                        </span>
                                        <button
                                            className="btn-icon"
                                            onClick={() => handleUpdateQuantity(item.productId, item.quantity + 1)}
                                        >
                                            +
                                        </button>
                                    </div>
                                    <div style={{ fontFamily: 'var(--font-display)', fontWeight: 700, color: 'var(--crimson)', width: '90px', textAlign: 'right' }}>
                                        {formatCurrency(item.totalPrice)}
                                    </div>
                                    <button
                                        className="btn-icon text-error hover-wobble"
                                        onClick={() => handleRemoveItem(item.productId)}
                                    >
                                        <Icons.X />
                                    </button>
                                </motion.div>
                            ))}
                        </div>
                    </div>

                    {/* Summary */}
                    <div className="card card-accent card-cart" style={{ height: 'fit-content' }}>
                        <div className="card-header">
                            <h3 className="card-title">Order Summary</h3>
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                <span style={{ color: 'var(--text-secondary)' }}>Items</span>
                                <span style={{ fontFamily: 'var(--font-display)', fontWeight: 600 }}>{itemCount}</span>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                <span style={{ color: 'var(--text-secondary)' }}>Subtotal</span>
                                <span style={{ fontFamily: 'var(--font-display)', fontWeight: 600 }}>{formatCurrency(totalPrice)}</span>
                            </div>
                            <div style={{ borderTop: '2px solid var(--cream)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-2)' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 'var(--space-4)' }}>
                                    <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 'var(--text-lg)' }}>Total</span>
                                    <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 'var(--text-xl)', color: 'var(--crimson)' }}>
                                        {formatCurrency(totalPrice)}
                                    </span>
                                </div>
                                {isAuthenticated ? (
                                    <Link to="/checkout" className="btn btn-primary w-full">
                                        Checkout <Icons.ArrowRight />
                                    </Link>
                                ) : (
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                                        <Link to="/login" className="btn btn-primary w-full">
                                            Sign in to Checkout <Icons.ArrowRight />
                                        </Link>
                                        <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-muted)', textAlign: 'center' }}>
                                            Your cart will be saved when you sign in
                                        </p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            ) : (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon" style={{ color: 'var(--cart-color)' }}>
                        <Icons.Cart />
                    </div>
                    <span className="empty-state-title">Your cart is empty</span>
                    <span className="empty-state-message">
                        Add some products to get started!
                    </span>
                    <Link to="/products" className="btn btn-primary mt-4">
                        Browse Products
                    </Link>
                </motion.div>
            )}
        </motion.div>
    )
}
