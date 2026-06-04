export interface AuthResponse {
  name: string
  email: string
  token: string
}

export interface Book {
  id: number
  title: string
  author: string
  year?: number
  description?: string
  createdAt: string
}

export interface BookPayload {
  title: string
  author: string
  year?: number
  description?: string
}

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  name: string
  email: string
  password: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
