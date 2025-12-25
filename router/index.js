import { createRouter, createWebHistory } from 'vue-router';
import UserLogin from '@/components/UserLogin.vue';
import UserRegister from '@/components/UserRegister.vue';
import HomePage from '@/views/HomePage.vue';
import MenuPage from '@/views/MenuPage.vue';
// 1. 引入 AdminLogin 组件 (注意确认文件路径是在 views 还是 components)
import AdminLogin from '@/views/AdminLogin.vue';
const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/login',
    name: 'Login',
    component: UserLogin
  },
  {
    path: '/register',
    name: 'Register',
    component: UserRegister
  },
  {
    path: '/menu',
    name: 'MenuPage',
    component: MenuPage
  },
  // --- 新增：管理员登录路由 ---
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  // --- 新增：管理员后台路由 ---
  // (这是你登录成功后要跳转的页面，使用了懒加载写法)
  {
    path: '/admin-dashboard',
    name: 'AdminDashboard',
    // 请确保你在 views 目录下新建了 AdminDashboard.vue 文件，否则会报错
    component: () => import('@/views/AdminDashboard.vue')
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;