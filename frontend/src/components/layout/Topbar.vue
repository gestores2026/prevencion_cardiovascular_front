<template>
  <header class="topbar">
    <button class="hamburger" @click="ui.toggleSidebar()" aria-label="Abrir menú">
      <span></span>
      <span></span>
      <span></span>
    </button>
    <div class="topbar-content">
      <div class="topbar-title">{{ titulo }}</div>
      <div class="topbar-sub">{{ fecha }}</div>
    </div>
    <slot />
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useUiStore } from '../../stores/uiStore.js'

defineProps({
  titulo: { type: String, default: '' }
})

const ui = useUiStore()

const fecha = computed(() => {
  return new Date().toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})
</script>

<style scoped>
.topbar {
  height: 60px;
  background: #fff;
  border-bottom: 0.5px solid #e0e0dc;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-content {
  flex: 1;
}

.topbar-title {
  font-size: 15px;
  font-weight: 500;
}

.topbar-sub {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
  text-transform: capitalize;
}

.hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  flex-shrink: 0;
  transition: background 0.12s;
}

.hamburger:hover {
  background: #f5f5f3;
}

.hamburger span {
  display: block;
  width: 20px;
  height: 2px;
  background: #444;
  border-radius: 2px;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
  }

  .topbar-sub {
    display: none;
  }
}
</style>
