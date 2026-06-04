<template>
  <div class="flex flex-col gap-1.5">
    <label :for="id" class="text-sm font-medium text-foreground">
      {{ label }}
      <span v-if="required" class="text-destructive"> *</span>
    </label>
    <input
      :id="id"
      v-bind="$attrs"
      :value="modelValue"
      @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      :class="['h-9 w-full rounded-md border px-3 py-1 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-3 transition-[color,box-shadow]',
        error ? 'border-destructive focus-visible:ring-destructive/50' : 'border-input focus-visible:ring-ring/50']"
    />
    <p v-if="error" class="text-xs text-destructive">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
defineOptions({ inheritAttrs: false })

defineProps<{
  id: string
  label: string
  modelValue?: string | number
  error?: string
  required?: boolean
}>()

const emit = defineEmits<{ 'update:modelValue': [value: string] }>()
</script>
