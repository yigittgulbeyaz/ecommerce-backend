import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiClient, ApiResponse } from '../api/client'
import { useCartStore } from '../stores/cartStore'
import { useAuthStore } from '../stores/authStore'
import { Icons } from '../components/Icons'
import './ProductDetail.css'

interface Product {
    id: number
    name: string
    description: string
    price: number
    categoryId: number
    categoryName: string
}

interface RelatedProduct {
    id: number
    name: string
    price: number
    categoryName: string
}

interface PagedProducts {
    items: RelatedProduct[]
}

export function ProductDetail() {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
    const addToCart = useCartStore((state) => state.addItem)

    const [product, setProduct] = useState<Product | null>(null)
    const [relatedProducts, setRelatedProducts] = useState<RelatedProduct[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [quantity, setQuantity] = useState(1)
    const [addingToCart, setAddingToCart] = useState(false)
    const [addedToCart, setAddedToCart] = useState(false)

    useEffect(() => {
        if (id) {
            fetchProduct(id)
        }
    }, [id])

    const fetchProduct = async (productId: string) => {
        setLoading(true)
        setError(null)
        try {
            const response = await apiClient.get<ApiResponse<Product>>(`/products/${productId}`)

            if (response.data.success && response.data.data) {
                setProduct(response.data.data)
                // Fetch related products from same category
                fetchRelatedProducts(response.data.data.categoryId, parseInt(productId))
            } else {
                setError('Product not found')
            }
        } catch (err) {
            console.error('Failed to fetch product:', err)
            setError('Failed to load product')
        } finally {
            setLoading(false)
        }
    }

    const fetchRelatedProducts = async (categoryId: number, excludeId: number) => {
        try {
            const response = await apiClient.get<ApiResponse<PagedProducts>>(
                `/products?categoryId=${categoryId}&size=4`
            )
            if (response.data.success && response.data.data?.items) {
                setRelatedProducts(
                    response.data.data.items.filter((p) => p.id !== excludeId).slice(0, 4)
                )
            }
        } catch (err) {
            console.error('Failed to fetch related products:', err)
        }
    }

    const handleAddToCart = async () => {
        if (!product) return

        if (!isAuthenticated) {
            navigate('/login')
            return
        }

        setAddingToCart(true)
        try {
            await addToCart(
                {
                    id: product.id,
                    name: product.name,
                    price: product.price,
                },
                quantity
            )
            setAddedToCart(true)
            setTimeout(() => setAddedToCart(false), 2000)
        } catch (err) {
            console.error('Failed to add to cart:', err)
        } finally {
            setAddingToCart(false)
        }
    }

    const handleBuyNow = async () => {
        if (!product) return

        await handleAddToCart()
        if (isAuthenticated) {
            navigate('/checkout')
        } else {
            navigate('/login?redirect=/checkout')
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
    }

    const getProductGradient = () => {
        if (!product) return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        const gradients = [
            'linear-gradient(135deg, #FF6B6B 0%, #FF8A65 100%)',
            'linear-gradient(135deg, #5C6BC0 0%, #7986CB 100%)',
            'linear-gradient(135deg, #4CAF50 0%, #81C784 100%)',
            'linear-gradient(135deg, #FF9800 0%, #FFB74D 100%)',
            'linear-gradient(135deg, #E91E63 0%, #F48FB1 100%)',
            'linear-gradient(135deg, #00BCD4 0%, #4DD0E1 100%)',
        ]
        return gradients[product.id % gradients.length]
    }

    if (loading) {
        return (
            <div className="product-detail-page">
                <div className="product-detail-container">
                    <div className="product-detail-skeleton">
                        <div className="skeleton product-image-skeleton" />
                        <div className="product-info-skeleton">
                            <div className="skeleton skeleton-text" style={{ width: '60%' }} />
                            <div className="skeleton skeleton-text" style={{ width: '80%', height: '2rem' }} />
                            <div className="skeleton skeleton-text" style={{ width: '100%' }} />
                            <div className="skeleton skeleton-text" style={{ width: '40%', height: '2rem' }} />
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    if (error || !product) {
        return (
            <div className="product-detail-page">
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon text-error">
                        <Icons.AlertCircle />
                    </div>
                    <span className="empty-state-title">Product Not Found</span>
                    <span className="empty-state-message">{error || 'This product does not exist'}</span>
                    <Link to="/products" className="btn btn-primary mt-4">
                        <Icons.ArrowLeft /> Back to Products
                    </Link>
                </motion.div>
            </div>
        )
    }

    return (
        <div className="product-detail-page">
            {/* Breadcrumb */}
            <nav className="breadcrumb">
                <Link to="/products">Products</Link>
                <span className="breadcrumb-separator">/</span>
                <Link to={`/products?categoryId=${product.categoryId}`}>{product.categoryName}</Link>
                <span className="breadcrumb-separator">/</span>
                <span className="breadcrumb-current">{product.name}</span>
            </nav>

            <div className="product-detail-container">
                {/* Product Image */}
                <motion.div
                    className="product-image-container"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                >
                    <div className="product-image-large" style={{ background: getProductGradient() }}>
                        <Icons.Products />
                    </div>
                </motion.div>

                {/* Product Info */}
                <motion.div
                    className="product-info-container"
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                >
                    <span className="product-category-badge">{product.categoryName}</span>
                    <h1 className="product-title">{product.name}</h1>

                    <div className="product-price-section">
                        <span className="product-price-large">
                            {formatCurrency(product.price)}
                        </span>
                        <span className="product-stock">✓ In Stock</span>
                    </div>

                    <div className="product-description">
                        <h3>Description</h3>
                        <p>{product.description || 'No description available for this product.'}</p>
                    </div>

                    {/* Quantity Selector */}
                    <div className="quantity-section">
                        <label className="quantity-label">Quantity</label>
                        <div className="quantity-selector">
                            <button
                                className="quantity-btn"
                                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                disabled={quantity <= 1}
                            >
                                <Icons.Minus />
                            </button>
                            <span className="quantity-value">{quantity}</span>
                            <button
                                className="quantity-btn"
                                onClick={() => setQuantity(quantity + 1)}
                            >
                                <Icons.Plus />
                            </button>
                        </div>
                    </div>

                    {/* Actions */}
                    <div className="product-actions">
                        <button
                            className={`btn btn-lg ${addedToCart ? 'btn-success' : 'btn-secondary'}`}
                            onClick={handleAddToCart}
                            disabled={addingToCart}
                        >
                            {addedToCart ? (
                                <>✓ Added to Cart</>
                            ) : addingToCart ? (
                                'Adding...'
                            ) : (
                                <><Icons.Cart /> Add to Cart</>
                            )}
                        </button>
                        <button className="btn btn-lg btn-primary" onClick={handleBuyNow}>
                            Buy Now
                        </button>
                    </div>

                    {/* Features */}
                    <div className="product-features">
                        <div className="feature-item">
                            <span className="feature-icon">🚚</span>
                            <span>Free shipping over $50</span>
                        </div>
                        <div className="feature-item">
                            <span className="feature-icon">↩️</span>
                            <span>30-day returns</span>
                        </div>
                        <div className="feature-item">
                            <span className="feature-icon">🛡️</span>
                            <span>Secure checkout</span>
                        </div>
                    </div>
                </motion.div>
            </div>

            {/* Related Products */}
            {relatedProducts.length > 0 && (
                <section className="related-section">
                    <h2 className="related-title">Related Products</h2>
                    <div className="related-grid">
                        {relatedProducts.map((relProduct, index) => (
                            <motion.div
                                key={relProduct.id}
                                className="related-card"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.1 }}
                                whileHover={{ y: -5 }}
                            >
                                <Link to={`/products/${relProduct.id}`} className="related-link">
                                    <div
                                        className="related-image"
                                        style={{
                                            background: `linear-gradient(135deg, hsl(${(relProduct.id * 60) % 360}, 70%, 60%), hsl(${(relProduct.id * 60 + 30) % 360}, 70%, 50%))`,
                                        }}
                                    >
                                        <Icons.Products />
                                    </div>
                                    <div className="related-info">
                                        <h4 className="related-name">{relProduct.name}</h4>
                                        <span className="related-price">{formatCurrency(relProduct.price)}</span>
                                    </div>
                                </Link>
                            </motion.div>
                        ))}
                    </div>
                </section>
            )}
        </div>
    )
}
