export type SourceType = 'URL' | 'RSS' | 'PDF' | 'NOTE';
export type SourceStatus = 'ACTIVE' | 'PAUSED' | 'ARCHIVED';
export type SummaryStatus = 'PENDING' | 'GATHERING' | 'PROMPTING' | 'COMPLETED' | 'FAILED';
export type FetchTriggerType = 'MANUAL' | 'SCHEDULED';
export type ChangeType = 'NEW' | 'UPDATED' | 'UNCHANGED' | 'ERROR';
export type FetchStatus = 'PENDING' | 'FETCHING' | 'PARSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiError {
  code: number;
  message: string;
  details?: Record<string, string>;
}
