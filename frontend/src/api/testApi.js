import api from './axios.js'
export const realizarTest = (pacienteId, data) => api.post(`/api/paciente/test/${pacienteId}`, data)
export const historialPaciente = pacienteId => api.get(`/api/paciente/test/historial/${pacienteId}`)
export const historialMedico = pacienteId => api.get(`/api/medico/test/historial/${pacienteId}`)