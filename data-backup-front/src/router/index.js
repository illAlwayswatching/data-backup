import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/Login'
    },
    {
      path: '/Home',
      component: Home,
      meta: { title: '主页' }
    },
    {
      path: '/Login',
      component: Login,
      meta: { title: '登录' }
    }
  ]
})

router.beforeEach((to, _, next) => {
  console.log(to.path)
	if (to.path === '/' || to.path === '/Login') {
		next()
	}else{
		const accountToken = localStorage.getItem('account');
		if (accountToken === null || accountToken === '') {
      ElMessage({
        message: '没有权限，请重新登录.',
        type: 'warning'
      })
			return next('/')
		}else{
			next()
		}
	}
})

export default router
