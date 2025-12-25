<template>
  <div class="admin-login-container">
    <!-- 背景图层：建议换一张更偏向"厨房后台"或"商务"风格的图片 -->
    <div class="background-image"></div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="card-header">
        <h2 class="system-name">味蕾餐饮</h2>
        <p class="system-desc">后台管理系统</p>
      </div>

      <form @submit.prevent="handleAdminLogin" class="login-form">
        <div class="form-group">
          <label for="username">管理员账号</label>
          <!-- 图标装饰（可选） -->
          <div class="input-wrapper">
            <input 
              v-model="username" 
              type="text" 
              id="username" 
              placeholder="请输入管理员ID" 
              required 
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">安全密码</label>
          <div class="input-wrapper">
            <input 
              v-model="password" 
              type="password" 
              id="password" 
              placeholder="请输入密码" 
              required 
              class="form-input"
            />
          </div>
        </div>

        <button type="submit" class="login-button">
          安全登录
        </button>
      </form>

      <div class="login-footer">
        <!-- 管理员通常没有注册入口，改为提示信息 -->
        <p>忘记密码？请联系系统维护人员</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AdminLogin',
  data() {
    return {
      username: "",
      password: ""
    };
  },
  methods: {
    async handleAdminLogin() {
      const { username, password } = this;

      try {
        // 修改点 1：请求后端的管理员登录接口
        const response = await axios.post('/api/manage/login', {
          username,
          password
        });

        const resData = response.data;

        if (resData.success) {
          // 登录成功
          // alert('欢迎回来，管理员！'); // 也可以用 Toast 提示
          
          // 修改点 2：跳转到管理员专属的仪表盘/菜单管理页
          // 请确保 router/index.js 里有 path: '/admin-dashboard'
          this.$router.push('/admin-dashboard'); 
        } else {
          // 登录失败（账号密码错误）
          alert(resData.message || '账号或密码错误');
        }

      } catch (error) {
        console.error('管理员登录异常:', error);
        alert('系统连接失败，请检查后端服务');
      }
    }
  }
};
</script>

<style scoped>
.admin-login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #2c3e50; /* 如果背景图加载失败，显示深色背景 */
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 建议更换为厨房、办公或深色纹理图片 */
  background-image: url('@/assets/images/kitchen-bg.jpg'); 
  background-size: cover;
  background-position: center;
  /* 加深滤镜，让文字更清晰，更有"后台"的深沉感 */
  filter: brightness(0.4) blur(2px); 
  z-index: 1;
}

.login-card {
  position: relative;
  z-index: 2;
  background-color: rgba(255, 255, 255, 0.98);
  padding: 40px;
  width: 380px;
  border-radius: 8px;
  /* 更强烈的阴影，增加立体感 */
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  border-top: 4px solid #d32f2f; /* 顶部红线装饰，体现品牌色 */
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.system-name {
  font-size: 26px;
  font-weight: 800;
  color: #2c3e50;
  margin: 0;
  letter-spacing: 1px;
}

.system-desc {
  margin: 5px 0 0;
  font-size: 14px;
  color: #7f8c8d;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  display: block;
}

.form-input {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  border: 1px solid #e0e0e0;
  background-color: #f9f9f9;
  border-radius: 4px;
  box-sizing: border-box;
  transition: all 0.3s;
}

.form-input:focus {
  background-color: #fff;
  border-color: #d32f2f; /* 聚焦时变红 */
  box-shadow: 0 0 0 3px rgba(211, 47, 47, 0.1);
  outline: none;
}

.login-button {
  width: 100%;
  padding: 14px;
  background-color: #d32f2f; /* 管理员风格的主色调：深红 */
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s;
  letter-spacing: 1px;
}

.login-button:hover {
  background-color: #b71c1c;
}

.login-footer {
  margin-top: 25px;
  text-align: center;
  font-size: 12px;
  color: #95a5a6;
  border-top: 1px solid #eee;
  padding-top: 15px;
}
</style>