import { lazy, Suspense } from 'react'
import { Route, Routes } from 'react-router-dom'
import { AdminRoute, LearnerRoute, ProtectedRoute } from './auth/ProtectedRoute'
import { AppShell } from './components/AppShell'
const AdminDashboard = lazy(() => import('./pages/AdminDashboardLive').then(module => ({ default: module.AdminDashboard })))
const CourseBuilder = lazy(() => import('./pages/CourseBuilderNew').then(module => ({ default: module.CourseBuilder })))
const LearnerDashboardLive = lazy(() => import('./pages/LearnerDashboardLive').then(module => ({ default: module.LearnerDashboardLive })))
const LearnerCourseView = lazy(() => import('./pages/LearnerCourseViewNew').then(module => ({ default: module.LearnerCourseView })))
const LoginPage = lazy(() => import('./pages/LoginPage').then(module => ({ default: module.LoginPage })))
const RegisterPage = lazy(() => import('./pages/RegisterPage').then(module => ({ default: module.RegisterPage })))
const AssessmentWizard = lazy(() => import('./pages/AssessmentWizardNew').then(module => ({ default: module.AssessmentWizard })))

export default function App() { return <Suspense fallback={<div className="grid min-h-screen place-items-center bg-slate-50 text-sm font-medium text-slate-600 dark:bg-slate-950 dark:text-slate-300">Loading RouteEd...</div>}><Routes><Route path="/login" element={<LoginPage />} /><Route path="/register" element={<RegisterPage />} /><Route element={<ProtectedRoute />}><Route element={<AppShell />}><Route element={<LearnerRoute />}><Route index element={<LearnerDashboardLive />} /><Route path="assessment/:courseId" element={<AssessmentWizard />} /><Route path="course/:courseId" element={<LearnerCourseView />} /></Route><Route element={<AdminRoute />}><Route path="admin" element={<AdminDashboard />} /><Route path="courses" element={<CourseBuilder />} /></Route></Route></Route></Routes></Suspense> }

