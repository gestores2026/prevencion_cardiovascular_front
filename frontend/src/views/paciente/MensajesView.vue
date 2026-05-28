<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Mensajes" />
      <div class="content">

        <div v-if="!medicoId" class="card">
          <div class="empty">
            <div class="empty-icon">💬</div>
            <div>No tienes un médico asignado aún.</div>
            <div class="empty-sub">Contacta con tu médico para que te asigne.</div>
          </div>
        </div>

        <div v-else class="chat-wrapper">
          <div class="card chat-card">
            <div class="chat-header">
              <div class="avatar avatar-blue">Dr</div>
              <div>
                <div class="chat-nombre">{{ medicoNombre }}</div>
                <div class="chat-sub">Tu médico asignado</div>
              </div>
            </div>

            <div class="chat-messages" ref="chatBox">
              <div v-if="mensajes.length === 0" class="empty">
                Sin mensajes aún. ¡Inicia la conversación!
              </div>
              <div
                v-for="m in mensajes"
                :key="m.id"
                class="msg-row"
                :class="m.emisorId === auth.usuario.id ? 'msg-enviado' : 'msg-recibido'"
              >
                <div class="msg-bubble">{{ m.contenido }}</div>
                <div class="msg-time">{{ formatFecha(m.fechaEnvio) }}</div>
              </div>
            </div>

            <div class="chat-input">
              <input
                v-model="nuevoMensaje"
                placeholder="Escribe un mensaje..."
                @keyup.enter="enviar"
              />
              <button
                class="btn-enviar"
                @click="enviar"
                :disabled="!nuevoMensaje.trim() || enviando"
              >
                {{ enviando ? '...' : 'Enviar' }}
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from '../../stores/authStore.js'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import { verPerfil } from '../../api/usuarioApi.js'
import { enviarMensaje, obtenerConversacion } from '../../api/mensajeApi.js'

const auth = useAuthStore()
const mensajes = ref([])
const nuevoMensaje = ref('')
const medicoId = ref(null)
const medicoNombre = ref('')
const chatBox = ref(null)
const enviando = ref(false)
let intervalo = null

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleTimeString('es-CO', {
    hour: '2-digit', minute: '2-digit'
  })
}

async function cargarMensajes() {
  if (!medicoId.value) return
  try {
    const { data } = await obtenerConversacion(auth.usuario.id, medicoId.value)
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
  if (!nuevoMensaje.value.trim() || enviando.value) return
  enviando.value = true
  try {
    await enviarMensaje(auth.usuario.id, {
      receptorId: medicoId.value,
      contenido: nuevoMensaje.value.trim()
    })
    nuevoMensaje.value = ''
    await cargarMensajes()
  } catch (e) {
    console.error(e)
  } finally {
    enviando.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await verPerfil(auth.usuario.id)
    if (data.medicoId) {
      medicoId.value = data.medicoId
      medicoNombre.value = data.medicoNombre || 'Tu médico'
      await cargarMensajes()
      intervalo = setInterval(cargarMensajes, 5000)
    }
  } catch (e) {
    console.error(e)
  }
})

onUnmounted(() => {
  clearInterval(intervalo)
})
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.main { flex: 1; margin-left: 220px; display: flex; flex-direction: column; }
.content { flex: 1; padding: 24px; }

.chat-wrapper { max-width: 640px; margin: 0 auto; }

.card {
  background: #fff;
  border: 0.5px solid #e0e0dc;
  border-radius: 12px;
  overflow: hidden;
}

.chat-card {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 130px);
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border-bottom: 0.5px solid #e0e0dc;
  background: #fff;
  flex-shrink: 0;
}

.avatar {
  width: 38px; height: 38px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 500;
  flex-shrink: 0;
}

.avatar-blue { background: #b5d4f4; color: #0c447c; }

.chat-nombre { font-size: 14px; font-weight: 500; }
.chat-sub { font-size: 11px; color: #999; }

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background: #f5f5f3;
}

.msg-row {
  display: flex;
  flex-direction: column;
  max-width: 72%;
}

.msg-enviado  { align-self: flex-end;  align-items: flex-end; }
.msg-recibido { align-self: flex-start; align-items: flex-start; }

.msg-bubble {
  padding: 10px 14px;
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
  flex-shrink: 0;
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
  padding: 9px 20px;
  background: #e24b4a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.15s;
}

.btn-enviar:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-enviar:hover:not(:disabled) { opacity: 0.88; }

.empty {
  text-align: center;
  padding: 40px 20px;
  color: #aaa;
  font-size: 13px;
}

.empty-icon { font-size: 32px; margin-bottom: 10px; }
.empty-sub { font-size: 12px; margin-top: 6px; }

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
  .chat-wrapper { max-width: 100%; }
  .chat-card { height: calc(100vh - 110px); }
}
</style>