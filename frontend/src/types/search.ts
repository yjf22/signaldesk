import type { SourceType } from './common';

export interface SearchRequest {
  keyword: string;
  sourceIds?: number[];
  tagIds?: number[];
  sourceTypes?: SourceType[];
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
}

export interface SearchResultResponse {
  id: number;
  title: string;
  sourceTitle: string;
  sourceId: number;
  sourceType: SourceType;
  tags: string[];
  snippet: string;
  fetchedAt: string;
}

export interface SearchFilter {
  sourceTypes?: SourceType[];
  tagIds?: number[];
  dateFrom?: string;
  dateTo?: string;
}
