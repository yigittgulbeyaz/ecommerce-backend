import { useState } from 'react'
import { motion } from 'framer-motion'
import { useAuthStore } from '../stores/authStore'
import { apiClient } from '../api/client'
import { Icons } from '../components/Icons'

export function Profile() {
    const { user, logout } = useAuthStore()
    const [name, setName] = useState(user?.name || '')
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
    const [loading, setLoading] = useState(false)

    const handleUpdateProfile = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)
        setMessage(null)

        try {
            await apiClient.patch('/users/me', { name })
            setMessage({ type: 'success', text: 'Profile updated successfully!' })
        } catch {
            setMessage({ type: 'error', text: 'Failed to update profile' })
        } finally {
            setLoading(false)
        }
    }

    const handleChangePassword = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)
        setMessage(null)

        try {
            await apiClient.post('/users/me/change-password', {
                currentPassword,
                newPassword,
            })
            setMessage({ type: 'success', text: 'Password changed successfully!' })
            setCurrentPassword('')
            setNewPassword('')
        } catch {
            setMessage({ type: 'error', text: 'Failed to change password. Check current password.' })
        } finally {
            setLoading(false)
        }
    }

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}
        >
            <header className="page-header">
                <h1 className="page-title">
                    <span className="page-title-icon" style={{ background: 'linear-gradient(135deg, var(--coral) 0%, var(--sunset) 100%)' }}>
                        <Icons.User />
                    </span>
                    Your Profile
                </h1>
                <p className="page-subtitle">Manage your account settings</p>
            </header>

            {message && (
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className={`alert ${message.type === 'error' ? 'alert-error' : 'alert-success'}`}
                >
                    {message.type === 'success' ? <Icons.Check /> : <Icons.AlertCircle />}
                    <span>{message.text}</span>
                </motion.div>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.5fr', gap: 'var(--space-6)' }}>
                {/* Account Info */}
                <motion.div
                    className="card"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                >
                    <div className="card-header">
                        <h3 className="card-title">Account Info</h3>
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                        <div style={{ display: 'flex', justifyContent: 'center' }}>
                            <div style={{
                                width: '80px',
                                height: '80px',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                background: 'linear-gradient(135deg, var(--coral) 0%, var(--sunset) 100%)',
                                borderRadius: 'var(--radius-full)',
                                color: 'var(--white)',
                                fontFamily: 'var(--font-display)',
                                fontSize: 'var(--text-3xl)',
                                fontWeight: 700
                            }}>
                                {user?.name?.charAt(0).toUpperCase() || 'U'}
                            </div>
                        </div>

                        <div>
                            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                                User ID
                            </span>
                            <div style={{ fontFamily: 'var(--font-display)', fontWeight: 600, color: 'var(--crimson)' }}>
                                #{user?.id}
                            </div>
                        </div>

                        <div>
                            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                                Email
                            </span>
                            <div style={{ fontFamily: 'var(--font-display)', fontWeight: 600 }}>
                                {user?.email}
                            </div>
                        </div>

                        <div>
                            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                                Role
                            </span>
                            <div>
                                <span className="badge badge-crimson">{user?.role}</span>
                            </div>
                        </div>
                    </div>
                </motion.div>

                {/* Settings */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                    <motion.form
                        className="card"
                        onSubmit={handleUpdateProfile}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.1 }}
                    >
                        <div className="card-header">
                            <h3 className="card-title">Update Profile</h3>
                        </div>
                        <div className="input-group mb-4">
                            <label className="input-label" htmlFor="name">Display Name</label>
                            <input
                                id="name"
                                type="text"
                                className="input"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="Your name"
                            />
                        </div>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Saving...' : 'Save Changes'}
                        </button>
                    </motion.form>

                    <motion.form
                        className="card"
                        onSubmit={handleChangePassword}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                    >
                        <div className="card-header">
                            <h3 className="card-title">Change Password</h3>
                        </div>
                        <div className="input-group mb-4">
                            <label className="input-label" htmlFor="currentPassword">Current Password</label>
                            <input
                                id="currentPassword"
                                type="password"
                                className="input"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                                placeholder="••••••••"
                                required
                            />
                        </div>
                        <div className="input-group mb-4">
                            <label className="input-label" htmlFor="newPassword">New Password</label>
                            <input
                                id="newPassword"
                                type="password"
                                className="input"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                placeholder="Min 6 characters"
                                required
                                minLength={6}
                            />
                        </div>
                        <button type="submit" className="btn btn-secondary" disabled={loading}>
                            {loading ? 'Updating...' : 'Update Password'}
                        </button>
                    </motion.form>

                    <motion.div
                        className="card"
                        style={{ borderColor: 'var(--error)' }}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.3 }}
                    >
                        <div className="card-header">
                            <h3 className="card-title text-error">Danger Zone</h3>
                        </div>
                        <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', marginBottom: 'var(--space-4)' }}>
                            This will log you out of the current session.
                        </p>
                        <button className="btn btn-secondary text-error" onClick={logout} style={{ borderColor: 'var(--error)', color: 'var(--error)' }}>
                            <Icons.Logout /> Logout
                        </button>
                    </motion.div>
                </div>
            </div>
        </motion.div>
    )
}
