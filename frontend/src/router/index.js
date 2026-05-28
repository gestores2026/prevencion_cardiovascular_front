import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore.js'

import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'

import DashboardView from '../views/medico/DashboardView.vue'
import PacientesView from '../views/medico/PacientesView.vue'
import PacienteDetalleView from '../views/medico/PacienteDetalleView.vue'
import AlertasView from '../views/medico/AlertasView.vue'
import RecomendacionesView from '../views/medico/RecomendacionesView.vue'
import MensajesMedicoView from '../views/medico/MensajesMedicoView.vue'
import EstadisticasView from '../views/medico/EstadisticasView.vue'

import InicioView from '../views/paciente/InicioView.vue'
import TestView from '../views/paciente/TestView.vue'
import HistorialView from '../views/paciente/HistorialView.vue'
import AlertasPacienteView from '../views/paciente/AlertasPacienteView.vue'
import MensajesView from '../views/paciente/MensajesView.vue'
import PerfilView from '../views/paciente/PerfilView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/registro', component: RegisterView },

  {
    path: '/medico',
    meta: { requiresAuth: true, rol: 'MEDICO' },
    children: [
      { path: 'dashboard', component: DashboardView },
      { path: 'pacientes', component: PacientesView },
      { path: 'pacientes/:id', component: PacienteDetalleView },
      { path: 'alertas', component: AlertasView },
      { path: 'mensajes', component: MensajesMedicoView },
      { path: 'recomendaciones', component: RecomendacionesView },
      { path: 'estadisticas', component: EstadisticasView },
    ]
  },

  {
    path: '/paciente',
    meta: { requiresAuth: true, rol: 'PACIENTE' },
    children: [
      { path: 'inicio', component: InicioView },
      { path: 'test', component: TestView },
      { path: 'historial', component: HistorialView },
      { path: 'alertas', component: AlertasPacienteView },
      { path: 'mensajes', component: MensajesView },
      { path: 'perfil', component: PerfilView },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.estaAutenticado) {
    return next('/login')
  }

  if (to.meta.rol && auth.usuario?.rol !== to.meta.rol) {
    return next('/login')
  }

  next()
})

export default router