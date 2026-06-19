import { ref, computed } from 'vue';

export function usePagination(totalItems: () => number, initialSize = 12) {
  const page = ref(1);
  const size = ref(initialSize);

  const totalPages = computed(() => Math.ceil(totalItems() / size.value));

  const hasNext = computed(() => page.value < totalPages.value);
  const hasPrev = computed(() => page.value > 1);

  function goTo(p: number) {
    if (p >= 1 && p <= totalPages.value) {
      page.value = p;
    }
  }

  function next() {
    if (hasNext.value) page.value++;
  }

  function prev() {
    if (hasPrev.value) page.value--;
  }

  function reset() {
    page.value = 1;
  }

  return {
    page,
    size,
    totalPages,
    hasNext,
    hasPrev,
    goTo,
    next,
    prev,
    reset,
  };
}
