<template>
  <el-container class="layout-shell">
    <el-aside width="260px" class="aside">
      <div class="logo-container">
        <div class="logo">
          <div class="logo-icon-wrapper">
            <el-icon class="logo-icon"><House /></el-icon>
          </div>
          <span class="logo-text">燃气管理系统</span>
        </div>
      </div>
      <el-menu
        :default-active="active"
        router
        class="menu"
        :collapse-transition="false"
      >
        <el-menu-item index="/customers" class="menu-item">
          <el-icon><User /></el-icon>
          <span>客户与表具</span>
        </el-menu-item>
        <el-menu-item index="/readings" class="menu-item">
          <el-icon><Document /></el-icon>
          <span>抄表录入</span>
        </el-menu-item>
        <el-menu-item index="/bills" class="menu-item">
          <el-icon><Money /></el-icon>
          <span>账单与缴费</span>
        </el-menu-item>
        <el-menu-item index="/work-orders" class="menu-item">
          <el-icon><Tools /></el-icon>
          <span>工单管理</span>
        </el-menu-item>
      </el-menu>
      
      <div class="aside-footer">
        <div class="version-tag">v1.0.0</div>
      </div>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header glass-effect">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentPage">{{ currentPage }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="action-item">
            <el-icon><Bell /></el-icon>
          </div>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar class="user-avatar" :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-details">
                <span class="user-name">{{ email }}</span>
                <span class="user-role">管理员</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="danger-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <div class="page-container">
          <router-view v-slot="{ Component }">
            <transition name="slide-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, User, Document, Money, Tools, ArrowDown, Bell, Setting, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const active = computed(() => route.path)
const email = localStorage.getItem('email') || 'Admin'

// 根据当前路由获取页面名称
const currentPage = computed(() => {
  const path = route.path
  if (path.includes('/customers')) return '客户与表具'
  if (path.includes('/readings')) return '抄表录入'
  if (path.includes('/bills')) return '账单与缴费'
  if (path.includes('/work-orders')) return '工单管理'
  return ''
})

const handleCommand = (command) => {
  if (command === 'logout') {
    logout()
  } else if (command === 'profile') {
    ElMessage.info('个人资料功能开发中')
  } else if (command === 'settings') {
    ElMessage.info('系统设置功能开发中')
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('email')
  router.push('/login')
}
</script>

<style scoped>
.layout-shell {
  height: 100vh;
  width: 100vw;
  background-color: var(--bg-color-page);
  overflow: hidden;
}

/* 侧边栏样式 */
.aside {
  background: #001529; /* 深色背景 */
  color: rgba(255, 255, 255, 0.65);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  z-index: 1001;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  padding-left: 24px;
  background: #002140;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon-wrapper {
  width: 32px;
  height: 32px;
  background: var(--primary-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 122, 255, 0.3);
}

.logo-icon {
  font-size: 20px;
  color: #fff;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.menu {
  border-right: none;
  background: transparent;
  flex: 1;
  padding-top: 16px;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.65);
}

.menu-item:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.08);
}

.menu-item.is-active {
  color: #fff;
  background-color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
  font-weight: 500;
}

.menu-item .el-icon {
  font-size: 18px;
  margin-right: 10px;
  color: inherit;
}

.aside-footer {
  padding: 16px;
  text-align: center;
}

.version-tag {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

/* 主内容区样式 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--bg-color-page);
}

.header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  color: var(--text-color-secondary);
  transition: all 0.3s;
}

.action-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
  color: var(--text-color-primary);
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.user-details {
  margin: 0 12px;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color-primary);
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: var(--text-color-secondary);
  line-height: 1.2;
}

.dropdown-icon {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.main-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.page-container {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .aside {
    width: 0 !important;
    overflow: hidden;
  }
  
  .page-container {
    padding: 16px;
  }
}
</style>
