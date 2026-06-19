import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { SummaryResponse, SummaryGenerateRequest } from '@/types/summary';
import { summaryApi } from '@/api/summaries';

export const useSummaryStore = defineStore('summary', () => {
  const currentSummary = ref<SummaryResponse | null>(null);
  const history = ref<SummaryResponse[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const historyTotal = ref(0);

  let pollTimer: ReturnType<typeof setInterval> | null = null;

  async function generateSummary(params: SummaryGenerateRequest): Promise<number> {
    loading.value = true;
    error.value = null;
    try {
      const result = await summaryApi.generate(params);
      currentSummary.value = result;

      if (result.status === 'PENDING' || result.status === 'GATHERING' || result.status === 'PROMPTING') {
        startPolling(result.id);
      } else {
        loading.value = false;
      }
      return result.id;
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '生成摘要失败';
      loading.value = false;
      throw e;
    }
  }

  function startPolling(id: number) {
    stopPolling();
    pollTimer = setInterval(async () => {
      try {
        const updated = await summaryApi.getStatus(id);
        currentSummary.value = updated;
        if (updated.status === 'COMPLETED' || updated.status === 'FAILED') {
          stopPolling();
          loading.value = false;
        }
      } catch {
        stopPolling();
        loading.value = false;
        error.value = '获取摘要状态失败';
      }
    }, 2000);
  }

  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer);
      pollTimer = null;
    }
  }

  async function fetchSummaryById(id: number) {
    loading.value = true;
    error.value = null;
    try {
      currentSummary.value = await summaryApi.getStatus(id);
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '加载摘要失败';
    } finally {
      loading.value = false;
    }
  }

  async function fetchHistory(page = 1) {
    loading.value = true;
    error.value = null;
    try {
      const result = await summaryApi.getHistory({ page, size: 20 });
      history.value = result.content;
      historyTotal.value = result.totalElements;
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '加载摘要历史失败';
    } finally {
      loading.value = false;
    }
  }

  async function retry() {
    if (!currentSummary.value) return;
    loading.value = true;
    error.value = null;
    try {
      const result = await summaryApi.retry(currentSummary.value.id);
      currentSummary.value = result;
      if (result.status !== 'COMPLETED' && result.status !== 'FAILED') {
        startPolling(result.id);
      } else {
        loading.value = false;
      }
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '重试失败';
      loading.value = false;
    }
  }

  return {
    currentSummary,
    history,
    loading,
    error,
    historyTotal,
    generateSummary,
    fetchSummaryById,
    fetchHistory,
    retry,
    stopPolling,
  };
});
