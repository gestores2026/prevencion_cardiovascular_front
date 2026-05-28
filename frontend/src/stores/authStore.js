import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const usuario = ref(JSON.parse(localStorage.getItem('usuario') || 'null'))

  const estaAutenticado = computed(() => !!token.value)
  const esMedico = computed(() => usuario.value?.rol === 'MEDICO')
  const esPaciente = computed(() => usuario.value?.rol === 'PACIENTE')

  function guardarSesion(data) {
    token.value = data.token
    usuario.value = {
      id: data.id,
      nombre: data.nombre,
      correo: data.correo,
      rol: data.rol
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('usuario', JSON.stringify(usuario.value))
  }

  function cerrarSesion() {
    token.value = null
    usuario.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('usuario')
  }

  return { token, usuario, estaAutenticado, esMedico, esPaciente, guardarSesion, cerrarSesion }
})