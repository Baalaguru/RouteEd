import axios from 'axios'
import type { Course } from '../types'

const apiBaseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

// Assessment submission waits for the local Ollama model to generate a personalized route.
// Smaller local models may legitimately take longer than a default web-request timeout.
export const api = axios.create({ baseURL: apiBaseUrl, timeout: 90_000 })
let refreshInFlight: Promise<string | null> | null = null

async function refreshAccessToken() {
  if (!refreshInFlight) {
    refreshInFlight = axios.post<{ accessToken: string; refreshToken: string; user: unknown }>(`${apiBaseUrl}/auth/refresh`, { refreshToken: localStorage.getItem('routeed.refreshToken') }, { timeout: 15_000 })
      .then(({ data }) => { localStorage.setItem('routeed.accessToken', data.accessToken); localStorage.setItem('routeed.refreshToken', data.refreshToken); if (data.user) localStorage.setItem('routeed.user', JSON.stringify(data.user)); return data.accessToken })
      .catch(() => null)
      .finally(() => { refreshInFlight = null })
  }
  return refreshInFlight
}

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('routeed.accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config as typeof error.config & { _routeedRetried?: boolean }
    if (error.response?.status === 401 && !original?._routeedRetried && localStorage.getItem('routeed.refreshToken')) {
      original._routeedRetried = true
      const token = await refreshAccessToken()
      if (token) { original.headers.Authorization = `Bearer ${token}`; return api.request(original) }
    }
    if (error.response?.status === 401) { localStorage.removeItem('routeed.accessToken'); localStorage.removeItem('routeed.refreshToken'); localStorage.removeItem('routeed.user') }
    return Promise.reject(error)
  },
)

export async function fetchCourses(): Promise<Course[]> {
  const response = await api.get<Course[]>('/admin/courses')
  return response.data
}

export async function createCourse(body: {
  title: string
  description?: string
  estimatedDurationDays?: number
}): Promise<Course> {
  const response = await api.post<Course>('/admin/courses', body)
  return response.data
}

export async function addModule(courseId: string, body: { title: string; description?: string; contentUrl?: string; estimatedMinutes?: number; defaultOrderIndex?: number; skillTag?: string }) {
  const response = await api.post(`/admin/courses/${courseId}/modules`, body)
  return response.data
}

export async function createAssessment(courseId: string, title: string) {
  const response = await api.post<{ id: string }>(`/admin/courses/${courseId}/assessments`, { title })
  return response.data
}
export async function fetchAssessment(courseId: string) {
  return (await api.get<{ id: string; courseId: string; title: string; questions: Array<{ id: string; questionText: string; options: string[]; correctOption: string; skillTag: string }> }>(`/admin/courses/${courseId}/assessments`)).data
}

export async function addQuestion(assessmentId: string, body: { questionText: string; options: string[]; correctOption: string; skillTag: string }) {
  const response = await api.post(`/admin/assessments/${assessmentId}/questions`, body)
  return response.data
}

export type LearnerAssessment = { id: string; courseId: string; title: string; questions: Array<{ id: string; questionText: string; options: string[]; skillTag: string }> }
export async function fetchLearnerAssessment(courseId: string) { return (await api.get<LearnerAssessment>(`/learner/courses/${courseId}/assessment`)).data }
export async function submitLearnerAssessment(assessmentId: string, answers: Record<string, string>) { return (await api.post<{ overallScore: number; skillScores: Record<string, number> }>(`/learner/assessments/${assessmentId}/submit`, { answers })).data }
export async function fetchLearnerCourses() { return (await api.get<Course[]>('/learner/courses')).data }
export async function fetchPath(courseId: string) { return (await api.get<{ moduleOrder: string[]; skippedModuleIds: string[]; aiRationale: Record<string, string> }>(`/learner/courses/${courseId}/path`)).data }
export async function fetchProgress(courseId: string) { return (await api.get<{ percentage: number; completedModuleIds: string[]; currentModuleId: string | null; skippedModuleIds: string[] }>(`/learner/courses/${courseId}/progress`)).data }
export async function completeModule(moduleId: string) { await api.post(`/learner/modules/${moduleId}/complete`) }
export async function fetchCourseAnalytics(courseId: string) { return (await api.get<{ totalLearners: number; completionRate: number; dropOffModules: Record<string, number> }>(`/admin/courses/${courseId}/analytics`)).data }
export async function fetchHealthScores(courseId: string) { return (await api.get<Array<{ learnerId: string; name: string; email: string; score: number; status: string }>>(`/admin/courses/${courseId}/health-scores`)).data }

