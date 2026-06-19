import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { DashboardStats, RecentChange } from '@/api/dashboard';
import { dashboardApi } from '@/api/dashboard';

export const useDashboardStore = defineStore('dashboard', () => {
  const stats = ref<DashboardStats | null>(null);
  const recentChanges = ref<RecentChange[]>([]);
  const loading = ref(false);
  const stale = ref(false);
  const error = ref<string | null>(null);

  async function fetchStats() {
    loading.value = true;
    error.value = null;
    try {
      const result = await dashboardApi.getStats();
      stats.value = result;
      stale.value = false;
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '加载统计数据失败';
    } finally {
      loading.value = false;
    }
  }

  async function fetchRecentChanges() {
    try {
      const result = await dashboardApi.getRecentChanges(10);
      recentChanges.value = result;
    } catch {
      stale.value = true;
    }
  }

  async function refreshAll() {
    await Promise.all([fetchStats(), fetchRecentChanges()]);
  }

  return {
    stats,
    recentChanges,
    loading,
    stale,
    error,
    fetchStats,
    fetchRecentChanges,
    refreshAll,
  };
});
