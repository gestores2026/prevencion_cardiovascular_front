<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Recomendaciones" />
      <div class="content">

        <div class="grid-2">
          <div class="card">
            <div class="card-header">
              <span class="card-title">Nueva recomendación</span>
            </div>

            <form @submit.prevent="enviar">
              <div class="form-group">
                <label>Paciente</label>
                <select v-model="form.pacienteId" required>
                  <option value="">Seleccionar paciente</option>
                  <option v-for="p in pacientes" :key="p.id" :value="p.id">
                    {{ p.nombre }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label>Tipo</label>
                <select v-model="form.tipo" required>
                  <option value="">Seleccionar tipo</option>
                  <option value="HABITOS">Cambio de hábitos</option>
                  <option value="ACTIVIDAD">Actividad física</option>
                  <option value="CITA">Cita médica</option>
                  <option value="MEDICACION">Medicación</option>
                </select>
              </div>

              <div class="form-group">
                <label>Mensaje</label>
                <textarea
                  v-model="form.mensaje"
                  rows="4"
                  placeholder="Ej: Reducir consumo de sal y caminar 30 minutos diarios..."
                  required
                />
              </div>

              <p v-if="exito" class="exito-msg">Recomendación enviada correctamente</p>
              <p v-if="error" class="error-msg">{{ error }}</p>

              <button type="submit" class="btn-primary" :disabled="cargando">
                {{ cargando ? 'Enviando...' : 'Enviar recomendación' }}
              </button>
            </form>
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">Enviadas recientemente</span>
            </div>
            <div class="rec-list">
              <div v-for="r in enviadas" :key="r.id" class="rec-item">
                <div class="rec-tipo">{{ tipoLabel(r.tipo) }}</div>
                <div class="rec-mensaje">{{ r.mensaje }}</div>
                <div class="rec-meta">Para {{ r.pacienteNombre }} · {{ formatFecha(r.fechaEnvio) }}</div>
              </div>
              <div v-if="enviadas.length === 0" class="empty">Sin recomendaciones enviadas</div>
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
import { listarPacientes } from '../../api/usuarioApi.js'
import { enviarRecomendacion, enviadasMedico } from '../../api/recomendacionApi.js'

const auth = useAuthStore()
const pacientes = ref([])
const enviadas = ref([])
const cargando = ref(false)
const exito = ref(false)
const error = ref('')

const form = ref({ pacienteId: '', tipo: '', mensaje: '' })

const tipoLabels = {
  HABITOS: 'Cambio de hábitos',
  ACTIVIDAD: 'Actividad física',
  CITA: 'Cita médica',
  MEDICACION: 'Medicación'
}

function tipoLabel(tipo) { return tipoLabels[tipo] || tipo }

function formatFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleDateString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric'
  })
}

async function enviar() {
  error.value = ''
  exito.value = false
  cargando.value = true
  try {
    await enviarRecomendacion(auth.usuario.id, form.value)
    exito.value = true
    form.value = { pacienteId: '', tipo: '', mensaje: '' }
    const { data } = await enviadasMedico(auth.usuario.id)
    enviadas.value = data
  } catch (e) {
    error.value = 'Error al enviar la recomendación'
  } finally {
    cargando.value = false
  }
}

onMounted(async () => {
  try {
    const [resPacientes, resEnviadas] = await Promise.all([
      listarPacientes(),
      enviadasMedico(auth.usuario.id)
    ])
    pacientes.value = resPacientes.data
    enviadas.value = resEnviadas.data
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 16px 18px;
}

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.card-title { font-size: 13px; font-weight: 500; }

.form-group { display: flex; flex-direction: column; gap: 5px; margin-bottom: 14px; }
.form-group label { font-size: 12px; font-weight: 500; color: #555; }

.form-group input,
.form-group select,
.form-group textarea {
  padding: 9px 12px;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-size: 13px;
  font-family: inherit;
  outline: none;
  background: #fff;
  resize: vertical;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus { border-color: #e24b4a; }

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
}

.btn-primary:hover { opacity: 0.88; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.exito-msg { font-size: 12px; color: #3b6d11; margin-bottom: 10px; }
.error-msg { font-size: 12px; color: #e24b4a; margin-bottom: 10px; }

.rec-list { display: flex; flex-direction: column; gap: 10px; }

.rec-item {
  padding: 10px 12px;
  background: #f5f5f3;
  border-radius: 8px;
}

.rec-tipo { font-size: 11px; font-weight: 500; color: #185fa5; margin-bottom: 3px; }
.rec-mensaje { font-size: 13px; margin-bottom: 4px; }
.rec-meta { font-size: 11px; color: #999; }

.empty { text-align: center; padding: 32px; color: #aaa; font-size: 13px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .grid-2 { grid-template-columns: 1fr; }
}
</style>