<template>
  <div v-if="ui.sidebarOpen" class="sidebar-backdrop" @click="ui.closeSidebar()" />
  <aside class="sidebar" :class="{ 'sidebar-open': ui.sidebarOpen }">
    <div class="sidebar-logo">
      <div class="logo-icon">♥</div>
      <div>
        <div class="logo-title">CardioRisk</div>
        <div class="logo-sub">Sistema de prevención CV</div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        active-class="active"
        @click="ui.closeSidebar()"
      >
        <span class="nav-icon">{{ item.icon }}</span>
        <span>{{ item.label }}</span>
        <span v-if="item.badge" class="badge">{{ item.badge }}</span>
      </router-link>
    </nav>

    <div class="sidebar-user">
      <div class="avatar" :class="avatarClass">
        {{ iniciales }}
      </div>
      <div class="user-info">
        <div class="user-name">{{ auth.usuario?.nombre }}</div>
        <div class="user-role">{{ auth.esMedico ? 'Médico' : 'Paciente' }}</div>
      </div>
      <button class="btn-logout" @click="cerrarSesion" title="Cerrar sesión">✕</button>
    </div>
  </aside>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore.js'
import { useUiStore } from '../../stores/uiStore.js'
import { contarAlertas } from '../../api/alertaApi.js'

const auth = useAuthStore()
const ui = useUiStore()
const router = useRouter()
const totalAlertas = ref(0)

const medicoNav = [
  { path: '/medico/dashboard',       label: 'Dashboard',       icon: '⊞' },
  { path: '/medico/pacientes',       label: 'Pacientes',       icon: '👥' },
  { path: '/medico/mensajes',        label: 'Mensajes',        icon: '💬' },
  { path: '/medico/alertas',         label: 'Alertas médicas', icon: '🔔', badge: totalAlertas },
  { path: '/medico/recomendaciones', label: 'Recomendaciones', icon: '📋' },
  { path: '/medico/estadisticas',    label: 'Estadísticas',    icon: '📊' },
]

const pacienteNav = [
  { path: '/paciente/inicio',    label: 'Inicio',              icon: '🏠' },
  { path: '/paciente/test',      label: 'Test Framingham',     icon: '📋' },
  { path: '/paciente/historial', label: 'Mi historial',        icon: '📅' },
  { path: '/paciente/alertas',   label: 'Mis alertas',         icon: '🔔' },
  { path: '/paciente/mensajes',  label: 'Mensajes',            icon: '✉️' },
  { path: '/paciente/perfil',    label: 'Mi perfil',           icon: '👤' },
]

const navItems = computed(() => auth.esMedico ? medicoNav : pacienteNav)

const iniciales = computed(() => {
  const nombre = auth.usuario?.nombre || ''
  return nombre.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
})

const avatarClass = computed(() => auth.esMedico ? 'avatar-blue' : 'avatar-green')

async function cargarAlertas() {
  if (auth.esMedico) {
    try {
      const { data } = await contarAlertas()
      totalAlertas.value = data.total
    } catch {}
  }
}

function cerrarSesion() {
  ui.closeSidebar()
  auth.cerrarSesion()
  router.push('/login')
}

onMounted(cargarAlertas)
</script>

<style scoped>
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 99;
}

.sidebar {
  width: 220px;
  min-width: 220px;
  height: 100vh;
  background: #fff;
  border-right: 0.5px solid #e0e0dc;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 0.5px solid #e0e0dc;
}

.logo-icon {
  width: 34px;
  height: 34px;
  background: #e24b4a;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #fff;
  flex-shrink: 0;
}

.logo-title {
  font-size: 14px;
  font-weight: 500;
}

.logo-sub {
  font-size: 10px;
  color: #999;
  margin-top: 1px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 10px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 9px 10px;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  text-decoration: none;
  transition: background 0.12s;
}

.nav-item:hover {
  background: #f5f5f3;
  color: #1a1a1a;
}

.nav-item.active {
  background: #fcebeb;
  color: #a32d2d;
  font-weight: 500;
}

.nav-icon {
  font-size: 15px;
  width: 20px;
  text-align: center;
}

.badge {
  margin-left: auto;
  background: #e24b4a;
  color: #fff;
  font-size: 10px;
  font-weight: 500;
  border-radius: 10px;
  padding: 1px 7px;
  min-width: 18px;
  text-align: center;
}

.sidebar-user {
  padding: 14px 16px;
  border-top: 0.5px solid #e0e0dc;
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 500;
  flex-shrink: 0;
}

.avatar-blue  { background: #b5d4f4; color: #0c447c; }
.avatar-green { background: #c0dd97; color: #27500a; }
.avatar-red   { background: #f7c1c1; color: #791f1f; }

.user-info { flex: 1; min-width: 0; }

.user-name {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: #999;
}

.btn-logout {
  background: none;
  border: none;
  color: #bbb;
  font-size: 13px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: color 0.12s;
}

.btn-logout:hover { color: #e24b4a; }

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.25s ease;
    z-index: 200;
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.15);
  }

  .sidebar.sidebar-open {
    transform: translateX(0);
  }
}
</style>
