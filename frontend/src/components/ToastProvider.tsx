import { createContext, useContext, useState, type ReactNode } from 'react'
const ToastContext=createContext<(message:string)=>void>(()=>{})
export function ToastProvider({children}:{children:ReactNode}){const [message,setMessage]=useState('');const show=(value:string)=>{setMessage(value);window.setTimeout(()=>setMessage(''),3000)};return <ToastContext.Provider value={show}>{children}{message&&<div className="fixed bottom-5 right-5 z-[100] rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white shadow-xl">{message}</div>}</ToastContext.Provider>}
export const useToast=()=>useContext(ToastContext)
