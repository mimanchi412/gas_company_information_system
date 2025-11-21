import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ResetPassword from '@/views/ResetPassword.vue'
import LayoutShell from '@/components/layout/LayoutShell.vue'
import Customers from '@/views/Customers.vue'
import Bills from '@/views/Bills.vue'
import Readings from '@/views/Readings.vue'
import WorkOrders from '@/views/WorkOrders.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login, meta: { public: true } },
    { path: '/register', component: Register, meta: { public: true } },
    { path: '/reset-password', component: ResetPassword, meta: { public: true } },
    {
      path: '/',
      component: LayoutShell,
      children: [
        { path: '', redirect: '/customers' },
        { path: 'customers', component: Customers },
        { path: 'readings', component: Readings },
        { path: 'bills', component: Bills },
        { path: 'work-orders', component: WorkOrders },
      ],
    },
  ],
})

router.beforeEach((to, from, next) => {
  if (to.meta.public) return next()
  const token = localStorage.getItem('token')
  if (!token) return next('/login')
  next()
})

export default router
