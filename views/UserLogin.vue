<template>
  <div class="login-container">
    <div class="background-image"></div>
    <div class="login-card">
      <h2 class="login-title">用户登录</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input v-model="username" type="text" id="username" placeholder="请输入用户名" required class="form-input"/>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input v-model="password" type="password" id="password" placeholder="请输入密码" required class="form-input"/>
        </div>
        <button type="submit" class="login-button">登录</button>
      </form>
      <div class="login-footer">
        <p>没有账号？<a href="/register">注册</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'LoginPage',
  data() {
    return {
      username: "",
      password: ""
    };
  },
  methods: {
   // Login.vue -> methods
async handleLogin() {
  try {
    const response = await axios.post('http://localhost:8080/api/users/login', {
      username: this.username,
      password: this.password
    });

    const resData = response.data; // 后端返回的完整包

    // ✅ 关键判断：后端有没有给 data？
    if (resData.data) {
        // 1. 把它转成字符串
        const userStr = JSON.stringify(resData.data);
        
        // 2. 存起来！(如果不存，跳过去也是未登录)
        localStorage.setItem('currentUser', userStr);
        
        console.log("存入成功:", userStr); // F12控制台看看有没有这一行
        
        alert("登录成功！");
        this.$router.push('/menu');
    } else {
        // 如果走到这里，说明第一步(后端)没写对
        console.error("后端没返回用户信息:", resData);
        alert("登录成功，但后端未返回用户数据，请检查 UserController");
        this.$router.push('/menu');
    }

  } catch (error) {
    alert("登录失败");
  }
}
  }
};
</script>

<style scoped>
/* 样式保持不变，直接用你原来的即可 */
.login-container { position: relative; display: flex; justify-content: center; align-items: center; height: 100vh; overflow: hidden; }
.background-image { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-image: url('https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=2070&auto=format&fit=crop'); background-size: cover; background-position: center; filter: brightness(0.7); z-index: 1; }
.login-card { position: relative; z-index: 2; background-color: rgba(255, 255, 255, 0.95); padding: 30px; width: 400px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15); text-align: center; backdrop-filter: blur(4px); }
.login-title { font-size: 22px; font-weight: bold; margin-bottom: 20px; color: #2d3436; }
.form-group { margin-bottom: 18px; text-align: left; }
.form-group label { font-size: 14px; color: #636e72; margin-bottom: 6px; display: block; }
.form-input { width: 100%; padding: 10px; font-size: 15px; border: 1px solid #ddd; border-radius: 6px; margin-top: 4px; box-sizing: border-box; }
.form-input:focus { border-color: #0984e3; outline: none; }
.login-button { width: 100%; padding: 12px; background-color: #0984e3; color: white; font-size: 16px; border: none; border-radius: 6px; cursor: pointer; margin-top: 16px; transition: background 0.3s; }
.login-button:hover { background-color: #74b9ff; }
.login-footer { margin-top: 20px; font-size: 14px; color: #666; }
.login-footer a { color: #0984e3; text-decoration: none; }
</style>