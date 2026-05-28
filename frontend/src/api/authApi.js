import api from './axios.js'
export const login = data => api.post('/api/auth/login', data)
export const registrar = data => api.post('/api/auth/registrar', data)