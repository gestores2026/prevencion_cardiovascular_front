<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Mi perfil" />
      <div class="content">

        <div class="card" style="max-width: 520px;">
          <div class="perfil-header">
            <div class="avatar avatar-green">{{ iniciales }}</div>
            <div>
              <div class="perfil-nombre">{{ auth.usuario?.nombre }}</div>
              <div class="perfil-sub">Paciente · {{ auth.usuario?.correo }}</div>
            </div>
            <button class="btn-editar" @click="editando = !editando">
              {{ editando ? 'Cancelar' : 'Editar' }}
            </button>
          </div>

          <div class="divider" />

          <form @submit.prevent="guardar">
            <div class="form-row">
              <div class="form-group">
                <label>Nombre completo</label>
                <input v-model="form.nombre" :disabled="!editando" />
              </div>
              <div class="form-group">
                <label>Documento</label>
                <input v-model="form.documento" :disabled="!editando" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Edad</label>
                <input v-model.number="form.edad" type="number" :disabled="!editando" />
              </div>
              <div class="form-group">
                <label>Sexo</label>
                <select v-model="form.sexo" :disabled="!editando">
                  <option value="M">Masculino</option>
                  <option value="F">Femenino</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Peso (kg)</label>
                <input v-model.number="form.peso" type="number" :disabled="!editando" />
              </div>
              <div class="form-group">
                <label>Talla (m)</label>
                <input v-model.number="form.talla" type="number" step="0.01" :disabled="!editando" />
              </div>
            </div>

            <div v-if="editando">
              <div class="divider" />
              <div class="form-group">
                <label>Nueva contraseña (opcional)</label>
                <input v-model="form.passwordNueva" type="password" placeholder="Mínimo 6 caracteres" />
              </div>
            </div>

            <p v-if="exito" class="exito-msg">Perfil actualizado correctamente</p>
            <p v-if="error" class="error-msg">{{ error }}</p>

            <button v-if="editando" type="submit" class="btn-primary" :disabled="cargando">
              {{ cargando ? 'Guardando...' : 'Guardar cambios' }}
            </button>
          </form>
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
import { verPerfil, actualizarPerfil } from '../../api/usuarioApi.js'

const auth = useAuthStore()
const editando = ref(false)
const cargando = ref(false)
const exito = ref(false)
const error = ref('')

const form = ref({
  nombre: '', documento: '', edad: null,
  sexo: '', peso: null, talla: null, passwordNueva: ''
})

const iniciales = computed(() => {
  return auth.usuario?.nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
})

async function guardar() {
  error.value = ''
  exito.value = false
  cargando.value = true
  try {
    await actualizarPerfil(auth.usuario.id, form.value)
    exito.value = true
    editando.value = false
    form.value.passwordNueva = ''
  } catch (e) {
    error.value = 'Error al actualizar el perfil'
  } finally {
    cargando.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await verPerfil(auth.usuario.id)
    form.value = {
      nombre: data.nombre,
      documento: data.documento,
      edad: data.edad,
      sexo: data.sexo,
      peso: data.peso,
      talla: data.talla,
      passwordNueva: ''
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

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  padding: 24px;
}

.perfil-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 4px;
}

.avatar {
  width: 48px; height: 48px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-green { background: #c0dd97; color: #27500a; }

.perfil-nombre { font-size: 16px; font-weight: 500; }
.perfil-sub { font-size: 12px; color: #999; }

.btn-editar {
  margin-left: auto;
  background: none;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  padding: 6px 14px;
  font-family: inherit;
  font-size: 12px;
  cursor: pointer;
}

.divider { height: 0.5px; background: #e0e0dc; margin: 16px 0; }

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

.form-group input:disabled,
.form-group select:disabled {
  background: #f5f5f3;
  color: #666;
}

.form-group input:focus,
.form-group select:focus { border-color: #e24b4a; }

.exito-msg { font-size: 12px; color: #3b6d11; margin-bottom: 12px; }
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

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .card { max-width: 100% !important; }
}

@media (max-width: 480px) {
  .form-row { grid-template-columns: 1fr; }
}
</style>