<template>
  <div class="admin-dashboard">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo">👨‍🍳 味蕾后台</div>
      <nav>
        <div class="nav-item" :class="{ active: currentTab === 'dish' }" @click="currentTab = 'dish'">
          🍛 菜品管理
        </div>
        <div class="nav-item" :class="{ active: currentTab === 'order' }" @click="currentTab = 'order'">
          🧾 订单查看
        </div>
      </nav>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </aside>

    <!-- 右侧内容 -->
    <main class="main-content">
      
      <!-- === 模块 A: 菜品管理 === -->
      <div v-if="currentTab === 'dish'" class="content-panel">
        <header class="panel-header">
          <h2>菜品列表</h2>
          <button class="btn-primary" @click="showAddModal = true">nw 新增菜品</button>
        </header>

        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>图片</th>
                <th>名称</th>
                <th>价格</th>
                <th>描述</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in menuList" :key="item.id">
                <td>{{ item.id }}</td>
                <td><img :src="getRealImage(item.imageUrl)" class="table-img" /></td>
                <td class="font-bold">{{ item.name }}</td>
                <td class="color-red">{{ item.price }} 元</td>
                <td class="text-desc">{{ item.description }}</td>
                <td><button class="btn-danger" @click="deleteDish(item.id)">删除</button></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- === 模块 B: 订单列表 (核心部分) === -->
      <div v-if="currentTab === 'order'" class="content-panel">
        <header class="panel-header">
          <h2>订单管理</h2>
          <button class="btn-refresh" @click="initData">刷新列表</button>
        </header>

        <div v-if="orderList.length === 0" class="empty-tip">暂无订单数据</div>

        <div class="order-list">
          <div v-for="order in orderList" :key="order.id" class="order-card">
            
            <!-- 1. 头部信息 -->
            <div class="order-header">
              <div>
                <span class="order-id">订单号 #{{ order.id }}</span>
                <span class="order-time">{{ formatTime(order.createTime) }}</span>
              </div>
              <button class="btn-finish" @click="finishOrder(order.id)">✅ 完成/删除</button>
            </div>
            
            <!-- 2. 客户信息 (显示你的用户名和地址) -->
            <div class="customer-section">
              <div class="info-row">
                <span class="label">👤 客户姓名:</span>
                <span class="value">{{ order.customerName || '匿名用户' }}</span>
              </div>
              <div class="info-row">
                <span class="label">📍 配送地址:</span>
                <span class="value address-value">{{ order.address || '店内就餐' }}</span>
              </div>
            </div>

            <hr class="divider">

            <!-- 3. 菜品明细 -->
            <div class="order-items">
              <div v-for="(item, index) in order.items" :key="index" class="item-row">
                <!-- 菜品图片 (尝试匹配) -->
                <img :src="getDishImage(item.menuItemId)" class="item-thumb">
                
                <div class="item-info">
                  <!-- 菜品名称 (通过 ID 查找) -->
                  <div class="dish-name">{{ getDishName(item.menuItemId) }}</div>
                  <div class="dish-meta">单价: {{ item.price }} 元</div>
                </div>
                
                <div class="item-qty">x{{ item.quantity }}</div>
                <div class="item-total">{{ (item.price * item.quantity).toFixed(2) }} 元</div>
              </div>
            </div>

            <!-- 4. 底部总计 -->
            <div class="order-footer">
              <div class="total-wrapper">
                <span>实付金额:</span>
                <span class="total-price">{{ order.totalAmount }} 元</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 新增菜品弹窗 (保持不变) -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-form">
        <h3>✨ 上架新菜品</h3>
        <div class="form-group">
          <label>菜品名称</label> <input v-model="newDish.name" type="text">
        </div>
        <div class="form-group">
          <label>价格 (元)</label> <input v-model="newDish.price" type="number">
        </div>
        <div class="form-group">
          <label>描述</label> <textarea v-model="newDish.description"></textarea>
        </div>
        <div class="form-group">
          <label>图片</label> <input type="file" @change="handleFileChange" accept="image/*">
          <div v-if="previewUrl" class="img-preview"><img :src="previewUrl"></div>
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-primary" @click="submitNewDish">确认上架</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      currentTab: 'dish',
      backendUrl: 'http://localhost:8080',
      menuList: [],
      orderList: [],
      showAddModal: false,
      newDish: { name: '', price: '', description: '', file: null },
      previewUrl: null
    };
  },
  created() {
    this.initData();
  },
  methods: {
    // 初始化数据：必须先加载菜单，再加载订单，否则菜名显示不出来
    async initData() {
      await this.fetchMenu();
      await this.fetchOrders();
    },

    async fetchMenu() {
      try {
        const res = await axios.get(this.backendUrl + '/api/menu/list');
        this.menuList = res.data;
      } catch (e) { console.error('菜单加载失败'); }
    },

    async fetchOrders() {
      try {
        const res = await axios.get(this.backendUrl + '/api/order/list');
        // 按时间倒序，最新的在上面
        this.orderList = res.data.reverse();
      } catch (e) { console.error('订单加载失败'); }
    },

    // --- 核心逻辑：数据关联 ---
    // 根据 ID 从 menuList 里找名字
    getDishName(id) {
      if (!this.menuList.length) return '加载中...';
      const dish = this.menuList.find(m => m.id === id);
      return dish ? dish.name : `未知菜品(ID:${id})`;
    },
    // 根据 ID 从 menuList 里找图片
    getDishImage(id) {
      const dish = this.menuList.find(m => m.id === id);
      return dish ? this.getRealImage(dish.imageUrl) : '';
    },

    // 删除订单
    async finishOrder(id) {
      if(!confirm(`确认订单 #${id} 处理完毕并删除？`)) return;
      try {
        await axios.delete(this.backendUrl + `/api/order/delete/${id}`);
        this.fetchOrders(); // 刷新
      } catch (e) { alert("删除失败"); }
    },

    // 删除菜品
    async deleteDish(id) {
      if (!confirm('确定删除此菜品？')) return;
      try {
        await axios.delete(this.backendUrl + `/api/menu/delete/${id}`);
        this.fetchMenu();
      } catch (e) { alert('删除失败'); }
    },

    // 上传逻辑
    handleFileChange(e) {
      const file = e.target.files[0];
      if (file) { this.newDish.file = file; this.previewUrl = URL.createObjectURL(file); }
    },
    async submitNewDish() {
      if (!this.newDish.name || !this.newDish.price || !this.newDish.file) {
        alert("请填写完整信息"); return;
      }
      const formData = new FormData();
      formData.append('name', this.newDish.name);
      formData.append('price', this.newDish.price);
      formData.append('description', this.newDish.description);
      formData.append('image', this.newDish.file);
      try {
        await axios.post(this.backendUrl + '/api/menu/add', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        this.showAddModal = false;
        this.newDish = { name: '', price: '', description: '', file: null };
        this.previewUrl = null;
        this.fetchMenu();
      } catch (e) { alert('上架失败'); }
    },

    // 工具函数
    handleLogout() { this.$router.push('/'); },
    getRealImage(url) { 
      if (!url) return 'https://via.placeholder.com/50'; // 默认图
      return url.startsWith('http') ? url : this.backendUrl + url; 
    },
    formatTime(t) {
      if (Array.isArray(t)) return `${t[0]}-${t[1]}-${t[2]} ${t[3]}:${t[4]}`;
      return t ? t.toString().replace('T', ' ').substring(0, 16) : '';
    }
  }
};
</script>

