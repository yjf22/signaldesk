import { apiClient } from './client';
import type { TagResponse } from '@/types/source';

export const tagApi = {
  list: () =>
    apiClient.get<TagResponse[]>('/tags'),

  create: (name: string, color?: string) =>
    apiClient.post<TagResponse>('/tags', { name, color }),

  delete: (id: number) =>
    apiClient.delete<void>(`/tags/${id}`),
};
