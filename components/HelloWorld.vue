<template>
  <div class="app-container">
    <!-- 1. �������� -->
    <header class="header">
      <h1>ζ�ٲ��� - ���ߵ��</h1>
    </header>

    <!-- 2. ��Ʒ�б����� (���񲼾�) -->
    <div class="menu-container">
      <div 
        v-for="item in menuItems" 
        :key="item.id" 
        class="menu-card"
        @click="showDetail(item)"
      >
        <!-- ͼƬ����CSS��ǿ�Ʋü���������� -->
        <div class="image-box">
          <img :src="item.imageUrl" :alt="item.name" />
          <!-- ������ʾ -->
          <div class="hover-tip">����鿴����</div>
        </div>
        
        <!-- ��Ƭ������Ϣ -->
        <div class="card-info">
          <h3>{{ item.name }}</h3>
          <div class="price-row">
            <span class="price">?{{ item.price }}</span>
            <!-- ��ֹð�ݣ��������Ӻ�ʱ��������� -->
            <button class="add-btn" @click.stop="addToCart(item)">���빺�ﳵ</button>
          </div>
        </div>
        
        <!-- ���ﳵ�Ǳ� (��ʾ�ò�Ʒѡ�˼���) -->
        <div v-if="getItemCount(item.id) > 0" class="badge">
          {{ getItemCount(item.id) }}
        </div>
      </div>
    </div>

    <!-- 3. ���鵯�� (Modal) -->
    <div v-if="isModalOpen" class="modal-overlay" @click="closeDetail">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeDetail">��</button>
        <img :src="selectedItem.imageUrl" class="modal-img">
        <h2>{{ selectedItem.name }}</h2>
        <p class="modal-desc">{{ selectedItem.description || '������ϸ���ܣ���Ҫ��ʵ��Ϊ׼��' }}</p>
        <div class="modal-footer">
          <span class="modal-price">?{{ selectedItem.price }}</span>
          <button class="modal-add-btn" @click="addToCart(selectedItem); closeDetail()">���붩��</button>
        </div>
      </div>
    </div>

    <!-- 4. �ײ��������ﳵ -->
    <div class="cart-bar">
      <div class="cart-info">
        <span class="cart-icon">?</span>
        <div class="cart-text">
          <div>��ѡ��Ʒ: {{ totalCount }} ��</div>
          <div class="total-price">�ϼ�: ?{{ totalPrice }}</div>
        </div>
      </div>
      <button 
        class="pay-btn" 
        :disabled="totalCount === 0"
        @click="submitOrder"
      >
        ����֧���µ�
      </button>
    </div>
    
    <!-- ֻ��Ϊ�˰ѵײ��ſ����������ݱ����ﳵ��ס -->
    <div style="height: 80px;"></div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MenuOrder',
  data() {
    return {
      // ģ��˵����� (ͼƬ��ʹ�����1920��ͼҲû���⣬CSS�ᴦ��)
      menuItems: [
        { id: 1, name: '���ƺ�����', price: 48.0, description: 'ѡ���ϵ��廨�⣬��������3Сʱ���ʶ����塣', imageUrl: 'https://images.unsplash.com/photo-1602088113235-229c19758e9f?w=800' },
        { id: 2, name: '��������', price: 32.0, description: '���䴨�ˣ�����΢����������ࡣ', imageUrl: 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=800' },
        { id: 3, name: '��������', price: 68.0, description: '��ɱ���㣬��������ԭ֭ԭζ��', imageUrl: 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=800' },
        { id: 4, name: 'ʱ���߲�', price: 18.0, description: '���ݼ��ڱ任����֤�߲����ʴ��ۡ�', imageUrl: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=800' },
        { id: 5, name: '���Ŷ���', price: 22.0, description: '�������㣬�·�������', imageUrl: 'https://images.unsplash.com/photo-1590301157890-4810ed352733?w=800' },
      ],
      cart: [], // ���ﳵ����: [{itemId: 1, quantity: 2, price: 48.0}]
      isModalOpen: false,
      selectedItem: {}
    };
  },
  computed: {
    totalCount() {
      return this.cart.reduce((sum, item) => sum + item.quantity, 0);
    },
    totalPrice() {
      return this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
    }
  },
  mounted() {
    // ҳ�����ʱ��ȡ�˵�����ȡ��ע�ͶԽ���ĺ��
    // this.fetchMenu();
  },
  methods: {
    async fetchMenu() {
      // axios.get('http://localhost:8080/menu/list').then(res => this.menuItems = res.data);
    },
    
    // �����鵯��
    showDetail(item) {
      this.selectedItem = item;
      this.isModalOpen = true;
    },
    closeDetail() {
      this.isModalOpen = false;
    },

    // ��ȡĳ�����ڹ��ﳵ�������
    getItemCount(id) {
      const item = this.cart.find(i => i.itemId === id);
      return item ? item.quantity : 0;
    },

    // ���빺�ﳵ�߼�
    addToCart(item) {
      const existingItem = this.cart.find(i => i.itemId === item.id);
      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.cart.push({
          itemId: item.id,
          name: item.name, // ������Կ�
          price: item.price,
          quantity: 1
        });
      }
    },

    // �ύ���������
    async submitOrder() {
      if (this.cart.length === 0) return;

      const orderPayload = {
        // �ܽ��
        totalAmount: parseFloat(this.totalPrice),
        // ���������б�
        items: this.cart.map(item => ({
          menuItemId: item.itemId,
          quantity: item.quantity,
          price: item.price
        }))
      };

      try {
        console.log("���͸���˵�����:", orderPayload);
        // ��ʵ����
        // const res = await axios.post('http://localhost:8080/order/add', orderPayload);
        // if(res.data.code === 200) { alert('�µ��ɹ���'); this.cart = []; }
        
        alert("ģ���µ��ɹ���\n��鿴����̨(F12)ȷ�Ϸ��͵�JSON��ʽ��");
        this.cart = []; // ��չ��ﳵ
      } catch (e) {
        alert("�µ�ʧ�ܣ�" + e.message);
      }
    }
  }
};
</script>

<style scoped>
/* ȫ������ */
.app-container {
  font-family: "Microsoft YaHei", sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

/* --- ���Ĳ��֣�����ϵͳ --- */
.menu-container {
  display: grid;
  /* �ؼ��������п�һ�з�3������Ļխ���Զ���2�� */
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); 
  gap: 20px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* --- ��Ƭ��ʽ --- */
.menu-card {
  background: white;
  border-radius: 12px;
  overflow: hidden; /* ��֤ͼƬԲ�ǲ���� */
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  cursor: pointer;
  position: relative;
  transition: transform 0.2s;
}
.menu-card:hover {
  transform: translateY(-5px); /* �����ϸ�Ч�� */
}

/* ���������ͼƬ����ĺ��Ĵ��룡���� */
.image-box {
  width: 100%;
  height: 200px; /* ǿ�ƹ̶��߶ȣ�������1281px�� */
  position: relative;
  background: #eee;
}

.image-box img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* �ؼ����Զ��ü����ಿ�֣��������� */
}

.hover-tip {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: rgba(0,0,0,0.5);
  color: white;
  text-align: center;
  padding: 5px 0;
  opacity: 0;
  transition: opacity 0.3s;
}
.menu-card:hover .hover-tip {
  opacity: 1;
}

.card-info {
  padding: 15px;
}
.card-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #e64340;
  font-size: 20px;
  font-weight: bold;
}
.add-btn {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 6px 15px;
  border-radius: 20px;
  cursor: pointer;
}
.add-btn:hover {
  background-color: #3aa876;
}

