import { apiClient } from './client';
import type { SearchRequest, SearchResultResponse } from '@/types/search';

function buildSearchUrl(params: SearchRequest) {
  const searchParams = new URLSearchParams();
  searchParams.set('keyword', params.keyword);

  if (params.sourceIds?.length) {
    searchParams.set('sourceId', String(params.sourceIds[0]));
  }
  if (params.sourceTypes?.length) {
    searchParams.set('sourceType', params.sourceTypes[0]);
  }
  if (params.page !== undefined) {
    searchParams.set('page', String(params.page));
  }
  if (params.size !== undefined) {
    searchParams.set('size', String(params.size));
  }

  return `/search?${searchParams.toString()}`;
}

export const searchApi = {
  search: (params: SearchRequest) =>
    apiClient.get<SearchResultResponse[]>(buildSearchUrl(params)),
};
