<template>
  <div class="bills-container">
    <el-card class="search-card">
      <div class="search-header">
        <h3 class="search-title">
          <el-icon><Search /></el-icon>
          账单查询
        </h3>
      </div>
      <div class="search-toolbar">
        <div class="search-input-wrapper">
          <el-input
              v-model="filters.customerId"
              type="number"
              placeholder="按客户ID过滤"
              class="search-input"
              clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </div>
        <el-select
            v-model="filters.status"
            placeholder="按状态"
            clearable
            class="status-select"
        >
          <el-option label="未付" value="UNPAID" />
          <el-option label="已付" value="PAID" />
          <el-option label="逾期" value="OVERDUE" />
        </el-select>
        <el-button type="primary" @click="loadBills" class="search-button">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="resetFilters" class="reset-button">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button type="success" @click="openCreate" class="add-button">
          <el-icon><Plus /></el-icon>
          生成账单
        </el-button>
        <el-button type="warning" @click="loadArrears" class="arrears-button">
          <el-icon><Warning /></el-icon>
          欠费/逾期
        </el-button>
      </div>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3 class="table-title">
          <el-icon><Document /></el-icon>
          账单列表
        </h3>
        <div class="table-info">
          <el-tag type="info" size="large">共 {{ bills.length }} 条记录</el-tag>
        </div>
      </div>

      <el-table
          :data="bills"
          stripe
          class="bills-table"
          v-loading="loading"
          element-loading-text="加载中..."
          empty-text="暂无账单数据"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" fixed="left" />

        <el-table-column prop="customerId" label="客户ID" min-width="120" align="center">
          <template #default="scope">
            <el-tag type="primary" size="small">{{ scope.row.customerId }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="meterId" label="表具ID" min-width="120" align="center">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.meterId }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="金额" min-width="130" align="right">
          <template #default="scope">
            <span class="amount">¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="dueDate" label="到期日" min-width="140" align="center">
          <template #default="scope">
            <span class="due-date">{{ formatDate(scope.row.dueDate) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
                :type="getStatusType(scope.row.status)"
                size="small"
                effect="dark"
            >
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button
                  type="success"
                  @click="openPay(scope.row)"
                  :disabled="scope.row.status==='PAID'"
                  size="small"
                  class="pay-btn"
              >
                <el-icon style="margin-right: 4px"><Money /></el-icon>
                支付
              </el-button>

              <el-button
                  type="primary"
                  @click="viewDetails(scope.row)"
                  size="small"
              >
                <el-icon style="margin-right: 4px"><View /></el-icon>
                详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        v-model="createDialog.visible"
        title="生成账单"
        width="520px"
        class="create-dialog"
    >
      <el-form
          :model="createDialog.form"
          :rules="createRules"
          ref="createFormRef"
          label-width="100px"
          class="create-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input
                  v-model.number="createDialog.form.customerId"
                  type="number"
                  placeholder="请输入客户ID"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表具ID" prop="meterId">
              <el-input
                  v-model.number="createDialog.form.meterId"
                  type="number"
                  placeholder="请输入表具ID"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="抄表ID" prop="readingId">
              <el-input
                  v-model.number="createDialog.form.readingId"
                  type="number"
                  placeholder="请输入抄表ID"
              >
                <template #prefix>
                  <el-icon><Reading /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="金额" prop="amount">
              <el-input
                  v-model.number="createDialog.form.amount"
                  type="number"
                  placeholder="请输入金额"
              >
                <template #prefix>
                  <el-icon><Money /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="到期日" prop="dueDate">
          <el-date-picker
              v-model="createDialog.form.dueDate"
              type="date"
              placeholder="请选择到期日"
              style="width:100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createDialog.visible=false">取消</el-button>
          <el-button type="primary" @click="createBill" :loading="createDialog.loading">生成</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="payDialog.visible"
        title="支付账单"
        width="420px"
        class="pay-dialog"
    >
      <el-form
          :model="payDialog.form"
          :rules="payRules"
          ref="payFormRef"
          label-width="80px"
          class="pay-form"
      >
        <el-form-item label="金额" prop="amount">
          <el-input
              v-model.number="payDialog.form.amount"
              type="number"
              placeholder="请输入支付金额"
          >
            <template #prefix>
              <el-icon><Money /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="方式" prop="method">
          <el-select v-model="payDialog.form.method" placeholder="请选择支付方式" style="width:100%">
            <el-option label="现金" value="CASH" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="银行卡" value="CARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
              v-model="payDialog.form.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="payDialog.visible=false">取消</el-button>
          <el-button type="success" plain @click="payBill" :loading="payDialog.loading">支付</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="detailDialog.visible"
        title="账单详情"
        width="500px"
        class="detail-dialog"
    >
      <div class="bill-detail" v-if="detailDialog.data">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单ID">{{ detailDialog.data.id }}</el-descriptions-item>
          <el-descriptions-item label="客户ID">{{ detailDialog.data.customerId }}</el-descriptions-item>
          <el-descriptions-item label="表具ID">{{ detailDialog.data.meterId }}</el-descriptions-item>
          <el-descriptions-item label="抄表ID">{{ detailDialog.data.readingId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ detailDialog.data.amount.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="到期日">{{ formatDate(detailDialog.data.dueDate) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailDialog.data.status)" effect="dark">
              {{ getStatusLabel(detailDialog.data.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(detailDialog.data.createdAt) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialog.visible=false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search, Refresh, Plus, Warning, Document, User, Reading, Money, View
} from '@element-plus/icons-vue'
import { billingApi } from '@/services/api'

// 响应式数据
const bills = ref([])
const loading = ref(false)
const filters = reactive({ customerId: '', status: '' })

// 表单引用
const createFormRef = ref(null)
const payFormRef = ref(null)

// 对话框状态
const createDialog = reactive({
  visible: false,
  loading: false,
  form: { customerId: null, meterId: null, readingId: null, amount: null, dueDate: '' }
})

const payDialog = reactive({
  visible: false,
  loading: false,
  id: null,
  form: { amount: null, method: '', remark: '' }
})

const detailDialog = reactive({
  visible: false,
  data: null
})

// 表单验证规则
const createRules = {
  customerId: [
    { required: true, message: '请输入客户ID', trigger: 'blur' },
    { type: 'number', message: '客户ID必须为数字', trigger: 'blur' }
  ],
  meterId: [
    { required: true, message: '请输入表具ID', trigger: 'blur' },
    { type: 'number', message: '表具ID必须为数字', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ],
  dueDate: [
    { required: true, message: '请选择到期日', trigger: 'change' }
  ]
}

const payRules = {
  amount: [
    { required: true, message: '请输入支付金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ],
  method: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 获取账单列表
const loadBills = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.customerId) params.customerId = filters.customerId
    if (filters.status) params.status = filters.status
    const res = await billingApi.listBills(params)
    if (!res.success) throw new Error(res.message)
    bills.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message)
  } finally {
    loading.value = false
  }
}

// 获取欠费/逾期账单
const loadArrears = async () => {
  loading.value = true
  try {
    const res = await billingApi.arrears()
    if (!res.success) throw new Error(res.message)
    bills.value = res.data || []
    ElMessage.success('已加载欠费/逾期账单')
  } catch (err) {
    ElMessage.error(err.message)
  } finally {
    loading.value = false
  }
}

// 重置筛选条件
const resetFilters = () => {
  filters.customerId = ''
  filters.status = ''
  loadBills()
}

// 打开生成账单对话框
const openCreate = () => {
  createDialog.form = { customerId: null, meterId: null, readingId: null, amount: null, dueDate: '' }
  createDialog.visible = true
}

// 生成账单
const createBill = async () => {
  if (!createFormRef.value) return

  try {
    await createFormRef.value.validate()
    createDialog.loading = true

    const res = await billingApi.createBill(createDialog.form)
    if (!res.success) throw new Error(res.message)

    ElMessage.success('账单生成成功')
    createDialog.visible = false
    loadBills()
  } catch (err) {
    if (err.message) {
      ElMessage.error(err.message)
    }
  } finally {
    createDialog.loading = false
  }
}

// 打开支付对话框
const openPay = (row) => {
  payDialog.id = row.id
  payDialog.form = { amount: row.amount, method: 'CASH', remark: '' }
  payDialog.visible = true
}

// 支付账单
const payBill = async () => {
  if (!payFormRef.value) return

  try {
    await payFormRef.value.validate()
    payDialog.loading = true

    const res = await billingApi.payBill(payDialog.id, payDialog.form)
    if (!res.success) throw new Error(res.message)

    ElMessage.success('支付成功')
    payDialog.visible = false
    loadBills()
  } catch (err) {
    if (err.message) {
      ElMessage.error(err.message)
    }
  } finally {
    payDialog.loading = false
  }
}

// 查看账单详情
const viewDetails = (row) => {
  detailDialog.data = row
  detailDialog.visible = true
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'PAID': return 'success'
    case 'UNPAID': return 'warning'
    case 'OVERDUE': return 'danger'
    default: return 'info'
  }
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  switch (status) {
    case 'PAID': return '已付'
    case 'UNPAID': return '未付'
    case 'OVERDUE': return '逾期'
    default: return '未知'
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(loadBills)
</script>

<style scoped>
.bills-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.search-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.search-title {
  display: flex;
  align-items: center;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-title .el-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.search-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input-wrapper {
  flex: 1;
  min-width: 280px;
  max-width: 400px;
}

.search-input {
  width: 100%;
}

.status-select {
  width: 160px;
}

.search-button, .reset-button, .add-button, .arrears-button {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 表格卡片样式 */
.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.table-title {
  display: flex;
  align-items: center;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.table-title .el-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.table-info {
  display: flex;
  align-items: center;
}

/* 表格样式 */
.bills-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #fafafa;
}

:deep(.el-table__row) {
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 金额和日期样式 */
.amount {
  font-weight: 600;
  color: #e6a23c;
}

.due-date {
  font-family: monospace;
}

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
}

.pay-btn {
  font-weight: bold;
  padding: 8px 15px;
}

/* 对话框样式 */
.create-dialog, .pay-dialog, .detail-dialog {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #ebeef5;
}

/* 表单样式 */
.create-form, .pay-form {
  padding: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 账单详情样式 */
.bill-detail {
  padding: 10px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .bills-container {
    padding: 15px;
  }

  .search-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input-wrapper {
    max-width: 100%;
  }

  .status-select {
    width: 100%;
  }

  .search-button, .reset-button, .add-button, .arrears-button {
    justify-content: center;
  }

  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .table-info {
    align-self: flex-end;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 5vh auto;
  }

  :deep(.el-col) {
    width: 100% !important;
    margin-bottom: 0;
  }

  /* 移动端表格优化：取消固定列，防止遮挡 */
  :deep(.el-table__fixed-right),
  :deep(.el-table__fixed-left) {
    box-shadow: none !important;
    height: auto !important;
    position: static !important;
  }

  .table-actions {
    flex-direction: row;
    justify-content: center;
  }

  /* 确保表格可以横向滚动 */
  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }

  /* 设置最小宽度强制出现滚动条，防止内容挤压 */
  :deep(.el-table__body) {
    min-width: 850px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff !important; /* 强制文字白色 */
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

:deep(.el-button--success) {
  background-color: #67c23a;
  border-color: #67c23a;
  color: #fff !important; /* 强制文字白色 */
}

:deep(.el-button--success:hover) {
  background-color: #85ce61;
  border-color: #85ce61;
}

:deep(.el-button--success.is-disabled) {
  background-color: #b3e19d;
  border-color: #b3e19d;
  color: #fff;
}

:deep(.el-button--warning) {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

:deep(.el-button--warning:hover) {
  background-color: #ebb563;
  border-color: #ebb563;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(2px);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff;
}

:deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}
</style>