<style scoped>
/* 保持原有布局风格 */
.admin-dashboard { display: flex; height: 100vh; background-color: #f3f4f6; color: #333; }
.sidebar { width: 240px; background-color: #1e293b; color: white; display: flex; flex-direction: column; }
.logo { height: 64px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; background-color: #0f172a; border-bottom: 1px solid #334155; }
.nav-item { padding: 16px 24px; cursor: pointer; color: #94a3b8; transition: 0.3s; }
.nav-item:hover { color: white; background-color: #334155; }
.nav-item.active { background-color: #d32f2f; color: white; font-weight: 500; }
.logout-btn { margin-top: auto; padding: 15px; background: #334155; color: #f87171; border: none; cursor: pointer; }

.main-content { flex: 1; padding: 30px; overflow-y: auto; }
.content-panel { max-width: 1000px; margin: 0 auto; }
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.panel-header h2 { font-size: 24px; color: #1e293b; margin: 0; }

/* 菜品表格 */
.table-container { background: white; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); overflow: hidden; }
table { width: 100%; border-collapse: collapse; }
th { background: #f8fafc; padding: 12px 16px; text-align: left; color: #64748b; font-weight: 600; }
td { padding: 12px 16px; border-top: 1px solid #e2e8f0; vertical-align: middle; }
.table-img { width: 50px; height: 50px; border-radius: 4px; object-fit: cover; }
.color-red { color: #d32f2f; font-weight: bold; }
.text-desc { max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #94a3b8; font-size: 13px; }

/* 订单卡片 - 优化版 */
.order-list { display: grid; gap: 20px; }
.order-card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.order-id { font-size: 18px; font-weight: 800; color: #1e293b; margin-right: 15px; }
.order-time { font-size: 14px; color: #94a3b8; }

/* 客户信息区 */
.customer-section { background-color: #f1f5f9; padding: 12px 15px; border-radius: 8px; font-size: 14px; color: #475569; }
.info-row { display: flex; margin-bottom: 4px; }
.info-row:last-child { margin-bottom: 0; }
.info-row .label { width: 80px; font-weight: 600; color: #64748b; }
.info-row .value { flex: 1; font-weight: 500; color: #334155; }
.address-value { line-height: 1.4; }

.divider { border: none; border-top: 1px dashed #cbd5e1; margin: 15px 0; }

/* 菜品条目 */
.item-row { display: flex; align-items: center; padding: 8px 0; border-bottom: 1px solid #f8fafc; }
.item-thumb { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; margin-right: 12px; background: #eee; }
.item-info { flex: 1; }
.dish-name { font-weight: 600; color: #334155; }
.dish-meta { font-size: 12px; color: #94a3b8; }
.item-qty { width: 50px; text-align: center; font-weight: bold; color: #64748b; }
.item-total { width: 80px; text-align: right; font-weight: 700; color: #334155; }

.order-footer { margin-top: 15px; text-align: right; }
.total-wrapper { display: inline-block; background: #fff1f2; padding: 8px 16px; border-radius: 30px; color: #be123c; }
.total-price { font-size: 20px; font-weight: 800; margin-left: 8px; }

/* 按钮 */
.btn-primary { background: #d32f2f; color: white; border: none; padding: 8px 20px; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #fee2e2; color: #ef4444; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
.btn-finish { background: #10b981; color: white; border: none; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-finish:hover { background: #059669; }
.btn-refresh { background: white; border: 1px solid #cbd5e1; padding: 6px 12px; border-radius: 4px; cursor: pointer; }

/* 弹窗 */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-form { background: white; padding: 30px; border-radius: 12px; width: 400px; animation: popIn 0.3s; }
.modal-form h3 { margin-top: 0; color: #1e293b; }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 500; font-size: 14px; color: #475569; }
.form-group input, .form-group textarea { width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; }
.form-group textarea { height: 80px; resize: none; }
.img-preview { margin-top: 10px; width: 100%; height: 150px; background: #f8fafc; border-radius: 6px; overflow: hidden; }
.img-preview img { width: 100%; height: 100%; object-fit: cover; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.btn-cancel { background: transparent; border: 1px solid #cbd5e1; color: #64748b; padding: 10px 20px; border-radius: 6px; cursor: pointer; }

@keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>