<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Gestión de pacientes" />
      <div class="content">

        <div class="search-bar">
          <span>🔍</span>
          <input
            v-model="busqueda"
            placeholder="Buscar por nombre, correo o documento..."
            @input="buscar"
          />
        </div>

        <div class="card">
          <div class="card-header">
            <span class="card-title">
              {{ mostrarTodos ? 'Todos los pacientes' : 'Mis pacientes' }}
              ({{ pacientes.length }})
            </span>
            <button class="btn-toggle" @click="toggleVista">
              {{ mostrarTodos ? '← Mis pacientes' : 'Ver todos los pacientes' }}
            </button>
          </div>

          <div v-if="cargando" class="empty">Cargando...</div>

          <div v-else class="patient-list">
            <div
              v-for="p in pacientes"
              :key="p.id"
              class="patient-row"
              @click="$router.push(`/medico/pacientes/${p.id}`)"
            >
              <div class="avatar avatar-blue">{{ iniciales(p.nombre) }}</div>
              <div class="patient-info">
                <div class="patient-name">{{ p.nombre }}</div>
                <div class="patient-meta">
                  Doc: {{ p.documento }} · {{ p.edad }} años · {{ p.correo }}
                </div>
              </div>
              <RiskPill :nivel="p.ultimoNivelRiesgo" :valor="p.ultimoRiesgo" />
              <button
                v-if="mostrarTodos && !p.medicoId"
                class="btn-asignar"
                @click.stop="asignar(p.id)"
              >
                Asignarme
              </button>
              <span v-if="mostrarTodos && p.medicoId" class="tag-asignado">
                Asignado
              </span>
              <span class="chevron">›</span>
            </div>

            <div v-if="pacientes.length === 0" class="empty">
              {{ mostrarTodos ? 'No hay pacientes registrados' : 'No tienes pacientes asignados aún' }}
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import RiskPill from '../../components/shared/RiskPill.vue'
import {
  listarPacientes,
  listarMisPacientes,
  buscarPacientes,
  buscarMisPacientes,
  asignarMedico
} from '../../api/usuarioApi.js'

const auth = useAuthStore()
const pacientes = ref([])
const busqueda = ref('')
const cargando = ref(false)
const mostrarTodos = ref(false)

function iniciales(nombre) {
  return nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
}

async function cargar() {
  cargando.value = true
  try {
    const { data } = mostrarTodos.value
      ? await listarPacientes()
      : await listarMisPacientes(auth.usuario.id)
    pacientes.value = data
  } catch (e) {
    console.error(e)
  } finally {
    cargando.value = false
  }
}

let timeout = null
async function buscar() {
  clearTimeout(timeout)
  timeout = setTimeout(async () => {
    if (busqueda.value.trim() === '') {
      cargar()
      return
    }
    try {
      const { data } = mostrarTodos.value
        ? await buscarPacientes(busqueda.value)
        : await buscarMisPacientes(auth.usuario.id, busqueda.value)
      pacientes.value = data
    } catch (e) {
      console.error(e)
    }
  }, 400)
}

async function asignar(pacienteId) {
  try {
    await asignarMedico(pacienteId, auth.usuario.id)
    mostrarTodos.value = false
    await cargar()
  } catch (e) {
    console.error(e)
  }
}

function toggleVista() {
  busqueda.value = ''
  mostrarTodos.value = !mostrarTodos.value
}

watch(mostrarTodos, () => cargar())

onMounted(cargar)
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 16px;
}

.search-bar input {
  border: none;
  outline: none;
  font-family: inherit;
  font-size: 13px;
  flex: 1;
  background: none;
}

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 16px 18px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-title { font-size: 13px; font-weight: 500; }

.btn-toggle {
  padding: 7px 14px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.15s;
}

.btn-toggle:hover { opacity: 0.88; }

.patient-list { display: flex; flex-direction: column; gap: 2px; }

.patient-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.12s;
}

.patient-row:hover { background: #f5f5f3; }

.avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-blue { background: #b5d4f4; color: #0c447c; }

.patient-info { flex: 1; min-width: 0; }
.patient-name { font-size: 13px; font-weight: 500; }
.patient-meta { font-size: 11px; color: #999; }

.btn-asignar {
  background: #eaf3de;
  color: #27500a;
  border: none;
  border-radius: 6px;
  padding: 5px 12px;
  font-family: inherit;
  font-size: 11px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.12s;
}

.btn-asignar:hover { background: #c0dd97; }

.tag-asignado {
  font-size: 10px;
  background: #f5f5f3;
  color: #999;
  padding: 3px 10px;
  border-radius: 20px;
  white-space: nowrap;
}

.chevron { color: #ccc; font-size: 18px; }

.empty {
  text-align: center;
  padding: 32px;
  color: #aaa;
  font-size: 13px;
}

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .patient-meta { font-size: 10px; }
  .card-header { flex-wrap: wrap; gap: 8px; }
}
</style>