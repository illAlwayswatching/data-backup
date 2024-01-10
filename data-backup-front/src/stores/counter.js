import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  let account = ref(localStorage.getItem('account')? localStorage.getItem('account') : null)
  let token = ref(localStorage.getItem('token')? localStorage.getItem('token') : null)

  function setAccount(val) {
    account.value = val
    localStorage.setItem('account', JSON.stringify(val))
  }

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', token)
  }

  function logOut() {
    // localStorage.removeItem('token')
    // token = null
    localStorage.removeItem('account')
    account.value = null
  }

  return { account, token, setAccount, setToken, logOut }
})
