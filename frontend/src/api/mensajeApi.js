import api from './axios.js'

export const enviarMensaje = (emisorId, data) => api.post(`/api/mensajes/enviar/${emisorId}`, data)
export const obtenerConversacion = (usuarioA, usuarioB) => api.get(`/api/mensajes/conversacion/${usuarioA}/${usuarioB}`)
export const contarNoLeidos = receptorId => api.get(`/api/mensajes/no-leidos/${receptorId}`)