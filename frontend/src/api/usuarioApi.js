import api from './axios.js'

export const listarPacientes = () => api.get('/api/medico/pacientes')
export const listarMisPacientes = medicoId => api.get(`/api/medico/mis-pacientes/${medicoId}`)
export const buscarPacientes = busqueda => api.get(`/api/medico/pacientes/buscar?busqueda=${busqueda}`)
export const buscarMisPacientes = (medicoId, busqueda) => api.get(`/api/medico/mis-pacientes/${medicoId}/buscar?busqueda=${busqueda}`)
export const obtenerPaciente = id => api.get(`/api/medico/pacientes/${id}`)
export const asignarMedico = (pacienteId, medicoId) => api.put(`/api/medico/asignar-paciente/${pacienteId}/${medicoId}`)
export const actualizarPerfil = (id, data) => api.put(`/api/paciente/perfil/${id}`, data)
export const verPerfil = id => api.get(`/api/paciente/perfil/${id}`)