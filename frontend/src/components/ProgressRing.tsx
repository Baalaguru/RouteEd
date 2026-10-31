type ProgressRingProps = { value: number }

export function ProgressRing({ value }: ProgressRingProps) {
  const radius = 42
  const circumference = 2 * Math.PI * radius
  const offset = circumference - (value / 100) * circumference
  return (
    <div className="relative grid size-28 place-items-center" aria-label={`${value}% complete`}>
      <svg viewBox="0 0 100 100" className="size-28 -rotate-90" aria-hidden="true">
        <circle cx="50" cy="50" r={radius} fill="none" stroke="currentColor" strokeWidth="8" className="text-slate-200" />
        <circle cx="50" cy="50" r={radius} fill="none" stroke="currentColor" strokeWidth="8" strokeLinecap="round"
          strokeDasharray={circumference} strokeDashoffset={offset} className="text-indigo-600" />
      </svg>
      <span className="absolute text-lg font-bold">{value}%</span>
    </div>
  )
}

