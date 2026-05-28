import api from './axios.js'
export const datosPaciente = pacienteId => api.get(`/api/medico/reportes/paciente/${pacienteId}`)
export const datosGenerales = () => api.get('/api/medico/reportes/general')