<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import AuthCard from '@/components/auth/AuthCard.vue';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const errorMsg = ref('');

async function handleRegister() {
  errorMsg.value = '';

  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致';
    return;
  }

  if (password.value.length < 6) {
    errorMsg.value = '密码长度至少 6 位';
    return;
  }

  try {
    await authStore.register(username.value, email.value, password.value);
    router.push('/dashboard');
  } catch (e: unknown) {
    errorMsg.value = e instanceof Error ? e.message : '注册失败，请稍后重试';
  }
}
</script>

<template>
  <AuthCard title="注册" subtitle="创建你的 SignalDesk 账号">
    <form @submit.prevent="handleRegister" class="space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
        <input
          v-model="username"
          type="text"
          class="input-base"
          placeholder="你的用户名"
          required
          autocomplete="username"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
        <input
          v-model="email"
          type="email"
          class="input-base"
          placeholder="you@example.com"
          required
          autocomplete="email"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
        <input
          v-model="password"
          type="password"
          class="input-base"
          placeholder="至少 6 位"
          required
          minlength="6"
          autocomplete="new-password"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
        <input
          v-model="confirmPassword"
          type="password"
          class="input-base"
          placeholder="再次输入密码"
          required
          autocomplete="new-password"
        />
      </div>

      <div v-if="errorMsg" class="text-sm text-danger-500 bg-danger-50 rounded-md px-3 py-2">
        {{ errorMsg }}
      </div>

      <button
        type="submit"
        class="btn-primary w-full"
        :disabled="authStore.loading"
      >
        {{ authStore.loading ? '注册中...' : '注册' }}
      </button>
    </form>

    <p class="mt-4 text-center text-sm text-gray-500">
      已有账号？
      <router-link to="/login" class="link">立即登录</router-link>
    </p>
  </AuthCard>
</template>
