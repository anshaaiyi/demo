<template>
  <div class="register-container">
    <div class="background-image"></div>
    
    <div class="register-card">
      <h2 class="register-title">用户注册</h2>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            v-model="username" 
            type="text" 
            id="username" 
            placeholder="请输入用户名" 
            required 
            class="form-input"
          />
        </div>

        <!-- 【修改点】这里改成了收货地址，类型是 text -->
        <div class="form-group">
          <label for="address">收货地址</label>
          <input 
            v-model="address" 
            type="text" 
            id="address" 
            placeholder="请输入您的详细收货地址 (如: 幸福小区1号楼)" 
            required 
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input 
            v-model="password" 
            type="password" 
            id="password" 
            placeholder="设置登录密码" 
            required 
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            v-model="confirmPassword" 
            type="password" 
            id="confirmPassword" 
            placeholder="请再次输入密码" 
            required 
            class="form-input"
          />
        </div>

        <button type="submit" class="register-button">注册</button>
      </form>

      <div class="register-footer">
        <p>已有账号？<a @click="$router.push('/login')">登录</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'UserRegister',
  data() {
    return {
      username: "",
      address: "", // 【修改点】变量名改为 address
      password: "",
      confirmPassword: ""
    };
  },
  methods: {
    async handleRegister() {
      // 1. 简单的密码一致性校验
      if (this.password !== this.confirmPassword) {
        alert("两次输入的密码不一致！");
        return;
      }

      try {
        // 2. 发送注册请求 (注意这里用的是 localhost:8080)
        const response = await axios.post('http://localhost:8080/api/users/register', {
          username: this.username,
          password: this.password,
          address: this.address // 【修改点】传给后端的字段名也是 address
        });

        // 3. 注册成功处理
        alert(response.data.message || "注册成功！");
        this.$router.push('/login'); // 跳转回登录页

      } catch (error) {
        console.error("注册失败:", error);
        if (error.response && error.response.data) {
          alert("注册失败: " + (error.response.data.message || "未知错误"));
        } else {
          alert("网络连接失败，请检查后端服务");
        }
      }
    }
  }
};
</script>

<style scoped>
/* 保持和你之前的登录页一致的风格 */
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  /* 建议用一张美食背景图 */
  background-image: url('@/assets/images/your-food.jpg'); 
  background-size: cover;
  background-position: center;
  filter: brightness(0.6);
  z-index: 1;
}

.register-card {
  position: relative;
  z-index: 2;
  background-color: rgba(255, 255, 255, 0.95);
  padding: 30px 40px;
  width: 400px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  text-align: center;
  backdrop-filter: blur(5px);
}

.register-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 25px;
  color: #333;
}

.form-group {
  margin-bottom: 18px;
  text-align: left;
}

.form-group label {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
  display: block;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  font-size: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box; /* 关键：防止输入框撑破容器 */
}

.form-input:focus {
  border-color: #0984e3;
  outline: none;
  box-shadow: 0 0 0 2px rgba(9, 132, 227, 0.1);
}

.register-button {
  width: 100%;
  padding: 12px;
  background-color: #0984e3;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s;
}

.register-button:hover {
  background-color: #0773c5;
}

.register-footer {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-footer a {
  color: #0984e3;
  text-decoration: none;
  cursor: pointer;
  font-weight: bold;
}

.register-footer a:hover {
  text-decoration: underline;
}
</style>