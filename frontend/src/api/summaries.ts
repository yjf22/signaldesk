import { apiClient } from './client';
import type { SummaryResponse, SummaryGenerateRequest } from '@/types/summary';

export const summaryApi = {
  generate: (data: SummaryGenerateRequest) =>
    apiClient.post<SummaryResponse>('/summaries', data),

  getStatus: (id: number) =>
    apiClient.get<SummaryResponse>(`/summaries/${id}`),

  getHistory: (params?: { page?: number; size?: number }) =>
    apiClient.getPage<SummaryResponse>('/summaries', params as Record<string, string | number | undefined>),

  retry: (id: number) =>
    apiClient.post<SummaryResponse>(`/summaries/${id}/retry`),

  delete: (id: number) =>
    apiClient.delete<void>(`/summaries/${id}`),
};
