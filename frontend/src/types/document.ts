import type { SourceType, ChangeType } from './common';

export interface DocumentResponse {
  id: number;
  sourceId: number;
  sourceTitle?: string;
  sourceType?: SourceType;
  title: string;
  contentPreview?: string;
  contentText?: string;
  wordCount: number;
  contentHash: string;
  isChanged?: boolean;
  changeType?: ChangeType;
  fetchedAt?: string;
  createdAt: string;
  publishedAt?: string | null;
  sourceUrl?: string | null;
  author?: string | null;
  isCurrent?: boolean;
  crawlTaskId?: number | null;
}

export interface DocumentVersionResponse {
  id: number;
  documentId: number;
  versionNumber: number;
  contentPreview: string;
  wordCount: number;
  contentHash: string;
  createdAt: string;
}
