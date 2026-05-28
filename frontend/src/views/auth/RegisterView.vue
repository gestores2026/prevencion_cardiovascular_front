<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-logo">
        <div class="logo-icon">♥</div>
        <h1>Crear cuenta</h1>
        <p>Completa tus datos para registrarte</p>
      </div>

      <form @submit.prevent="handleRegistro">
        <div class="form-row">
          <div class="form-group">
            <label>Nombre completo</label>
            <input v-model="form.nombre" type="text" placeholder="Juan Pérez" required />
          </div>
          <div class="form-group">
            <label>Documento</label>
            <input v-model="form.documento" type="text" placeholder="123456789" required />
          </div>
        </div>

        <div class="form-group">
          <label>Correo electrónico</label>
          <input v-model="form.correo" type="email" placeholder="correo@ejemplo.com" required />
        </div>

        <div class="form-group">
          <label>Contraseña</label>
          <input v-model="form.password" type="password" placeholder="Mínimo 6 caracteres" required />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Edad</label>
            <input v-model.number="form.edad" type="number" placeholder="30" min="18" max="99" />
          </div>
          <div class="form-group">
            <label>Sexo</label>
            <select v-model="form.sexo">
              <option value="">Seleccionar</option>
              <option value="M">Masculino</option>
              <option value="F">Femenino</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Peso (kg)</label>
            <input v-model.number="form.peso" type="number" placeholder="70" />
          </div>
          <div class="form-group">
            <label>Talla (m)</label>
            <input v-model.number="form.talla" type="number" placeholder="1.70" step="0.01" />
          </div>
        </div>

        <div class="form-group">
          <label>Rol</label>
          <select v-model="form.rol" required>
            <option value="">Seleccionar rol</option>
            <option value="PACIENTE">Paciente</option>
            <option value="MEDICO">Médico</option>
          </select>
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="cargando">
          {{ cargando ? 'Registrando...' : 'Crear cuenta' }}
        </button>
      </form>

      <p class="auth-link">
        ¿Ya tienes cuenta?
        <router-link to="/login">Inicia sesión</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore.js'
import { registrar } from '../../api/authApi.js'

const router = useRouter()
const auth = useAuthStore()

const form = ref({
  nombre: '',
  documento: '',
  correo: '',
  password: '',
  edad: null,
  sexo: '',
  peso: null,
  talla: null,
  rol: ''
})

const error = ref('')
const cargando = ref(false)

async function handleRegistro() {
  error.value = ''
  cargando.value = true
  try {
    const { data } = await registrar(form.value)
    auth.guardarSesion(data)
    if (data.rol === 'MEDICO') {
      router.push('/medico/dashboard')
    } else {
      router.push('/paciente/inicio')
    }
  } catch (e) {
    error.value = e.response?.data?.mensaje || 'Error al registrar. Verifica los datos.'
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f3;
  padding: 24px;
}

.auth-card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 520px;
}

.auth-logo {
  text-align: center;
  margin-bottom: 28px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: #e24b4a;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  margin: 0 auto 12px;
}

.auth-logo h1 {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 4px;
}

.auth-logo p {
  font-size: 13px;
  color: #888;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 14px;
}

.form-group label {
  font-size: 12px;
  font-weight: 500;
  color: #555;
}

.form-group input,
.form-group select {
  padding: 10px 12px;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-size: 13px;
  font-family: inherit;
  outline: none;
  background: #fff;
  transition: border 0.15s;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #e24b4a;
}

.error-msg {
  font-size: 12px;
  color: #e24b4a;
  margin-bottom: 12px;
}

.btn-primary {
  width: 100%;
  padding: 11px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: opacity 0.15s;
}

.btn-primary:hover { opacity: 0.88; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.auth-link {
  text-align: center;
  font-size: 13px;
  color: #888;
  margin-top: 20px;
}

.auth-link a {
  color: #e24b4a;
  text-decoration: none;
  font-weight: 500;
}

@media (max-width: 480px) {
  .auth-card { padding: 24px; border-radius: 12px; }
  .form-row { grid-template-columns: 1fr; }
}
</style>