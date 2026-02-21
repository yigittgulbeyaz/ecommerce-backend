# E-Commerce Dashboard Frontend

A developer-focused, brutalist dashboard for the e-commerce backend API.

## Design Philosophy

- **No sidebar** — Command palette (⌘+K) + top bar navigation
- **Terminal-inspired** — Monospace typography, dark theme
- **Brutalist aesthetic** — Sharp corners, intentional asymmetry
- **Developer-friendly** — Keyboard shortcuts, technical copy

## Tech Stack

- **Vite** + **React 18** + **TypeScript**
- **Zustand** for state management
- **Axios** for API requests (with JWT interceptors)
- **Framer Motion** for animations

## Quick Start

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

Open [http://localhost:5173](http://localhost:5173)

## Project Structure

```
src/
├── api/           # API client and endpoints
├── components/    # Reusable UI components
│   └── layout/    # TopBar, StatusBar, PageLayout
├── pages/         # Route pages
├── stores/        # Zustand state stores
└── index.css      # Design system (CSS variables)
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `⌘+K` / `Ctrl+K` | Open command palette |
| `Esc` | Close overlays |

## API Integration

The frontend proxies API requests to `http://localhost:8080/api/v1`.
Make sure the backend is running before testing.

## Color Palette

| Name | Hex | Usage |
|------|-----|-------|
| Obsidian | `#0D0D0D` | Background |
| Lime | `#BFFF00` | Accent, actions |
| Error | `#FF6B6B` | Errors, destructive |

---

Built with 🖤 for developers
