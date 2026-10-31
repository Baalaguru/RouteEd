import { Navigate, Outlet } from 'react-router-dom'
import { useAuth, type UserRole } from './AuthContext'

export function ProtectedRoute({ role }: { role?: UserRole }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="grid min-h-screen place-items-center text-slate-500">Loading RouteEd…</div>
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/'} replace />
  return <Outlet />
}

export const AdminRoute = () => <ProtectedRoute role="ADMIN" />
export const LearnerRoute = () => <ProtectedRoute role="LEARNER" />
