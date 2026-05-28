import api from './axios.js'

export const obtenerEstadisticas = () => api.get('/api/medico/estadisticas')
export const obtenerEstadisticasMedico = medicoId => api.get(`/api/medico/estadisticas/${medicoId}`)