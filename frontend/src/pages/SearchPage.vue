<script setup lang="ts">
import { ref } from 'vue';
import { useSearchStore } from '@/stores/search';
import { useSummaryStore } from '@/stores/summary';
import SearchResultCard from '@/components/search/SearchResultCard.vue';
import EmptyState from '@/components/shared/EmptyState.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton.vue';
import SummaryDrawer from '@/components/summary/SummaryDrawer.vue';
import type { SourceType } from '@/types/common';

const searchStore = useSearchStore();
const summaryStore = useSummaryStore();

const showDrawer = ref(false);
const summaryGenerating = ref(false);

const typeOptions: { value: SourceType; label: string }[] = [
  { value: 'URL', label: 'URL' },
  { value: 'RSS', label: 'RSS' },
  { value: 'PDF', label: 'PDF' },
  { value: 'NOTE', label: '笔记' },
];

const hasResults = () => !searchStore.loading && !searchStore.error && searchStore.results.length > 0;
const hasNoResults = () => !searchStore.loading && !searchStore.error && searchStore.query && searchStore.results.length === 0;
const isInitial = () => !searchStore.query && searchStore.results.length === 0;

async function handleGenerateSummary() {
  summaryGenerating.value = true;
  try {
    await summaryStore.generateSummary({
      searchKeyword: searchStore.query,
      searchFilters: searchStore.filter,
    });
    showDrawer.value = true;
  } catch {
    // error handled in store
  } finally {
    summaryGenerating.value = false;
  }
}
</script>

<template>
  <div>
    <div :class="[isInitial() ? 'py-16' : 'mb-6']">
      <h1 class="text-2xl font-bold text-gray-900 mb-4">搜索</h1>
      <div class="relative max-w-2xl">
        <svg class="absolute left-3.5 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          v-model="searchStore.query"
          type="text"
          class="w-full pl-10 pr-4 py-3 text-base bg-white border border-gray-300 rounded-lg
                 placeholder:text-gray-400 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200
                 shadow-sm"
          :class="{ 'text-lg': isInitial() }"
          placeholder="搜索文档内容..."
          autofocus
        />
      </div>

      <div v-if="!isInitial()" class="flex flex-wrap items-center gap-2 mt-3">
        <span class="text-xs text-gray-500">类型:</span>
        <button
          v-for="opt in typeOptions"
          :key="opt.value"
          class="px-2 py-0.5 rounded-full text-xs border transition-colors"
          :class="searchStore.filter.sourceTypes?.includes(opt.value)
            ? 'bg-primary-50 border-primary-300 text-primary-700'
            : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'"
          @click="searchStore.setFilter({
            sourceTypes: searchStore.filter.sourceTypes?.includes(opt.value)
              ? searchStore.filter.sourceTypes.filter(t => t !== opt.value)
              : [...(searchStore.filter.sourceTypes || []), opt.value]
          })"
        >
          {{ opt.label }}
        </button>
      </div>
    </div>

    <LoadingSkeleton v-if="searchStore.loading && !searchStore.results.length" :count="5" />

    <ErrorState
      v-else-if="searchStore.error"
      :message="searchStore.error"
      @retry="searchStore.search()"
    />

    <EmptyState
      v-else-if="isInitial()"
      icon="⌕"
      title="输入关键词开始搜索"
      description="搜索所有已抓取的文档内容。"
    />

    <EmptyState
      v-else-if="hasNoResults()"
      title="未找到匹配结果"
      description="试试其他关键词或调整筛选条件？"
    />

    <template v-else-if="hasResults()">
      <div class="flex items-center justify-between mb-4">
        <p class="text-sm text-gray-500">
          找到 <span class="font-medium text-gray-900">{{ searchStore.total }}</span> 条结果
        </p>
        <button
          class="btn-primary btn-sm"
          :disabled="summaryGenerating"
          @click="handleGenerateSummary"
        >
          ✦ {{ summaryGenerating ? '生成中...' : '对搜索结果生成摘要' }}
        </button>
      </div>

      <div class="space-y-3">
        <SearchResultCard
          v-for="result in searchStore.results"
          :key="result.id"
          :result="result"
          :keyword="searchStore.query"
        />
      </div>
    </template>

    <SummaryDrawer
      :open="showDrawer"
      :summary="summaryStore.currentSummary"
      :loading="summaryStore.loading"
      :error="summaryStore.error"
      @close="showDrawer = false"
      @retry="summaryStore.retry()"
    />
  </div>
</template>
