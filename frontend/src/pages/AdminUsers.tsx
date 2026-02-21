import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { adminUsersApi, adminCartsApi, User, Cart } from '../api/admin'
import { Icons } from '../components/Icons'
import './AdminUsers.css'

export function AdminUsers() {
    const [users, setUsers] = useState<User[]>([])
    const [selectedUser, setSelectedUser] = useState<User | null>(null)
    const [userCart, setUserCart] = useState<Cart | null>(null)
    const [loading, setLoading] = useState(true)
    const [actionLoading, setActionLoading] = useState<number | null>(null)
    const [showCartModal, setShowCartModal] = useState(false)

    useEffect(() => {
        fetchUsers()
    }, [])

    const fetchUsers = async () => {
        try {
            const response = await adminUsersApi.getAll()
            if (response.success) {
                setUsers(response.data)
            }
        } catch (error) {
            console.error('Failed to fetch users:', error)
        } finally {
            setLoading(false)
        }
    }

    const handleRoleChange = async (userId: number, newRole: 'USER' | 'ADMIN') => {
        setActionLoading(userId)
        try {
            const response = await adminUsersApi.updateRole(userId, newRole)
            if (response.success) {
                setUsers(users.map(u => u.id === userId ? response.data : u))
            }
        } catch (error) {
            console.error('Failed to update role:', error)
        } finally {
            setActionLoading(null)
        }
    }

    const handleDeleteUser = async (userId: number) => {
        if (!confirm('Are you sure you want to delete this user?')) return

        setActionLoading(userId)
        try {
            await adminUsersApi.delete(userId)
            setUsers(users.filter(u => u.id !== userId))
        } catch (error) {
            console.error('Failed to delete user:', error)
        } finally {
            setActionLoading(null)
        }
    }

    const handleViewCart = async (user: User) => {
        setSelectedUser(user)
        setShowCartModal(true)
        try {
            const response = await adminCartsApi.getByUserId(user.id)
            if (response.success) {
                setUserCart(response.data)
            }
        } catch (error) {
            console.error('Failed to fetch cart:', error)
            setUserCart(null)
        }
    }

    const handleClearCart = async (userId: number) => {
        try {
            await adminCartsApi.clearByUserId(userId)
            setUserCart(prev => prev ? { ...prev, items: [], totalAmount: 0 } : null)
        } catch (error) {
            console.error('Failed to clear cart:', error)
        }
    }

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        })
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount)
    }

    return (
        <div className="admin-users-page">
            <header className="admin-page-header">
                <div className="header-content">
                    <h1 className="admin-page-title">
                        <Icons.User />
                        User Management
                    </h1>
                    <p className="admin-page-subtitle">
                        Manage user accounts and permissions
                    </p>
                </div>
                <div className="header-stats">
                    <div className="stat-chip">
                        <span className="stat-value">{users.length}</span>
                        <span className="stat-label">Total Users</span>
                    </div>
                    <div className="stat-chip">
                        <span className="stat-value">{users.filter(u => u.role === 'ADMIN').length}</span>
                        <span className="stat-label">Admins</span>
                    </div>
                </div>
            </header>

            <div className="users-table-container">
                {loading ? (
                    <div className="loading-state">
                        <div className="loading-spinner" />
                        <p>Loading users...</p>
                    </div>
                ) : (
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>User</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Joined</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <AnimatePresence>
                                {users.map((user, index) => (
                                    <motion.tr
                                        key={user.id}
                                        initial={{ opacity: 0, y: 10 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        exit={{ opacity: 0, x: -20 }}
                                        transition={{ delay: index * 0.05 }}
                                    >
                                        <td>
                                            <div className="user-cell">
                                                <div className={`user-avatar ${user.role.toLowerCase()}`}>
                                                    {user.name.charAt(0).toUpperCase()}
                                                </div>
                                                <span className="user-name">{user.name}</span>
                                            </div>
                                        </td>
                                        <td className="email-cell">{user.email}</td>
                                        <td>
                                            <select
                                                className={`role-select ${user.role.toLowerCase()}`}
                                                value={user.role}
                                                onChange={(e) => handleRoleChange(user.id, e.target.value as 'USER' | 'ADMIN')}
                                                disabled={actionLoading === user.id}
                                            >
                                                <option value="USER">User</option>
                                                <option value="ADMIN">Admin</option>
                                            </select>
                                        </td>
                                        <td className="date-cell">{formatDate(user.createdAt)}</td>
                                        <td>
                                            <div className="action-buttons">
                                                <button
                                                    className="btn-icon btn-view"
                                                    onClick={() => handleViewCart(user)}
                                                    title="View Cart"
                                                >
                                                    <Icons.Cart />
                                                </button>
                                                <button
                                                    className="btn-icon btn-delete"
                                                    onClick={() => handleDeleteUser(user.id)}
                                                    disabled={actionLoading === user.id}
                                                    title="Delete User"
                                                >
                                                    <Icons.Trash />
                                                </button>
                                            </div>
                                        </td>
                                    </motion.tr>
                                ))}
                            </AnimatePresence>
                        </tbody>
                    </table>
                )}
            </div>

            {/* Cart Modal */}
            <AnimatePresence>
                {showCartModal && selectedUser && (
                    <motion.div
                        className="modal-overlay"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setShowCartModal(false)}
                    >
                        <motion.div
                            className="modal-content cart-modal"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            onClick={(e) => e.stopPropagation()}
                        >
                            <div className="modal-header">
                                <h2>{selectedUser.name}'s Cart</h2>
                                <button className="btn-close" onClick={() => setShowCartModal(false)}>
                                    <Icons.X />
                                </button>
                            </div>
                            <div className="modal-body">
                                {userCart && userCart.items.length > 0 ? (
                                    <>
                                        <div className="cart-items-list">
                                            {userCart.items.map(item => (
                                                <div key={item.id} className="cart-item-row">
                                                    <span className="item-name">{item.productName}</span>
                                                    <span className="item-qty">x{item.quantity}</span>
                                                    <span className="item-price">{formatCurrency(item.subtotal)}</span>
                                                </div>
                                            ))}
                                        </div>
                                        <div className="cart-total">
                                            <span>Total</span>
                                            <span>{formatCurrency(userCart.totalAmount)}</span>
                                        </div>
                                        <button
                                            className="btn btn-danger"
                                            onClick={() => handleClearCart(selectedUser.id)}
                                        >
                                            Clear Cart
                                        </button>
                                    </>
                                ) : (
                                    <div className="empty-cart">
                                        <Icons.Cart />
                                        <p>Cart is empty</p>
                                    </div>
                                )}
                            </div>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    )
}
