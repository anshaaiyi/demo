<template>
  <div class="app-container">
    <!-- 1. 顶部导航栏 (玻璃拟态风格) -->
    <header class="navbar">
      <div class="nav-content">
        <div class="brand-box">
          <h1 class="logo">🥢 味蕾 · 严选</h1>
          <span class="sub-title">用心烹饪每一餐</span>
        </div>
        <!-- 显示当前登录用户 -->
        <div class="user-info" v-if="currentUser">
          <span class="user-name">👤 {{ currentUser.username }}</span>
          <button class="logout-btn" @click="logout">退出</button>
        </div>
        <div class="user-info" v-else>
          <span class="user-name" @click="$router.push('/login')" style="cursor:pointer">未登录 (点击登录)</span>
        </div>
      </div>
    </header>

    <!-- 2. 内容区域 -->
    <main class="main-content">
      <!-- 渐变 Hero Banner -->
      <div class="hero-banner">
        <div class="banner-text">
          <h2>今日推荐菜单</h2>
          <p>热销榜单 | 新鲜食材 | 极速送达</p>
        </div>
      </div>

      <!-- 菜品列表 -->
      <div class="menu-grid">
        <div 
          v-for="item in menuItems" 
          :key="item.id" 
          class="menu-card"
          @click="showDetail(item)"
        >
          <div class="image-wrapper">
            <img :src="getRealImage(item.imageUrl)" loading="lazy" />
            <!-- 模拟热销标签 -->
            <div class="tag" v-if="item.id % 2 === 0">🔥 热销</div>
          </div>
          
          <div class="card-body">
            <h3 class="dish-name">{{ item.name }}</h3>
            <p class="dish-desc">{{ item.description }}</p>
            <div class="card-footer">
              <span class="price">
                <span class="symbol">¥</span>{{ item.price.toFixed(2) }}
              </span>
              <button class="add-icon-btn" @click.stop="quickAdd(item)">+</button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 3. 详情弹窗 (已恢复功能) -->
    <transition name="fade">
      <div v-if="selectedItem" class="modal-mask" @click="closeModal">
        <div class="modal-panel" @click.stop>
          <button class="close-btn" @click="closeModal">×</button>
          <div class="modal-img-box">
             <img :src="getRealImage(selectedItem.imageUrl)" />
          </div>
          <div class="modal-info">
            <h2>{{ selectedItem.name }}</h2>
            <p class="desc-text">{{ selectedItem.description }}</p>
            <div class="modal-actions">
              <div class="price-large">¥{{ selectedItem.price.toFixed(2) }}</div>
              <div class="stepper">
                <button @click="decreaseQty" :disabled="qty <= 1" class="step-btn minus">-</button>
                <span class="qty-num">{{ qty }}</span>
                <button @click="increaseQty" class="step-btn plus">+</button>
              </div>
            </div>
            <button class="confirm-btn" @click="addToCartFromModal">
              加入购物车 - ¥{{ (selectedItem.price * qty).toFixed(2) }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 4. 支付成功弹窗 -->
    <transition name="scale-in">
      <div v-if="showSuccessModal" class="modal-mask" style="z-index: 2000;">
        <div class="success-card">
          <div class="success-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
          </div>
          <h3>下单成功</h3>
          <p class="success-desc">厨房已接单，美味马上就好！</p>
          <button class="success-btn" @click="showSuccessModal = false">完成</button>
        </div>
      </div>
    </transition>

    <!-- 5. 底部悬浮购物车 -->
    <transition name="slide-up">
      <div class="cart-floater" v-if="cartTotalItems > 0">
        <div class="cart-left" @click="showCartDetails">
          <div class="cart-icon-box">
            🛒
            <span class="badge">{{ cartTotalItems }}</span>
          </div>
          <div class="price-info">
            <div class="total">¥{{ cartTotalPrice.toFixed(2) }}</div>
            <div class="tip">免配送费</div>
          </div>
        </div>
        <button class="pay-button" @click="handlePay">去结算</button>
      </div>
    </transition>

    <div style="height: 100px;"></div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MenuPage',
  data() {
    return {
      menuItems: [],
      backendUrl: 'http://localhost:8080',
      
      // 购物车与交互状态
      cart: [],
      selectedItem: null, // 控制弹窗显示的变量
      qty: 1,
      showSuccessModal: false,
      
      // 用户信息
      currentUser: null
    };
  },
  
  created() {
    this.fetchMenu();
    this.checkLogin();
  },

  computed: {
    cartTotalItems() { return this.cart.reduce((sum, c) => sum + c.quantity, 0); },
    cartTotalPrice() { return this.cart.reduce((sum, c) => sum + c.item.price * c.quantity, 0); }
  },

  methods: {
    async fetchMenu() {
      try {
        const response = await axios.get(this.backendUrl + '/api/menu/list');
        this.menuItems = response.data;
      } catch (error) {
        console.error("无法连接后端:", error);
      }
    },

    // 读取本地存储的用户信息
    checkLogin() {
      const userStr = localStorage.getItem('currentUser');
      if (userStr) {
        try {
          this.currentUser = JSON.parse(userStr);
        } catch(e) {
          console.error("解析用户信息失败", e);
        }
      }
    },

    logout() {
      localStorage.removeItem('currentUser');
      this.currentUser = null;
      this.$router.push('/login');
    },

    getRealImage(imgUrl) {
      if (!imgUrl) return 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80';
      if (imgUrl.startsWith('http')) return imgUrl;
      return this.backendUrl + imgUrl;
    },

    // --- 弹窗与购物车逻辑 (关键) ---
    showDetail(item) { 
      // 点击卡片，把 selectedItem 设置为当前菜品，弹窗就会通过 v-if 显示出来
      this.selectedItem = item; 
      this.qty = 1; 
    },
    closeModal() { this.selectedItem = null; },
    increaseQty() { this.qty++; },
    decreaseQty() { if (this.qty > 1) this.qty--; },
    
    quickAdd(item) { 
      this.addToCartLogic(item, 1); 
      // 简单提示，实际可以用Toast
      console.log(`已加入: ${item.name}`); 
    },
    addToCartFromModal() { 
      this.addToCartLogic(this.selectedItem, this.qty); 
      this.closeModal(); 
    },
    
    addToCartLogic(item, quantity) {
      const existing = this.cart.find(c => c.item.id === item.id);
      if (existing) { existing.quantity += quantity; } 
      else { this.cart.push({ item, quantity }); }
    },
    
    showCartDetails() {
        // 这里留空，或者你可以做一个购物车详情弹窗
        console.log("查看购物车详情");
    },

    // --- 结算逻辑 (集成后端) ---
    async handlePay() {
      if (this.cart.length === 0) return;

      // 1. 检查登录
      if (!this.currentUser) {
        if(confirm("您尚未登录，是否前往登录页面？")) {
          this.$router.push('/login');
        }
        return;
      }

      const currentTotal = this.cartTotalPrice.toFixed(2);
      
      // 2. 封装订单数据 (注意：没有 phone)
      const orderData = {
        userId: this.currentUser.id,
        username: this.currentUser.username,
        address: this.currentUser.address,
        totalAmount: parseFloat(currentTotal),
        items: this.cart.map(c => ({ 
            menuItemId: c.item.id, 
            quantity: c.quantity, 
            price: c.item.price 
        }))
      };

      try {
        await axios.post(this.backendUrl + '/api/order/create', orderData);
        this.showSuccessModal = true;
        this.cart = [];
      } catch (e) {
        console.error(e);
        alert("下单失败：" + (e.response?.data?.message || "后端接口异常"));
      }
    }
  }
};
</script>

