<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import type { SourceType, SourceStatus } from '@/types/common';
import type { SourceResponse, TagResponse } from '@/types/source';
import { tagApi } from '@/api/tags';

const props = defineProps<{
  initial?: SourceResponse | null;
}>();

const emit = defineEmits<{
  (e: 'submit', data: {
    title: string;
    url: string;
    sourceType: SourceType;
    description: string;
    fetchIntervalMin: number;
    status: SourceStatus;
    tagIds: number[];
  }): void;
  (e: 'cancel'): void;
}>();

const title = ref(props.initial?.title ?? '');
const url = ref(props.initial?.url ?? '');
const sourceType = ref<SourceType>(props.initial?.sourceType ?? 'URL');
const description = ref(props.initial?.description ?? '');
const fetchIntervalMin = ref(props.initial?.fetchIntervalMin ?? 60);
const status = ref<SourceStatus>(props.initial?.status ?? 'ACTIVE');
const selectedTagIds = ref<number[]>([...new Set(props.initial?.tags?.map(t => t.id) ?? [])]);
const allTags = ref<TagResponse[]>([]);
const newTagName = ref('');
const errorMsg = ref('');

const isEditing = !!props.initial;

const typeOptions: { value: SourceType; label: string }[] = [
  { value: 'URL', label: 'URL 网页' },
  { value: 'RSS', label: 'RSS 订阅' },
  { value: 'PDF', label: 'PDF 文件' },
  { value: 'NOTE', label: '笔记' },
];

const intervalOptions = [
  { value: 15, label: '15 分钟' },
  { value: 30, label: '30 分钟' },
  { value: 60, label: '1 小时' },
  { value: 360, label: '6 小时' },
  { value: 720, label: '12 小时' },
  { value: 1440, label: '24 小时' },
];

const needsUrl = () => sourceType.value !== 'NOTE';

onMounted(async () => {
  try {
    allTags.value = await tagApi.list();
  } catch {
    // tags not critical
  }
});

async function addTag() {
  if (!newTagName.value.trim()) return;
  try {
    const tag = await tagApi.create(newTagName.value.trim());
    allTags.value.push(tag);
    selectedTagIds.value.push(tag.id);
    newTagName.value = '';
  } catch (e: unknown) {
    errorMsg.value = e instanceof Error ? e.message : '创建标签失败';
  }
}

function toggleTag(tagId: number) {
  const idx = selectedTagIds.value.indexOf(tagId);
  if (idx >= 0) {
    selectedTagIds.value.splice(idx, 1);
  } else {
    selectedTagIds.value.push(tagId);
  }
}

function handleSubmit() {
  if (!title.value.trim()) {
    errorMsg.value = '标题不能为空';
    return;
  }
  if (needsUrl() && !url.value.trim()) {
    errorMsg.value = 'URL 不能为空';
    return;
  }
  emit('submit', {
    title: title.value.trim(),
    url: url.value.trim() || '',
    sourceType: sourceType.value,
    description: description.value.trim(),
    fetchIntervalMin: fetchIntervalMin.value,
    status: status.value,
    tagIds: [...new Set(selectedTagIds.value)],
  });
}
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-5 max-w-2xl">
    <!-- Title -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">标题 *</label>
      <input v-model="title" type="text" class="input-base" placeholder="例如：美团技术团队" required />
    </div>

    <!-- Type -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
      <div class="flex gap-2 flex-wrap">
        <button
          v-for="opt in typeOptions"
          :key="opt.value"
          type="button"
          class="px-3 py-1.5 rounded-md text-xs font-medium border transition-colors"
          :class="sourceType === opt.value
            ? 'bg-primary-50 border-primary-300 text-primary-700'
            : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'"
          @click="sourceType = opt.value"
        >
          {{ opt.label }}
        </button>
      </div>
    </div>

    <!-- URL (conditional) -->
    <div v-if="needsUrl()">
      <label class="block text-sm font-medium text-gray-700 mb-1">URL *</label>
      <input v-model="url" type="url" class="input-base" placeholder="https://..." required />
    </div>

    <!-- Description -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
      <textarea v-model="description" class="input-base" rows="3" placeholder="简要描述这个信息源..." />
    </div>

    <!-- Tags -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">标签</label>
      <div class="flex flex-wrap gap-1.5 mb-2">
        <button
          v-for="tag in allTags"
          :key="tag.id"
          type="button"
          class="px-2 py-0.5 rounded-full text-xs font-medium border transition-colors"
          :class="selectedTagIds.includes(tag.id)
            ? 'bg-primary-50 border-primary-300 text-primary-700'
            : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'"
          @click="toggleTag(tag.id)"
        >
          {{ tag.name }}
        </button>
      </div>
      <div class="flex gap-2">
        <input
          v-model="newTagName"
          type="text"
          class="input-base flex-1 text-xs"
          placeholder="新建标签..."
          @keydown.enter.prevent="addTag"
        />
        <button type="button" class="btn-secondary btn-sm" @click="addTag">添加</button>
      </div>
    </div>

    <!-- Fetch interval -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">抓取间隔</label>
      <select v-model.number="fetchIntervalMin" class="input-base w-auto text-sm">
        <option v-for="opt in intervalOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>
    </div>

    <!-- Status toggle -->
    <div v-if="isEditing">
      <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
      <div class="flex gap-2">
        <button
          v-for="s in (['ACTIVE', 'PAUSED', 'ARCHIVED'] as const)"
          :key="s"
          type="button"
          class="px-3 py-1.5 rounded-md text-xs font-medium border transition-colors"
          :class="status === s
            ? s === 'ACTIVE' ? 'bg-success-50 border-success-300 text-success-700'
              : s === 'PAUSED' ? 'bg-gray-100 border-gray-300 text-gray-700'
              : 'bg-gray-100 border-gray-300 text-gray-500'
            : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'"
          @click="status = s"
        >
          {{ s === 'ACTIVE' ? '启用' : s === 'PAUSED' ? '暂停' : '归档' }}
        </button>
      </div>
    </div>

    <!-- Error -->
    <div v-if="errorMsg" class="text-sm text-danger-500 bg-danger-50 rounded-md px-3 py-2">
      {{ errorMsg }}
    </div>

    <!-- Actions -->
    <div class="flex items-center gap-3 pt-2">
      <button type="submit" class="btn-primary">
        {{ isEditing ? '保存修改' : '创建信息源' }}
      </button>
      <button type="button" class="btn-secondary" @click="$emit('cancel')">
        取消
      </button>
    </div>
  </form>
</template>
