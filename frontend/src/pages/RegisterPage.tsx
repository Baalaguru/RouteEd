import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { AuthLayout } from './LoginPage'
import { useAuth } from '../auth/AuthContext'

export function RegisterPage() {
  const { user, register } = useAuth(); const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '' }); const [error, setError] = useState(''); const [pending, setPending] = useState(false)
  if (user) return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/'} replace />
  const update = (key: keyof typeof form, value: string) => setForm(current => ({ ...current, [key]: value }))
  const submit = async (event: React.FormEvent) => { event.preventDefault(); setPending(true); setError(''); try { await register(form); navigate('/') } catch { setError('We could not create your account. Try a different email.') } finally { setPending(false) } }
  return <AuthLayout title="Build a better route" subtitle="Create your learner account and begin with a personalized diagnostic."><form onSubmit={submit} className="space-y-4"><label className="block space-y-1.5"><span className="text-sm font-medium text-slate-700">Full name</span><Input required value={form.name} onChange={e => update('name', e.target.value)} placeholder="Asha Patel" /></label><label className="block space-y-1.5"><span className="text-sm font-medium text-slate-700">Work email</span><Input required type="email" value={form.email} onChange={e => update('email', e.target.value)} placeholder="asha@company.com" /></label><label className="block space-y-1.5"><span className="text-sm font-medium text-slate-700">Password</span><Input required type="password" minLength={8} value={form.password} onChange={e => update('password', e.target.value)} placeholder="At least 8 characters" /></label><p className="rounded-lg bg-indigo-50 p-3 text-sm text-indigo-800">Administrator accounts are created by an existing administrator.</p>{error && <p className="rounded-lg bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}<Button disabled={pending} className="w-full gap-2">{pending ? 'Creating account…' : <><ArrowRight size={17} /> Create learner account</>}</Button><p className="text-center text-sm text-slate-500">Already have an account? <Link className="font-semibold text-indigo-600 hover:text-indigo-500" to="/login">Sign in</Link></p></form></AuthLayout>
}
