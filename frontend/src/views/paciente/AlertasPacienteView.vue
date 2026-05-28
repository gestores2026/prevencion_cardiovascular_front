<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Mis alertas" />
      <div class="content">

        <div class="card">
          <div class="card-header">
            <span class="card-title">Alertas activas</span>
          </div>

          <div v-if="cargando" class="empty">Cargando...</div>

          <div v-else class="alerta-list">
            <div v-for="a in alertas" :key="a.id" class="alerta-item" :class="{ atendida: a.atendida }">
              <div class="alerta-icon" :class="iconClass(a.tipo)">
                {{ iconoTipo(a.tipo) }}
              </div>
              <div class="alerta-body">
                <div class="alerta-desc">{{ a.descripcion }}</div>
                <div class="alerta-fecha">{{ formatFecha(a.fechaAlerta) }}</div>
              </div>
              <span v-if="a.atendida" class="tag-atendida">Atendida</span>
              <button
                v-else-if="a.tipo === 'RIESGO_ALTO'"
                class="btn-test"
                @click="$router.push('/paciente/test')"
              >
                Hacer test
              </button>
            </div>

            <div v-if="alertas.length === 0" class="empty">
              No tienes alertas activas
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import { alertasPaciente } from '../../api/alertaApi.js'

const auth = useAuthStore()
const alertas = ref([])
const cargando = ref(false)

const iconos = {
  PRESION_ALTA: '🫀', COLESTEROL_ELEVADO: '💧',
  IMC_PELIGROSO: '⚖️', FUMADOR: '🚬', RIESGO_ALTO: '⚠️'
}

function iconoTipo(tipo) { return iconos[tipo] || '🔔' }

function iconClass(tipo) {
  if (tipo === 'PRESION_ALTA' || tipo === 'RIESGO_ALTO') return 'icon-danger'
  return 'icon-warn'
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric'
  })
}

onMounted(async () => {
  cargando.value = true
  try {
    const { data } = await alertasPaciente(auth.usuario.id)
    alertas.value = data
  } catch (e) {
    console.error(e)
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 16px 18px;
}

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.card-title { font-size: 13px; font-weight: 500; }

.alerta-list { display: flex; flex-direction: column; gap: 8px; }

.alerta-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 8px;
  border: 0.5px solid #e0e0dc;
  transition: background 0.12s;
}

.alerta-item.atendida { opacity: 0.5; }

.alerta-icon {
  width: 32px; height: 32px;
  border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.icon-danger { background: #fcebeb; }
.icon-warn   { background: #faeeda; }

.alerta-body { flex: 1; }
.alerta-desc { font-size: 13px; font-weight: 500; }
.alerta-fecha { font-size: 11px; color: #999; margin-top: 2px; }

.tag-atendida {
  font-size: 11px;
  background: #eaf3de;
  color: #27500a;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.btn-test {
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-family: inherit;
  font-size: 11px;
  cursor: pointer;
}

.empty { text-align: center; padding: 32px; color: #aaa; font-size: 13px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
}
</style>