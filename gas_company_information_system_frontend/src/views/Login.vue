<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="auth-gradient-bg"></div>
        <div class="auth-content">
          <div class="auth-logo">
            <el-icon class="auth-logo-icon"><House /></el-icon>
            <h1 class="auth-logo-text">燃气管理系统</h1>
          </div>
          <div class="auth-slogan">
            <p class="slogan-main">智慧燃气 · 高效运营</p>
            <p class="slogan-sub">为燃气企业提供一站式客户、账单与工单管理解决方案</p>
          </div>
          <div class="auth-features">
            <div class="feature-item">
              <FeatureUser />
              <span>精准户籍管理</span>
            </div>
            <div class="feature-item">
              <FeatureBill />
              <span>智能计费结算</span>
            </div>
            <div class="feature-item">
              <FeatureWork />
              <span>高效工单流转</span>
            </div>
          </div>
        </div>
        <div class="decoration-circle-1"></div>
        <div class="decoration-circle-2"></div>
      </div>

      <div class="auth-right">
        <div class="form-wrapper">
          <div class="auth-header">
            <h2 class="auth-title">欢迎回来</h2>
            <p class="auth-subtitle">请登录您的账户以继续</p>
          </div>
          <el-form
              :model="form"
              :rules="rules"
              ref="formRef"
              label-position="top"
              size="large"
              @keyup.enter="onSubmit"
          >
            <el-form-item label="邮箱账号" prop="email">
              <el-input
                  v-model="form.email"
                  placeholder="请输入您的邮箱"
                  prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item label="登录密码" prop="password">
              <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入您的密码"
                  prefix-icon="Lock"
                  show-password
              />
            </el-form-item>
            <div class="auth-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-button type="primary" link class="forgot-password" @click="goReset">忘记密码?</el-button>
            </div>
            <el-form-item class="auth-button-item">
              <el-button
                  type="primary"
                  :loading="loading"
                  @click="onSubmit"
                  class="auth-button"
              >
                登录
              </el-button>
            </el-form-item>
            <div class="auth-footer">
              <span>还没有账户?</span>
              <el-button type="primary" link @click="goRegister" class="link-btn">立即注册</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House } from '@element-plus/icons-vue'
import FeatureUser from '@/components/icons/FeatureUser.vue'
import FeatureBill from '@/components/icons/FeatureBill.vue'
import FeatureWork from '@/components/icons/FeatureWork.vue'
import { authApi } from '@/services/api'

const router = useRouter()
const formRef = ref()
const form = reactive({ email: '', password: '' })
const loading = ref(false)
const rememberMe = ref(false)

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await authApi.login(form)
      if (!res.success) throw new Error(res.message)
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('email', res.data.email)
      if (rememberMe.value) {
        localStorage.setItem('rememberedEmail', form.email)
      } else {
        localStorage.removeItem('rememberedEmail')
      }
      ElMessage.success('登录成功')
      router.push('/customers')
    } catch (err) {
      ElMessage.error(err.message)
    } finally {
      loading.value = false
    }
  })
}

const goRegister = () => router.push('/register')
const goReset = () => router.push('/reset-password')
const rememberedEmail = localStorage.getItem('rememberedEmail')
if (rememberedEmail) {
  form.email = rememberedEmail
  rememberMe.value = true
}
</script>

<style scoped>
.auth-page {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: #fff;
}

.auth-container {
  display: flex;
  height: 100%;
  width: 100%;
}

/* 左侧样式 - 全屏半边 */
.auth-left {
  position: relative;
  width: 50%; /* 占据左半边 */
  height: 100%;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.auth-content {
  position: relative;
  z-index: 2;
  padding: 60px;
  max-width: 600px;
}

/* 装饰性圆圈 */
.decoration-circle-1 {
  position: absolute;
  top: -100px;
  left: -100px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 1;
}
.decoration-circle-2 {
  position: absolute;
  bottom: -50px;
  right: -50px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  z-index: 1;
}

.auth-logo {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.auth-logo-icon {
  font-size: 48px;
  margin-right: 16px;
  background: rgba(255, 255, 255, 0.2);
  padding: 12px;
  border-radius: 12px;
}

.auth-logo-text {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.slogan-main {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 16px;
}

.slogan-sub {
  font-size: 16px;
  opacity: 0.8;
  line-height: 1.6;
  margin-bottom: 40px;
}

.auth-features {
  display: flex;
  gap: 30px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-item .el-icon {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  padding: 12px;
  border-radius: 50%;
}

.feature-item span {
  font-size: 14px;
  opacity: 0.9;
}

/* 右侧样式 - 全屏半边白色 */
.auth-right {
  width: 50%;
  height: 100%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-wrapper {
  width: 100%;
  max-width: 420px; /* 稍微宽一点 */
  padding: 40px;
}

.auth-header {
  margin-bottom: 40px;
}

.auth-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.auth-subtitle {
  color: #909399;
  font-size: 14px;
}

.auth-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.auth-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
}

.auth-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.link-btn {
  font-weight: 500;
  margin-left: 5px;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .auth-left {
    display: none; /* 小屏幕隐藏左侧，只保留右侧表单 */
  }
  .auth-right {
    width: 100%;
  }
  .form-wrapper {
    max-width: 500px;
  }
}
</style>
