import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
type Theme='light'|'dark';const ThemeContext=createContext<{theme:Theme;toggle:()=>void}|undefined>(undefined)
export function ThemeProvider({children}:{children:ReactNode}){const [theme,setTheme]=useState<Theme>(()=>(localStorage.getItem('routeed.theme') as Theme)||'light');useEffect(()=>{document.documentElement.classList.toggle('dark',theme==='dark');localStorage.setItem('routeed.theme',theme)},[theme]);return <ThemeContext.Provider value={{theme,toggle:()=>setTheme(value=>value==='light'?'dark':'light')}}>{children}</ThemeContext.Provider>}
export function useTheme(){const context=useContext(ThemeContext);if(!context)throw new Error('useTheme must be used within ThemeProvider');return context}
