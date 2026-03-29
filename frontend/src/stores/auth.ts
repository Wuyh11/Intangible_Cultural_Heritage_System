import { defineStore } from 'pinia'
import type { LoginResult } from '@/types/models'

interface AuthState {
  token: string
  profile: LoginResult | null
}

const TOKEN_KEY = 'ICH_ADMIN_TOKEN'
const PROFILE_KEY = 'ICH_ADMIN_PROFILE'

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    profile: localStorage.getItem(PROFILE_KEY)
      ? JSON.parse(localStorage.getItem(PROFILE_KEY) || 'null')
      : null
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    roles: (state) => state.profile?.roles || [],
    isSuperAdmin: (state) => (state.profile?.roles || []).includes('SUPER_ADMIN')
  },
  actions: {
    setLogin(payload: LoginResult) {
      this.token = payload.token
      this.profile = payload
      localStorage.setItem(TOKEN_KEY, payload.token)
      localStorage.setItem(PROFILE_KEY, JSON.stringify(payload))
    },
    logout() {
      this.token = ''
      this.profile = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(PROFILE_KEY)
    }
  }
})
