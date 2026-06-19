<script setup lang="ts">
defineProps<{
  open: boolean;
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
}>();

defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();
</script>

<template>
  <Teleport to="body">
    <Transition name="page-fade">
      <div
        v-if="open"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
        @click.self="$emit('cancel')"
      >
        <div class="bg-white rounded-lg shadow-xl p-6 max-w-sm mx-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ title }}</h3>
          <p class="text-sm text-gray-600 mb-6">{{ message }}</p>
          <div class="flex justify-end gap-3">
            <button type="button" class="btn-secondary" @click="$emit('cancel')">
              {{ cancelLabel ?? '取消' }}
            </button>
            <button
              type="button"
              :class="danger ? 'btn-danger' : 'btn-primary'"
              @click="$emit('confirm')"
            >
              {{ confirmLabel ?? '确认' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
