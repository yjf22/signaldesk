import { computed, ref } from 'vue';
import { defineStore } from 'pinia';
import type {
  FetchTaskResponse,
  SourceCreateRequest,
  SourceDetailResponse,
  SourceFilter,
  SourceResponse,
  SourceUpdateRequest,
} from '@/types/source';
import { sourceApi } from '@/api/sources';

export const useSourceStore = defineStore('sources', () => {
  const sources = ref<SourceResponse[]>([]);
  const currentSource = ref<SourceDetailResponse | null>(null);
  const filter = ref<SourceFilter>({ status: '', sort: 'recent' });
  const loading = ref(false);
  const error = ref<string | null>(null);
  const pagination = ref({ page: 1, size: 12, total: 0 });

  const filteredSources = computed(() => sources.value);

  async function fetchSources() {
    loading.value = true;
    error.value = null;
    try {
      const result = await sourceApi.list({
        status: filter.value.status || undefined,
        type: filter.value.type || undefined,
        tagId: filter.value.tagId,
        sort: filter.value.sort,
        page: pagination.value.page,
        size: pagination.value.size,
      });
      sources.value = result.content;
      pagination.value.total = result.totalElements;
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '加载信息源失败';
    } finally {
      loading.value = false;
    }
  }

  async function fetchSourceById(id: number) {
    loading.value = true;
    error.value = null;
    try {
      currentSource.value = await sourceApi.getById(id);
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '加载信息源详情失败';
    } finally {
      loading.value = false;
    }
  }

  async function createSource(data: SourceCreateRequest): Promise<SourceResponse> {
    const result = await sourceApi.create(data);
    await fetchSources();
    return result;
  }

  async function updateSource(id: number, data: SourceUpdateRequest): Promise<SourceResponse> {
    const result = await sourceApi.update(id, data);
    currentSource.value = result as unknown as SourceDetailResponse;
    return result;
  }

  async function deleteSource(id: number) {
    await sourceApi.delete(id);
    await fetchSources();
  }

  async function deleteSources(ids: number[]) {
    await Promise.all(ids.map((id) => sourceApi.delete(id)));
    await fetchSources();
  }

  async function triggerFetch(id: number): Promise<FetchTaskResponse> {
    return sourceApi.triggerFetch(id);
  }

  async function updateStatus(id: number, status: string) {
    await sourceApi.updateStatus(id, status as 'ACTIVE' | 'PAUSED' | 'ARCHIVED');
    await fetchSources();
  }

  async function updateStatuses(ids: number[], status: string) {
    await Promise.all(
      ids.map((id) => sourceApi.updateStatus(id, status as 'ACTIVE' | 'PAUSED' | 'ARCHIVED')),
    );
    await fetchSources();
  }

  function setFilter(newFilter: Partial<SourceFilter>) {
    filter.value = { ...filter.value, ...newFilter };
    pagination.value.page = 1;
  }

  function setPage(page: number) {
    pagination.value.page = page;
  }

  return {
    sources,
    currentSource,
    filter,
    loading,
    error,
    pagination,
    filteredSources,
    fetchSources,
    fetchSourceById,
    createSource,
    updateSource,
    deleteSource,
    deleteSources,
    triggerFetch,
    updateStatus,
    updateStatuses,
    setFilter,
    setPage,
  };
});
