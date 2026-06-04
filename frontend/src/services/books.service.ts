import api from './api'
import type { Book, BookPayload, Page } from '@/types'

export interface BookFilters {
  title?: string
  author?: string
  yearFrom?: number
  yearTo?: number
  page?: number
  size?: number
}

export const booksService = {
  async list(filters: BookFilters = {}): Promise<Page<Book>> {
    const { data } = await api.get('/books', {
      params: {
        title: filters.title || undefined,
        author: filters.author || undefined,
        yearFrom: filters.yearFrom || undefined,
        yearTo: filters.yearTo || undefined,
        page: filters.page ?? 0,
        size: filters.size ?? 10,
      },
    })
    return data
  },

  async getById(id: number): Promise<Book> {
    const { data } = await api.get(`/books/${id}`)
    return data
  },

  async create(payload: BookPayload): Promise<Book> {
    const { data } = await api.post('/books', payload)
    return data
  },

  async update(id: number, payload: BookPayload): Promise<Book> {
    const { data } = await api.put(`/books/${id}`, payload)
    return data
  },

  async remove(id: number): Promise<void> {
    await api.delete(`/books/${id}`)
  },
}
