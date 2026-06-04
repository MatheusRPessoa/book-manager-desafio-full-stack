import api from './api'
import type { LoginPayload, RegisterPayload, AuthResponse } from '@/types'

export const authService = {
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post('/auth/login', payload)
    return data
  },

  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const { data } = await api.post('/auth/register', payload)
    return data
  },

  async logout(): Promise<void> {
    await api.post('/auth/logout')
  },
}
