<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Test de Framingham" />
      <div class="content">

        <div v-if="!resultado" class="card form-card">
          <div class="test-intro">
            <div class="test-icon">📋</div>
            <div class="test-title">Test de Framingham</div>
            <div class="test-sub">Calcula tu riesgo cardiovascular a 10 años</div>
          </div>

          <form @submit.prevent="calcular">
            <div class="form-row">
              <div class="form-group">
                <label>Edad</label>
                <input v-model.number="form.edad" type="number" placeholder="años" required />
              </div>
              <div class="form-group">
                <label>Sexo</label>
                <select v-model="form.sexo" required>
                  <option value="">Seleccionar</option>
                  <option value="M">Masculino</option>
                  <option value="F">Femenino</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Colesterol total (mg/dL)</label>
                <input v-model.number="form.colesterolTotal" type="number" placeholder="200" required />
              </div>
              <div class="form-group">
                <label>HDL colesterol (mg/dL)</label>
                <input v-model.number="form.colesterolHdl" type="number" placeholder="50" required />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Presión sistólica (mmHg)</label>
                <input v-model.number="form.presionSistolica" type="number" placeholder="120" required />
              </div>
              <div class="form-group">
                <label>¿Tratamiento para HTA?</label>
                <select v-model="form.tratamientoHipertension" required>
                  <option :value="null">Seleccionar</option>
                  <option :value="false">No</option>
                  <option :value="true">Sí</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>¿Fuma actualmente?</label>
                <select v-model="form.fumador" required>
                  <option :value="null">Seleccionar</option>
                  <option :value="false">No</option>
                  <option :value="true">Sí</option>
                </select>
              </div>
              <div class="form-group">
                <label>IMC</label>
                <input v-model.number="form.imc" type="number" step="0.1" placeholder="25.0" required />
              </div>
            </div>

            <p v-if="error" class="error-msg">{{ error }}</p>

            <button type="submit" class="btn-primary" :disabled="cargando">
              {{ cargando ? 'Calculando...' : 'Calcular mi riesgo' }}
            </button>
          </form>
        </div>

        <div v-else class="card resultado-card">
          <div class="resultado-header" :class="riskClass(resultado.nivelRiesgo)">
            <div class="resultado-label">Tu riesgo cardiovascular a 10 años</div>
            <div class="resultado-percent">{{ resultado.porcentajeRiesgo }}%</div>
            <div class="resultado-nivel">Riesgo {{ resultado.nivelRiesgo?.toLowerCase() }}</div>
          </div>

          <div class="rec-automaticas">
            <div class="rec-title">Recomendaciones</div>
            <div v-for="(r, i) in resultado.recomendacionesAutomaticas" :key="i" class="rec-auto-item">
              ✓ {{ r }}
            </div>
          </div>

          <div class="resultado-actions">
            <button class="btn-outline" @click="resultado = null">Nuevo test</button>
            <button class="btn-primary" @click="$router.push('/paciente/historial')">
              Ver historial
            </button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import { realizarTest } from '../../api/testApi.js'

const auth = useAuthStore()
const resultado = ref(null)
const cargando = ref(false)
const error = ref('')

const form = ref({
  edad: null,
  sexo: '',
  colesterolTotal: null,
  colesterolHdl: null,
  presionSistolica: null,
  tratamientoHipertension: null,
  fumador: null,
  imc: null
})

function riskClass(nivel) {
  if (nivel === 'ALTO')     return 'resultado-alto'
  if (nivel === 'MODERADO') return 'resultado-moderado'
  return 'resultado-bajo'
}

async function calcular() {
  error.value = ''
  cargando.value = true
  try {
    const { data } = await realizarTest(auth.usuario.id, form.value)
    resultado.value = data
  } catch (e) {
    error.value = e.response?.data?.mensaje || 'Error al calcular el riesgo'
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 24px;
}

.form-card { max-width: 560px; margin: 0 auto; }

.test-intro { text-align: center; margin-bottom: 24px; }
.test-icon { font-size: 36px; margin-bottom: 8px; }
.test-title { font-size: 18px; font-weight: 500; }
.test-sub { font-size: 13px; color: #888; margin-top: 4px; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }

.form-group { display: flex; flex-direction: column; gap: 5px; margin-bottom: 14px; }
.form-group label { font-size: 12px; font-weight: 500; color: #555; }

.form-group input,
.form-group select {
  padding: 9px 12px;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-size: 13px;
  font-family: inherit;
  outline: none;
  background: #fff;
}

.form-group input:focus,
.form-group select:focus { border-color: #e24b4a; }

.error-msg { font-size: 12px; color: #e24b4a; margin-bottom: 12px; }

.btn-primary {
  width: 100%;
  padding: 11px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.btn-primary:hover { opacity: 0.88; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.resultado-card { max-width: 560px; margin: 0 auto; }

.resultado-header {
  text-align: center;
  padding: 28px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.resultado-bajo     { background: #eaf3de; }
.resultado-moderado { background: #faeeda; }
.resultado-alto     { background: #fcebeb; }

.resultado-label { font-size: 12px; color: #888; margin-bottom: 8px; }

.resultado-percent {
  font-size: 64px;
  font-weight: 300;
  line-height: 1;
  margin-bottom: 8px;
}

.resultado-bajo     .resultado-percent { color: #3b6d11; }
.resultado-moderado .resultado-percent { color: #ba7517; }
.resultado-alto     .resultado-percent { color: #a32d2d; }

.resultado-nivel { font-size: 15px; font-weight: 500; }
.resultado-bajo     .resultado-nivel { color: #3b6d11; }
.resultado-moderado .resultado-nivel { color: #ba7517; }
.resultado-alto     .resultado-nivel { color: #a32d2d; }

.rec-automaticas { margin-bottom: 20px; }
.rec-title { font-size: 13px; font-weight: 500; margin-bottom: 10px; }

.rec-auto-item {
  font-size: 13px;
  padding: 9px 12px;
  background: #f5f5f3;
  border-radius: 8px;
  margin-bottom: 6px;
  color: #444;
}

.resultado-actions { display: flex; gap: 10px; }

.btn-outline {
  flex: 1;
  padding: 10px;
  background: none;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .form-card { max-width: 100%; }
  .resultado-card { max-width: 100%; }
}

@media (max-width: 480px) {
  .form-row { grid-template-columns: 1fr; }
}
</style>