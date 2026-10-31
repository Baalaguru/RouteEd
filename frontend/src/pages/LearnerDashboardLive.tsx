import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchLearnerCourses } from '../lib/api'
import type { Course } from '../types'
import { Skeleton } from '../components/ui/skeleton'

export function LearnerDashboardLive() {
 const [courses,setCourses]=useState<Course[]>([]); const [error,setError]=useState(''); const [loading,setLoading]=useState(true)
 useEffect(()=>{fetchLearnerCourses().then(setCourses).catch(()=>setError('Your courses could not be loaded yet. Ask your administrator to create one.')).finally(()=>setLoading(false))},[])
 return <div className="space-y-7"><section className="rounded-3xl bg-gradient-to-br from-indigo-700 to-violet-700 p-8 text-white"><p className="text-sm font-medium text-indigo-100">Your adaptive workspace</p><h1 className="mt-2 text-3xl font-bold">Learn the route that fits you.</h1><p className="mt-2 max-w-xl text-indigo-100">Start with a quick diagnostic. RouteEd then orders the right lessons for your current skill level.</p></section>{error&&<p className="rounded-xl bg-amber-50 p-4 text-sm text-amber-800">{error}</p>}<section className="grid gap-4 md:grid-cols-2">{loading&&[1,2].map(item=><Skeleton key={item} className="h-52" />)}{courses.map(course=><article key={course.id} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:shadow-lg dark:border-slate-800 dark:bg-slate-900"><p className="text-sm font-semibold text-indigo-600">{course.status}</p><h2 className="mt-2 text-xl font-bold">{course.title}</h2><p className="mt-2 text-sm text-slate-500">{course.description}</p><p className="mt-3 text-sm text-slate-500">{course.modules.length} modules · {course.estimatedDurationDays} days</p><div className="mt-5 flex flex-wrap gap-3"><Link to={`/assessment/${course.id}`} className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700">Take assessment</Link><Link to={`/course/${course.id}`} className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-200">Open route</Link></div></article>)}</section></div>
}
