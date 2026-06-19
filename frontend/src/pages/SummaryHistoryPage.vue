<script setup lang="ts">
import { onMounted } from 'vue';
import { useSummaryStore } from '@/stores/summary';
import SummaryCard from '@/components/source/SummaryCard.vue';
import EmptyState from '@/components/shared/EmptyState.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton.vue';

const store = useSummaryStore();

onMounted(() => {
  store.fetchHistory();
});
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-gray-900 mb-6">摘要历史</h1>

    <!-- Loading -->
    <LoadingSkeleton v-if="store.loading && !store.history.length" :count="5" />

    <!-- Error -->
    <ErrorState
      v-else-if="store.error && !store.history.length"
      :message="store.error"
      @retry="store.fetchHistory()"
    />

    <!-- Empty -->
    <EmptyState
      v-else-if="!store.history.length"
      icon="✦"
      title="暂无摘要"
      description="在搜索页面或信息源详情页生成摘要。"
    />

    <!-- History list -->
    <div v-else class="space-y-3">
      <SummaryCard
        v-for="s in store.history"
        :key="s.id"
        :summary="s"
      />
    </div>
  </div>
</template>
