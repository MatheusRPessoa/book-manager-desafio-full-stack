import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/auth.service'
import type { AuthResponse, LoginPayload, RegisterPayload } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const name = ref<string | null>(localStorage.getItem('name'))
  const email = ref<string | null>(localStorage.getItem('email'))

  const isAuthenticated = computed(() => !!name.value)

  function persist(data: AuthResponse) {
    name.value = data.name
    email.value = data.email
    localStorage.setItem('name', data.name)
    localStorage.setItem('email', data.email)
  }

  async function login(payload: LoginPayload) {
    persist(await authService.login(payload))
  }

  async function register(payload: RegisterPayload) {
    persist(await authService.register(payload))
  }

  async function logout() {
    try {
      await authService.logout()
    } finally {
      name.value = null
      email.value = null
      localStorage.removeItem('name')
      localStorage.removeItem('email')
    }
  }

  return { name, email, isAuthenticated, login, register, logout }
})
