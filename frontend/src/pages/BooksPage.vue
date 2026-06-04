<template>
  <div class="flex flex-col gap-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-semibold tracking-tight text-foreground">Meus Livros</h1>
        <p v-if="books.page" class="text-sm text-muted-foreground mt-0.5">
          {{ books.page.totalElements }} {{ books.page.totalElements === 1 ? 'livro cadastrado' : 'livros cadastrados' }}
        </p>
      </div>
      <RouterLink
        to="/books/new"
        class="flex items-center gap-1.5 h-9 px-4 bg-primary text-primary-foreground text-sm font-medium rounded-md hover:bg-primary/90 transition-all"
      >
        <Plus :size="16" />
        Novo livro
      </RouterLink>
    </div>

    <div class="flex flex-col gap-3">
      <div class="relative">
        <Search :size="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input
          v-model="filters.title"
          @input="handleSearch"
          type="text"
          placeholder="Buscar por título..."
          class="h-9 w-full rounded-md border border-input bg-transparent pl-9 pr-3 py-1 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 transition-[color,box-shadow]"
        />
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
        <input
          v-model="filters.author"
          @input="handleSearch"
          type="text"
          placeholder="Filtrar por autor..."
          class="h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 transition-[color,box-shadow]"
        />
        <input
          v-model.number="filters.yearFrom"
          @input="handleSearch"
          type="number"
          placeholder="Ano de"
          min="1000"
          max="2100"
          class="h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 transition-[color,box-shadow]"
        />
        <input
          v-model.number="filters.yearTo"
          @input="handleSearch"
          type="number"
          placeholder="Ano até"
          min="1000"
          max="2100"
          class="h-9 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 transition-[color,box-shadow]"
        />
        <button
          @click="clearFilters"
          class="h-9 px-3 flex items-center justify-center gap-1.5 rounded-md border border-input text-sm text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <X :size="14" />
          Limpar
        </button>
      </div>
    </div>

    <div v-if="books.loading" class="flex items-center justify-center py-16 text-muted-foreground gap-2">
      <Loader2 :size="18" class="animate-spin" />
      <span class="text-sm">Carregando...</span>
    </div>

    <div v-else-if="books.error" class="flex items-center gap-2 text-sm text-destructive bg-destructive/10 px-3 py-2 rounded-md">
      <AlertCircle :size="14" />
      {{ books.error }}
    </div>

    <div v-if="deleteError" class="flex items-center gap-2 text-sm text-destructive bg-destructive/10 px-3 py-2 rounded-md">
      <AlertCircle :size="14" />
      {{ deleteError }}
    </div>

    <div v-else-if="!books.page?.content.length" class="flex flex-col items-center justify-center py-16 gap-3">
      <BookOpen :size="40" class="text-muted-foreground/40" />
      <p class="text-sm text-muted-foreground">Nenhum livro encontrado.</p>
      <RouterLink
        v-if="!hasActiveFilters"
        to="/books/new"
        class="text-sm font-medium text-foreground hover:underline underline-offset-4"
      >
        Adicionar primeiro livro
      </RouterLink>
    </div>

    <div v-else class="flex flex-col gap-3">
      <BookCard
        v-for="book in books.page.content"
        :key="book.id"
        :book="book"
        @delete="handleDelete"
      />
    </div>

    <div v-if="books.page && books.page.totalPages > 1" class="flex justify-center gap-1">
      <button
        v-for="p in books.page.totalPages"
        :key="p"
        @click="books.fetchBooks(filters, p - 1)"
        :class="[
          'h-8 w-8 rounded-md text-sm font-medium transition-colors',
          books.page.number === p - 1
            ? 'bg-primary text-primary-foreground'
            : 'border border-input hover:bg-accent hover:text-accent-foreground'
        ]"
      >
        {{ p }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { BookOpen, Plus, Search, Loader2, X, AlertCircle } from 'lucide-vue-next'
import { useBooksStore } from '@/stores/books'
import BookCard from '@/components/BookCard.vue'
import type { BookFilters } from '@/services/books.service'
import type { AxiosError } from 'axios'

const books = useBooksStore()
const deleteError = ref('')

const filters = reactive<BookFilters>({
  title: '',
  author: '',
  yearFrom: undefined,
  yearTo: undefined,
})

let debounce: ReturnType<typeof setTimeout>

const hasActiveFilters = computed(() =>
  !!filters.title || !!filters.author || !!filters.yearFrom || !!filters.yearTo
)

onMounted(() => books.fetchBooks())
onUnmounted(() => clearTimeout(debounce))

function handleSearch() {
  clearTimeout(debounce)
  debounce = setTimeout(() => books.fetchBooks({ ...filters }), 400)
}

function clearFilters() {
  filters.title = ''
  filters.author = ''
  filters.yearFrom = undefined
  filters.yearTo = undefined
  books.fetchBooks()
}

async function handleDelete(id: number) {
  if (confirm('Deseja excluir este livro?')) {
    deleteError.value = ''
    try {
      await books.remove(id)
    } catch (e) {
      const err = e as AxiosError<{ error: string }>
      deleteError.value = err.response?.data?.error ?? 'Erro ao excluir livro. Tente novamente.'
    }
  }
}
</script>