<style scoped>
/* --- 2025年审美风格 CSS (已完全恢复) --- */

* { box-sizing: border-box; }
.app-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background-color: #f7f8fa;
  min-height: 100vh;
  color: #333;
}

/* 顶部导航 */
.navbar {
  background: rgba(255, 255, 255, 0.85); /* 玻璃半透明 */
  backdrop-filter: blur(12px);
  position: sticky; top: 0; z-index: 100;
  box-shadow: 0 1px 15px rgba(0,0,0,0.04);
  padding: 12px 0;
}
.nav-content { 
  max-width: 1200px; margin: 0 auto; padding: 0 20px; 
  display: flex; justify-content: space-between; align-items: center; 
}
.brand-box { display: flex; align-items: baseline; gap: 12px; }
.logo { margin: 0; font-size: 24px; color: #ff6b01; font-weight: 800; letter-spacing: -0.5px; }
.sub-title { color: #999; font-size: 13px; font-weight: 500; }

.user-info { font-size: 14px; color: #555; display: flex; align-items: center; gap: 10px; }
.logout-btn { background: none; border: 1px solid #ddd; padding: 4px 10px; border-radius: 20px; cursor: pointer; color: #666; font-size: 12px; }

/* 内容区 */
.main-content { max-width: 1200px; margin: 0 auto; padding: 24px; }

/* 渐变 Banner */
.hero-banner {
  background: linear-gradient(120deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
  padding: 40px; border-radius: 24px; margin-bottom: 40px; color: white;
  box-shadow: 0 15px 30px rgba(255, 107, 1, 0.15);
  position: relative; overflow: hidden;
}
.hero-banner h2 { margin: 0 0 10px 0; font-size: 28px; font-weight: 800; }
.hero-banner p { margin: 0; opacity: 0.9; font-size: 15px; }

/* 卡片网格 */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); 
  gap: 24px;
}

/* 菜品卡片 */
.menu-card {
  background: white; border-radius: 20px; overflow: hidden;
  box-shadow: 0 10px 20px rgba(0,0,0,0.03); 
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(0,0,0,0.02); cursor: pointer;
}
.menu-card:hover { transform: translateY(-8px); box-shadow: 0 20px 40px rgba(0,0,0,0.08); }
.image-wrapper { position: relative; width: 100%; padding-top: 75%; background: #f0f0f0; overflow: hidden; }
.image-wrapper img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s ease; }
.menu-card:hover .image-wrapper img { transform: scale(1.08); }
.tag { position: absolute; top: 12px; left: 12px; background: rgba(255, 77, 79, 0.9); backdrop-filter: blur(4px); color: white; font-size: 11px; padding: 4px 10px; border-radius: 20px; font-weight: 700; box-shadow: 0 4px 10px rgba(255, 77, 79, 0.3); }

.card-body { padding: 18px; }
.dish-name { font-size: 17px; font-weight: 700; margin: 0 0 6px 0; color: #2c3e50; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.dish-desc { font-size: 13px; color: #95a5a6; margin: 0 0 15px 0; height: 36px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.price { color: #ff4d4f; font-size: 18px; font-weight: 800; letter-spacing: -0.5px; }
.symbol { font-size: 12px; margin-right: 2px; }
.add-icon-btn { width: 32px; height: 32px; border-radius: 50%; background: #ff6b01; color: white; border: none; font-size: 20px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 10px rgba(255, 107, 1, 0.3); }
.add-icon-btn:hover { background: #ff8534; transform: scale(1.1); }

/* 详情弹窗 */
.modal-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); backdrop-filter: blur(5px); z-index: 999; display: flex; justify-content: center; align-items: center; padding: 20px; }
.modal-panel { background: white; width: 100%; max-width: 460px; border-radius: 24px; overflow: hidden; position: relative; animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1); box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); }
.modal-img-box { width: 100%; height: 260px; position: relative; }
.modal-img-box img { width: 100%; height: 100%; object-fit: cover; }
.modal-info { padding: 24px; }
.modal-info h2 { margin-top: 0; font-size: 22px; color: #333; }
.desc-text { color: #666; line-height: 1.6; margin: 10px 0 20px; background: #f9f9fc; padding: 12px; border-radius: 12px; font-size: 14px; }
.modal-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.price-large { font-size: 26px; color: #ff4d4f; font-weight: 800; }
.stepper { display: flex; align-items: center; background: #f0f2f5; border-radius: 30px; padding: 4px; }
.step-btn { width: 32px; height: 32px; border: none; background: white; border-radius: 50%; cursor: pointer; font-weight: bold; box-shadow: 0 2px 5px rgba(0,0,0,0.05); color: #333; transition: 0.2s; }
.step-btn:hover:not(:disabled) { transform: scale(1.1); }
.step-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.qty-num { width: 36px; text-align: center; font-weight: bold; font-size: 15px; }
.confirm-btn { width: 100%; background: #ff6b01; color: white; border: none; padding: 16px; border-radius: 50px; font-size: 16px; font-weight: bold; cursor: pointer; box-shadow: 0 8px 20px rgba(255, 107, 1, 0.3); transition: transform 0.2s; }
.confirm-btn:active { transform: scale(0.98); }
.close-btn { position: absolute; top: 15px; right: 15px; width: 32px; height: 32px; background: rgba(0,0,0,0.3); color: white; border-radius: 50%; border: none; cursor: pointer; z-index: 2; display: flex; align-items: center; justify-content: center; font-size: 20px; backdrop-filter: blur(4px); }

/* 支付成功弹窗 */
.success-card { background: white; width: 320px; border-radius: 24px; padding: 40px 30px; text-align: center; box-shadow: 0 20px 60px rgba(0,0,0,0.15); animation: scaleIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.success-icon { width: 70px; height: 70px; background: #e6f7ff; border-radius: 50%; color: #52c41a; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; }
.success-icon svg { width: 36px; height: 36px; stroke: #52c41a; }
.success-card h3 { font-size: 20px; margin: 0 0 10px; color: #333; }
.success-desc { color: #888; font-size: 14px; margin-bottom: 30px; }
.success-btn { width: 100%; background: #52c41a; color: white; border: none; padding: 12px; border-radius: 50px; font-size: 15px; font-weight: bold; cursor: pointer; box-shadow: 0 6px 15px rgba(82, 196, 26, 0.3); }

/* 底部购物车 */
.cart-floater {
  position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%);
  width: 90%; max-width: 500px; height: 64px; 
  background: #2d3436; color: white;
  border-radius: 50px; display: flex; align-items: center; justify-content: space-between;
  padding: 6px 6px 6px 28px; 
  box-shadow: 0 15px 40px rgba(0,0,0,0.25); z-index: 1000;
}
.cart-left { display: flex; align-items: center; gap: 16px; cursor: pointer; flex: 1; }
.cart-icon-box { position: relative; font-size: 24px; }
.badge { position: absolute; top: -6px; right: -8px; background: #ff4d4f; color: white; font-size: 11px; padding: 2px 6px; border-radius: 10px; border: 2px solid #2d3436; font-weight: bold; }
.total { font-size: 18px; font-weight: 700; }
.tip { font-size: 11px; color: #b2bec3; }
.pay-button { 
  background: #ff6b01; color: white; border: none; 
  height: 100%; padding: 0 32px; border-radius: 0 40px 40px 0; 
  font-size: 16px; font-weight: bold; cursor: pointer; 
  transition: filter 0.2s;
}
.pay-button:hover { filter: brightness(1.1); }

/* 动画定义 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter, .fade-leave-to { opacity: 0; }
@keyframes slideUp { from { transform: translateY(50px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
@keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>