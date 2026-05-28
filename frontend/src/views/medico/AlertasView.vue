<template>
  <div class="layout">
    <Sidebar />
    <div class="main">
      <Topbar titulo="Alertas médicas" />
      <div class="content">

        <div class="card">
          <div class="card-header">
            <span class="card-title">Alertas activas ({{ alertas.length }})</span>
          </div>

          <div v-if="cargando" class="empty">Cargando...</div>

          <div v-else class="alerta-list">
            <div v-for="a in alertas" :key="a.id" class="alerta-row">
              <AlertaItem :alerta="a">
                <button class="btn-atender" @click="atender(a.id)">
                  Atender
                </button>
              </AlertaItem>
            </div>
            <div v-if="alertas.length === 0" class="empty">
              No hay alertas activas
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Sidebar from '../../components/layout/Sidebar.vue'
import Topbar from '../../components/layout/Topbar.vue'
import AlertaItem from '../../components/shared/AlertaItem.vue'
import { listarAlertas, atenderAlerta } from '../../api/alertaApi.js'

const alertas = ref([])
const cargando = ref(false)

async function cargar() {
  cargando.value = true
  try {
    const { data } = await listarAlertas()
    alertas.value = data
  } catch (e) {
    console.error(e)
  } finally {
    cargando.value = false
  }
}

async function atender(id) {
  try {
    await atenderAlerta(id)
    alertas.value = alertas.value.filter(a => a.id !== id)
  } catch (e) {
    console.error(e)
  }
}

onMounted(cargar)
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.card-title { font-size: 13px; font-weight: 500; }
.alerta-list { display: flex; flex-direction: column; gap: 8px; }
.alerta-row { display: flex; align-items: center; gap: 8px; }

.btn-atender {
  background: none;
  border: 0.5px solid #d0d0cc;
  border-radius: 6px;
  padding: 5px 12px;
  font-family: inherit;
  font-size: 11px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.12s;
}

.btn-atender:hover { background: #f5f5f3; }

.empty {
  text-align: center;
  padding: 32px;
  color: #aaa;
  font-size: 13px;
}

@media (max-width: 768px) {
  .main { margin-left: 0; }
  .content { padding: 16px; }
}
</style>