import axios from 'axios'
import { useAuthStore } from '../stores/authStore.js'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080', // url de conexion al railway al awake-celebration
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      auth.cerrarSesion()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api