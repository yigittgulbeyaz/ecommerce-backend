import { ReactNode } from 'react'
import { Header } from './Header'
import './PageLayout.css'

interface PageLayoutProps {
    children: ReactNode
}

export function PageLayout({ children }: PageLayoutProps) {
    return (
        <div className="app-layout">
            <Header />
            <main className="main-content">
                {children}
            </main>
        </div>
    )
}
