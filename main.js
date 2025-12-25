import { createApp } from 'vue';  // 使用 createApp 而不是直接导入 Vue
import App from './App.vue';
import router from './router';

createApp(App)
  .use(router)  // 使用 Vue Router
  .mount('#app');  // 挂载应用
