<template>
  <div class="w-full max-w-sm">
    <div class="flex flex-col gap-1 mb-8 text-center">
      <div class="flex justify-center mb-3">
        <div class="p-2.5 bg-primary rounded-xl">
          <BookOpen :size="24" class="text-primary-foreground" />
        </div>
      </div>
      <h1 class="text-2xl font-semibold tracking-tight text-foreground">Book Manager</h1>
      <p class="text-sm text-muted-foreground">Crie sua conta para começar</p>
    </div>

    <div class="bg-card rounded-xl border border-border shadow-sm p-6">
      <form @submit="onSubmit" class="flex flex-col gap-4">
        <AppInput
          id="name"
          label="Nome"
          v-model="name"
          placeholder="Seu nome"
          :error="errors.name"
        />
        <AppInput
          id="email"
          label="Email"
          type="email"
          v-model="email"
          placeholder="seu@email.com"
          :error="errors.email"
        />
        <AppInput
          id="password"
          label="Senha"
          type="password"
          v-model="password"
          placeholder="••••••••"
          :error="errors.password"
        />

        <div v-if="serverError" class="flex items-center gap-2 text-sm text-destructive bg-destructive/10 px-3 py-2 rounded-md">
          <AlertCircle :size="14" />
          {{ serverError }}
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="h-9 w-full bg-primary text-primary-foreground text-sm font-medium rounded-md hover:bg-primary/90 disabled:opacity-50 transition-all mt-1"
        >
          {{ loading ? 'Criando conta...' : 'Criar conta' }}
        </button>
      </form>
    </div>

    <p class="text-sm text-center text-muted-foreground mt-6">
      Já tem conta?
      <RouterLink to="/login" class="text-foreground font-medium hover:underline underline-offset-4">
        Entrar
      </RouterLink>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { BookOpen, AlertCircle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import AppInput from '@/components/AppInput.vue'
import type { AxiosError } from 'axios'

const schema = toTypedSchema(z.object({
  name: z.string({ required_error: 'Nome é obrigatório' }).min(2, 'Mínimo 2 caracteres').max(100, 'Máximo 100 caracteres'),
  email: z.string({ required_error: 'Email é obrigatório' }).email('Email inválido'),
  password: z.string({ required_error: 'Senha é obrigatória' }).min(6, 'Mínimo 6 caracteres').max(72, 'Máximo 72 caracteres'),
}))

const { handleSubmit, errors } = useForm({ validationSchema: schema })
const { value: name } = useField<string>('name')
const { value: email } = useField<string>('email')
const { value: password } = useField<string>('password')

const auth = useAuthStore()
const router = useRouter()
const loading = ref(false)
const serverError = ref('')

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  serverError.value = ''
  try {
    await auth.register({ name: values.name, email: values.email, password: values.password })
    router.push('/books')
  } catch (e) {
    const error = e as AxiosError<{ error: string }>
    serverError.value = error.response?.data?.error ?? 'Erro ao criar conta'
  } finally {
    loading.value = false
  }
})
</script>
