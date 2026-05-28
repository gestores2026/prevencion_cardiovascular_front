<template>
  <span class="pill" :class="clase">{{ texto }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  nivel: { type: String, default: '' },
  valor: { type: Number, default: null }
})

const clase = computed(() => {
  const n = props.nivel?.toUpperCase()
  if (n === 'ALTO')     return 'pill-alto'
  if (n === 'MODERADO') return 'pill-moderado'
  if (n === 'BAJO')     return 'pill-bajo'
  return 'pill-bajo'
})

const texto = computed(() => {
  if (props.valor !== null) return props.valor + '%'
  return props.nivel
})
</script>

<style scoped>
.pill {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 20px;
  white-space: nowrap;
}

.pill-alto     { background: #fcebeb; color: #791f1f; }
.pill-moderado { background: #faeeda; color: #633806; }
.pill-bajo     { background: #eaf3de; color: #27500a; }
</style>