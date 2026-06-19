import { apiClient } from './client';
import type { SearchRequest, SearchResultResponse } from '@/types/search';

export const searchApi = {
  search: (params: SearchRequest) =>
    apiClient.post<{ content: SearchResultResponse[]; page: number; size: number; totalElements: number; totalPages: number }>('/search', params),
};
