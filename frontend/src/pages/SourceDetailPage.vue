<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSourceStore } from '@/stores/sources';
import { useSummaryStore } from '@/stores/summary';
import { sourceApi } from '@/api/sources';
import { documentApi } from '@/api/documents';
import { summaryApi } from '@/api/summaries';
import { taskApi } from '@/api/tasks';
import StatusBadge from '@/components/shared/StatusBadge.vue';
import TagChips from '@/components/shared/TagChips.vue';
import SourceMetaBar from '@/components/source/SourceMetaBar.vue';
import DocumentCard from '@/components/source/DocumentCard.vue';
import FetchHistoryItem from '@/components/source/FetchHistoryItem.vue';
import SummaryCard from '@/components/source/SummaryCard.vue';
import EmptyState from '@/components/shared/EmptyState.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton.vue';
import type { DocumentResponse } from '@/types/document';
import type { FetchTaskResponse } from '@/types/source';
import type { SummaryResponse } from '@/types/summary';

const router = useRouter();
const route = useRoute();
const store = useSourceStore();
const summaryStore = useSummaryStore();

const sourceId = computed(() => Number(route.params.id));

const activeTab = ref<'content' | 'history' | 'summaries'>('content');

// Documents
const documents = ref<DocumentResponse[]>([]);
const docsLoading = ref(false);
const docsError = ref('');

// Fetch history
const fetchHistory = ref<FetchTaskResponse[]>([]);
const historyLoading = ref(false);
const historyError = ref('');

// Summaries for source
const sourceSummaries = ref<SummaryResponse[]>([]);
const summariesLoading = ref(false);
const summariesError = ref('');

const fetching = ref(false);
const fetchError = ref('');
const fetchStatusText = ref('');

async function loadDocuments() {
  docsLoading.value = true;
  docsError.value = '';
  try {
    const result = await documentApi.getBySource(sourceId.value, { page: 1, size: 50 });
    documents.value = result.content;
  } catch (e: unknown) {
    docsError.value = e instanceof Error ? e.message : '加载文档失败';
  } finally {
    docsLoading.value = false;
  }
}

async function loadFetchHistory() {
  historyLoading.value = true;
  historyError.value = '';
  try {
    const result = await sourceApi.getFetchHistory(sourceId.value, { page: 1, size: 50 });
    fetchHistory.value = result.content;
  } catch (e: unknown) {
    historyError.value = e instanceof Error ? e.message : '加载抓取历史失败';
  } finally {
    historyLoading.value = false;
  }
}

async function loadSummaries() {
  summariesLoading.value = true;
  summariesError.value = '';
  try {
    const result = await summaryApi.getHistory({ page: 1, size: 50 });
    sourceSummaries.value = result.content.filter(s =>
      s.references?.some(r => r.sourceTitle === store.currentSource?.title)
    );
  } catch (e: unknown) {
    summariesError.value = e instanceof Error ? e.message : '加载摘要失败';
  } finally {
    summariesLoading.value = false;
  }
}

async function refreshSourceData() {
  await Promise.all([
    store.fetchSourceById(sourceId.value),
    loadDocuments(),
    loadFetchHistory(),
  ]);
}

async function pollFetchTask(taskId: number) {
  const maxAttempts = 20;
  for (let attempt = 0; attempt < maxAttempts; attempt += 1) {
    await new Promise((resolve) => setTimeout(resolve, 1500));
    const task = await taskApi.getById(taskId);
    if (task.status === 'COMPLETED') {
      fetchStatusText.value = '抓取完成';
      await refreshSourceData();
      return;
    }
    if (task.status === 'FAILED' || task.status === 'CANCELLED') {
      throw new Error(task.errorMessage || '抓取失败');
    }
    fetchStatusText.value = task.status === 'FETCHING' ? '正在抓取内容...' : '正在解析内容...';
  }

  fetchStatusText.value = '抓取任务仍在后台运行，可稍后刷新查看结果';
  await refreshSourceData();
}

async function handleManualFetch() {
  fetching.value = true;
  fetchError.value = '';
  fetchStatusText.value = '正在创建抓取任务...';
  try {
    const task = await store.triggerFetch(sourceId.value);
    fetchStatusText.value = '任务已创建，等待开始...';
    await pollFetchTask(task.id);
  } catch (e: unknown) {
    fetchError.value = e instanceof Error ? e.message : '抓取失败';
  } finally {
    fetching.value = false;
  }
}

async function handleGenerateSummary() {
  try {
    await summaryStore.generateSummary({ sourceId: sourceId.value });
  } catch {
    // error shown in store
  }
}

function handleTabChange(tab: 'content' | 'history' | 'summaries') {
  activeTab.value = tab;
  if (tab === 'content' && !documents.value.length && !docsLoading.value) loadDocuments();
  if (tab === 'history' && !fetchHistory.value.length && !historyLoading.value) loadFetchHistory();
  if (tab === 'summaries' && !sourceSummaries.value.length && !summariesLoading.value) loadSummaries();
}

onMounted(async () => {
  await store.fetchSourceById(sourceId.value);
  loadDocuments();
});

const displayUrl = computed(() => {
  if (!store.currentSource?.url) return null;
  try {
    const u = new URL(store.currentSource.url);
    return u.hostname + u.pathname;
  } catch {
    return store.currentSource.url;
  }
});

