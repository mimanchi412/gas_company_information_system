<template>
  <div class="customers-container">
    <!-- 顶部统计卡片 (可选增强) -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">总客户数</div>
          <div class="stat-value">{{ customers.length }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><Odometer /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">活跃表具</div>
          <div class="stat-value">{{ totalMeters }}</div>
        </div>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-header">
        <div class="title-wrapper">
          <div class="title-indicator"></div>
          <h3 class="search-title">客户查询</h3>
        </div>
        <div class="header-actions">
          <el-button type="primary" class="add-button" @click="openCustomer()">
            <el-icon><Plus /></el-icon>
            新增客户
          </el-button>
        </div>
      </div>
      <div class="search-toolbar">
        <div class="search-input-wrapper">
          <el-input
            v-model="keyword"
            placeholder="搜索姓名、地址或电话..."
            clearable
            class="search-input"
            @keyup.enter="loadCustomers"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <el-button type="primary" plain @click="loadCustomers">
          搜索
        </el-button>
        <el-button @click="reset">
          重置
        </el-button>
      </div>
    </el-card>

    <!-- 客户列表 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        :data="customers"
        class="customers-table"
        @row-click="selectCustomer"
        v-loading="loading"
        element-loading-text="加载中..."
        highlight-current-row
      >
        <el-table-column prop="id" label="ID" width="80" align="center">
          <template #default="scope">
            <span class="id-cell">#{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="客户姓名" min-width="160">
          <template #default="scope">
            <div class="customer-name">
              <el-avatar :size="36" class="customer-avatar" :style="{ backgroundColor: getAvatarColor(scope.row.name) }">
                {{ scope.row.name.charAt(0) }}
              </el-avatar>
              <div class="name-info">
                <span class="name-text">{{ scope.row.name }}</span>
                <span class="type-text">{{ getTypeLabel(scope.row.type) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" min-width="140" />
        <el-table-column prop="email" label="电子邮箱" min-width="180" />
        <el-table-column prop="address" label="详细地址" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" link @click.stop="openCustomer(scope.row)">
                编辑
              </el-button>
              <el-button type="success" link @click.stop="loadMeters(scope.row)">
                表具
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 表具列表 (侧边抽屉或下方卡片，这里保持原有逻辑但美化) -->
    <transition name="slide-fade">
      <el-card v-if="activeCustomer" class="table-card meters-card" shadow="hover">
        <div class="table-header">
          <div class="title-wrapper">
            <div class="title-indicator green"></div>
            <h3 class="table-title">
              {{ activeCustomer.name }} 的表具
            </h3>
          </div>
          <el-button size="small" type="success" @click="openMeter()">
            <el-icon><Plus /></el-icon>
            新增表具
          </el-button>
        </div>
        
        <el-table
          :data="meters"
          class="meters-table"
          v-loading="metersLoading"
          element-loading-text="加载中..."
          empty-text="暂无表具数据"
        >
          <el-table-column prop="meterNo" label="表具编号" min-width="150">
            <template #default="scope">
              <span class="meter-no">{{ scope.row.meterNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="getMeterStatusType(scope.row.status)" effect="light" round>
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="安装位置" min-width="150" />
          <el-table-column prop="installedAt" label="安装时间" width="180" align="center">
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.installedAt) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </transition>

    <!-- 客户表单对话框 -->
    <el-dialog
      v-model="customerDialog.visible"
      :title="customerDialog.form.id ? '编辑客户' : '新增客户'"
      width="520px"
      class="custom-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        :model="customerDialog.form"
        :rules="customerRules"
        ref="customerFormRef"
        label-position="top"
        class="customer-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="name">
              <el-input v-model="customerDialog.form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="type">
              <el-select v-model="customerDialog.form.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="居民用户" value="RESIDENTIAL" />
                <el-option label="商业用户" value="COMMERCIAL" />
                <el-option label="工业用户" value="INDUSTRIAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="customerDialog.form.contactPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="customerDialog.form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="customerDialog.form.address" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="customerDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="saveCustomer" :loading="customerDialog.saving">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 表具表单对话框 -->
    <el-dialog
      v-model="meterDialog.visible"
      title="新增表具"
      width="520px"
      class="custom-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        :model="meterDialog.form"
        :rules="meterRules"
        ref="meterFormRef"
        label-position="top"
        class="meter-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="表具编号" prop="meterNo">
              <el-input v-model="meterDialog.form.meterNo" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前状态" prop="status">
              <el-select v-model="meterDialog.form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="正常" />
                <el-option label="停用" value="停用" />
                <el-option label="维修中" value="维修中" />
                <el-option label="已报废" value="已报废" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="安装位置" prop="location">
          <el-input v-model="meterDialog.form.location" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="安装时间" prop="installedAt">
          <el-date-picker
            v-model="meterDialog.form.installedAt"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="meterDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="saveMeter" :loading="meterDialog.saving">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, User, Edit, List, Odometer } from '@element-plus/icons-vue'
import { customerApi } from '@/services/api'

const keyword = ref('')
const customers = ref([])
const meters = ref([])
const activeCustomer = ref(null)
const loading = ref(false)
const metersLoading = ref(false)

// 简单的统计数据 (实际项目中可能需要从后端获取)
const totalMeters = computed(() => {
  // 这里只是演示，实际应该请求API
  return customers.value.reduce((acc, curr) => acc + (curr.meterCount || 1), 0)
})

// 表单引用
const customerFormRef = ref(null)
const meterFormRef = ref(null)

// 对话框状态
const customerDialog = reactive({ 
  visible: false, 
  form: {},
  saving: false
})

const meterDialog = reactive({ 
  visible: false, 
  form: {},
  saving: false
})

// 表单验证规则
const customerRules = reactive({
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择客户类型', trigger: 'change' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
})

const meterRules = reactive({
  meterNo: [
    { required: true, message: '请输入表具编号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择表具状态', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入安装位置', trigger: 'blur' }
  ],
  installedAt: [
    { required: true, message: '请选择安装时间', trigger: 'change' }
  ]
})

// 辅助函数：根据名字生成颜色
const getAvatarColor = (name) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#8e44ad', '#2c3e50', '#16a085']
  let hash = 0
  if (!name) return colors[0]
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}

// 获取客户类型标签
const getTypeLabel = (type) => {
  const typeMap = {
    'RESIDENTIAL': '居民',
    'COMMERCIAL': '商业',
    'INDUSTRIAL': '工业'
  }
  return typeMap[type] || type
}

// 获取表具状态标签颜色
const getMeterStatusType = (status) => {
  const statusMap = {
    '正常': 'success',
    '停用': 'info',
    '维修中': 'warning',
    '已报废': 'danger'
  }
  return statusMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载客户列表
const loadCustomers = async () => {
  loading.value = true
  try {
    const res = await customerApi.list(keyword.value)
    if (!res.success) throw new Error(res.message)
    customers.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '加载客户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载表具列表
const loadMeters = async (customer) => {
  activeCustomer.value = customer
  metersLoading.value = true
  try {
    const res = await customerApi.meters(customer.id)
    if (!res.success) throw new Error(res.message)
    meters.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '加载表具列表失败')
  } finally {
    metersLoading.value = false
  }
}

// 选择客户
const selectCustomer = (row) => loadMeters(row)

// 打开客户表单
const openCustomer = (row = {}) => {
  customerDialog.form = { ...row }
  customerDialog.visible = true
  // 重置表单验证
  if (customerFormRef.value) {
    customerFormRef.value.clearValidate()
  }
}

// 保存客户
const saveCustomer = async () => {
  if (!customerFormRef.value) return
  
  try {
    await customerFormRef.value.validate()
    customerDialog.saving = true
    
    const payload = { ...customerDialog.form }
    const isEdit = !!payload.id
    const res = isEdit
      ? await customerApi.update(payload.id, payload)
      : await customerApi.create(payload)
    
    if (!res.success) throw new Error(res.message)
    ElMessage.success(isEdit ? '更新成功' : '创建成功')
    customerDialog.visible = false
    loadCustomers()
  } catch (err) {
    if (err.message) {
      ElMessage.error(err.message)
    }
  } finally {
    customerDialog.saving = false
  }
}

// 打开表具表单
const openMeter = () => {
  if (!activeCustomer.value) {
    ElMessage.warning('请先选择客户')
    return
  }
  meterDialog.form = { 
    meterNo: '', 
    status: '正常', 
    location: '', 
    installedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }
  meterDialog.visible = true
  // 重置表单验证
  if (meterFormRef.value) {
    meterFormRef.value.clearValidate()
  }
}

// 保存表具
const saveMeter = async () => {
  if (!activeCustomer.value || !meterFormRef.value) return
  
  try {
    await meterFormRef.value.validate()
    meterDialog.saving = true
    
    const res = await customerApi.addMeter(activeCustomer.value.id, meterDialog.form)
    if (!res.success) throw new Error(res.message)
    ElMessage.success('新增表具成功')
    meterDialog.visible = false
    loadMeters(activeCustomer.value)
  } catch (err) {
    if (err.message) {
      ElMessage.error(err.message)
    }
  } finally {
    meterDialog.saving = false
  }
}

// 重置搜索
const reset = () => {
  keyword.value = ''
  loadCustomers()
}

onMounted(loadCustomers)
</script>

<style scoped>
.customers-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: var(--box-shadow-card);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--box-shadow-hover);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 20px;
}

.stat-icon.blue {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.stat-icon.green {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--text-color-secondary);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color-primary);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 搜索卡片 */
.search-card {
  overflow: visible;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-indicator {
  width: 4px;
  height: 20px;
  background: var(--primary-color);
  border-radius: 2px;
}

.title-indicator.green {
  background: var(--success-color);
}

.search-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color-primary);
}

.search-toolbar {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input-wrapper {
  flex: 1;
  max-width: 400px;
}

.search-icon {
  font-size: 16px;
  color: var(--text-color-secondary);
}

/* 表格卡片 */
.table-card {
  flex: 1;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color-lighter);
}

.table-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-primary);
}

/* 客户列表样式 */
.customer-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-avatar {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.name-info {
  display: flex;
  flex-direction: column;
}

.name-text {
  font-weight: 500;
  color: var(--text-color-primary);
}

.type-text {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.id-cell {
  color: var(--text-color-secondary);
  font-family: monospace;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

/* 表具列表样式 */
.meters-card {
  margin-top: 0;
  border-top: 1px solid var(--border-color-lighter);
}

.meter-no {
  font-family: monospace;
  font-weight: 500;
  color: var(--text-color-primary);
}

.date-text {
  color: var(--text-color-secondary);
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 768px) {
  .search-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input-wrapper {
    max-width: 100%;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
