<template>
  <div class="bg-card rounded-xl border border-border p-5 flex flex-col gap-3 shadow-sm hover:shadow-md transition-shadow">
    <div class="flex items-start justify-between gap-4">
      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-1.5 mb-1">
          <BookOpen :size="15" class="text-primary shrink-0" />
          <h3 class="font-semibold text-foreground truncate">{{ book.title }}</h3>
        </div>
        <div class="flex items-center gap-3">
          <span class="flex items-center gap-1 text-sm text-muted-foreground">
            <UserIcon :size="13" class="shrink-0" />
            {{ book.author }}
          </span>
          <span v-if="book.year" class="flex items-center gap-1 text-sm text-muted-foreground">
            <Calendar :size="13" class="shrink-0" />
            {{ book.year }}
          </span>
        </div>
      </div>
      <div class="flex items-center gap-1 shrink-0">
        <RouterLink
          :to="`/books/${book.id}/edit`"
          class="flex items-center gap-1 text-xs font-medium px-2.5 py-1.5 rounded-md border border-border hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <Pencil :size="13" />
          Editar
        </RouterLink>
        <button
          @click="emit('delete', book.id)"
          class="flex items-center gap-1 text-xs font-medium px-2.5 py-1.5 rounded-md text-destructive hover:bg-destructive/10 transition-colors"
        >
          <Trash2 :size="13" />
          Excluir
        </button>
      </div>
    </div>
    <div v-if="book.description" class="flex items-start gap-1.5">
      <AlignLeft :size="13" class="text-muted-foreground shrink-0 mt-0.5" />
      <p class="text-sm text-muted-foreground line-clamp-2">{{ book.description }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BookOpen, User as UserIcon, Calendar, AlignLeft, Pencil, Trash2 } from 'lucide-vue-next'
import type { Book } from '@/types'

defineProps<{ book: Book }>()
const emit = defineEmits<{ delete: [id: number] }>()
</script>
