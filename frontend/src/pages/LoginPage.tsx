import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { GraduationCap, ArrowRight } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { useAuth } from '../auth/AuthContext'
import axios from 'axios'

export function LoginPage() {
  const { user, login } = useAuth(); const navigate = useNavigate()
  const [email, setEmail] = useState(''); const [password, setPassword] = useState(''); const [error, setError] = useState(''); const [pending, setPending] = useState(false)
  if (user) return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/'} replace />
  const submit = async (event: React.FormEvent) => { event.preventDefault(); setPending(true); setError(''); try { await login(email.trim(), password); navigate('/') } catch (reason) { const apiMessage = axios.isAxiosError(reason) ? reason.response?.data?.message : undefined; setError(apiMessage || 'Sign-in failed. Use the same email and password used during account creation.') } finally { setPending(false) } }
  return <AuthLayout title="Welcome back" subtitle="Pick up exactly where your learning path left off."><form onSubmit={submit} className="space-y-4"><Field label="Work email"><Input required type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="you@company.com" /></Field><Field label="Password"><Input required type="password" minLength={8} value={password} onChange={e => setPassword(e.target.value)} placeholder="••••••••" /></Field>{error && <p className="rounded-lg bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}<Button disabled={pending} className="w-full gap-2">{pending ? 'Signing in…' : <><ArrowRight size={17} /> Sign in</>}</Button><p className="text-center text-sm text-slate-500">New to RouteEd? <Link className="font-semibold text-indigo-600 hover:text-indigo-500" to="/register">Create an account</Link></p></form></AuthLayout>
}
function Field({ label, children }: { label: string; children: React.ReactNode }) { return <label className="block space-y-1.5"><span className="text-sm font-medium text-slate-700">{label}</span>{children}</label> }
export function AuthLayout({ title, subtitle, children }: { title: string; subtitle: string; children: React.ReactNode }) { return <main className="grid min-h-screen place-items-center bg-[radial-gradient(circle_at_top_left,_#e0e7ff,_#f8fafc_45%,_#fff)] p-5"><section className="w-full max-w-md rounded-3xl border border-white/70 bg-white/80 p-8 shadow-2xl shadow-indigo-100/70 backdrop-blur"><div className="mb-8"><div className="mb-5 flex size-11 items-center justify-center rounded-2xl bg-indigo-600 text-white shadow-lg shadow-indigo-200"><GraduationCap /></div><h1 className="text-2xl font-bold tracking-tight text-slate-900">{title}</h1><p className="mt-2 text-sm leading-6 text-slate-500">{subtitle}</p></div>{children}</section></main> }
