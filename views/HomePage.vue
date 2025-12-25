<template>
  <div class="home-page">
    <!-- 全屏背景图 -->
    <div class="background">
      <!-- 建议用一张诱人的美食大图 -->
      <img src="@/assets/images/pizza.jpg" alt="美味披萨" />
    </div>

    <!-- 悬浮内容层 -->
    <div class="content-overlay">
      <!-- 1. 标题改回"味蕾" -->
      <h1 class="title">味蕾</h1>
      <!-- 2. 标语改回面向大众的宣传语 -->
      <p class="subtitle">发现城市中最打动你的味道</p>

      <div class="buttons">
        <!-- 3. 客户入口：使用醒目的红色按钮 (btn-primary) -->
        <button @click="goToUserLogin" class="btn btn-primary">我是顾客，去点餐</button>
        
        <!-- 4. 管理员入口：使用绿色/次要按钮 (btn-secondary) -->
        <button @click="goToAdminLogin" class="btn btn-secondary">我是管理员</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  methods: {
    // 顾客登录 -> 跳转到普通登录页
    goToUserLogin() {
      this.$router.push({ name: 'Login' });
    },
    // 管理员登录 -> 跳转到管理员专用登录页
    goToAdminLogin() {
      this.$router.push({ name: 'AdminLogin' });
    }
  }
};
</script>

<style scoped>
/* 样式完全保持不变，维持原有的美观设计 */
.home-page {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(1.1);
}

.content-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  z-index: 2;
  padding: 30px; /* 稍微增加内边距 */
  width: 90%; /* 手机端适配 */
  max-width: 600px;
  background-color: rgba(0, 0, 0, 0.4); /*稍微加深一点背景，让字更清晰*/
  border-radius: 16px;
  backdrop-filter: blur(4px);
}

.title {
  font-size: 4rem; /* 标题加大一点，更霸气 */
  font-weight: 800;
  margin: 0;
  color: #d32f2f; /* 品牌红 */
  text-shadow: 2px 2px 4px rgba(0,0,0,0.6);
  letter-spacing: 4px;
}

.subtitle {
  font-size: 1.3rem;
  margin: 15px 0 35px;
  opacity: 0.95;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
  letter-spacing: 1px;
}

.buttons {
  display: flex;
  gap: 20px; /* 增加按钮间距 */
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 14px 35px;
  font-size: 1.1rem;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(0,0,0,0.3);
  min-width: 160px; /* 保证按钮宽度一致，更好看 */
}

/* 顾客按钮：红色，最醒目 */
.btn-primary {
  background-color: #d32f2f;
  color: white;
}

.btn-primary:hover {
  background-color: #b71c1c;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(211, 47, 47, 0.4);
}

/* 管理员按钮：绿色或灰色，次要 */
.btn-secondary {
  background-color: #2c3e50; /* 改用深色，显得像后台 */
  color: white;
}

.btn-secondary:hover {
  background-color: #1a252f;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(44, 62, 80, 0.4);
}
</style>