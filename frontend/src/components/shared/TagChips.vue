<script setup lang="ts">
interface Tag {
  id: number;
  name: string;
  color?: string | null;
}

defineProps<{
  tags: Tag[];
  onRemove?: (tagId: number) => void;
  onClick?: (tag: Tag) => void;
}>();

defineEmits<{
  (e: 'remove', tagId: number): void;
  (e: 'click', tag: Tag): void;
}>();
</script>

<template>
  <div v-if="tags.length" class="flex flex-wrap gap-1.5">
    <span
      v-for="tag in tags"
      :key="tag.id"
      class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium cursor-default"
      :class="{ 'cursor-pointer hover:ring-1 hover:ring-primary-300': !!onClick }"
      :style="{
        backgroundColor: tag.color ? `${tag.color}20` : '#F3F4F6',
        color: tag.color ?? '#6B7280',
      }"
      @click="onClick?.(tag)"
    >
      {{ tag.name }}
      <button
        v-if="onRemove"
        type="button"
        class="ml-0.5 hover:opacity-70"
        @click.stop="$emit('remove', tag.id)"
        :aria-label="`移除标签 ${tag.name}`"
      >
        &times;
      </button>
    </span>
  </div>
</template>
