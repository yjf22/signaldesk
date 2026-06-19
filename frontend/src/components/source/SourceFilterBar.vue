<script setup lang="ts">
import { ref } from 'vue';
import type { SourceStatus, SourceType } from '@/types/common';

const status = defineModel<string>('status', { default: '' });
const type = defineModel<string>('type', { default: '' });
const sort = defineModel<string>('sort', { default: 'recent' });

const emit = defineEmits<{
  (e: 'change'): void;
}>();

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'ACTIVE', label: '活跃' },
  { value: 'PAUSED', label: '已暂停' },
  { value: 'ARCHIVED', label: '已归档' },
];

const typeOptions = [
  { value: '', label: '全部类型' },
  { value: 'URL', label: 'URL' },
  { value: 'RSS', label: 'RSS' },
  { value: 'PDF', label: 'PDF' },
  { value: 'NOTE', label: '笔记' },
];

const sortOptions = [
  { value: 'recent', label: '最近更新' },
  { value: 'name', label: '名称' },
  { value: 'created', label: '创建时间' },
];

function handleChange() {
  emit('change');
}
</script>

<template>
  <div class="flex flex-wrap items-center gap-3 mb-4">
    <select
      v-model="status"
      class="input-base w-auto text-xs py-1.5"
      @change="handleChange"
    >
      <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
        {{ opt.label }}
      </option>
    </select>

    <select
      v-model="type"
      class="input-base w-auto text-xs py-1.5"
      @change="handleChange"
    >
      <option v-for="opt in typeOptions" :key="opt.value" :value="opt.value">
        {{ opt.label }}
      </option>
    </select>

    <select
      v-model="sort"
      class="input-base w-auto text-xs py-1.5"
      @change="handleChange"
    >
      <option v-for="opt in sortOptions" :key="opt.value" :value="opt.value">
        {{ opt.label }}
      </option>
    </select>
  </div>
</template>
