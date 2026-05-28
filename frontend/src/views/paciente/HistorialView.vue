<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Mi historial" />
      <div class="content">

        <div class="card" style="margin-bottom: 16px;">
          <div class="card-header">
            <span class="card-title">Evolución del riesgo</span>
          </div>
          <div v-if="tests.length > 0" style="height: 220px;">
            <Line :data="lineData" :options="lineOptions" style="height: 100%;" />
          </div>
          <div v-else class="empty">Sin datos para graficar</div>
        </div>

        <div class="card">
          <div class="card-header">
            <span class="card-title">Historial de tests</span>
            <button class="btn-pdf" @click="exportarPDF">⬇ Exportar PDF</button>
          </div>

          <div v-if="cargando" class="empty">Cargando...</div>

          <table v-else class="data-table">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Riesgo</th>
                <th>Nivel</th>
                <th>Colesterol</th>
                <th>Presión</th>
                <th>Fumador</th>
                <th>IMC</th>
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
                <td>{{ t.imc }}</td>
              </tr>
              <tr v-if="tests.length === 0">
                <td colspan="7" class="empty">Sin tests registrados</td>
              </tr>
            </tbody>
          </table>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS, CategoryScale, LinearScale,
  PointElement, LineElement, Tooltip, Legend
} from 'chart.js'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import RiskPill from '../../components/shared/RiskPill.vue'
import { historialPaciente } from '../../api/testApi.js'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend)

const auth = useAuthStore()
const tests = ref([])
const cargando = ref(false)

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
    borderColor: '#3b6d11',
    backgroundColor: 'rgba(59,109,17,0.08)',
    tension: 0.4,
    fill: true,
    pointBackgroundColor: '#3b6d11',
    pointRadius: 5
  }]
}))

const lineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
  scales: { y: { ticks: { callback: v => v + '%' } } }
}

function exportarPDF() {
  const doc = new jsPDF()

  doc.setFontSize(16)
  doc.setTextColor(40)
  doc.text('CardioRisk — Historial de tests', 14, 20)

  doc.setFontSize(11)
  doc.setTextColor(120)
  doc.text(`Paciente: ${auth.usuario?.nombre}`, 14, 30)
  doc.text(`Generado: ${new Date().toLocaleDateString('es-CO')}`, 14, 37)

  autoTable(doc, {
    startY: 45,
    head: [['Fecha', 'Riesgo %', 'Nivel', 'Colesterol', 'Presión', 'Fumador', 'IMC']],
    body: tests.value.map(t => [
      formatFecha(t.fechaTest),
      t.porcentajeRiesgo + '%',
      t.nivelRiesgo,
      t.colesterolTotal + ' mg/dL',
      t.presionSistolica + ' mmHg',
      t.fumador ? 'Sí' : 'No',
      t.imc
    ]),
    styles: { fontSize: 10 },
    headStyles: { fillColor: [226, 75, 74] }
  })

  doc.save(`historial_${auth.usuario?.nombre?.replace(' ', '_')}.pdf`)
}

onMounted(async () => {
  cargando.value = true
  try {
    const { data } = await historialPaciente(auth.usuario.id)
    tests.value = data
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

.btn-pdf {
  background: none;
  border: 0.5px solid #d0d0cc;
  border-radius: 6px;
  padding: 6px 14px;
  font-family: inherit;
  font-size: 12px;
  cursor: pointer;
}

.btn-pdf:hover { background: #f5f5f3; }

.data-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.data-table th {
  text-align: left;
  padding: 8px 12px;
  color: #888;
  border-bottom: 0.5px solid #e0e0dc;
  font-weight: 500;
}
.data-table td { padding: 10px 12px; border-bottom: 0.5px solid #e0e0dc; }
.data-table tr:last-child td { border-bottom: none; }

.empty { text-align: center; padding: 32px; color: #aaa; font-size: 13px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .data-table { display: block; overflow-x: auto; -webkit-overflow-scrolling: touch; }
}
</style>