import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ordersApi } from '../api/orders'
import { useCartStore, CartItem } from '../stores/cartStore'
import { apiClient, ApiResponse } from '../api/client'
import { Icons } from '../components/Icons'
import './Checkout.css'

interface Address {
    id: number
    title: string
    addressLine: string
    city: string
    district: string
    zipCode: string
    country: string
}

export function Checkout() {
    const { items, totalPrice, fetchCart, clearCart } = useCartStore()

    const [addresses, setAddresses] = useState<Address[]>([])
    const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null)
    const [loading, setLoading] = useState(true)
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [step, setStep] = useState<'review' | 'address' | 'payment' | 'success'>('review')
    const [orderId, setOrderId] = useState<number | null>(null)

    // Payment form state
    const [cardNumber, setCardNumber] = useState('')
    const [cardHolder, setCardHolder] = useState('')
    const [expireDate, setExpireDate] = useState('')
    const [cvv, setCvv] = useState('')

    useEffect(() => {
        fetchCart()
        fetchAddresses()
    }, [])

    const fetchAddresses = async () => {
        try {
            const response = await apiClient.get<ApiResponse<Address[]>>('/addresses')
            if (response.data.success) {
                setAddresses(response.data.data || [])
                if (response.data.data?.length > 0) {
                    setSelectedAddressId(response.data.data[0].id)
                }
            }
        } catch (err) {
            console.error('Failed to fetch addresses:', err)
        } finally {
            setLoading(false)
        }
    }

    const handlePlaceOrder = async () => {
        if (!selectedAddressId) {
            setError('Please select a shipping address')
            return
        }

        if (!cardNumber || !cardHolder || !expireDate || !cvv) {
            setError('Please fill in all payment details')
            return
        }

        setSubmitting(true)
        setError(null)

        try {
            const response = await ordersApi.checkout({
                addressId: selectedAddressId,
                paymentRequest: {
                    amount: totalPrice,
                    cardNumber: cardNumber.replace(/\s/g, ''),
                    cardHolder,
                    cvv,
                    expireDate,
                },
            })

            if (response.success) {
                setOrderId(response.data.id)
                await clearCart()
                setStep('success')
            } else {
                setError('Failed to place order. Please try again.')
            }
        } catch (err) {
            console.error('Checkout failed:', err)
            setError('Failed to place order. Please try again.')
        } finally {
            setSubmitting(false)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
    }

    const formatCardNumber = (value: string) => {
        const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '')
        const matches = v.match(/\d{4,16}/g)
        const match = (matches && matches[0]) || ''
        const parts = []
        for (let i = 0, len = match.length; i < len; i += 4) {
            parts.push(match.substring(i, i + 4))
        }
        if (parts.length) {
            return parts.join(' ')
        } else {
            return v
        }
    }

    if (loading) {
        return (
            <div className="checkout-page">
                <div className="checkout-loading">
                    <div className="skeleton" style={{ height: '400px' }} />
                </div>
            </div>
        )
    }

    if (items.length === 0 && step !== 'success') {
        return (
            <div className="checkout-page">
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon">
                        <Icons.Cart />
                    </div>
                    <span className="empty-state-title">Your cart is empty</span>
                    <span className="empty-state-message">
                        Add some products to your cart before checkout
                    </span>
                    <Link to="/products" className="btn btn-primary mt-4">
                        <Icons.Products /> Browse Products
                    </Link>
                </motion.div>
            </div>
        )
    }

    if (step === 'success') {
        return (
            <div className="checkout-page">
                <motion.div
                    className="checkout-success"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="success-icon">✓</div>
                    <h1 className="success-title">Order Placed Successfully!</h1>
                    <p className="success-message">
                        Thank you for your order. Your order #{orderId} has been confirmed.
                    </p>
                    <div className="success-actions">
                        <Link to="/orders" className="btn btn-primary">
                            View My Orders
                        </Link>
                        <Link to="/products" className="btn btn-secondary">
                            Continue Shopping
                        </Link>
                    </div>
                </motion.div>
            </div>
        )
    }

    return (
        <div className="checkout-page">
            <header className="page-header">
                <h1 className="page-title">
                    <span className="page-title-icon">
                        <Icons.Cart />
                    </span>
                    Checkout
                </h1>
                <p className="page-subtitle">Complete your purchase</p>
            </header>

            {/* Progress Steps */}
            <div className="checkout-progress">
                <div className={`progress-step ${step === 'review' ? 'active' : ''}`}>
                    <span className="step-number">1</span>
                    <span className="step-label">Review</span>
                </div>
                <div className="progress-line" />
                <div className={`progress-step ${step === 'address' ? 'active' : ''}`}>
                    <span className="step-number">2</span>
                    <span className="step-label">Address</span>
                </div>
                <div className="progress-line" />
                <div className={`progress-step ${step === 'payment' ? 'active' : ''}`}>
                    <span className="step-number">3</span>
                    <span className="step-label">Payment</span>
                </div>
            </div>

            <div className="checkout-container">
                {/* Main Content */}
                <div className="checkout-main">
                    {error && (
                        <div className="checkout-error">
                            <Icons.AlertCircle /> {error}
                        </div>
                    )}

                    {/* Step 1: Review */}
                    {step === 'review' && (
                        <motion.div
                            className="checkout-section"
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                        >
                            <h2 className="section-title">Review Your Order</h2>
                            <div className="review-items">
                                {items.map((item: CartItem) => (
                                    <div key={item.productId} className="review-item">
                                        <div className="item-image">
                                            <Icons.Products />
                                        </div>
                                        <div className="item-details">
                                            <span className="item-name">{item.productName}</span>
                                            <span className="item-quantity">Qty: {item.quantity}</span>
                                        </div>
                                        <span className="item-price">
                                            {formatCurrency(item.totalPrice)}
                                        </span>
                                    </div>
                                ))}
                            </div>
                            <button
                                className="btn btn-primary btn-lg"
                                onClick={() => setStep('address')}
                            >
                                Continue to Address
                            </button>
                        </motion.div>
                    )}

                    {/* Step 2: Address */}
                    {step === 'address' && (
                        <motion.div
                            className="checkout-section"
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                        >
                            <h2 className="section-title">Shipping Address</h2>
                            {addresses.length > 0 ? (
                                <div className="address-list">
                                    {addresses.map((address) => (
                                        <div
                                            key={address.id}
                                            className={`address-card ${selectedAddressId === address.id ? 'selected' : ''}`}
                                            onClick={() => setSelectedAddressId(address.id)}
                                        >
                                            <div className="address-radio">
                                                {selectedAddressId === address.id ? '●' : '○'}
                                            </div>
                                            <div className="address-content">
                                                <span className="address-title">{address.title}</span>
                                                <span className="address-line">{address.addressLine}</span>
                                                <span className="address-city">
                                                    {address.district}, {address.city}, {address.zipCode}
                                                </span>
                                                <span className="address-country">{address.country}</span>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="no-address">
                                    <p>No saved addresses found.</p>
                                    <Link to="/profile" className="btn btn-secondary">
                                        Add Address in Profile
                                    </Link>
                                </div>
                            )}
                            <div className="step-actions">
                                <button
                                    className="btn btn-ghost"
                                    onClick={() => setStep('review')}
                                >
                                    Back
                                </button>
                                <button
                                    className="btn btn-primary btn-lg"
                                    onClick={() => setStep('payment')}
                                    disabled={!selectedAddressId}
                                >
                                    Continue to Payment
                                </button>
                            </div>
                        </motion.div>
                    )}

                    {/* Step 3: Payment */}
                    {step === 'payment' && (
                        <motion.div
                            className="checkout-section"
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                        >
                            <h2 className="section-title">Payment Details</h2>
                            <div className="payment-form">
                                <div className="form-group">
                                    <label className="form-label">Card Number</label>
                                    <input
                                        type="text"
                                        className="input"
                                        placeholder="1234 5678 9012 3456"
                                        value={cardNumber}
                                        onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                                        maxLength={19}
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Card Holder Name</label>
                                    <input
                                        type="text"
                                        className="input"
                                        placeholder="JOHN DOE"
                                        value={cardHolder}
                                        onChange={(e) => setCardHolder(e.target.value.toUpperCase())}
                                    />
                                </div>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label className="form-label">Expiry Date</label>
                                        <input
                                            type="text"
                                            className="input"
                                            placeholder="MM/YY"
                                            value={expireDate}
                                            onChange={(e) => setExpireDate(e.target.value)}
                                            maxLength={5}
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">CVV</label>
                                        <input
                                            type="text"
                                            className="input"
                                            placeholder="123"
                                            value={cvv}
                                            onChange={(e) => setCvv(e.target.value)}
                                            maxLength={4}
                                        />
                                    </div>
                                </div>

                                <div className="test-card-info">
                                    <Icons.Info />
                                    <span>For testing, use any card number like 4242 4242 4242 4242</span>
                                </div>
                            </div>
                            <div className="step-actions">
                                <button
                                    className="btn btn-ghost"
                                    onClick={() => setStep('address')}
                                >
                                    Back
                                </button>
                                <button
                                    className="btn btn-primary btn-lg"
                                    onClick={handlePlaceOrder}
                                    disabled={submitting}
                                >
                                    {submitting ? 'Processing...' : `Pay ${formatCurrency(totalPrice)}`}
                                </button>
                            </div>
                        </motion.div>
                    )}
                </div>

                {/* Order Summary Sidebar */}
                <aside className="checkout-sidebar">
                    <div className="summary-card">
                        <h3 className="summary-title">Order Summary</h3>
                        <div className="summary-items">
                            {items.map((item: CartItem) => (
                                <div key={item.productId} className="summary-item">
                                    <span className="summary-item-name">
                                        {item.productName} × {item.quantity}
                                    </span>
                                    <span className="summary-item-price">
                                        {formatCurrency(item.totalPrice)}
                                    </span>
                                </div>
                            ))}
                        </div>
                        <div className="summary-divider" />
                        <div className="summary-row">
                            <span>Subtotal</span>
                            <span>{formatCurrency(totalPrice)}</span>
                        </div>
                        <div className="summary-row">
                            <span>Shipping</span>
                            <span className="free">Free</span>
                        </div>
                        <div className="summary-divider" />
                        <div className="summary-total">
                            <span>Total</span>
                            <span>{formatCurrency(totalPrice)}</span>
                        </div>
                    </div>
                </aside>
            </div>
        </div>
    )
}
