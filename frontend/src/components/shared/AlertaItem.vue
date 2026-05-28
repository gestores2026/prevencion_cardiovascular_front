<template>
  <div class="alerta-item">
    <div class="alerta-icon" :class="iconClass">
      {{ icono }}
    </div>
    <div class="alerta-text">
      <div class="alerta-title">{{ alerta.descripcion }}</div>
      <div class="alerta-meta">
        {{ alerta.pacienteNombre }} · {{ formatFecha(alerta.fechaAlerta) }}
      </div>
    </div>
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  alerta: { type: Object, required: true }
})

const iconosMap = {
  PRESION_ALTA:       '🫀',
  COLESTEROL_ELEVADO: '💧',
  IMC_PELIGROSO:      '⚖️',
  FUMADOR:            '🚬',
  RIESGO_ALTO:        '⚠️',
}

const icono = computed(() => iconosMap[props.alerta.tipo] || '🔔')

const iconClass = computed(() => {
  const tipo = props.alerta.tipo
  if (tipo === 'PRESION_ALTA' || tipo === 'RIESGO_ALTO') return 'icon-danger'
  return 'icon-warn'
})

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric'
  })
}
</script>

<style scoped>
.alerta-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 0.5px solid #e0e0dc;
}

.alerta-icon {
  width: 30px;
  height: 30px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  flex-shrink: 0;
}

.icon-danger { background: #fcebeb; }
.icon-warn   { background: #faeeda; }

.alerta-text { flex: 1; }

.alerta-title {
  font-size: 12px;
  font-weight: 500;
}

.alerta-meta {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}
</style>