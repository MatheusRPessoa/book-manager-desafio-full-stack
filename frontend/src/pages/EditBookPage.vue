<template>
  <div class="max-w-lg">
    <div class="mb-6">
      <RouterLink
        to="/books"
        class="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors mb-4"
      >
        <ArrowLeft :size="16" />
        Voltar
      </RouterLink>
      <h1 class="text-2xl font-semibold tracking-tight text-foreground">Editar Livro</h1>
      <p class="text-sm text-muted-foreground mt-1">Atualize as informações do livro</p>
    </div>

    <div class="bg-card rounded-xl border border-border shadow-sm p-6">
      <div v-if="loadingBook" class="flex items-center gap-2 text-muted-foreground py-4">
        <Loader2 :size="16" class="animate-spin" />
        <span class="text-sm">Carregando...</span>
      </div>
      <div v-else-if="loadError" class="flex items-center gap-2 text-sm text-destructive bg-destructive/10 px-3 py-2 rounded-md">
        <AlertCircle :size="14" />
        {{ loadError }}
      </div>
      <BookForm
        v-else
        :initial="book ?? undefined"
        :loading="loading"
        :error="error"
        submit-label="Salvar alterações"
        @submit="handleSubmit"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Loader2, AlertCircle } from 'lucide-vue-next'
import { useBooksStore } from '@/stores/books'
import { booksService } from '@/services/books.service'
import BookForm from '@/components/BookForm.vue'
import type { Book, BookPayload } from '@/types'
import type { AxiosError } from 'axios'

const books = useBooksStore()
const route = useRoute()
const router = useRouter()

const bookId = Number(route.params.id)
const book = ref<Book | null>(null)
const loadingBook = ref(true)
const loadError = ref('')
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  try {
    book.value = await booksService.getById(bookId)
  } catch {
    loadError.value = 'Livro não encontrado'
  } finally {
    loadingBook.value = false
  }
})

async function handleSubmit(payload: BookPayload) {
  loading.value = true
  error.value = ''
  try {
    await books.update(bookId, payload)
    router.push('/books')
  } catch (e) {
    const err = e as AxiosError<{ error: string }>
    error.value = err.response?.data?.error ?? 'Erro ao atualizar livro'
  } finally {
    loading.value = false
  }
}
</script>
