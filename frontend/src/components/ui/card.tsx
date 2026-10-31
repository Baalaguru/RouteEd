import type { HTMLAttributes } from 'react'
import { cn } from '../../lib/utils'

export function Card({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('rounded-2xl border border-white/20 bg-white/80 shadow-sm backdrop-blur dark:bg-slate-900/80', className)} {...props} />
}
