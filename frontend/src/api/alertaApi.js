import api from './axios.js'
export const listarAlertas = () => api.get('/api/medico/alertas')
export const contarAlertas = () => api.get('/api/medico/alertas/count')
export const atenderAlerta = id => api.put(`/api/medico/alertas/${id}/atender`)
export const alertasPaciente = pacienteId => api.get(`/api/paciente/alertas/${pacienteId}`)
