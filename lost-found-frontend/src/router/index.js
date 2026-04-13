import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue') },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/post/:id/:type',
        name: 'PostDetail',
        component: () => import('@/views/PostDetail.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/my-posts',
        name: 'MyPosts',
        component: () => import('@/views/MyPosts.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/messages',
        name: 'Messages',
        component: () => import('@/views/Messages.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/publish',
        name: 'Publish',
        component: () => import('@/views/PublishView.vue'),
        meta: { requiresAuth: true, role: 'user' }
    },
    {
        path: '/publish/form',
        name: 'PublishForm',
        component: () => import('@/views/PublishForm.vue'),
        meta: { requiresAuth: true, role: 'user' }
    },
    {
        path: '/user/:id',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/personal-center',
        name: 'PersonalCenter',
        component: () => import('@/views/PersonalCenter.vue'),
        meta: { requiresAuth: true }
    },
    // 管理员路由
    {
        path: '/admin',
        name: 'AdminHome',
        component: () => import('@/views/admin/AdminHome.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/admin/users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/admin/reports',
        name: 'Reports',
        component: () => import('@/views/admin/Reports.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/admin/top-requests',
        name: 'TopRequests',
        component: () => import('@/views/admin/TopRequests.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/admin/statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const token = localStorage.getItem('token')
    const userType = localStorage.getItem('userType')
    
    // 恢复 userType
    if (userStore.userType === null && userType !== null) {
        userStore.userType = parseInt(userType)
    }
    
    if (to.meta.requiresAuth) {
        if (!token) {
            next('/login')
            return
        }
        
        // 检查角色权限
        if (to.meta.role === 'admin' && userStore.userType !== 1) {
            next('/home')
            return
        }
        if (to.meta.role === 'user' && userStore.userType !== 0) {
            next('/admin')
            return
        }
        next()
    } else {
        next()
    }
})

export default router