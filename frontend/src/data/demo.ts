import type { PathItem } from '../types'

export const demoPath: PathItem[] = [
  { id: 'm1', title: 'Java foundations review', minutes: 30, state: 'skipped', reason: 'You scored 92% in Java fundamentals.' },
  { id: 'm2', title: 'Collections and generics', minutes: 45, state: 'complete' },
  { id: 'm3', title: 'Streams and functional patterns', minutes: 50, state: 'current' },
  { id: 'm4', title: 'Spring Boot REST APIs', minutes: 65, state: 'locked' },
  { id: 'm5', title: 'Production deployment', minutes: 40, state: 'locked' },
]

export const demoOrganizationId = '00000000-0000-0000-0000-000000000001'

