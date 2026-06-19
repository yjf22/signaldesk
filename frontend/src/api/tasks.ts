import { apiClient } from './client';
import type { FetchTaskResponse } from '@/types/source';

export const taskApi = {
  getById: (id: number) =>
    apiClient.get<FetchTaskResponse>(`/tasks/${id}`),
};
