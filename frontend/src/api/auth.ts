import { apiClient } from './client';
import type { User } from '@/types/auth';

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  user: User;
}

export const authApi = {
  login: (username: string, password: string) =>
    apiClient.post<AuthResponse>('/auth/login', { username, password }),

  register: (username: string, email: string, password: string) =>
    apiClient.post<AuthResponse>('/auth/register', { username, email, password }),

  getProfile: () =>
    apiClient.get<User>('/users/me'),

  refreshToken: (refreshToken: string) =>
    apiClient.post<AuthResponse>('/auth/refresh', { refreshToken }),

  logout: () =>
    apiClient.post<void>('/auth/logout'),
};
