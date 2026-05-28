<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Mensajes" />
      <div class="content">

        <div class="grid-chat">
          <div class="card lista-card">
            <div class="lista-header">Mis pacientes</div>
            <div
              v-for="p in pacientes"
              :key="p.id"
              class="paciente-item"
              :class="{ activo: pacienteActivo?.id === p.id }"
              @click="seleccionar(p)"
            >
              <div class="avatar avatar-green">{{ iniciales(p.nombre) }}</div>
              <div class="paciente-info">
                <div class="paciente-nombre">{{ p.nombre }}</div>
                <div class="paciente-sub">{{ p.edad }} años</div>
              </div>
            </div>
            <div v-if="pacientes.length === 0" class="empty">Sin pacientes</div>
          </div>

          <div class="card chat-card" v-if="pacienteActivo">
            <div class="chat-header">
              <div class="avatar avatar-green">{{ iniciales(pacienteActivo.nombre) }}</div>
              <div>
                <div class="chat-nombre">{{ pacienteActivo.nombre }}</div>
                <div class="chat-sub">{{ pacienteActivo.edad }} años</div>
              </div>
              <button class="btn-quitar" @click="quitar(pacienteActivo.id)">
                Quitar paciente
              </button>
            </div>

            <div class="chat-messages" ref="chatBox">
              <div
                v-for="m in mensajes"
                :key="m.id"
                class="msg-row"
                :class="m.emisorId === auth.usuario.id ? 'msg-enviado' : 'msg-recibido'"
              >
                <div class="msg-bubble">{{ m.contenido }}</div>
                <div class="msg-time">{{ formatFecha(m.fechaEnvio) }}</div>
              </div>
              <div v-if="mensajes.length === 0" class="empty">
                Sin mensajes aún
              </div>
            </div>

            <div class="chat-input">
              <input
                v-model="nuevoMensaje"
                placeholder="Escribe un mensaje..."
                @keyup.enter="enviar"
              />
              <button class="btn-enviar" @click="enviar" :disabled="!nuevoMensaje.trim()">
                Enviar
              </button>
            </div>
          </div>

          <div class="card chat-vacio" v-else>
            <div class="empty">Selecciona un paciente para chatear</div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import { listarMisPacientes } from '../../api/usuarioApi.js'
import { enviarMensaje, obtenerConversacion } from '../../api/mensajeApi.js'
import api from '../../api/axios.js'

const auth = useAuthStore()
const pacientes = ref([])
const pacienteActivo = ref(null)
const mensajes = ref([])
const nuevoMensaje = ref('')
const chatBox = ref(null)
let intervalo = null

function iniciales(nombre) {
  return nombre?.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleTimeString('es-CO', {
    hour: '2-digit', minute: '2-digit'
  })
}

async function seleccionar(paciente) {
  pacienteActivo.value = paciente
  clearInterval(intervalo)
  await cargarMensajes()
  intervalo = setInterval(cargarMensajes, 5000)
}

async function cargarMensajes() {
  if (!pacienteActivo.value) return
  try {
    const { data } = await obtenerConversacion(
      auth.usuario.id,
      pacienteActivo.value.id
    )
    mensajes.value = data
    await nextTick()
    if (chatBox.value) {
      chatBox.value.scrollTop = chatBox.value.scrollHeight
    }
  } catch (e) {
    console.error(e)
  }
}

async function enviar() {
  if (!nuevoMensaje.value.trim()) return
  try {
    await enviarMensaje(auth.usuario.id, {
      receptorId: pacienteActivo.value.id,
      contenido: nuevoMensaje.value.trim()
    })
    nuevoMensaje.value = ''
    await cargarMensajes()
  } catch (e) {
    console.error(e)
  }
}

async function quitar(pacienteId) {
  try {
    await api.put(`/api/medico/quitar-paciente/${pacienteId}`)
    pacienteActivo.value = null
    mensajes.value = []
    clearInterval(intervalo)
    const { data } = await listarMisPacientes(auth.usuario.id)
    pacientes.value = data
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  try {
    const { data } = await listarMisPacientes(auth.usuario.id)
    pacientes.value = data
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.grid-chat {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
  height: calc(100vh - 110px);
}

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  overflow: hidden;
}

.lista-card {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.lista-header {
  padding: 14px 16px;
  font-size: 13px;
  font-weight: 500;
  border-bottom: 0.5px solid #e0e0dc;
}

.paciente-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.12s;
  border-bottom: 0.5px solid #f0f0ee;
}

.paciente-item:hover { background: #f5f5f3; }
.paciente-item.activo { background: #fcebeb; }

.avatar {
  width: 34px; height: 34px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-green { background: #c0dd97; color: #27500a; }
.avatar-blue  { background: #b5d4f4; color: #0c447c; }

.paciente-info { flex: 1; }
.paciente-nombre { font-size: 13px; font-weight: 500; }
.paciente-sub { font-size: 11px; color: #999; }

.chat-card { display: flex; flex-direction: column; }
.chat-vacio { display: flex; align-items: center; justify-content: center; }

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border-bottom: 0.5px solid #e0e0dc;
}

.chat-nombre { font-size: 14px; font-weight: 500; }
.chat-sub { font-size: 11px; color: #999; }

.btn-quitar {
  margin-left: auto;
  background: none;
  border: 0.5px solid #f7c1c1;
  color: #a32d2d;
  border-radius: 6px;
  padding: 5px 12px;
  font-family: inherit;
  font-size: 11px;
  cursor: pointer;
  transition: background 0.12s;
}

.btn-quitar:hover { background: #fcebeb; }

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background: #f5f5f3;
}

.msg-row { display: flex; flex-direction: column; max-width: 70%; }
.msg-enviado { align-self: flex-end; align-items: flex-end; }
.msg-recibido { align-self: flex-start; align-items: flex-start; }

.msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

.msg-enviado .msg-bubble {
  background: #e24b4a;
  color: #fff;
  border-radius: 12px 12px 0 12px;
}

.msg-recibido .msg-bubble {
  background: #fff;
  color: #333;
  border-radius: 12px 12px 12px 0;
  border: 0.5px solid #e0e0dc;
}

.msg-time { font-size: 10px; color: #aaa; margin-top: 3px; }

.chat-input {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 0.5px solid #e0e0dc;
  background: #fff;
}

.chat-input input {
  flex: 1;
  padding: 9px 12px;
  border: 0.5px solid #d0d0cc;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  outline: none;
}

.chat-input input:focus { border-color: #e24b4a; }

.btn-enviar {
  padding: 9px 18px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.btn-enviar:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-enviar:hover:not(:disabled) { opacity: 0.88; }

.empty { text-align: center; padding: 40px; color: #aaa; font-size: 13px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .grid-chat {
    grid-template-columns: 1fr;
    height: auto;
  }
  .lista-card { max-height: 220px; }
  .chat-card { height: calc(100vh - 400px); min-height: 300px; }
}
</style>