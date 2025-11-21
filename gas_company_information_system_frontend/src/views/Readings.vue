<template>
  <div class="readings-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><EditPen /></el-icon>
        抄表录入
      </h2>
      <p class="page-description">录入燃气表读数，系统将自动计算用量并生成账单</p>
    </div>

    <!-- 抄表录入表单 -->
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Document /></el-icon>
            抄表信息
          </span>
          <el-tag type="info" size="small">必填项已标记 *</el-tag>
        </div>
      </template>
      
      <el-form 
        :model="form" 
        :rules="formRules"
        ref="readingFormRef"
        label-width="120px"
        class="reading-form"
        size="large"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="表具编号" prop="meterNo">
              <el-input 
                v-model="form.meterNo" 
                placeholder="请输入表具编号"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前读数" prop="readingValue">
              <el-input 
                v-model.number="form.readingValue" 
                placeholder="请输入当前读数"
                type="number"
                :min="0"
                clearable
              >
                <template #prefix>
                  <el-icon><DataAnalysis /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="抄表时间" prop="readingTime">
              <el-date-picker 
                v-model="form.readingTime" 
                type="datetime" 
                placeholder="请选择抄表时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账期" prop="periodLabel">
              <el-input 
                v-model="form.periodLabel" 
                placeholder="例如：2025-11"
                clearable
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="抄表员" prop="readerName">
              <el-input 
                v-model="form.readerName" 
                placeholder="请输入抄表员姓名"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input 
                v-model="form.remark" 
                placeholder="请输入备注信息"
                clearable
              >
                <template #prefix>
                  <el-icon><ChatDotRound /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item class="form-actions">
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="submit"
            size="large"
            class="submit-button"
          >
            <el-icon><Check /></el-icon>
            提交抄表数据
          </el-button>
          <el-button @click="resetForm" size="large" class="reset-button">
            <el-icon><RefreshRight /></el-icon>
            重置表单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 最近录入记录 -->
    <el-card class="history-card" v-if="last">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Clock /></el-icon>
            最近录入记录
          </span>
        </div>
      </template>
      
      <el-alert 
        type="success" 
        :closable="false" 
        class="success-alert"
      >
        <template #title>
          <div class="alert-content">
            <div class="alert-item">
              <span class="label">表具编号：</span>
              <span class="value">{{ last.meterNo }}</span>
            </div>
            <div class="alert-item">
              <span class="label">当前读数：</span>
              <span class="value">{{ last.readingValue }}</span>
            </div>
            <div class="alert-item">
              <span class="label">录入时间：</span>
              <span class="value">{{ formatDate(last.readingTime) }}</span>
            </div>
          </div>
        </template>
      </el-alert>
    </el-card>

    <!-- 历史记录表格 -->
    <el-card class="history-table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><List /></el-icon>
            抄表历史记录
          </span>
          <div class="header-actions">
            <el-button 
              type="primary" 
              @click="loadHistory"
              :loading="historyLoading"
              size="small"
            >
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="history" 
        v-loading="historyLoading"
        element-loading-text="加载中..."
        empty-text="暂无历史记录"
        class="history-table"
      >
        <el-table-column prop="meterNo" label="表具编号" width="150" align="center">
          <template #default="scope">
            <el-tag type="primary" size="small">{{ scope.row.meterNo }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readingValue" label="读数" width="120" align="right">
          <template #default="scope">
            <span class="reading-value">{{ scope.row.readingValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="periodLabel" label="账期" width="120" align="center" />
        <el-table-column prop="readerName" label="抄表员" width="120" align="center" />
        <el-table-column prop="readingTime" label="抄表时间" width="180" align="center">
          <template #default="scope">
            <span class="reading-time">{{ formatDate(scope.row.readingTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  EditPen,
  Document,
  DataAnalysis,
  Calendar,
  User,
  ChatDotRound,
  Check,
  RefreshRight,
  Clock,
  List,
  Refresh
} from '@element-plus/icons-vue'
import { billingApi } from '@/services/api'

// 响应式变量
const loading = ref(false)
const historyLoading = ref(false)
const last = ref(null)
const history = ref([])
const readingFormRef = ref(null)

// 表单数据
const form = reactive({
  meterNo: '',
  readingValue: null,
  readingTime: '',
  periodLabel: '',
  readerName: '',
  remark: ''
})

// 表单验证规则
const formRules = reactive({
  meterNo: [
    { required: true, message: '请输入表具编号', trigger: 'blur' }
  ],
  readingValue: [
    { required: true, message: '请输入当前读数', trigger: 'blur' },
    { type: 'number', min: 0, message: '读数必须为非负数', trigger: 'blur' }
  ],
  readingTime: [
    { required: true, message: '请选择抄表时间', trigger: 'change' }
  ],
  periodLabel: [
    { required: true, message: '请输入账期', trigger: 'blur' }
  ],
  readerName: [
    { required: true, message: '请输入抄表员姓名', trigger: 'blur' }
  ]
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 重置表单
const resetForm = () => {
  if (readingFormRef.value) {
    readingFormRef.value.resetFields()
  }
  // 设置默认时间
  form.readingTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  // 设置默认账期
  const now = new Date()
  form.periodLabel = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}

const loadHistory = () => {
  // 暂无历史接口，这里保持本地列表
}

// 提交表单
const submit = async () => {
  if (!readingFormRef.value) return

  try {
    await readingFormRef.value.validate()
    loading.value = true

    const res = await billingApi.recordReading({ ...form })
    if (!res.success) throw new Error(res.message)
    ElMessage.success('抄表数据提交成功')

    last.value = res.data
    history.value = [res.data, ...history.value].slice(0, 10)
    resetForm()
  } catch (error) {
    if (error.name !== 'ElFormValidateError') {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  form.readingTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  const now = new Date()
  form.periodLabel = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
})
</script>

<style scoped>
.readings-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 140px);
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.page-title .el-icon {
  margin-right: 12px;
  font-size: 32px;
  color: #409EFF;
}

.page-description {
  color: #606266;
  font-size: 16px;
  margin: 0;
}

/* 卡片通用样式 */
.form-card,
.history-card,
.history-table-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 18px;
  color: #303133;
}

.card-title .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

/* 表单样式 */
.reading-form {
  padding: 10px 0;
}

.reading-form .el-form-item {
  margin-bottom: 24px;
}

.reading-form .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.reading-form .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.reading-form .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.reading-form .el-date-editor {
  width: 100%;
}

.form-actions {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px dashed #e4e7ed;
}

.submit-button {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.reset-button {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 8px;
  margin-left: 16px;
  transition: all 0.3s;
}

.reset-button:hover {
  transform: translateY(-2px);
}

/* 最近录入记录样式 */
.success-alert {
  border-radius: 8px;
}

.alert-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.alert-item {
  display: flex;
  align-items: center;
}

.alert-item .label {
  font-weight: 600;
  color: #606266;
  margin-right: 8px;
}

.alert-item .value {
  color: #303133;
  font-weight: 500;
}

/* 历史记录表格样式 */
.history-table {
  border-radius: 8px;
  overflow: hidden;
}

.history-table .el-table__header-wrapper {
  background-color: #f5f7fa;
}

.history-table .el-table__header th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 16px 12px;
}

.history-table .el-table__row {
  transition: all 0.3s;
}

.history-table .el-table__row:hover {
  background-color: #f0f9ff;
}

.reading-value {
  font-weight: 600;
  color: #409EFF;
  font-size: 16px;
}

.reading-time {
  color: #606266;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .readings-container {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-description {
    font-size: 14px;
  }
  
  .form-actions {
    text-align: center;
  }
  
  .submit-button,
  .reset-button {
    width: 100%;
    margin: 8px 0;
  }
  
  .alert-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .header-actions {
    margin-top: 12px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-card__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fafafa;
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-input__prefix) {
  color: #909399;
}

:deep(.el-table .el-table__cell) {
  padding: 12px 0;
}

:deep(.el-loading-spinner) {
  margin-top: -30px;
}

:deep(.el-tag) {
  border-radius: 6px;
}
</style>
