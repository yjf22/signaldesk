<script setup lang="ts">
import { computed } from 'vue';
import MarkdownIt from 'markdown-it';
import DOMPurify from 'dompurify';

const props = defineProps<{
  content: string;
}>();

const md = new MarkdownIt({ html: false, breaks: true, linkify: true });

const renderedHtml = computed(() => {
  const raw = md.render(props.content);
  return DOMPurify.sanitize(raw);
});
</script>

<template>
  <div
    class="prose-summary"
    v-html="renderedHtml"
  />
</template>
