import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { apiClient, ApiResponse } from '../api/client'
import { useAuthStore } from '../stores/authStore'
import { Icons } from '../components/Icons'

interface Category {
    id: number
    name: string
}

export function Categories() {
    const user = useAuthStore((state) => state.user)
    const [categories, setCategories] = useState<Category[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const isAdmin = user?.role === 'ADMIN'

    useEffect(() => {
        fetchCategories()
    }, [])

    const fetchCategories = async () => {
        setLoading(true)
        setError(null)
        try {
            const response = await apiClient.get<ApiResponse<Category[]>>('/categories')
            if (response.data.success) {
                setCategories(response.data.data)
            }
        } catch (err) {
            console.error('Failed to fetch categories:', err)
            setError('Could not connect to server. Is the backend running?')
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async (id: number) => {
        if (!confirm('Delete this category?')) return

        try {
            await apiClient.delete(`/admin/categories/${id}`)
            fetchCategories()
        } catch (error) {
            console.error('Failed to delete category:', error)
        }
    }

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
                            <span className="page-title-icon" style={{ background: 'linear-gradient(135deg, var(--categories-color) 0%, #81C784 100%)' }}>
                                <Icons.Categories />
                            </span>
                            Categories
                        </h1>
                        <p className="page-subtitle">
                            {isAdmin
                                ? `${categories.length} categories in your store`
                                : `Explore ${categories.length} categories`}
                        </p>
                    </div>
                    {isAdmin && (
                        <button className="btn btn-primary">
                            <Icons.Plus /> Add Category
                        </button>
                    )}
                </div>
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
                    <button className="btn btn-secondary mt-4" onClick={fetchCategories}>
                        <Icons.Refresh /> Try Again
                    </button>
                </motion.div>
            ) : loading ? (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 'var(--space-4)' }}>
                    {[1, 2, 3, 4, 5, 6].map((i) => (
                        <div key={i} className="card">
                            <div className="skeleton skeleton-text" style={{ width: '60%' }} />
                        </div>
                    ))}
                </div>
            ) : categories.length > 0 ? (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 'var(--space-4)' }}>
                    {categories.map((category, index) => (
                        <motion.div
                            key={category.id}
                            className="card card-accent card-categories"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: index * 0.05 }}
                            whileHover={{ y: -4, boxShadow: 'var(--shadow-lg)' }}
                            style={{ cursor: 'pointer' }}
                        >
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-4)' }}>
                                    <div style={{
                                        width: '48px',
                                        height: '48px',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        background: 'linear-gradient(135deg, var(--categories-color) 0%, #81C784 100%)',
                                        borderRadius: 'var(--radius-md)',
                                        color: 'var(--white)'
                                    }}>
                                        <Icons.Categories />
                                    </div>
                                    <div style={{ fontFamily: 'var(--font-display)', fontWeight: 600, fontSize: 'var(--text-lg)' }}>
                                        {category.name}
                                    </div>
                                </div>
                                {isAdmin && (
                                    <div style={{ display: 'flex', gap: 'var(--space-2)' }}>
                                        <button className="btn-icon hover-wobble">
                                            <Icons.Edit />
                                        </button>
                                        <button
                                            className="btn-icon text-error hover-wobble"
                                            onClick={() => handleDelete(category.id)}
                                        >
                                            <Icons.Trash />
                                        </button>
                                    </div>
                                )}
                            </div>
                        </motion.div>
                    ))}
                </div>
            ) : (
                <motion.div
                    className="card empty-state"
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                >
                    <div className="empty-state-icon" style={{ color: 'var(--categories-color)' }}>
                        <Icons.Categories />
                    </div>
                    <span className="empty-state-title">No categories yet</span>
                    <span className="empty-state-message">
                        Create your first category to organize products
                    </span>
                    {isAdmin && (
                        <button className="btn btn-primary mt-4">
                            <Icons.Plus /> Add Category
                        </button>
                    )}
                </motion.div>
            )}
        </motion.div>
    )
}
