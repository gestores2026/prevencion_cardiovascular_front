<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Bienvenida" />
      <div class="content">

        <div class="bienvenida-card">
          <div class="bienvenida-info">
            <div class="avatar avatar-green">{{ iniciales }}</div>
            <div>
              <div class="bienvenida-nombre">Hola, {{ auth.usuario?.nombre?.split(' ')[0] }}</div>
              <div class="bienvenida-sub">
                {{ ultimoTest
                  ? `Último test hace ${diasDesde(ultimoTest.fechaTest)} días · Riesgo: ${ultimoTest.porcentajeRiesgo}%`
                  : 'Aún no tienes tests registrados'
                }}
              </div>
            </div>
          </div>
          <button class="btn-primary" @click="$router.push('/paciente/test')">
            Nuevo test
          </button>
        </div>

        <div class="grid-2">
          <div class="card">
            <div class="card-header">
              <span class="card-title">Mi riesgo cardiovascular</span>
            </div>
            <div v-if="ultimoTest" class="risk-result" :class="riskClass">
              <div class="risk-percent">{{ ultimoTest.porcentajeRiesgo }}%</div>
              <div class="risk-label">Riesgo {{ ultimoTest.nivelRiesgo?.toLowerCase() }}</div>
              <div class="risk-desc">
                Tu riesgo de evento cardiovascular en los próximos 10 años.
              </div>
            </div>
            <div v-else class="empty">
              Realiza tu primer test para ver tu riesgo
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">Recomendaciones del médico</span>
            </div>
            <div class="rec-list">
              <div v-for="r in recomendaciones.slice(0, 4)" :key="r.id" class="rec-item">
                <span class="rec-icon">{{ tipoIcono(r.tipo) }}</span>
                <div>
                  <div class="rec-tipo">{{ tipoLabel(r.tipo) }}</div>
                  <div class="rec-mensaje">{{ r.mensaje }}</div>
                </div>
              </div>
              <div v-if="recomendaciones.length === 0" class="empty">
                Sin recomendaciones por ahora
              </div>
            </div>
          </div>
        </div>

        <div v-if="ultimoTest?.recomendacionesAutomaticas?.length" class="card" style="margin-top:16px;">
          <div class="card-header">
            <span class="card-title">Recomendaciones automáticas del último test</span>
          </div>
          <div class="auto-rec-list">
            <div v-for="(r, i) in ultimoTest.recomendacionesAutomaticas" :key="i" class="auto-rec-item">
              ✓ {{ r }}
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import { historialPaciente } from '../../api/testApi.js'
import { recibidasPaciente } from '../../api/recomendacionApi.js'

const auth = useAuthStore()
const ultimoTest = ref(null)
const recomendaciones = ref([])

const iniciales = computed(() => {
  return auth.usuario?.nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
})

const riskClass = computed(() => {
  const n = ultimoTest.value?.nivelRiesgo?.toUpperCase()
  if (n === 'ALTO')     return 'risk-alto'
  if (n === 'MODERADO') return 'risk-moderado'
  return 'risk-bajo'
})

function diasDesde(fecha) {
  if (!fecha) return 0
  const diff = new Date() - new Date(fecha)
  return Math.floor(diff / (1000 * 60 * 60 * 24))
}

const tipoIconos = { HABITOS: '🥗', ACTIVIDAD: '🚶', CITA: '📅', MEDICACION: '💊' }
const tipoLabels = { HABITOS: 'Hábitos', ACTIVIDAD: 'Actividad física', CITA: 'Cita médica', MEDICACION: 'Medicación' }

function tipoIcono(tipo) { return tipoIconos[tipo] || '💬' }
function tipoLabel(tipo) { return tipoLabels[tipo] || tipo }

onMounted(async () => {
  const id = auth.usuario?.id
  try {
    const [resTests, resRecs] = await Promise.all([
      historialPaciente(id),
      recibidasPaciente(id)
    ])
    ultimoTest.value = resTests.data[0] || null
    recomendaciones.value = resRecs.data
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.bienvenida-card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-left: 3px solid #e24b4a;
  border-radius: 0 12px 12px 0;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.bienvenida-info { display: flex; align-items: center; gap: 14px; }

.avatar {
  width: 44px; height: 44px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-green { background: #c0dd97; color: #27500a; }

.bienvenida-nombre { font-size: 16px; font-weight: 500; }
.bienvenida-sub { font-size: 12px; color: #888; margin-top: 2px; }

.btn-primary {
  padding: 9px 20px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.btn-primary:hover { opacity: 0.88; }

.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 16px 18px;
}

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.card-title { font-size: 13px; font-weight: 500; }

.risk-result {
  text-align: center;
  padding: 20px;
  border-radius: 10px;
}

.risk-bajo     { background: #eaf3de; }
.risk-moderado { background: #faeeda; }
.risk-alto     { background: #fcebeb; }

.risk-percent {
  font-size: 52px;
  font-weight: 300;
  line-height: 1;
  margin-bottom: 6px;
}

.risk-bajo     .risk-percent { color: #3b6d11; }
.risk-moderado .risk-percent { color: #ba7517; }
.risk-alto     .risk-percent { color: #a32d2d; }

.risk-label { font-size: 14px; font-weight: 500; margin-bottom: 8px; }
.risk-bajo     .risk-label { color: #3b6d11; }
.risk-moderado .risk-label { color: #ba7517; }
.risk-alto     .risk-label { color: #a32d2d; }

.risk-desc { font-size: 12px; color: #888; }

.rec-list { display: flex; flex-direction: column; gap: 10px; }

.rec-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  background: #f5f5f3;
  border-radius: 8px;
}

.rec-icon { font-size: 18px; flex-shrink: 0; }
.rec-tipo { font-size: 11px; font-weight: 500; color: #185fa5; margin-bottom: 2px; }
.rec-mensaje { font-size: 12px; color: #444; }

.auto-rec-list { display: flex; flex-direction: column; gap: 8px; }

.auto-rec-item {
  font-size: 13px;
  padding: 9px 12px;
  background: #f5f5f3;
  border-radius: 8px;
  color: #444;
}

.empty { text-align: center; padding: 32px; color: #aaa; font-size: 13px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .grid-2 { grid-template-columns: 1fr; }
  .bienvenida-card { flex-direction: column; align-items: flex-start; gap: 12px; }
}
</style>