import { useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Icons } from '../components/Icons'
import './Login.css'

export function ForgotPassword() {
    const [email, setEmail] = useState('')
    const [submitted, setSubmitted] = useState(false)

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        // TODO: Implement actual password reset when backend supports it
        setSubmitted(true)
    }

    return (
        <div className="auth-page">
            <motion.div
                className="auth-container"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5 }}
            >
                {/* Header */}
                <div className="auth-header">
                    <motion.div
                        className="auth-logo"
                        initial={{ scale: 0 }}
                        animate={{ scale: 1 }}
                        transition={{ type: 'spring', stiffness: 260, damping: 20, delay: 0.1 }}
                    >
                        <Icons.ShoppingBag />
                    </motion.div>
                    <h1 className="auth-title">Reset Password</h1>
                    <p className="auth-subtitle">
                        {submitted
                            ? "Check your email for reset instructions"
                            : "Enter your email to receive reset instructions"}
                    </p>
                </div>

                {submitted ? (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        style={{ textAlign: 'center' }}
                    >
                        <div style={{
                            width: '64px',
                            height: '64px',
                            margin: '0 auto var(--space-6)',
                            background: 'rgba(102, 187, 106, 0.1)',
                            borderRadius: 'var(--radius-full)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: 'var(--success)'
                        }}>
                            <Icons.Check />
                        </div>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: 'var(--space-6)' }}>
                            If an account exists with <strong>{email}</strong>, you'll receive password reset instructions.
                        </p>
                        <Link to="/login" className="btn btn-primary">
                            Back to Login
                        </Link>
                    </motion.div>
                ) : (
                    <form onSubmit={handleSubmit} className="auth-form">
                        <div className="input-group">
                            <label className="input-label" htmlFor="email">Email Address</label>
                            <input
                                id="email"
                                type="email"
                                className="input"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                autoFocus
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-full"
                        >
                            Send Reset Instructions
                        </button>
                    </form>
                )}

                {/* Footer */}
                {!submitted && (
                    <div className="auth-footer">
                        <span>Remember your password?</span>
                        <Link to="/login" className="auth-link">Sign in</Link>
                    </div>
                )}
            </motion.div>

            {/* Decorative Elements */}
            <div className="auth-decoration">
                <motion.div
                    className="decoration-circle circle-1"
                    animate={{ y: [0, -20, 0] }}
                    transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut' }}
                />
                <motion.div
                    className="decoration-circle circle-2"
                    animate={{ y: [0, 20, 0] }}
                    transition={{ duration: 7, repeat: Infinity, ease: 'easeInOut' }}
                />
            </div>
        </div>
    )
}
