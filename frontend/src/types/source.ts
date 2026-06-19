import type { SourceType, SourceStatus } from './common';

export interface TagResponse {
  id: number;
  name: string;
  color: string | null;
}

export interface SourceResponse {
  id: number;
  title: string;
  url: string | null;
  sourceType: SourceType;
  description: string | null;
  status: SourceStatus;
  fetchIntervalMin: number;
  lastFetchedAt: string | null;
  lastChangeAt: string | null;
  nextFetchAt: string | null;
  isPinned: boolean;
  tags: TagResponse[];
  createdAt: string;
}

export interface SourceDetailResponse extends SourceResponse {
  totalDocuments?: number;
  totalFetchTasks?: number;
  lastFetchStatus?: 'SUCCESS' | 'FAILED' | null;
}

export interface SourceCreateRequest {
  title: string;
  url?: string;
  sourceType: SourceType;
  description?: string;
  fetchIntervalMin?: number;
  tagIds?: number[];
}

export interface SourceUpdateRequest {
  title?: string;
  url?: string;
  sourceType?: SourceType;
  description?: string;
  status?: SourceStatus;
  fetchIntervalMin?: number;
  isPinned?: boolean;
  tagIds?: number[];
}

export interface SourceFilter {
  status?: string;
  type?: string;
  tagId?: number;
  sort?: string;
}

export interface FetchTaskResponse {
  id: number;
  sourceId: number;
  sourceTitle?: string;
  status: 'PENDING' | 'FETCHING' | 'PARSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  triggerType: 'MANUAL' | 'SCHEDULED';
  resultMessage?: string | null;
  errorMessage: string | null;
  startedAt: string | null;
  completedAt: string | null;
  createdAt?: string | null;
}
