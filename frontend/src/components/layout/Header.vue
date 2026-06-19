<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchQuery = ref('');

function handleQuickSearch() {
  if (searchQuery.value.trim()) {
    router.push({ name: 'Search', query: { q: searchQuery.value.trim() } });
  }
}
</script>

<template>
  <header class="h-14 bg-white border-b border-gray-200 flex items-center px-6 gap-4">
    <!-- Quick search bar -->
    <div class="flex-1 max-w-md">
      <form @submit.prevent="handleQuickSearch" class="relative">
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="快速搜索... (Ctrl+K)"
          class="w-full pl-9 pr-3 py-1.5 text-sm bg-gray-50 border border-gray-200 rounded-md
                 placeholder:text-gray-400 focus:bg-white focus:border-primary-400 focus:outline-none focus:ring-1 focus:ring-primary-400"
        />
      </form>
    </div>

    <div class="flex items-center gap-3 ml-auto">
      <span class="text-xs text-gray-400 hidden sm:inline">SignalDesk v1.0</span>
    </div>
  </header>
</template>
