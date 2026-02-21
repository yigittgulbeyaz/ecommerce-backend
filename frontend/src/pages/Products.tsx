import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiClient, ApiResponse } from '../api/client'
import { useAuthStore } from '../stores/authStore'
import { useCartStore } from '../stores/cartStore'
import { Icons } from '../components/Icons'
import './Products.css'


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

export function Products() {
    const user = useAuthStore((state) => state.user)
    const addToCart = useCartStore((state) => state.addItem)
    const [searchParams] = useSearchParams()
    const categoryId = searchParams.get('categoryId')
    const searchFromUrl = searchParams.get('search') || ''
    const [products, setProducts] = useState<Product[]>([])
    const [pageInfo, setPageInfo] = useState<PaginationMeta | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [search, setSearch] = useState(searchFromUrl)
    const [currentPage, setCurrentPage] = useState(0)
    const [addingToCart, setAddingToCart] = useState<number | null>(null)

    const isAdmin = user?.role === 'ADMIN'

    // Sync search with URL param changes (e.g. from header search bar)
    useEffect(() => {
        setSearch(searchFromUrl)
        setCurrentPage(0)
    }, [searchFromUrl])

    useEffect(() => {
        fetchProducts()
    }, [currentPage, search, categoryId])

    const fetchProducts = async () => {
        setLoading(true)
        setError(null)
        try {
            const params = new URLSearchParams({
                page: currentPage.toString(),
                size: '10',
            })
            if (search) {
                params.append('search', search)
            }
            if (categoryId) {
                params.append('categoryId', categoryId)
            }

            const response = await apiClient.get<ApiResponse<ProductsResponse>>(
                `/products?${params.toString()}`
            )

            if (response.data.success && response.data.data) {
                const data = response.data.data
                if (data.items) {
                    setProducts(data.items)
                    setPageInfo(data.meta || null)
                } else {
                    setProducts([])
                    setPageInfo(null)
                }
            } else {
                setProducts([])
                setPageInfo(null)
            }
        } catch (err) {
            console.error('Failed to fetch products:', err)
            setError('Could not connect to server. Is the backend running?')
            setProducts([])
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async (id: number) => {
        if (!confirm('Are you sure you want to delete this product?')) return

        try {
            await apiClient.delete(`/admin/products/${id}`)
            fetchProducts()
        } catch (error) {
            console.error('Failed to delete product:', error)
        }
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
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

    return (
        <motion.div
            className="products-page"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
        >
            <header className="page-header">
                <div className="page-header-row">
                    <div>
                        <h1 className="page-title">
                            <span className="page-title-icon">
                                <Icons.Products />
                            </span>
                            Products
                        </h1>
                        <p className="page-subtitle">
                            {pageInfo?.totalElements || 0} items in your catalog
                        </p>
                    </div>
                    {isAdmin && (
                        <button className="btn btn-primary">
                            <Icons.Plus /> Add Product
                        </button>
                    )}
                </div>
            </header>

            {/* Search */}
            <div className="products-toolbar">
                <div className="search-input-wrapper">
                    <Icons.Search />
                    <input
                        type="text"
                        className="input search-input"
                        placeholder="Search products..."
                        value={search}
                        onChange={(e) => {
                            setSearch(e.target.value)
                            setCurrentPage(0)
                        }}
                    />
                </div>
            </div>

            {/* Error State */}
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
                    <button className="btn btn-secondary mt-4" onClick={fetchProducts}>
                        <Icons.Refresh /> Try Again
                    </button>
                </motion.div>
            ) : loading ? (
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Category</th>
                                <th>Price</th>
                                {isAdmin && <th>Actions</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {[1, 2, 3, 4, 5].map((i) => (
                                <tr key={i}>
                                    <td>
                                        <div className="skeleton skeleton-text" style={{ width: '180px' }} />
                                        <div className="skeleton skeleton-text mt-2" style={{ width: '120px' }} />
                                    </td>
                                    <td><div className="skeleton skeleton-text" style={{ width: '80px' }} /></td>
                                    <td><div className="skeleton skeleton-text" style={{ width: '70px' }} /></td>
                                    {isAdmin && <td><div className="skeleton skeleton-text" style={{ width: '100px' }} /></td>}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : products.length > 0 ? (
                <>
                    <div className="table-container">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Category</th>
                                    <th>Price</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product, index) => (
                                    <motion.tr
                                        key={product.id}
                                        initial={{ opacity: 0, y: 10 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: index * 0.03 }}
                                    >
                                        <td>
                                            <div className="product-info">
                                                <div className="product-icon">
                                                    <Icons.Products />
                                                </div>
                                                <div>
                                                    <div className="product-name">{product.name}</div>
                                                    <div className="product-desc">
                                                        {product.description?.substring(0, 60)}...
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span className="badge">{product.categoryName || 'Uncategorized'}</span>
                                        </td>
                                        <td>
                                            <span className="price-tag">{formatCurrency(product.price)}</span>
                                        </td>
                                        <td>
                                            <div className="table-actions">
                                                <button
                                                    className="btn btn-sm btn-secondary"
                                                    onClick={() => handleAddToCart(product)}
                                                    disabled={addingToCart === product.id}
                                                    style={{ display: 'flex', alignItems: 'center', gap: '4px' }}
                                                >
                                                    {addingToCart === product.id ? (
                                                        'Adding...'
                                                    ) : (
                                                        <><Icons.Cart /> Add</>)}
                                                </button>
                                                {isAdmin && (
                                                    <>
                                                        <button className="btn-icon hover-wobble">
                                                            <Icons.Edit />
                                                        </button>
                                                        <button
                                                            className="btn-icon text-error hover-wobble"
                                                            onClick={() => handleDelete(product.id)}
                                                        >
                                                            <Icons.Trash />
                                                        </button>
                                                    </>
                                                )}
                                            </div>
                                        </td>
                                    </motion.tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Pagination */}
                    {pageInfo && pageInfo.totalPages > 1 && (
                        <div className="pagination">
                            <button
                                className="btn btn-ghost"
                                disabled={currentPage === 0}
                                onClick={() => setCurrentPage(currentPage - 1)}
                            >
                                Previous
                            </button>
                            <span className="pagination-info">
                                Page {currentPage + 1} of {pageInfo.totalPages}
                            </span>
                            <button
                                className="btn btn-ghost"
                                disabled={currentPage >= pageInfo.totalPages - 1}
                                onClick={() => setCurrentPage(currentPage + 1)}
                            >
                                Next
                            </button>
                        </div>
                    )}
                </>
            ) : (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon" style={{ color: 'var(--products-color)' }}>
                        <Icons.Products />
                    </div>
                    <span className="empty-state-title">No products yet</span>
                    <span className="empty-state-message">
                        Add your first product to get started!
                    </span>
                    {isAdmin && (
                        <button className="btn btn-primary mt-4">
                            <Icons.Plus /> Add Product
                        </button>
                    )}
                </motion.div>
            )}
        </motion.div>
    )
}
