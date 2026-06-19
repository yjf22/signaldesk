<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSummaryStore } from '@/stores/summary';
import SummaryStatusIndicator from '@/components/summary/SummaryStatusIndicator.vue';
import SummaryContent from '@/components/summary/SummaryContent.vue';
import ReferenceList from '@/components/summary/ReferenceList.vue';
import ErrorState from '@/components/shared/ErrorState.vue';

const router = useRouter();
const route = useRoute();
const store = useSummaryStore();

const summaryId = computed(() => Number(route.params.id));

onMounted(() => {
  store.fetchSummaryById(summaryId.value);
});
</script>

<template>
  <div>
    <!-- Back link -->
    <button
      class="text-sm text-gray-500 hover:text-gray-700 mb-4 flex items-center gap-1"
      @click="router.push('/summaries')"
    >
      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      返回摘要历史
    </button>

    <!-- Loading -->
    <div v-if="store.loading && !store.currentSummary" class="card p-8 text-center">
      <p class="text-sm text-gray-500">加载中...</p>
    </div>

    <!-- Error -->
    <ErrorState
      v-else-if="store.error && !store.currentSummary"
      :message="store.error"
      @retry="store.fetchSummaryById(summaryId)"
    />

    <!-- Detail -->
    <template v-else-if="store.currentSummary">
      <div class="card p-6">
        <div class="flex items-start justify-between mb-4">
          <h1 class="text-xl font-bold text-gray-900">
            {{ store.currentSummary.title || '未命名摘要' }}
          </h1>
          <button
            v-if="store.currentSummary.status === 'FAILED'"
            class="btn-primary btn-sm"
            @click="store.retry()"
          >
            重新生成
          </button>
        </div>

        <SummaryStatusIndicator :status="store.currentSummary.status" />

        <template v-if="store.currentSummary.status === 'COMPLETED'">
          <SummaryContent :content="store.currentSummary.contentMd" />
          <ReferenceList :references="store.currentSummary.references" />

          <div class="mt-6 pt-4 border-t border-gray-100 flex flex-wrap gap-x-4 gap-y-1 text-xs text-gray-400">
            <span>模型: {{ store.currentSummary.modelName }}</span>
            <span v-if="store.currentSummary.promptTokens">Prompt: {{ store.currentSummary.promptTokens }} tokens</span>
            <span v-if="store.currentSummary.completionTokens">输出: {{ store.currentSummary.completionTokens }} tokens</span>
            <span v-if="store.currentSummary.durationSec">耗时: {{ store.currentSummary.durationSec }}s</span>
          </div>
        </template>

        <div v-else-if="store.currentSummary.status === 'FAILED'" class="text-center py-8">
          <p class="text-sm text-danger-500">{{ store.currentSummary.errorMessage || '摘要生成失败' }}</p>
        </div>
      </div>
    </template>
  </div>
</template>
