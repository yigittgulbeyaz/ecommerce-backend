import { useState, useRef, useEffect } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuthStore } from '../../stores/authStore'
import { useCartStore } from '../../stores/cartStore'
import { Icons } from '../Icons'
import './Header.css'

const navItems = [
    { path: '/', label: 'Dashboard', icon: Icons.Home },
    { path: '/products', label: 'Products', icon: Icons.Products },
    { path: '/orders', label: 'Orders', icon: Icons.Orders },
    { path: '/categories', label: 'Categories', icon: Icons.Categories },
    { path: '/cart', label: 'Cart', icon: Icons.Cart },
]

export function Header() {
    const navigate = useNavigate()
    const location = useLocation()
    const { user, logout } = useAuthStore()
    const itemCount = useCartStore((state) => state.itemCount)
    const fetchCart = useCartStore((state) => state.fetchCart)
    const [navOpen, setNavOpen] = useState(false)
    const [userMenuOpen, setUserMenuOpen] = useState(false)
    const [searchQuery, setSearchQuery] = useState('')
    const navRef = useRef<HTMLDivElement>(null)
    const userRef = useRef<HTMLDivElement>(null)

    // Fetch cart on mount
    useEffect(() => {
        fetchCart()
    }, [fetchCart])

    // Close dropdowns on outside click
    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (navRef.current && !navRef.current.contains(e.target as Node)) {
                setNavOpen(false)
            }
            if (userRef.current && !userRef.current.contains(e.target as Node)) {
                setUserMenuOpen(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    const currentNav = navItems.find(item => item.path === location.pathname) || navItems[0]

    return (
        <header className="header">
            <div className="header-container">
                {/* Logo */}
                <Link to="/" className="header-logo">
                    <div className="logo-icon">
                        <Icons.MagicWand className="wand-icon" />
                    </div>
                    <span className="logo-text">Magic Wand</span>
                </Link>

                {/* Navigation Dropdown */}
                <div className="dropdown" ref={navRef}>
                    <button
                        className={`dropdown-trigger ${navOpen ? 'open' : ''}`}
                        onClick={() => setNavOpen(!navOpen)}
                    >
                        <currentNav.icon />
                        <span>{currentNav.label}</span>
                        <Icons.ChevronDown />
                    </button>

                    <AnimatePresence>
                        {navOpen && (
                            <motion.div
                                className="dropdown-menu"
                                initial={{ opacity: 0, y: -8 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -8 }}
                                transition={{ duration: 0.2 }}
                            >
                                {navItems.map((item) => (
                                    <Link
                                        key={item.path}
                                        to={item.path}
                                        className={`dropdown-item ${location.pathname === item.path ? 'active' : ''}`}
                                        onClick={() => setNavOpen(false)}
                                    >
                                        <item.icon />
                                        <span>{item.label}</span>
                                        {item.path === '/cart' && itemCount > 0 && (
                                            <span className="cart-badge">{itemCount > 99 ? '99+' : itemCount}</span>
                                        )}
                                    </Link>
                                ))}
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>

                {/* Search */}
                <div className="header-search">
                    <div className="search-input-wrapper">
                        <Icons.Search />
                        <input
                            type="text"
                            className="input search-input"
                            placeholder="Search products..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && searchQuery.trim()) {
                                    navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`)
                                    setSearchQuery('')
                                }
                            }}
                        />
                    </div>
                </div>

                {/* User Menu */}
                <div className="user-menu-wrapper" ref={userRef}>
                    <button
                        className={`user-menu-btn ${userMenuOpen ? 'open' : ''}`}
                        onClick={() => setUserMenuOpen(!userMenuOpen)}
                    >
                        <div className="user-avatar-ring">
                            <span className="user-avatar-initial">
                                {user?.name?.charAt(0).toUpperCase() || 'U'}
                            </span>
                        </div>
                        <div className="user-info">
                            <span className="user-display-name">{user?.name || 'User'}</span>
                            <span className="user-role-label">{user?.role === 'ADMIN' ? 'Admin' : 'Member'}</span>
                        </div>
                        <Icons.ChevronDown />
                    </button>

                    <AnimatePresence>
                        {userMenuOpen && (
                            <motion.div
                                className="user-dropdown"
                                initial={{ opacity: 0, y: -8, scale: 0.96 }}
                                animate={{ opacity: 1, y: 0, scale: 1 }}
                                exit={{ opacity: 0, y: -8, scale: 0.96 }}
                                transition={{ duration: 0.2 }}
                            >
                                <div className="user-dropdown-header">
                                    <div className="user-avatar-ring large">
                                        <span className="user-avatar-initial">{user?.name?.charAt(0).toUpperCase() || 'U'}</span>
                                    </div>
                                    <div>
                                        <p className="dropdown-user-name">{user?.name}</p>
                                        <p className="dropdown-user-email">{user?.email || 'Member'}</p>
                                    </div>
                                </div>
                                <div className="dropdown-divider" />
                                <Link to="/profile" className="dropdown-item" onClick={() => setUserMenuOpen(false)}>
                                    <Icons.User />
                                    <span>Profile</span>
                                </Link>
                                <Link to="/settings" className="dropdown-item" onClick={() => setUserMenuOpen(false)}>
                                    <Icons.Settings />
                                    <span>Settings</span>
                                </Link>
                                <div className="dropdown-divider" />
                                <button className="dropdown-item text-error" onClick={handleLogout}>
                                    <Icons.Logout />
                                    <span>Logout</span>
                                </button>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </div>
        </header>
    )
}
