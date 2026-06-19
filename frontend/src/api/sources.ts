import { apiClient } from './client';
import type { SourceResponse, SourceDetailResponse, SourceCreateRequest, SourceUpdateRequest, FetchTaskResponse } from '@/types/source';
import type { PageResponse } from '@/types/common';

export const sourceApi = {
  list: (params?: {
    status?: string;
    type?: string;
    tagId?: number;
    sort?: string;
    page?: number;
    size?: number;
  }) => apiClient.getPage<SourceResponse>('/sources', params as Record<string, string | number | undefined>),

  getById: (id: number) =>
    apiClient.get<SourceDetailResponse>(`/sources/${id}`),

  create: (data: SourceCreateRequest) =>
    apiClient.post<SourceResponse>('/sources', data),

  update: (id: number, data: SourceUpdateRequest) =>
    apiClient.put<SourceResponse>(`/sources/${id}`, data),

  updateStatus: (id: number, status: 'ACTIVE' | 'PAUSED' | 'ARCHIVED') =>
    apiClient.patch<SourceResponse>(`/sources/${id}/status?status=${status}`),

  delete: (id: number) =>
    apiClient.delete<void>(`/sources/${id}`),

  triggerFetch: (sourceId: number) =>
    apiClient.post<FetchTaskResponse>(`/sources/${sourceId}/fetch`),

  getFetchHistory: (sourceId: number, params?: { page?: number; size?: number }) =>
    apiClient.getPage<FetchTaskResponse>(`/sources/${sourceId}/fetch-history`, params as Record<string, string | number | undefined>),
};
