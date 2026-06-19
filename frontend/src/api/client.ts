const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}

export class ApiClientError extends Error {
  constructor(
    public code: number,
    message: string,
    public details?: Record<string, unknown>,
  ) {
    super(message);
    this.name = 'ApiClientError';
  }
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<T> {
  const token = localStorage.getItem('signaldesk_token');

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  const json: ApiResponse<T> = await response.json();

  if (json.code !== 0) {
    if (response.status === 401) {
      localStorage.removeItem('signaldesk_token');
      localStorage.removeItem('signaldesk_user');
      window.location.href = '/login';
    }
    throw new ApiClientError(json.code, json.message);
  }

  return json.data;
}

export const apiClient = {
  get: <T>(url: string) => request<T>(url),

  post: <T>(url: string, body?: unknown) =>
    request<T>(url, { method: 'POST', body: body ? JSON.stringify(body) : undefined }),

  put: <T>(url: string, body?: unknown) =>
    request<T>(url, { method: 'PUT', body: body ? JSON.stringify(body) : undefined }),

  patch: <T>(url: string, body?: unknown) =>
    request<T>(url, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }),

  delete: <T>(url: string) => request<T>(url, { method: 'DELETE' }),

  getPage: async <T>(url: string, params?: Record<string, string | number | undefined>): Promise<{
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  }> => {
    let fullUrl = url;
    if (params) {
      const searchParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined) searchParams.set(key, String(value));
      });
      const qs = searchParams.toString();
      if (qs) fullUrl += `?${qs}`;
    }
    return request(fullUrl);
  },
};
