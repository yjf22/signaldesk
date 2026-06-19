<script setup lang="ts">
import { watch } from 'vue';
import SummaryStatusIndicator from './SummaryStatusIndicator.vue';
import SummaryContent from './SummaryContent.vue';
import ReferenceList from './ReferenceList.vue';
import type { SummaryResponse } from '@/types/summary';

const props = defineProps<{
  open: boolean;
  summary: SummaryResponse | null;
  loading: boolean;
  error: string | null;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'retry'): void;
}>();

watch(() => props.open, (val) => {
  if (val) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});
</script>

<template>
  <Teleport to="body">
    <Transition name="page-fade">
      <div
        v-if="open"
        class="fixed inset-0 z-40 bg-black/30"
        @click.self="$emit('close')"
      />
    </Transition>
    <Transition name="slide-right">
      <div
        v-if="open"
        class="fixed right-0 top-0 h-full w-full max-w-lg bg-white shadow-xl z-50 flex flex-col"
      >
        <!-- Header -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">
            {{ summary?.title || 'AI 摘要' }}
          </h3>
          <button class="btn-ghost btn-sm p-1" @click="$emit('close')">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto px-6 py-4">
          <!-- Status indicator -->
          <SummaryStatusIndicator
            v-if="summary"
            :status="summary.status"
          />

          <!-- Loading -->
          <div v-if="loading && (!summary || summary.status !== 'COMPLETED')" class="text-center py-8">
            <p class="text-sm text-gray-500">请稍候...</p>
          </div>

          <!-- Error -->
          <div v-else-if="error" class="text-center py-8">
            <p class="text-sm text-danger-500 mb-4">{{ error }}</p>
            <button class="btn-primary btn-sm" @click="$emit('retry')">重新生成</button>
          </div>

          <!-- Completed -->
          <template v-else-if="summary?.status === 'COMPLETED'">
            <SummaryContent :content="summary.contentMd" />
            <ReferenceList :references="summary.references" />

            <!-- Meta info -->
            <div class="mt-6 pt-4 border-t border-gray-100 flex flex-wrap gap-x-4 gap-y-1 text-xs text-gray-400">
              <span>模型: {{ summary.modelName }}</span>
              <span v-if="summary.promptTokens">Prompt: {{ summary.promptTokens }} tokens</span>
              <span v-if="summary.completionTokens">输出: {{ summary.completionTokens }} tokens</span>
              <span v-if="summary.durationSec">耗时: {{ summary.durationSec }}s</span>
            </div>
          </template>

          <!-- Failed -->
          <div v-else-if="summary?.status === 'FAILED'" class="text-center py-8">
            <p class="text-sm text-danger-500 mb-2">{{ summary.errorMessage || '摘要生成失败' }}</p>
            <button class="btn-primary btn-sm" @click="$emit('retry')">重新生成</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
