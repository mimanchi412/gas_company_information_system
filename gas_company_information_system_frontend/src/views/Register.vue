<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="auth-content">
          <div class="auth-logo">
            <el-icon class="auth-logo-icon"><House /></el-icon>
            <h1 class="auth-logo-text">燃气管理系统</h1>
          </div>
          <div class="auth-slogan">
            <p class="slogan-main">加入我们 · 开启新程</p>
            <p class="slogan-sub">只需几步，即可享受企业级燃气管理服务</p>
          </div>
          <div class="auth-features">
            <div class="feature-item">
              <FeatureUser />
              <span>账户创建</span>
            </div>
            <div class="feature-item">
              <FeatureBill />
              <span>数据安全</span>
            </div>
            <div class="feature-item">
              <FeatureWork />
              <span>快速接入</span>
            </div>
          </div>
        </div>
        <div class="decoration-circle-1"></div>
        <div class="decoration-circle-2"></div>
      </div>

      <div class="auth-right">
        <div class="form-wrapper">
          <div class="auth-header">
            <h2 class="auth-title">创建新账户</h2>
            <p class="auth-subtitle">填写以下信息完成注册</p>
          </div>

          <el-form
              ref="registerFormRef"
              :model="form"
              :rules="registerRules"
              label-position="top"
              size="large"
              @keyup.enter="onSubmit"
          >
            <el-form-item label="邮箱" prop="email">
              <el-input
                  v-model="form.email"
                  type="email"
                  placeholder="请输入邮箱"
                  prefix-icon="Message"
              />
            </el-form-item>

            <el-form-item label="昵称" prop="name">
              <el-input
                  v-model="form.name"
                  placeholder="请输入您的称呼"
                  prefix-icon="User"
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="设置登录密码"
                  prefix-icon="Lock"
                  show-password
              />
            </el-form-item>

            <el-form-item label="验证码" prop="code">
              <div class="code-input-group">
                <el-input
                    v-model="form.code"
                    placeholder="6位验证码"
                    prefix-icon="Key"
                    maxlength="6"
                    class="code-input"
                />
                <el-button
                    type="primary"
                    plain
                    :disabled="countdown > 0"
                    :loading="sendingCode"
                    class="code-button"
                    @click="sendCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item class="auth-button-item">
              <el-button
                  type="primary"
                  class="auth-button"
                  :loading="loading"
                  @click="onSubmit"
              >
                注册并登录
              </el-button>
            </el-form-item>

            <div class="auth-footer">
              <span>已有账户？</span>
              <el-button type="primary" link @click="goLogin" class="link-btn">立即登录</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Lock, Message, Key } from '@element-plus/icons-vue'
import FeatureUser from '@/components/icons/FeatureUser.vue'
import FeatureBill from '@/components/icons/FeatureBill.vue'
import FeatureWork from '@/components/icons/FeatureWork.vue'
import { authApi } from '@/services/api'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

const form = reactive({
  email: '',
  name: '',
  password: '',
  code: ''
})

const registerRules = reactive({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为 6 位', trigger: 'blur' }
  ]
})

const goLogin = () => router.push('/login')

const sendCode = async () => {
  try {
    await registerFormRef.value.validateField('email')
    sendingCode.value = true
    await authApi.sendCode(form.email, 'REGISTER')
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    if (error?.message) {
      if(error !== 'email') console.error(error)
    } else {
      ElMessage.error(error.response?.data?.message || '发送失败')
    }
  } finally {
    sendingCode.value = false
  }
}

const onSubmit = async () => {
  if (!registerFormRef.value) return
  try {
    await registerFormRef.value.validate()
    loading.value = true
    await authApi.register(form)
    ElMessage.success('注册成功')
    setTimeout(() => router.push('/login'), 1500)
  } catch (error) {
    if (!error.message) {
      // 非表单验证错误
      ElMessage.error(error.response?.data?.message || '注册失败')
    }
  } finally {
    loading.value = false
  }
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

/* 左侧样式 */
.auth-left {
  position: relative;
  width: 50%;
  height: 100%;
  /* 统一使用蓝色渐变 */
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

/* 装饰 */
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

/* 右侧样式 */
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
  max-width: 420px;
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

.code-input-group {
  display: flex;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.code-button {
  width: 110px;
}

.auth-button-item {
  margin-top: 10px;
  margin-bottom: 20px;
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

/* 响应式 */
@media (max-width: 900px) {
  .auth-left {
    display: none;
  }
  .auth-right {
    width: 100%;
  }
  .form-wrapper {
    max-width: 500px;
  }
}
</style>
