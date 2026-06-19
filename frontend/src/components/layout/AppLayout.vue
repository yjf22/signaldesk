<script setup lang="ts">
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';
</script>

<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar />
    <div class="flex-1 flex flex-col min-w-0">
      <Header />
      <main class="flex-1 overflow-y-auto p-6 max-w-content mx-auto w-full">
        <router-view v-slot="{ Component }">
          <Transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </Transition>
        </router-view>
      </main>
    </div>
  </div>
</template>