/* ���Ͻ������Ǳ� */
.badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff4d4f;
  color: white;
  width: 25px;
  height: 25px;
  border-radius: 50%;
  text-align: center;
  line-height: 25px;
  font-size: 14px;
  font-weight: bold;
}

/* --- �ײ����ﳵ�� --- */
.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
  color: white;
  z-index: 999;
}
.cart-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.cart-icon {
  font-size: 24px;
}
.total-price {
  font-size: 18px;
  color: #42b983;
  font-weight: bold;
}
.pay-btn {
  height: 100%;
  width: 120px;
  background: #e64340;
  border: none;
  color: white;
  font-size: 16px;
  margin-right: -20px; /* ���� */
  cursor: pointer;
}
.pay-btn:disabled {
  background: #666;
  cursor: not-allowed;
}

/* --- ���鵯����ʽ --- */
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.6);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  width: 90%;
  max-width: 500px;
  border-radius: 10px;
  padding: 20px;
  position: relative;
  animation: popIn 0.3s;
}
.modal-img {
  width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}
.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  font-size: 30px;
  cursor: pointer;
  color: #666;
}
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}
.modal-price {
  font-size: 24px;
  color: #e64340;
  font-weight: bold;
}
.modal-add-btn {
  background: #42b983;
  color: white;
  border: none;
  padding: 10px 25px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

@keyframes popIn {
  from { transform: scale(0.8); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
</style>