import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User } from '@/types/auth';
import { authApi } from '@/api/auth';

const TOKEN_KEY = 'signaldesk_token';
const USER_KEY = 'signaldesk_user';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const token = ref<string | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isAuthenticated = computed(() => !!token.value);

  function initAuth() {
    const savedToken = localStorage.getItem(TOKEN_KEY);
    const savedUser = localStorage.getItem(USER_KEY);
    if (savedToken) {
      token.value = savedToken;
      try {
        user.value = savedUser ? JSON.parse(savedUser) : null;
      } catch {
        // corrupted user data
      }
      // verify token is still valid
      fetchProfile().catch(() => {
        logout();
      });
    }
  }

  async function login(username: string, password: string) {
    loading.value = true;
    error.value = null;
    try {
      const result = await authApi.login(username, password);
      token.value = result.accessToken;
      user.value = result.user;
      localStorage.setItem(TOKEN_KEY, result.accessToken);
      localStorage.setItem(USER_KEY, JSON.stringify(result.user));
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '登录失败';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function register(username: string, email: string, password: string) {
    loading.value = true;
    error.value = null;
    try {
      const result = await authApi.register(username, email, password);
      token.value = result.accessToken;
      user.value = result.user;
      localStorage.setItem(TOKEN_KEY, result.accessToken);
      localStorage.setItem(USER_KEY, JSON.stringify(result.user));
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : '注册失败';
      throw e;
    } finally {
      loading.value = false;
    }
  }

  async function fetchProfile() {
    try {
      const profile = await authApi.getProfile();
      user.value = profile;
      localStorage.setItem(USER_KEY, JSON.stringify(profile));
    } catch {
      throw new Error('获取用户信息失败');
    }
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    authApi.logout().catch(() => {});
  }

  return {
    user,
    token,
    loading,
    error,
    isAuthenticated,
    initAuth,
    login,
    register,
    fetchProfile,
    logout,
  };
});
