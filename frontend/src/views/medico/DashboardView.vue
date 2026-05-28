<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Dashboard médico" />
      <div class="content">

        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-label">Total pacientes</div>
            <div class="stat-value">{{ stats.totalPacientes }}</div>
          </div>
          <div class="stat-card stat-red">
            <div class="stat-label">Riesgo alto</div>
            <div class="stat-value">{{ stats.pacientesRiesgoAlto }}</div>
          </div>
          <div class="stat-card stat-amber">
            <div class="stat-label">Riesgo moderado</div>
            <div class="stat-value">{{ stats.pacientesRiesgoModerado }}</div>
          </div>
          <div class="stat-card stat-green">
            <div class="stat-label">Promedio de riesgo</div>
            <div class="stat-value">{{ stats.promedioRiesgo?.toFixed(1) }}%</div>
          </div>
        </div>

        <div class="grid-2">
          <div class="card">
            <div class="card-header">
              <span class="card-title">Distribución de riesgo</span>
            </div>
            <Pie v-if="pieData" :data="pieData" :options="pieOptions" />
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">Pacientes recientes</span>
              <router-link to="/medico/pacientes" class="card-action">Ver todos →</router-link>
            </div>
            <div class="patient-list">
              <div
                v-for="p in pacientes.slice(0, 5)"
                :key="p.id"
                class="patient-row"
                @click="$router.push(`/medico/pacientes/${p.id}`)"
              >
                <div class="avatar avatar-blue">{{ iniciales(p.nombre) }}</div>
                <div class="patient-info">
                  <div class="patient-name">{{ p.nombre }}</div>
                  <div class="patient-meta">{{ p.edad }} años · Doc: {{ p.documento }}</div>
                </div>
                <RiskPill :nivel="p.ultimoNivelRiesgo" :valor="p.ultimoRiesgo" />
              </div>
            </div>
          </div>
        </div>

        <div class="card" style="margin-top: 16px;">
          <div class="card-header">
            <span class="card-title">Alertas recientes</span>
            <router-link to="/medico/alertas" class="card-action">Ver todas →</router-link>
          </div>
          <div class="alerta-list">
            <AlertaItem
              v-for="a in alertas.slice(0, 3)"
              :key="a.id"
              :alerta="a"
            />
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import RiskPill from '../../components/shared/RiskPill.vue'
import AlertaItem from '../../components/shared/AlertaItem.vue'
import { obtenerEstadisticas } from '../../api/estadisticasApi.js'
import { listarPacientes } from '../../api/usuarioApi.js'
import { listarAlertas } from '../../api/alertaApi.js'

ChartJS.register(ArcElement, Tooltip, Legend)

const stats = ref({})
const pacientes = ref([])
const alertas = ref([])
const pieData = ref(null)

const pieOptions = {
  responsive: true,
  plugins: { legend: { position: 'bottom' } }
}

function iniciales(nombre) {
  return nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
}

onMounted(async () => {
  try {
    const [resStats, resPacientes, resAlertas] = await Promise.all([
      obtenerEstadisticas(),
      listarPacientes(),
      listarAlertas()
    ])

    stats.value = resStats.data
    pacientes.value = resPacientes.data
    alertas.value = resAlertas.data

    pieData.value = {
      labels: ['Bajo', 'Moderado', 'Alto'],
      datasets: [{
        data: [
          stats.value.pacientesRiesgoBajo,
          stats.value.pacientesRiesgoModerado,
          stats.value.pacientesRiesgoAlto
        ],
        backgroundColor: ['#639922', '#BA7517', '#E24B4A'],
        borderWidth: 0
      }]
    }
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  background: #f5f5f3;
  border-radius: 8px;
  padding: 14px 16px;
}

.stat-label { font-size: 12px; color: #888; margin-bottom: 6px; }
.stat-value { font-size: 24px; font-weight: 500; }
.stat-red .stat-value   { color: #e24b4a; }
.stat-amber .stat-value { color: #ba7517; }
.stat-green .stat-value { color: #3b6d11; }

.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 16px 18px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.card-title { font-size: 13px; font-weight: 500; }
.card-action { font-size: 12px; color: #185fa5; text-decoration: none; }

.patient-list { display: flex; flex-direction: column; gap: 2px; }

.patient-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.12s;
}

.patient-row:hover { background: #f5f5f3; }

.avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-blue  { background: #b5d4f4; color: #0c447c; }

.patient-info { flex: 1; min-width: 0; }
.patient-name { font-size: 13px; font-weight: 500; }
.patient-meta { font-size: 11px; color: #999; }

.alerta-list { display: flex; flex-direction: column; gap: 8px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .grid-2 { grid-template-columns: 1fr; }
}
</style>