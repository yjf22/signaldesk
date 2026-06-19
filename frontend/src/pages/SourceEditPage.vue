<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSourceStore } from '@/stores/sources';
import SourceForm from '@/components/source/SourceForm.vue';
import ConfirmDialog from '@/components/shared/ConfirmDialog.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import type { SourceType } from '@/types/common';

const router = useRouter();
const route = useRoute();
const store = useSourceStore();

const sourceId = computed(() => {
  const id = route.params.id;
  return id ? Number(id) : null;
});

const isEditing = computed(() => !!sourceId.value);
const saving = ref(false);
const saveError = ref('');
const showDeleteConfirm = ref(false);
const deleting = ref(false);

onMounted(async () => {
  if (sourceId.value) {
    await store.fetchSourceById(sourceId.value);
  }
});

async function handleSubmit(data: {
  title: string;
  url: string;
  sourceType: SourceType;
  description: string;
  fetchIntervalMin: number;
  tagIds: number[];
}) {
  saving.value = true;
  saveError.value = '';
  try {
    if (isEditing.value && sourceId.value) {
      await store.updateSource(sourceId.value, {
        title: data.title,
        url: data.url,
        sourceType: data.sourceType,
        description: data.description,
        fetchIntervalMin: data.fetchIntervalMin,
        tagIds: data.tagIds,
      });
      router.push(`/sources/${sourceId.value}`);
    } else {
      const created = await store.createSource({
        title: data.title,
        url: data.url || undefined,
        sourceType: data.sourceType,
        description: data.description || undefined,
        fetchIntervalMin: data.fetchIntervalMin,
        tagIds: data.tagIds.length ? data.tagIds : undefined,
      });
      router.push(`/sources/${created.id}`);
    }
  } catch (e: unknown) {
    saveError.value = e instanceof Error ? e.message : '保存失败';
  } finally {
    saving.value = false;
  }
}

async function handleDelete() {
  if (!sourceId.value) return;
  deleting.value = true;
  try {
    await store.deleteSource(sourceId.value);
    router.push('/sources');
  } catch (e: unknown) {
    saveError.value = e instanceof Error ? e.message : '删除失败';
  } finally {
    deleting.value = false;
    showDeleteConfirm.value = false;
  }
}

function handleCancel() {
  if (isEditing.value && sourceId.value) {
    router.push(`/sources/${sourceId.value}`);
  } else {
    router.push('/sources');
  }
}
</script>

<template>
  <div>
    <!-- Header -->
    <div class="mb-6">
      <button class="text-sm text-gray-500 hover:text-gray-700 mb-2 flex items-center gap-1" @click="handleCancel">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回
      </button>
      <h1 class="text-2xl font-bold text-gray-900">
        {{ isEditing ? '编辑信息源' : '添加信息源' }}
      </h1>
    </div>

    <!-- Loading initial data -->
    <div v-if="isEditing && store.loading && !store.currentSource" class="card p-8 text-center">
      <p class="text-sm text-gray-500">加载中...</p>
    </div>

    <!-- Error loading -->
    <ErrorState
      v-else-if="isEditing && store.error && !store.currentSource"
      :message="store.error"
      @retry="sourceId && store.fetchSourceById(sourceId)"
    />

    <!-- Form -->
    <SourceForm
      v-else
      :initial="isEditing ? store.currentSource : null"
      @submit="handleSubmit"
      @cancel="handleCancel"
    />

    <!-- Save error -->
    <div v-if="saveError" class="mt-4 text-sm text-danger-500 bg-danger-50 rounded-md px-3 py-2 max-w-2xl">
      {{ saveError }}
    </div>

    <!-- Danger zone (edit mode only) -->
    <div v-if="isEditing" class="mt-8 pt-6 border-t border-gray-200 max-w-2xl">
      <h3 class="text-sm font-semibold text-danger-500 mb-2">危险操作</h3>
      <p class="text-xs text-gray-500 mb-3">删除信息源将同时删除所有关联的文档和摘要，此操作不可撤销。</p>
      <button
        class="btn-danger btn-sm"
        :disabled="deleting"
        @click="showDeleteConfirm = true"
      >
        {{ deleting ? '删除中...' : '删除信息源' }}
      </button>
    </div>

    <!-- Confirm dialog -->
    <ConfirmDialog
      :open="showDeleteConfirm"
      title="确认删除"
      message="确定要删除这个信息源吗？所有相关数据将被永久删除。"
      confirm-label="删除"
      danger
      @confirm="handleDelete"
      @cancel="showDeleteConfirm = false"
    />
  </div>
</template>
