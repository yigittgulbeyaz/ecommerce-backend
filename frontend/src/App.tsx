import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'
import { PageLayout } from './components/layout/PageLayout'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { ForgotPassword } from './pages/ForgotPassword'
import { LandingPage } from './pages/LandingPage'
import { Dashboard } from './pages/Dashboard'
import { AdminDashboard } from './pages/AdminDashboard'
import { AdminUsers } from './pages/AdminUsers'
import { Products } from './pages/Products'
import { ProductDetail } from './pages/ProductDetail'
import { Orders } from './pages/Orders'
import { Categories } from './pages/Categories'
import { Cart } from './pages/Cart'
import { Checkout } from './pages/Checkout'
import { Profile } from './pages/Profile'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />
    }

    return <>{children}</>
}

function AdminRoute({ children }: { children: React.ReactNode }) {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
    const user = useAuthStore((state) => state.user)

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />
    }

    if (user?.role !== 'ADMIN') {
        return <Navigate to="/dashboard" replace />
    }

    return <>{children}</>
}

function App() {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)

    return (
        <Routes>
            {/* Public routes - no auth required */}
            <Route path="/" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LandingPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />

            {/* Public browsing - accessible without login */}
            <Route path="/browse" element={<LandingPage />} />
            <Route
                path="/products"
                element={
                    <PageLayout>
                        <Products />
                    </PageLayout>
                }
            />
            <Route
                path="/products/:id"
                element={
                    <PageLayout>
                        <ProductDetail />
                    </PageLayout>
                }
            />
            <Route
                path="/categories"
                element={
                    <PageLayout>
                        <Categories />
                    </PageLayout>
                }
            />
            <Route
                path="/cart"
                element={
                    <PageLayout>
                        <Cart />
                    </PageLayout>
                }
            />

            {/* Protected routes - auth required */}
            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <PageLayout>
                            <DashboardRouter />
                        </PageLayout>
                    </ProtectedRoute>
                }
            />

            {/* Admin-only routes */}
            <Route
                path="/admin/dashboard"
                element={
                    <AdminRoute>
                        <PageLayout>
                            <AdminDashboard />
                        </PageLayout>
                    </AdminRoute>
                }
            />
            <Route
                path="/admin/users"
                element={
                    <AdminRoute>
                        <PageLayout>
                            <AdminUsers />
                        </PageLayout>
                    </AdminRoute>
                }
            />
            <Route
                path="/orders"
                element={
                    <ProtectedRoute>
                        <PageLayout>
                            <Orders />
                        </PageLayout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/checkout"
                element={
                    <ProtectedRoute>
                        <PageLayout>
                            <Checkout />
                        </PageLayout>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/profile"
                element={
                    <ProtectedRoute>
                        <PageLayout>
                            <Profile />
                        </PageLayout>
                    </ProtectedRoute>
                }
            />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

// Route admin to AdminDashboard, users to Dashboard
function DashboardRouter() {
    const user = useAuthStore((state) => state.user)

    if (user?.role === 'ADMIN') {
        return <AdminDashboard />
    }

    return <Dashboard />
}

export default App
