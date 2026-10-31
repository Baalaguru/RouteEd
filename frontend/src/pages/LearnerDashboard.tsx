import { ArrowRight, CheckCircle2, Clock3, LockKeyhole, Sparkles } from 'lucide-react'
import { ProgressRing } from '../components/ProgressRing'
import { demoPath } from '../data/demo'

export function LearnerDashboard() {
  return (
    <div className="space-y-8">
      <section className="rounded-2xl bg-gradient-to-br from-indigo-700 to-violet-700 p-7 text-white shadow-sm">
        <p className="mb-2 text-sm font-medium text-indigo-100">Welcome back, Aisha</p>
        <div className="flex flex-col justify-between gap-6 sm:flex-row sm:items-end">
          <div><h1 className="text-3xl font-bold tracking-tight">Your path is built around what you know.</h1><p className="mt-2 max-w-2xl text-indigo-100">We skipped the concepts you already mastered and brought your next challenge forward.</p></div>
          <div className="flex items-center gap-3 rounded-xl bg-white/10 px-4 py-3"><Sparkles size={20} /><span className="text-sm font-medium">Personalized today</span></div>
        </div>
      </section>

      <section className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div><p className="text-sm font-medium text-indigo-600">Java backend foundations</p><h2 className="mt-1 text-2xl font-bold">Continue your learning route</h2><p className="mt-2 text-sm text-slate-500">2 of 4 required modules completed</p></div>
            <ProgressRing value={50} />
          </div>
          <div className="mt-7 space-y-3">
            {demoPath.map((item, index) => <div key={item.id} className={`flex gap-4 rounded-xl border p-4 ${item.state === 'current' ? 'border-indigo-200 bg-indigo-50' : 'border-slate-100'}`}>
              <div className="mt-0.5 text-slate-400">{item.state === 'complete' ? <CheckCircle2 className="text-emerald-500" /> : item.state === 'locked' ? <LockKeyhole size={20} /> : <span className="grid size-5 place-items-center rounded-full border text-xs">{index + 1}</span>}</div>
              <div className="min-w-0 flex-1"><div className="flex flex-wrap items-center gap-2"><h3 className={`font-semibold ${item.state === 'skipped' ? 'text-slate-500 line-through' : ''}`}>{item.title}</h3>{item.state === 'current' && <span className="rounded-full bg-indigo-600 px-2 py-0.5 text-xs font-semibold text-white">Up next</span>}</div>
                <p className="mt-1 flex items-center gap-1 text-sm text-slate-500"><Clock3 size={14} /> {item.minutes} min{item.reason && ` · ${item.reason}`}</p></div>
              {item.state === 'current' && <button className="self-center rounded-lg bg-indigo-600 px-3 py-2 text-sm font-semibold text-white hover:bg-indigo-700">Start <ArrowRight className="ml-1 inline" size={15} /></button>}
            </div>)}
          </div>
        </div>
        <aside className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"><p className="text-sm font-medium text-slate-500">Learner health</p><p className="mt-2 text-4xl font-bold text-emerald-600">82</p><p className="mt-1 text-sm text-slate-600">Healthy - you are ahead of your planned pace.</p><hr className="my-5 border-slate-100" /><p className="text-sm font-medium">This week</p><p className="mt-2 text-sm text-slate-500">Complete Streams and functional patterns to stay on track.</p></aside>
      </section>
    </div>
  )
}

