export type PathItem = {
  id: string
  title: string
  minutes: number
  state: 'complete' | 'current' | 'locked' | 'skipped'
  reason?: string
}

export type Course = {
  id: string
  title: string
  description: string | null
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  estimatedDurationDays: number
  modules: Array<{
    id: string
    title: string
    description: string | null
    contentUrl: string | null
    order: number
    estimatedMinutes: number
    skillTag?: string | null
  }>
}

