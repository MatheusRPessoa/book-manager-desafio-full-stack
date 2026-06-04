<template>
  <header class="bg-card border-b border-border">
    <div class="max-w-5xl mx-auto px-4 h-14 flex items-center justify-between">
      <RouterLink to="/books" class="flex items-center gap-2 font-semibold text-foreground">
        <BookOpen :size="20" />
        Book Manager
      </RouterLink>
      <div class="flex items-center gap-4">
        <span class="text-sm text-muted-foreground hidden sm:block">{{ auth.name }}</span>
        <button
          @click="handleLogout"
          class="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors"
        >
          <LogOut :size="16" />
          Sair
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { BookOpen, LogOut } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

async function handleLogout() {
  try {
    await auth.logout()
  } finally {
    router.push('/login')
  }
}
</script>
