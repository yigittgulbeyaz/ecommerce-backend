import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiClient, ApiResponse } from '../api/client'
import { useAuthStore } from '../stores/authStore'
import { useCartStore } from '../stores/cartStore'
import { Icons } from '../components/Icons'
import './LandingPage.css'

interface Product {
    id: number
    name: string
    description: string
    price: number
    categoryName: string
}

interface Category {
    id: number
    name: string
}

interface PagedProducts {
    items: Product[]
    meta: { totalElements: number }
}

export function LandingPage() {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
    const addToCart = useCartStore((state) => state.addItem)
    const navigate = useNavigate()
    const [featuredProducts, setFeaturedProducts] = useState<Product[]>([])
    const [categories, setCategories] = useState<Category[]>([])
    const [loading, setLoading] = useState(true)
    const [addingToCart, setAddingToCart] = useState<number | null>(null)

    useEffect(() => {
        fetchData()
    }, [])

    const fetchData = async () => {
        try {
            const [productsRes, categoriesRes] = await Promise.all([
                apiClient.get<ApiResponse<PagedProducts>>('/products?size=40'),
                apiClient.get<ApiResponse<Category[]>>('/categories'),
            ])

            if (productsRes.data.success) {
                const allProducts = productsRes.data.data.items || []
                // Pick one product per category for diversity, then fill remaining
                const byCategory = new Map<string, Product[]>()
                allProducts.forEach((p) => {
                    const cat = p.categoryName || 'Other'
                    if (!byCategory.has(cat)) byCategory.set(cat, [])
                    byCategory.get(cat)!.push(p)
                })
                const picked: Product[] = []
                const usedIds = new Set<number>()
                // One from each category
                byCategory.forEach((products) => {
                    if (picked.length < 8) {
                        const randomPick = products[Math.floor(Math.random() * products.length)]
                        picked.push(randomPick)
                        usedIds.add(randomPick.id)
                    }
                })
                // Fill remaining slots with random products
                const remaining = allProducts.filter((p) => !usedIds.has(p.id))
                while (picked.length < 8 && remaining.length > 0) {
                    const idx = Math.floor(Math.random() * remaining.length)
                    picked.push(remaining.splice(idx, 1)[0])
                }
                setFeaturedProducts(picked.slice(0, 8))
            }
            if (categoriesRes.data.success) {
                setCategories(categoriesRes.data.data || [])
            }
        } catch (error) {
            console.error('Failed to fetch data:', error)
        } finally {
            setLoading(false)
        }
    }

    const handleAddToCart = async (product: Product) => {
        if (!isAuthenticated) {
            navigate('/login')
            return
        }
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

    const getCategoryGradient = (index: number) => {
        const gradients = [
            'linear-gradient(135deg, #9333EA 0%, #C084FC 100%)',
            'linear-gradient(135deg, #6366F1 0%, #A5B4FC 100%)',
            'linear-gradient(135deg, #EC4899 0%, #F9A8D4 100%)',
            'linear-gradient(135deg, #8B5CF6 0%, #DDD6FE 100%)',
            'linear-gradient(135deg, #F59E0B 0%, #FCD34D 100%)',
            'linear-gradient(135deg, #10B981 0%, #6EE7B7 100%)',
            'linear-gradient(135deg, #3B82F6 0%, #93C5FD 100%)',
            'linear-gradient(135deg, #EF4444 0%, #FCA5A5 100%)',
        ]
        return gradients[index % gradients.length]
    }

    return (
        <div className="landing-page">
            {/* Hero Section */}
            <section className="hero-section">
                <div className="hero-background">
                    <div className="hero-gradient" />
                    <div className="hero-pattern" />
                    <div className="hero-floating-elements">
                        <div className="floating-star star-1">✨</div>
                        <div className="floating-star star-2">⭐</div>
                        <div className="floating-star star-3">🌟</div>
                    </div>
                </div>
                <div className="hero-content">
                    <motion.div
                        className="hero-text"
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.6 }}
                    >
                        <span className="hero-badge">🪄 Welcome to Magic Wand</span>
                        <h1 className="hero-title">
                            Where Dreams Become
                            <br />
                            <span className="hero-title-accent">Reality</span>
                        </h1>
                        <p className="hero-subtitle">
                            Discover enchanting products curated just for you. From everyday essentials
                            to extraordinary finds — experience the magic of effortless shopping with
                            over <strong>2 million</strong> happy customers worldwide.
                        </p>
                        <div className="hero-stats">
                            <div className="hero-stat">
                                <span className="stat-number">2M+</span>
                                <span className="stat-label">Happy Customers</span>
                            </div>
                            <div className="hero-stat">
                                <span className="stat-number">50K+</span>
                                <span className="stat-label">Products</span>
                            </div>
                            <div className="hero-stat">
                                <span className="stat-number">100+</span>
                                <span className="stat-label">Countries</span>
                            </div>
                        </div>
                        <div className="hero-buttons">
                            <Link to="/products" className="btn btn-primary btn-lg">
                                <Icons.Products /> Explore Collection
                            </Link>
                            {!isAuthenticated && (
                                <Link to="/register" className="btn btn-secondary btn-lg">
                                    Join the Magic ✨
                                </Link>
                            )}
                        </div>
                    </motion.div>
                    <motion.div
                        className="hero-visual"
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                    >
                        <div className="hero-cards">
                            {[0, 1, 2].map((i) => (
                                <motion.div
                                    key={i}
                                    className="hero-card"
                                    style={{ background: getCategoryGradient(i) }}
                                    animate={{
                                        y: [0, -10, 0],
                                    }}
                                    transition={{
                                        duration: 3,
                                        repeat: Infinity,
                                        delay: i * 0.3,
                                    }}
                                >
                                    <span className="hero-card-icon">{['🪄', '✨', '🌟'][i]}</span>
                                </motion.div>
                            ))}
                        </div>
                    </motion.div>
                </div>
            </section>

            {/* Trusted By Section */}
            <section className="trusted-section">
                <p className="trusted-text">Trusted by leading brands worldwide</p>
                <div className="trusted-logos">
                    <span className="trusted-logo">◆ Forbes</span>
                    <span className="trusted-logo">◆ TechCrunch</span>
                    <span className="trusted-logo">◆ Vogue</span>
                    <span className="trusted-logo">◆ Wired</span>
                    <span className="trusted-logo">◆ Bloomberg</span>
                </div>
            </section>

            {/* Categories Section */}
            <section className="categories-section">
                <div className="section-header">
                    <div>
                        <h2 className="section-title">✨ Shop by Category</h2>
                        <p className="section-subtitle">Find exactly what you're looking for</p>
                    </div>
                    <Link to="/categories" className="btn btn-ghost">
                        View All <Icons.ArrowRight />
                    </Link>
                </div>
                <div className="categories-grid">
                    {loading ? (
                        Array.from({ length: 6 }).map((_, i) => (
                            <div key={i} className="category-card-skeleton">
                                <div className="skeleton" style={{ height: '80px' }} />
                            </div>
                        ))
                    ) : (
                        categories.slice(0, 6).map((category, index) => (
                            <motion.div
                                key={category.id}
                                className="category-card"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                whileHover={{ scale: 1.05, y: -5 }}
                            >
                                <Link to={`/products?categoryId=${category.id}`} className="category-card-link">
                                    <div
                                        className="category-icon"
                                        style={{ background: getCategoryGradient(index) }}
                                    >
                                        <Icons.Categories />
                                    </div>
                                    <span className="category-name">{category.name}</span>
                                </Link>
                            </motion.div>
                        ))
                    )}
                </div>
            </section>

            {/* Featured Products Section */}
            <section className="featured-section">
                <div className="section-header">
                    <div>
                        <h2 className="section-title">
                            <span className="section-icon">🔮</span>
                            Magical Picks
                        </h2>
                        <p className="section-subtitle">Handpicked treasures just for you</p>
                    </div>
                    <Link to="/products" className="btn btn-secondary">
                        View All <Icons.ArrowRight />
                    </Link>
                </div>
                <div className="products-grid">
                    {loading ? (
                        Array.from({ length: 8 }).map((_, i) => (
                            <div key={i} className="product-card-skeleton">
                                <div className="skeleton" style={{ height: '140px' }} />
                                <div className="skeleton skeleton-text" style={{ width: '80%', marginTop: '12px' }} />
                                <div className="skeleton skeleton-text" style={{ width: '50%' }} />
                            </div>
                        ))
                    ) : (
                        featuredProducts.map((product, index) => (
                            <motion.div
                                key={product.id}
                                className="product-card"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.05 }}
                                whileHover={{ y: -8 }}
                            >
                                <Link to={`/products/${product.id}`} className="product-link">
                                    <div
                                        className="product-image"
                                        style={{ background: getCategoryGradient(index) }}
                                    >
                                        <div className="product-image-icon">
                                            <Icons.Products />
                                        </div>
                                        {index < 3 && (
                                            <span className="trending-badge">🔥 Bestseller</span>
                                        )}
                                    </div>
                                </Link>
                                <div className="product-info">
                                    <span className="product-category">
                                        {product.categoryName || 'Uncategorized'}
                                    </span>
                                    <h3 className="product-name">{product.name}</h3>
                                    <p className="product-desc">
                                        {product.description || 'Discover this amazing product.'}
                                    </p>
                                    <div className="product-footer">
                                        <span className="product-price">
                                            {formatCurrency(product.price)}
                                        </span>
                                        <button
                                            className="btn btn-sm btn-primary"
                                            onClick={(e) => {
                                                e.preventDefault()
                                                handleAddToCart(product)
                                            }}
                                            disabled={addingToCart === product.id}
                                        >
                                            {addingToCart === product.id ? '...' : <Icons.Cart />}
                                        </button>
                                    </div>
                                </div>
                            </motion.div>
                        ))
                    )}
                </div>
            </section>

            {/* About Section */}
            <section className="about-section">
                <div className="about-content">
                    <motion.div
                        className="about-text"
                        initial={{ opacity: 0, x: -30 }}
                        whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }}
                    >
                        <span className="about-badge">Our Story</span>
                        <h2 className="about-title">Making Shopping Magical Since 2019</h2>
                        <p className="about-description">
                            What started as a small dream has grown into one of the world's most beloved
                            shopping destinations. At Magic Wand, we believe that every purchase should
                            feel special — like finding a hidden treasure.
                        </p>
                        <p className="about-description">
                            Our team of passionate curators travels the globe to discover unique products
                            from artisans and brands who share our commitment to quality, sustainability,
                            and exceptional craftsmanship.
                        </p>
                        <div className="about-features">
                            <div className="about-feature">
                                <span className="feature-check">✓</span>
                                <span>Ethically sourced products</span>
                            </div>
                            <div className="about-feature">
                                <span className="feature-check">✓</span>
                                <span>Carbon-neutral shipping</span>
                            </div>
                            <div className="about-feature">
                                <span className="feature-check">✓</span>
                                <span>Award-winning customer service</span>
                            </div>
                        </div>
                    </motion.div>
                    <motion.div
                        className="about-visual"
                        initial={{ opacity: 0, x: 30 }}
                        whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }}
                    >
                        <div className="about-image">
                            <div className="about-image-gradient"></div>
                            <span className="about-emoji">🪄</span>
                        </div>
                    </motion.div>
                </div>
            </section>

            {/* Features Section */}
            <section className="features-section">
                <h2 className="section-title centered">Why Choose Magic Wand</h2>
                <p className="section-subtitle centered">Experience the difference</p>
                <div className="features-grid">
                    <motion.div
                        className="feature-card"
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.1 }}
                    >
                        <div className="feature-icon">🚀</div>
                        <h3 className="feature-title">Lightning Fast Delivery</h3>
                        <p className="feature-desc">Free express shipping on orders over $50. Most orders arrive within 2-3 business days.</p>
                    </motion.div>
                    <motion.div
                        className="feature-card"
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.2 }}
                    >
                        <div className="feature-icon">🛡️</div>
                        <h3 className="feature-title">Secure & Protected</h3>
                        <p className="feature-desc">Bank-level encryption protects your data. Shop with complete peace of mind.</p>
                    </motion.div>
                    <motion.div
                        className="feature-card"
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.3 }}
                    >
                        <div className="feature-icon">💬</div>
                        <h3 className="feature-title">24/7 Expert Support</h3>
                        <p className="feature-desc">Our award-winning team is always here to help. Chat, call, or email — anytime.</p>
                    </motion.div>
                    <motion.div
                        className="feature-card"
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.4 }}
                    >
                        <div className="feature-icon">↩️</div>
                        <h3 className="feature-title">Hassle-Free Returns</h3>
                        <p className="feature-desc">Not satisfied? Return within 30 days for a full refund. No questions asked.</p>
                    </motion.div>
                </div>
            </section>

            {/* Testimonials Section */}
            <section className="testimonials-section">
                <h2 className="section-title centered">What Our Customers Say</h2>
                <div className="testimonials-grid">
                    <motion.div
                        className="testimonial-card"
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                    >
                        <div className="testimonial-stars">⭐⭐⭐⭐⭐</div>
                        <p className="testimonial-text">
                            "Magic Wand has completely transformed my shopping experience. The quality of products
                            and customer service is unmatched. I'm a customer for life!"
                        </p>
                        <div className="testimonial-author">
                            <div className="author-avatar">S</div>
                            <div className="author-info">
                                <span className="author-name">Sarah Mitchell</span>
                                <span className="author-title">Verified Buyer</span>
                            </div>
                        </div>
                    </motion.div>
                    <motion.div
                        className="testimonial-card"
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.1 }}
                    >
                        <div className="testimonial-stars">⭐⭐⭐⭐⭐</div>
                        <p className="testimonial-text">
                            "The attention to detail is incredible. Every package feels like unwrapping a gift.
                            Highly recommend to anyone looking for quality products."
                        </p>
                        <div className="testimonial-author">
                            <div className="author-avatar">J</div>
                            <div className="author-info">
                                <span className="author-name">James Rodriguez</span>
                                <span className="author-title">Verified Buyer</span>
                            </div>
                        </div>
                    </motion.div>
                    <motion.div
                        className="testimonial-card"
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                        transition={{ delay: 0.2 }}
                    >
                        <div className="testimonial-stars">⭐⭐⭐⭐⭐</div>
                        <p className="testimonial-text">
                            "Fast shipping, amazing products, and the best customer support I've ever experienced.
                            Magic Wand truly lives up to its name!"
                        </p>
                        <div className="testimonial-author">
                            <div className="author-avatar">E</div>
                            <div className="author-info">
                                <span className="author-name">Emily Chen</span>
                                <span className="author-title">Verified Buyer</span>
                            </div>
                        </div>
                    </motion.div>
                </div>
            </section>

            {/* CTA Section */}
            {!isAuthenticated && (
                <section className="cta-section">
                    <motion.div
                        className="cta-content"
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                    >
                        <span className="cta-badge">✨ Limited Time Offer</span>
                        <h2 className="cta-title">Get 20% Off Your First Order</h2>
                        <p className="cta-subtitle">
                            Join over 2 million happy customers. Create an account today and receive
                            an exclusive discount on your first purchase.
                        </p>
                        <div className="cta-buttons">
                            <Link to="/register" className="btn btn-primary btn-lg">
                                Claim Your Discount 🎉
                            </Link>
                            <Link to="/login" className="btn btn-ghost btn-lg">
                                Already a member? Sign In
                            </Link>
                        </div>
                    </motion.div>
                </section>
            )}

            {/* Newsletter Section */}
            <section className="newsletter-section">
                <div className="newsletter-content">
                    <h3 className="newsletter-title">Stay in the Loop</h3>
                    <p className="newsletter-subtitle">
                        Subscribe to get exclusive deals, new product alerts, and magic delivered to your inbox.
                    </p>
                    <div className="newsletter-form">
                        <input type="email" placeholder="Enter your email" className="newsletter-input" />
                        <button className="btn btn-primary">Subscribe</button>
                    </div>
                    <p className="newsletter-note">No spam, unsubscribe anytime. 🔒</p>
                </div>
            </section>

            {/* Footer */}
            <footer className="landing-footer">
                <div className="footer-main">
                    <div className="footer-brand">
                        <span className="footer-logo">🪄 Magic Wand</span>
                        <p className="footer-tagline">Where dreams become reality</p>
                        <div className="footer-social">
                            <a href="#" className="social-link">📘</a>
                            <a href="#" className="social-link">📸</a>
                            <a href="#" className="social-link">🐦</a>
                            <a href="#" className="social-link">💼</a>
                        </div>
                    </div>
                    <div className="footer-links-grid">
                        <div className="footer-column">
                            <h4 className="footer-column-title">Shop</h4>
                            <Link to="/products">All Products</Link>
                            <Link to="/categories">Categories</Link>
                            <a href="#">New Arrivals</a>
                            <a href="#">Best Sellers</a>
                        </div>
                        <div className="footer-column">
                            <h4 className="footer-column-title">Company</h4>
                            <a href="#">About Us</a>
                            <a href="#">Careers</a>
                            <a href="#">Press</a>
                            <a href="#">Blog</a>
                        </div>
                        <div className="footer-column">
                            <h4 className="footer-column-title">Support</h4>
                            <a href="#">Help Center</a>
                            <a href="#">Contact Us</a>
                            <a href="#">Shipping Info</a>
                            <a href="#">Returns</a>
                        </div>
                        <div className="footer-column">
                            <h4 className="footer-column-title">Legal</h4>
                            <a href="#">Privacy Policy</a>
                            <a href="#">Terms of Service</a>
                            <a href="#">Cookie Policy</a>
                        </div>
                    </div>
                </div>
                <div className="footer-bottom">
                    <p>&copy; 2024 Magic Wand. All rights reserved.</p>
                    <div className="footer-payments">
                        <span>💳</span>
                        <span>We accept all major payment methods</span>
                    </div>
                </div>
            </footer>
        </div>
    )
}
