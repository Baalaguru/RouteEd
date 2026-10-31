import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import { api } from '../lib/api'

export type UserRole = 'ADMIN' | 'LEARNER'
export type AuthUser = { id: string; organizationId: string; name: string; email: string; role: UserRole; createdAt: string }
type AuthResponse = { accessToken: string; refreshToken: string; tokenType: string; user: AuthUser }
type AuthContextValue = { user: AuthUser | null; loading: boolean; login: (email: string, password: string) => Promise<void>; register: (data: { name: string; email: string; password: string }) => Promise<void>; logout: () => void }

const AuthContext = createContext<AuthContextValue | undefined>(undefined)
const TOKEN_KEY = 'routeed.accessToken'
const REFRESH_TOKEN_KEY = 'routeed.refreshToken'
const USER_KEY = 'routeed.user'

function readStoredUser(): AuthUser | null {
  try { return JSON.parse(localStorage.getItem(USER_KEY) ?? 'null') as AuthUser | null } catch { return null }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(readStoredUser)
  const [loading] = useState(false)
  const persist = (response: AuthResponse) => { localStorage.setItem(TOKEN_KEY, response.accessToken); localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken); localStorage.setItem(USER_KEY, JSON.stringify(response.user)); setUser(response.user) }
  const login = async (email: string, password: string) => { const { data } = await api.post<AuthResponse>('/auth/login', { email, password }); persist(data) }
  const register = async (data: { name: string; email: string; password: string }) => { const response = await api.post<AuthResponse>('/auth/register', data); persist(response.data) }
  const logout = () => { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(REFRESH_TOKEN_KEY); localStorage.removeItem(USER_KEY); setUser(null) }
  const value = useMemo(() => ({ user, loading, login, register, logout }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() { const context = useContext(AuthContext); if (!context) throw new Error('useAuth must be used within AuthProvider'); return context }
