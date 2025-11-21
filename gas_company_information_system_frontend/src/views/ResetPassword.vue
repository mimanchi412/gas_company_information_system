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
            <p class="slogan-main">安全守护 · 找回无忧</p>
            <p class="slogan-sub">通过安全验证快速重置您的账户密码，保障账号安全</p>
          </div>
          <div class="auth-features">
            <div class="feature-item">
              <FeatureUser />
              <span>账户保护</span>
            </div>
            <div class="feature-item">
              <FeatureBill />
              <span>安全验证</span>
            </div>
            <div class="feature-item">
              <FeatureWork />
              <span>自助重置</span>
            </div>
          </div>
        </div>
        <div class="decoration-circle-1"></div>
        <div class="decoration-circle-2"></div>
      </div>

      <div class="auth-right">
        <div class="form-wrapper">
          <div class="auth-header">
            <h2 class="auth-title">找回密码</h2>
            <p class="auth-subtitle">请输入您的注册邮箱以获取验证码</p>
          </div>

          <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              size="large"
              @keyup.enter="onSubmit"
          >
            <el-form-item label="邮箱账号" prop="email">
              <el-input
                  v-model="form.email"
                  placeholder="请输入注册邮箱"
                  prefix-icon="Message"
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
                    :loading="sending"
                    class="code-button"
                    @click="sendCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="设置新密码" prop="newPassword">
              <el-input
                  v-model="form.newPassword"
                  type="password"
                  placeholder="请输入新密码（至少6位）"
                  prefix-icon="Lock"
                  show-password
              />
            </el-form-item>

            <el-form-item class="auth-button-item">
              <el-button
                  type="primary"
                  :loading="loading"
                  @click="onSubmit"
                  class="auth-button"
              >
                重置密码
              </el-button>
            </el-form-item>

            <div class="auth-footer">
              <el-button type="primary" link @click="router.push('/login')" class="link-btn">
                <el-icon><Back /></el-icon> 返回登录
              </el-button>
              <span class="divider">|</span>
              <el-button type="primary" link @click="router.push('/register')" class="link-btn">
                注册账户
              </el-button>
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
// 引入必要的图标
import { House, Lock, Message, Key, Back } from '@element-plus/icons-vue'
import FeatureUser from '@/components/icons/FeatureUser.vue'
import FeatureBill from '@/components/icons/FeatureBill.vue'
import FeatureWork from '@/components/icons/FeatureWork.vue'
import { authApi } from '@/services/api'

const router = useRouter()
const formRef = ref(null)
const form = reactive({ email: '', newPassword: '', code: '' })
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)
let timer = null

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为 6 位', trigger: 'blur' }
  ]
}

const sendCode = async () => {
  // 先单独校验邮箱格式
  try {
    await formRef.value.validateField('email')
  } catch (e) {
    return // 邮箱校验未通过
  }

  sending.value = true
  try {
    await authApi.sendCode(form.email, 'RESET')
    ElMessage.success('验证码已发送，请查收邮件')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (err) {
    ElMessage.error(err.message || '验证码发送失败')
  } finally {
    sending.value = false
  }
}

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await authApi.resetPassword({
        email: form.email,
        newPassword: form.newPassword,
        code: form.code
      })
      if (!res.success) throw new Error(res.message)
      ElMessage.success('密码重置成功，请重新登录')
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } catch (err) {
      ElMessage.error(err.message || '重置失败')
    } finally {
      loading.value = false
    }
  })
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

.auth-left {
  position: relative;
  width: 50%;
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

/* 装饰背景圆 */
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

/* --- 右侧表单区 --- */
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

/* 验证码输入组 */
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

/* 主按钮 */
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

/* 底部链接 */
.auth-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 6px;
}

.link-btn:hover {
  color: #409eff;
}

.back-link {
  font-weight: 500;
}

.divider {
  color: #dcdfe6;
}

/* 响应式适配 */
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
