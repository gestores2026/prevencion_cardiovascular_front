<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar :titulo="paciente ? paciente.nombre : 'Perfil del paciente'" />
      <div class="content">

        <button class="btn-back" @click="$router.push('/medico/pacientes')">
          ← Volver
        </button>

        <div v-if="cargando" class="empty">Cargando...</div>

        <div v-else-if="paciente" class="grid-2">
          <div class="card">
            <div class="card-header">
              <div class="paciente-header">
                <div class="avatar avatar-blue">{{ iniciales(paciente.nombre) }}</div>
                <div>
                  <div class="paciente-nombre">{{ paciente.nombre }}</div>
                  <div class="paciente-meta">Doc: {{ paciente.documento }} · {{ paciente.edad }} años</div>
                </div>
              </div>
              <RiskPill :nivel="paciente.ultimoNivelRiesgo" :valor="paciente.ultimoRiesgo" />
            </div>

            <div class="divider" />

            <table class="info-table">
                <tbody>
                    <tr><td class="info-label">Correo</td><td>{{ paciente.correo }}</td></tr>
                    <tr><td class="info-label">Sexo</td><td>{{ paciente.sexo || '—' }}</td></tr>
                    <tr><td class="info-label">Peso</td><td>{{ paciente.peso ? paciente.peso + ' kg' : '—' }}</td></tr>
                    <tr><td class="info-label">Talla</td><td>{{ paciente.talla ? paciente.talla + ' m' : '—' }}</td></tr>
                    <tr><td class="info-label">Registrado</td><td>{{ formatFecha(paciente.fechaRegistro) }}</td></tr>
                </tbody>
            </table>

            <div class="divider" />

            <button class="btn-primary" @click="$router.push('/medico/recomendaciones')">
              Enviar recomendación
            </button>
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">Evolución del riesgo</span>
            </div>
            <div v-if="tests.length > 0">
              <Line :data="lineData" :options="lineOptions" />
            </div>
            <div v-else class="empty">Sin tests registrados</div>
          </div>

          <div class="card" style="grid-column: span 2;">
            <div class="card-header">
              <span class="card-title">Historial de tests</span>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Riesgo</th>
                  <th>Nivel</th>
                  <th>Colesterol</th>
                  <th>Presión</th>
                  <th>Fumador</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="t in tests" :key="t.id">
                  <td>{{ formatFecha(t.fechaTest) }}</td>
                  <td>{{ t.porcentajeRiesgo }}%</td>
                  <td><RiskPill :nivel="t.nivelRiesgo" /></td>
                  <td>{{ t.colesterolTotal }} mg/dL</td>
                  <td>{{ t.presionSistolica }} mmHg</td>
                  <td>{{ t.fumador ? 'Sí' : 'No' }}</td>
                </tr>
                <tr v-if="tests.length === 0">
                  <td colspan="6" class="empty">Sin tests</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS, CategoryScale, LinearScale,
  PointElement, LineElement, Tooltip, Legend
} from 'chart.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import RiskPill from '../../components/shared/RiskPill.vue'
import { obtenerPaciente } from '../../api/usuarioApi.js'
import { historialMedico } from '../../api/testApi.js'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend)

const route = useRoute()
const paciente = ref(null)
const tests = ref([])
const cargando = ref(false)

function iniciales(nombre) {
  return nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
}

function formatFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleDateString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric'
  })
}

const lineData = computed(() => ({
  labels: [...tests.value].reverse().map(t => formatFecha(t.fechaTest)),
  datasets: [{
    label: 'Riesgo %',
    data: [...tests.value].reverse().map(t => t.porcentajeRiesgo),
    borderColor: '#e24b4a',
    backgroundColor: 'rgba(226,75,74,0.08)',
    tension: 0.4,
    fill: true,
    pointBackgroundColor: '#e24b4a',
    pointRadius: 4
  }]
}))

const lineOptions = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: {
    y: { ticks: { callback: v => v + '%' } }
  }
}

onMounted(async () => {
  cargando.value = true
  try {
    const id = route.params.id
    const [resPaciente, resTests] = await Promise.all([
      obtenerPaciente(id),
      historialMedico(id)
    ])
    paciente.value = resPaciente.data
    tests.value = resTests.data
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

.btn-back {
  background: none;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  padding: 7px 14px;
  font-family: inherit;
  font-size: 12px;
  cursor: pointer;
  margin-bottom: 20px;
  color: #555;
}

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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.card-title { font-size: 13px; font-weight: 500; }

.paciente-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 44px; height: 44px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 500;
}

.avatar-blue { background: #b5d4f4; color: #0c447c; }

.paciente-nombre { font-size: 15px; font-weight: 500; }
.paciente-meta { font-size: 12px; color: #999; }

.divider {
  height: 0.5px;
  background: #e0e0dc;
  margin: 14px 0;
}

.info-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.info-table tr td { padding: 6px 0; }
.info-label { color: #888; width: 100px; }

.btn-primary {
  width: 100%;
  padding: 10px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  margin-top: 14px;
}

.btn-primary:hover { opacity: 0.88; }

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.data-table th {
  text-align: left;
  padding: 8px 12px;
  color: #888;
  border-bottom: 0.5px solid #e0e0dc;
  font-weight: 500;
}

.data-table td {
  padding: 10px 12px;
  border-bottom: 0.5px solid #e0e0dc;
}

.data-table tr:last-child td { border-bottom: none; }

.empty {
  text-align: center;
  padding: 32px;
  color: #aaa;
  font-size: 13px;
}

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .grid-2 { grid-template-columns: 1fr; }
  .data-table { display: block; overflow-x: auto; -webkit-overflow-scrolling: touch; }
}
</style>