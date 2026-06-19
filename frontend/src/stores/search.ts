import { defineStore } from 'pinia';
import { ref, watch } from 'vue';
import type { SearchResultResponse, SearchFilter } from '@/types/search';
import { searchApi } from '@/api/search';

export const useSearchStore = defineStore('search', () => {
  const query = ref('');
  const results = ref<SearchResultResponse[]>([]);
  const filter = ref<SearchFilter>({});
  const loading = ref(false);
  const error = ref<string | null>(null);
  const total = ref(0);

  let debounceTimer: ReturnType<typeof setTimeout>;

  watch(query, () => {
    clearTimeout(debounceTimer);
    if (query.value.trim()) {
      debounceTimer = setTimeout(() => search(), 300);
    } else {
      results.value = [];
      total.value = 0;
      error.value = null;
    }
  });

  async function search() {
    if (!query.value.trim()) return;
    loading.value = true;
    error.value = null;
    try {
      const result = await searchApi.search({
        keyword: query.value,
        sourceTypes: filter.value.sourceTypes,
        tagIds: filter.value.tagIds,
        dateFrom: filter.value.dateFrom,
        dateTo: filter.value.dateTo,
        page: 0,
        size: 20,
      });
      results.value = result;
      total.value = result.length;
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'ËÑË÷Ê§°Ü';
    } finally {
      loading.value = false;
    }
  }

  function setFilter(newFilter: Partial<SearchFilter>) {
    filter.value = { ...filter.value, ...newFilter };
    if (query.value.trim()) {
      void search();
    }
  }

  return {
    query,
    results,
    filter,
    loading,
    error,
    total,
    search,
    setFilter,
  };
});
