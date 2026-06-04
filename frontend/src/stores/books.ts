import { defineStore } from 'pinia'
import { ref } from 'vue'
import { booksService, type BookFilters } from '@/services/books.service'
import type { Book, BookPayload, Page } from '@/types'
import type { AxiosError } from 'axios'

export const useBooksStore = defineStore('books', () => {
  const page = ref<Page<Book> | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const currentFilters = ref<BookFilters>({})
  const currentPage = ref(0)

  async function fetchBooks(filters: BookFilters = {}, pageNumber = 0) {
    currentFilters.value = filters
    currentPage.value = pageNumber
    loading.value = true
    error.value = null
    try {
      page.value = await booksService.list({ ...filters, page: pageNumber })
    } catch (e) {
      const err = e as AxiosError<{ error: string }>
      error.value = err.response?.data?.error ?? 'Erro ao carregar livros. Tente novamente.'
    } finally {
      loading.value = false
    }
  }

  async function create(payload: BookPayload) {
    const book = await booksService.create(payload)
    await fetchBooks(currentFilters.value, currentPage.value)
    return book
  }

  async function update(id: number, payload: BookPayload) {
    const book = await booksService.update(id, payload)
    await fetchBooks(currentFilters.value, currentPage.value)
    return book
  }

  async function remove(id: number) {
    await booksService.remove(id)
    await fetchBooks(currentFilters.value, currentPage.value)
  }

  return { page, loading, error, fetchBooks, create, update, remove }
})
