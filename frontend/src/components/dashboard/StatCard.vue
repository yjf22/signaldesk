<script setup lang="ts">
const props = withDefaults(defineProps<{
  label: string;
  value: string | number;
  icon?: string;
  trend?: string;
  clickable?: boolean;
  hint?: string;
}>(), {
  clickable: false,
  hint: '',
});

const emit = defineEmits<{
  (e: 'click'): void;
}>();

function handleClick() {
  if (!props.clickable) return;
  emit('click');
}
</script>

<template>
  <button
    type="button"
    class="card flex flex-col p-5 text-left transition"
    :class="clickable ? 'card-hover cursor-pointer hover:ring-2 hover:ring-primary-200' : 'cursor-default'"
    @click="handleClick"
  >
    <div class="mb-3 flex items-center justify-between">
      <span class="text-xs font-medium uppercase tracking-wide text-gray-500">{{ label }}</span>
      <span v-if="icon" class="text-lg">{{ icon }}</span>
    </div>
    <p class="mb-1 text-2xl font-bold text-gray-900">{{ value }}</p>
    <p v-if="trend" class="text-xs text-gray-500">{{ trend }}</p>
    <p v-if="clickable && hint" class="mt-3 text-xs text-primary-600">{{ hint }}</p>
  </button>
</template>
