import type { SummaryStatus } from './common';

export interface SummaryResponse {
  id: number;
  title: string;
  contentMd: string;
  status: SummaryStatus;
  errorMessage: string | null;
  modelName: string;
  promptTokens?: number;
  completionTokens?: number;
  durationSec?: number | null;
  references: SummaryReference[];
  createdAt: string;
}

export interface SummaryReference {
  refIndex: number;
  documentId: number;
  sourceTitle: string;
  quoteText: string;
}

export interface SummaryGenerateRequest {
  title?: string;
  sourceId?: number;
  searchQuery?: string;
}
