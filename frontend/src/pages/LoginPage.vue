<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import AuthCard from '@/components/auth/AuthCard.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const errorMsg = ref('');

async function handleLogin() {
  errorMsg.value = '';
  try {
    await authStore.login(username.value, password.value);
    const redirect = (route.query.redirect as string) || '/dashboard';
    router.push(redirect);
  } catch (e: unknown) {
    errorMsg.value = e instanceof Error ? e.message : '登录失败，请检查用户名和密码';
  }
}
</script>

<template>
  <AuthCard title="登录" subtitle="登录你的 SignalDesk 工作空间">
    <form @submit.prevent="handleLogin" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
        <input
          v-model="username"
          type="text"
          class="input-base"
          placeholder="请输入用户名"
          required
          autocomplete="username"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
        <input
          v-model="password"
          type="password"
          class="input-base"
          placeholder="请输入密码"
          required
          autocomplete="current-password"
        />
      </div>

      <div v-if="errorMsg" class="text-sm text-danger-500 bg-danger-50 rounded-md px-3 py-2">
        {{ errorMsg }}
      </div>

      <button type="submit" class="btn-primary w-full" :disabled="authStore.loading">
        {{ authStore.loading ? '登录中...' : '登录' }}
      </button>
    </form>

    <p class="mt-4 text-center text-sm text-gray-500">
      还没有账号？
      <router-link to="/register" class="link">立即注册</router-link>
    </p>
  </AuthCard>
</template>
