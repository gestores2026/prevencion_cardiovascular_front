import api from './axios.js'

export const enviarRecomendacion = (medicoId, data) => api.post(`/api/medico/recomendaciones/${medicoId}`, data)
export const enviadasMedico = medicoId => api.get(`/api/medico/recomendaciones/enviadas/${medicoId}`)
export const recibidasPaciente = pacienteId => api.get(`/api/paciente/recomendaciones/${pacienteId}`)