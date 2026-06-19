import { onUnmounted, ref } from 'vue';

export function usePolling(fn: () => Promise<void>, intervalMs = 3000) {
  const isPolling = ref(false);
  let timer: ReturnType<typeof setInterval> | null = null;

  function start() {
    if (isPolling.value) return;
    isPolling.value = true;
    timer = setInterval(async () => {
      await fn();
    }, intervalMs);
  }

  function stop() {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
    isPolling.value = false;
  }

  onUnmounted(() => stop());

  return { isPolling, start, stop };
}
