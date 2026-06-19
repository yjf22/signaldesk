<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useDashboardStore } from '@/stores/dashboard';
import { useSourceStore } from '@/stores/sources';
import StatCard from '@/components/dashboard/StatCard.vue';
import ChangeCard from '@/components/dashboard/ChangeCard.vue';
import EmptyState from '@/components/shared/EmptyState.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton.vue';

const router = useRouter();
const dashboardStore = useDashboardStore();
const sourceStore = useSourceStore();

const today = computed(() => {
  const d = new Date();
  const hours = d.getHours();
  const greeting = hours < 12 ? '早上好' : hours < 18 ? '下午好' : '晚上好';
  return `${greeting}，${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
});

onMounted(() => {
  dashboardStore.refreshAll();
});

const hasNoSources = computed(
  () => !dashboardStore.loading && dashboardStore.stats && dashboardStore.stats.activeSourcesCount === 0,
);

function goToSources(status?: '' | 'ACTIVE' | 'PAUSED' | 'ARCHIVED') {
  sourceStore.setFilter({ status: status ?? '' });
  router.push('/sources');
}

function goToSourceListRecent() {
  sourceStore.setFilter({ status: '', sort: 'recent' });
  router.push('/sources');
}
</script>

<template>
  <div>
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">仪表盘</h1>
      <p class="mt-1 text-sm text-gray-500">{{ today }}</p>
    </div>

    <template v-if="dashboardStore.loading && !dashboardStore.stats">
      <div class="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div v-for="i in 4" :key="i" class="card space-y-3 p-5">
          <div class="skeleton h-3 w-16" />
          <div class="skeleton h-8 w-20" />
          <div class="skeleton h-3 w-24" />
        </div>
      </div>
      <LoadingSkeleton :count="5" />
    </template>

    <ErrorState
      v-else-if="dashboardStore.error && !dashboardStore.stats"
      :message="dashboardStore.error"
      @retry="dashboardStore.refreshAll()"
    />

    <EmptyState
      v-else-if="hasNoSources"
      icon="👋"
      title="欢迎使用 SignalDesk"
      description="添加你的第一个信息源，开始追踪网页、RSS 或笔记内容变化。"
      action-label="添加信息源"
      @action="router.push('/sources/new')"
    />

    <template v-else-if="dashboardStore.stats">
      <div
        v-if="dashboardStore.stale"
        class="mb-4 inline-flex items-center gap-2 rounded-md bg-gray-50 px-3 py-2 text-xs text-gray-400"
      >
        <span>部分数据可能不是最新的</span>
        <button class="text-primary-600 hover:underline" @click="dashboardStore.refreshAll()">
          刷新
        </button>
      </div>

      <div class="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          label="今日新增"
          :value="dashboardStore.stats.todayNewDocuments"
          icon="📄"
          trend="今天新抓取入库的文档数"
          clickable
          hint="点击查看最近信息源"
          @click="goToSourceListRecent"
        />
        <StatCard
          label="活跃信息源"
          :value="dashboardStore.stats.activeSourcesCount"
          icon="📡"
          trend="当前处于启用状态的信息源"
          clickable
          hint="点击查看活跃信息源"
          @click="goToSources('ACTIVE')"
        />
        <StatCard
          label="最近抓取"
          :value="dashboardStore.stats.recentFetchCount"
          icon="⏱"
          trend="近期完成的抓取任务数"
          clickable
          hint="点击查看信息源列表"
          @click="goToSourceListRecent"
        />
        <StatCard
          label="待处理"
          :value="dashboardStore.stats.pendingTaskCount"
          icon="⏳"
          trend="等待或进行中的抓取任务"
          clickable
          hint="点击查看活跃信息源"
          @click="goToSources('ACTIVE')"
        />
      </div>

      <section>
        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-lg font-semibold text-gray-900">最近变化</h2>
          <button class="text-sm text-primary-600 hover:underline" @click="goToSourceListRecent">
            查看全部信息源
          </button>
        </div>

        <div v-if="dashboardStore.recentChanges.length" class="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <ChangeCard
            v-for="change in dashboardStore.recentChanges"
            :key="change.documentId"
            :change="change"
          />
        </div>
        <p v-else class="py-8 text-center text-sm text-gray-500">
          暂无最近变化记录
        </p>
      </section>
    </template>
  </div>
</template>
