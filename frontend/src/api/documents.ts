import { apiClient } from './client';
import type { DocumentResponse, DocumentVersionResponse } from '@/types/document';

export const documentApi = {
  getBySource: (sourceId: number, params?: { page?: number; size?: number }) =>
    apiClient.getPage<DocumentResponse>(`/sources/${sourceId}/documents`, params as Record<string, string | number | undefined>),

  getById: (id: number) =>
    apiClient.get<DocumentResponse>(`/documents/${id}`),

  getVersions: (documentId: number) =>
    apiClient.get<DocumentVersionResponse[]>(`/documents/${documentId}/versions`),
};
