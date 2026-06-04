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
      <h1 class="text-2xl font-semibold tracking-tight text-foreground">Novo Livro</h1>
      <p class="text-sm text-muted-foreground mt-1">Adicione um novo livro à sua coleção</p>
    </div>

    <div class="bg-card rounded-xl border border-border shadow-sm p-6">
      <BookForm
        :loading="loading"
        :error="error"
        submit-label="Criar livro"
        @submit="handleSubmit"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'
import { useBooksStore } from '@/stores/books'
import BookForm from '@/components/BookForm.vue'
import type { BookPayload } from '@/types'
import type { AxiosError } from 'axios'

const books = useBooksStore()
const router = useRouter()
const loading = ref(false)
const error = ref('')

async function handleSubmit(payload: BookPayload) {
  loading.value = true
  error.value = ''
  try {
    await books.create(payload)
    router.push('/books')
  } catch (e) {
    const err = e as AxiosError<{ error: string }>
    error.value = err.response?.data?.error ?? 'Erro ao criar livro'
  } finally {
    loading.value = false
  }
}
</script>
