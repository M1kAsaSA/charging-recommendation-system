import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '监控大屏', icon: 'DataLine' }
      }
    ]
  },
  {
    path: '/station',
    component: Layout,
    meta: { title: '站点管理', icon: 'Location' },
    children: [
      {
        path: 'list',
        name: 'StationList',
        component: () => import('@/views/station/list.vue'),
        meta: { title: '电站列表', icon: 'Menu' }
      }
    ]
  },
  {
    path: '/pile',
    component: Layout,
    meta: { title: '电桩管理', icon: 'Lightning' },
    children: [
      {
        path: 'list',
        name: 'PileList',
        component: () => import('@/views/pile/list.vue'),
        meta: { title: '设备档案', icon: 'List' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    meta: { title: '订单监控', icon: 'ShoppingCart' },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '订单列表', icon: 'DocumentCopy' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
})

export default router
