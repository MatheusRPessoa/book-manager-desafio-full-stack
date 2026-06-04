<template>
  <form @submit="onSubmit" class="flex flex-col gap-5">
    <AppInput
      id="title"
      label="Título"
      v-model="title"
      placeholder="Ex: Clean Code"
      :error="errors.title"
      required
    />
    <AppInput
      id="author"
      label="Autor"
      v-model="author"
      placeholder="Ex: Robert C. Martin"
      :error="errors.author"
      required
    />
    <AppInput
      id="year"
      label="Ano"
      type="number"
      :model-value="year"
      @update:model-value="year = $event ? Number($event) : undefined"
      placeholder="Ex: 2008"
      :error="errors.year"
    />

    <div class="flex flex-col gap-1.5">
      <label for="description" class="text-sm font-medium text-foreground">Descrição</label>
      <textarea
        id="description"
        v-model="description"
        rows="3"
        placeholder="Uma breve descrição do livro..."
        class="w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 transition-[color,box-shadow] resize-none"
      />
      <p v-if="errors.description" class="text-xs text-destructive">{{ errors.description }}</p>
    </div>

    <div v-if="error" class="flex items-center gap-2 text-sm text-destructive bg-destructive/10 px-3 py-2 rounded-md">
      <AlertCircle :size="14" />
      {{ error }}
    </div>

    <div class="flex gap-3 pt-1">
      <button
        type="submit"
        :disabled="loading"
        class="h-9 px-4 bg-primary text-primary-foreground text-sm font-medium rounded-md hover:bg-primary/90 disabled:opacity-50 transition-all"
      >
        {{ loading ? 'Salvando...' : props.submitLabel }}
      </button>
      <RouterLink
        to="/books"
        class="h-9 px-4 flex items-center text-sm font-medium rounded-md border border-input hover:bg-accent hover:text-accent-foreground transition-colors"
      >
        Cancelar
      </RouterLink>
    </div>
  </form>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import { AlertCircle } from 'lucide-vue-next'
import type { Book, BookPayload } from '@/types'
import AppInput from '@/components/AppInput.vue'

const props = withDefaults(defineProps<{
  initial?: Partial<Book>
  loading: boolean
  error?: string
  submitLabel?: string
}>(), {
  submitLabel: 'Salvar',
})

const emit = defineEmits<{ submit: [payload: BookPayload] }>()

const schema = toTypedSchema(z.object({
  title: z.string({ required_error: 'Título é obrigatório' }).min(1, 'Título é obrigatório').max(500, 'Máximo 500 caracteres'),
  author: z.string({ required_error: 'Autor é obrigatório' }).min(1, 'Autor é obrigatório').max(150, 'Máximo 150 caracteres'),
  year: z.preprocess(
    (val) => (val === '' || val === undefined || val === null || isNaN(Number(val))) ? undefined : Number(val),
    z.number().min(1000, 'Ano inválido').max(2100, 'Ano inválido').optional()
  ),

  description: z.string().max(5000, 'Máximo 5000 caracteres').optional(),
}))

const { handleSubmit, errors, setValues } = useForm({ validationSchema: schema })
const { value: title } = useField<string>('title')
const { value: author } = useField<string>('author')
const { value: year } = useField<number | undefined>('year')
const { value: description } = useField<string>('description')

watch(() => props.initial, (val) => {
  if (val) {
    setValues({
      title: val.title ?? '',
      author: val.author ?? '',
      year: val.year,
      description: val.description ?? '',
    })
  }
}, { immediate: true })

const onSubmit = handleSubmit((values) => {
  emit('submit', values as BookPayload)
})
</script>
