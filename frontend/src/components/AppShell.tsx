import { ArrowRight, BarChart3, BookOpen, GraduationCap, LayoutDashboard, Moon, Sun } from 'lucide-react'
import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { useTheme } from '../theme/ThemeProvider'

const navigation = [
  { to: '/', label: 'Learner dashboard', icon: LayoutDashboard, roles: ['LEARNER'] },
  { to: '/admin', label: 'Admin workspace', icon: BarChart3, roles: ['ADMIN'] },
  { to: '/courses', label: 'Course builder', icon: BookOpen, roles: ['ADMIN'] },
]

export function AppShell() {
  const { user, logout } = useAuth()
  const { theme, toggle } = useTheme()
  const initial = user?.name.slice(0, 1).toUpperCase() ?? 'R'
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-slate-950 dark:text-slate-100">
      <header className="border-b border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <NavLink to="/" className="flex items-center gap-2 text-xl font-bold tracking-tight text-indigo-700">
            <GraduationCap aria-hidden="true" /> RouteEd
          </NavLink>
          <div className="flex items-center gap-3 text-sm">
            <span className="hidden text-slate-500 sm:inline">{user?.name}</span>
            <button onClick={toggle} className="rounded-lg p-2 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800" aria-label="Toggle colour theme">{theme === 'light' ? <Moon size={18} /> : <Sun size={18} />}</button>
            <button onClick={logout} className="rounded-lg p-2 text-slate-500 hover:bg-slate-100" aria-label="Sign out" title="Sign out">
              <ArrowRight size={18} />
            </button>
            <div className="grid size-9 place-items-center rounded-full bg-indigo-100 font-semibold text-indigo-700">{initial}</div>
          </div>
        </div>
      </header>
      <div className="mx-auto flex max-w-7xl gap-8 px-6 py-8">
        <nav className="hidden w-52 shrink-0 space-y-1 md:block" aria-label="Primary navigation">
          {navigation.filter(item => item.roles.includes(user?.role ?? 'LEARNER')).map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} end={to === '/'}
              className={({ isActive }) => `flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium ${isActive ? 'bg-indigo-50 text-indigo-700' : 'text-slate-600 hover:bg-slate-100'}`}>
              <Icon size={18} aria-hidden="true" /> {label}
            </NavLink>
          ))}
        </nav>
        <main className="min-w-0 flex-1"><Outlet /></main>
      </div>
    </div>
  )
}

