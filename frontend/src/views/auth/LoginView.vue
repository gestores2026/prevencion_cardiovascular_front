<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-logo">
        <div class="logo-icon">♥</div>
        <h1>CardioRisk</h1>
        <p>Sistema de prevención cardiovascular</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Correo electrónico</label>
          <input
            v-model="form.correo"
            type="email"
            placeholder="correo@ejemplo.com"
            required
          />
        </div>

        <div class="form-group">
          <label>Contraseña</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="••••••••"
            required
          />
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="cargando">
          {{ cargando ? 'Iniciando sesión...' : 'Iniciar sesión' }}
        </button>
      </form>

      <p class="auth-link">
        ¿No tienes cuenta?
        <router-link to="/registro">Regístrate aquí</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore.js'
import { login } from '../../api/authApi.js'

const router = useRouter()
const auth = useAuthStore()

const form = ref({ correo: '', password: '' })
const error = ref('')
const cargando = ref(false)

async function handleLogin() {
  error.value = ''
  cargando.value = true
  try {
    const { data } = await login(form.value)
    auth.guardarSesion(data)
    if (data.rol === 'MEDICO') {
      router.push('/medico/dashboard')
    } else {
      router.push('/paciente/inicio')
    }
  } catch (e) {
    error.value = e.response?.data?.mensaje || 'Correo o contraseña incorrectos'
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
}

.auth-card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 420px;
}

@media (max-width: 480px) {
  .auth-container { padding: 16px; align-items: flex-start; padding-top: 40px; }
  .auth-card { padding: 24px; border-radius: 12px; }
}

.auth-logo {
  text-align: center;
  margin-bottom: 32px;
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
  font-size: 22px;
  font-weight: 500;
  margin-bottom: 4px;
}

.auth-logo p {
  font-size: 13px;
  color: #888;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 12px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  padding: 10px 12px;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: border 0.15s;
}

.form-group input:focus {
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
</style>