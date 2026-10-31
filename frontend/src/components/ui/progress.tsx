import * as ProgressPrimitive from '@radix-ui/react-progress'

export function Progress({ value }: { value: number }) {
  return <ProgressPrimitive.Root value={value} className="h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800"><ProgressPrimitive.Indicator className="h-full bg-indigo-600 transition-transform" style={{ transform: `translateX(-${100 - value}%)` }} /></ProgressPrimitive.Root>
}