const tabs = [
  { key: 'content' as const, label: '最新内容' },
  { key: 'history' as const, label: '抓取历史' },
  { key: 'summaries' as const, label: '摘要' },
];
</script>

<template>
  <div>
    <!-- Back link -->
    <button
      class="text-sm text-gray-500 hover:text-gray-700 mb-4 flex items-center gap-1"
      @click="router.push('/sources')"
    >
      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      返回信息源列表
    </button>

    <!-- Loading -->
    <div v-if="store.loading && !store.currentSource">
      <div class="skeleton h-8 w-64 mb-4" />
      <div class="skeleton h-4 w-96 mb-6" />
      <LoadingSkeleton :count="3" />
    </div>

    <!-- Error -->
    <ErrorState
      v-else-if="store.error && !store.currentSource"
      :message="store.error"
      @retry="store.fetchSourceById(sourceId)"
    />

    <!-- Detail -->
    <template v-else-if="store.currentSource">
      <!-- Source header -->
      <div class="mb-6">
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-3 mb-2">
              <h1 class="text-2xl font-bold text-gray-900 truncate">
                {{ store.currentSource.title }}
              </h1>
              <StatusBadge :status="store.currentSource.status" />
            </div>
            <a
              v-if="displayUrl"
              :href="store.currentSource.url ?? '#'"
              target="_blank"
              rel="noopener"
              class="text-sm text-primary-600 hover:underline truncate block"
            >
              {{ displayUrl }}
            </a>
            <TagChips
              v-if="store.currentSource.tags?.length"
              :tags="store.currentSource.tags"
              class="mt-2"
            />
          </div>
          <div class="flex items-center gap-2 flex-shrink-0">
            <button class="btn-secondary btn-sm" @click="router.push(`/sources/${sourceId}/edit`)">
              编辑
            </button>
            <button
              class="btn-primary btn-sm"
              :disabled="fetching"
              @click="handleManualFetch"
            >
              {{ fetching ? '抓取中...' : '立即抓取' }}
            </button>
            <button class="btn-ghost btn-sm" @click="handleGenerateSummary">
              ✦ 生成摘要
            </button>
          </div>
        </div>
      </div>

      <!-- Fetch error banner -->
      <div v-if="fetchError" class="mb-4 text-sm text-danger-600 bg-danger-50 rounded-md px-3 py-2">
        {{ fetchError }}
      </div>
      <div v-else-if="fetchStatusText" class="mb-4 rounded-md bg-primary-50 px-3 py-2 text-sm text-primary-700">
        {{ fetchStatusText }}
      </div>

      <!-- Meta bar -->
      <SourceMetaBar
        :created-at="store.currentSource.createdAt"
        :last-fetched-at="store.currentSource.lastFetchedAt"
        :total-documents="store.currentSource.totalDocuments ?? documents.length"
        :fetch-interval-min="store.currentSource.fetchIntervalMin"
      />

      <!-- Tab bar -->
      <div class="flex border-b border-gray-200 mb-4">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="px-4 py-2.5 text-sm font-medium border-b-2 transition-colors"
          :class="activeTab === tab.key
            ? 'border-primary-500 text-primary-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
          @click="handleTabChange(tab.key)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Tab: Latest Content -->
      <div v-if="activeTab === 'content'">
        <LoadingSkeleton v-if="docsLoading && !documents.length" :count="3" />
        <ErrorState v-else-if="docsError" :message="docsError" @retry="loadDocuments" />
        <EmptyState
          v-else-if="!documents.length"
          icon="📄"
          title="尚未抓取内容"
          description="点击「立即抓取」获取最新内容。"
          action-label="立即抓取"
          @action="handleManualFetch"
        />
        <div v-else class="space-y-3">
          <DocumentCard v-for="doc in documents" :key="doc.id" :doc="doc" />
        </div>
      </div>

      <!-- Tab: Fetch History -->
      <div v-if="activeTab === 'history'">
        <LoadingSkeleton v-if="historyLoading && !fetchHistory.length" :count="5" />
        <ErrorState v-else-if="historyError" :message="historyError" @retry="loadFetchHistory" />
        <EmptyState
          v-else-if="!fetchHistory.length"
          title="暂无抓取记录"
        />
        <div v-else class="card p-4">
          <FetchHistoryItem
            v-for="task in fetchHistory"
            :key="task.id"
            :task="task"
          />
        </div>
      </div>

      <!-- Tab: Summaries -->
      <div v-if="activeTab === 'summaries'">
        <LoadingSkeleton v-if="summariesLoading && !sourceSummaries.length" :count="3" />
        <ErrorState v-else-if="summariesError" :message="summariesError" @retry="loadSummaries" />
        <EmptyState
          v-else-if="!sourceSummaries.length"
          title="暂无摘要"
          description="点击「生成摘要」对最新内容进行 AI 总结。"
          action-label="生成摘要"
          @action="handleGenerateSummary"
        />
        <div v-else class="space-y-3">
          <SummaryCard v-for="s in sourceSummaries" :key="s.id" :summary="s" />
        </div>
      </div>
    </template>
  </div>
</template>
