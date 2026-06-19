import { apiClient } from './client';

export interface DashboardStats {
  todayNewDocuments: number;
  activeSourcesCount: number;
  recentFetchCount: number;
  pendingTaskCount: number;
}

export interface RecentChange {
  documentId: number;
  sourceId: number;
  sourceTitle: string;
  changeType: 'NEW' | 'UPDATED' | 'ERROR';
  snippet: string | null;
  changedAt: string | null;
}

export const dashboardApi = {
  getStats: () => apiClient.get<DashboardStats>('/dashboard/stats'),

  getRecentChanges: (limit?: number) =>
    apiClient.get<RecentChange[]>(`/dashboard/recent-changes${limit ? `?limit=${limit}` : ''}`),
};
