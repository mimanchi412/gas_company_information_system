<template>
  <div class="workorders-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Tools /></el-icon>
        工单管理
      </h2>
      <p class="page-description">管理燃气公司工单，包括报修、安检等业务</p>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <div class="search-toolbar">
        <div class="search-inputs">
          <el-select 
            v-model="filterStatus" 
            placeholder="按状态筛选" 
            clearable 
            class="filter-select"
          >
            <el-option label="新建" value="NEW" />
            <el-option label="已派单" value="ASSIGNED" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已完成" value="DONE" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
          <el-select 
            v-model="filterCategory" 
            placeholder="按类型筛选" 
            clearable 
            class="filter-select"
          >
            <el-option label="报修" value="REPAIR" />
            <el-option label="安检" value="INSPECTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
          <el-input 
            v-model="filterAssigned"
            placeholder="按处理人筛选"
            clearable
            class="filter-input"
          />
        </div>
        <div class="search-actions">
          <el-button 
            type="primary" 
            @click="loadList" 
            :loading="loading"
            class="search-button"
          >
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters" class="reset-button">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
          <el-button 
            type="success" 
            @click="openCreate" 
            class="create-button"
          >
            <el-icon><Plus /></el-icon>
            新建工单
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 工单列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><List /></el-icon>
            工单列表
          </span>
          <el-tag type="info" size="small">共 {{ list.length }} 条记录</el-tag>
        </div>
      </template>
      
      <el-table 
        :data="list" 
        v-loading="loading"
        element-loading-text="加载中..."
        empty-text="暂无工单数据"
        class="workorders-table"
        stripe
      >
        <el-table-column prop="id" label="工单ID" width="80" align="center">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getCategoryType(scope.row.category)" size="small">
              {{ getCategoryLabel(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerId" label="客户ID" width="100" align="center" />
        <el-table-column prop="meterId" label="表具ID" width="100" align="center" />
        <el-table-column prop="assignedTo" label="处理人" width="120" align="center">
          <template #default="scope">
            <span v-if="scope.row.assignedTo" class="assigned-to">
              <el-icon><User /></el-icon>
              {{ scope.row.assignedTo }}
            </span>
            <span v-else class="not-assigned">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="scope">
            <span class="create-time">{{ formatDate(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button 
                type="primary" 
                link 
                @click="openAssign(scope.row)"
                :disabled="scope.row.status === 'DONE' || scope.row.status === 'CLOSED'"
                class="action-button"
              >
                <el-icon><UserFilled /></el-icon>
                派单
              </el-button>
              <el-button 
                type="warning" 
                link 
                @click="openStatus(scope.row)"
                :disabled="scope.row.status === 'DONE' || scope.row.status === 'CLOSED'"
                class="action-button"
              >
                <el-icon><Refresh /></el-icon>
                更新状态
              </el-button>
              <el-button 
                type="success" 
                link 
                @click="viewDetails(scope.row)"
                class="action-button"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建工单对话框 -->
    <el-dialog 
      v-model="createDialog.visible" 
      title="新建工单" 
      width="600px"
      class="create-dialog"
    >
      <el-form 
        :model="createDialog.form" 
        :rules="createRules"
        ref="createFormRef"
        label-width="100px"
        size="large"
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
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="createDialog.form.title" 
            placeholder="请输入工单标题"
          >
            <template #prefix>
              <el-icon><EditPen /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="createDialog.form.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入工单描述"
          />
        </el-form-item>
        <el-form-item label="类型" prop="category">
          <el-select v-model="createDialog.form.category" style="width: 100%">
            <el-option label="报修" value="REPAIR" />
            <el-option label="安检" value="INSPECTION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialog.visible=false" size="large">取消</el-button>
        <el-button type="primary" @click="createWorkOrder" :loading="createDialog.loading" size="large">
          创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 派单对话框 -->
    <el-dialog 
      v-model="assignDialog.visible" 
      title="派单" 
      width="500px"
      class="assign-dialog"
    >
      <el-form 
        :model="assignDialog.form" 
        :rules="assignRules"
        ref="assignFormRef"
        label-width="80px"
        size="large"
      >
        <el-form-item label="处理人" prop="assignedTo">
          <el-input 
            v-model="assignDialog.form.assignedTo" 
            placeholder="请输入处理人姓名"
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialog.visible=false" size="large">取消</el-button>
        <el-button type="primary" @click="assignWorkOrder" :loading="assignDialog.loading" size="large">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 更新状态对话框 -->
    <el-dialog 
      v-model="statusDialog.visible" 
      title="更新状态" 
      width="500px"
      class="status-dialog"
    >
      <el-form 
        :model="statusDialog.form" 
        :rules="statusRules"
        ref="statusFormRef"
        label-width="100px"
        size="large"
      >
        <el-form-item label="当前状态" v-if="statusDialog.currentStatus">
          <el-tag :type="getStatusType(statusDialog.currentStatus)">
            {{ getStatusLabel(statusDialog.currentStatus) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="status">
          <el-select v-model="statusDialog.form.status" style="width: 100%">
            <el-option label="新建" value="NEW" />
            <el-option label="已派单" value="ASSIGNED" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已完成" value="DONE" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialog.visible=false" size="large">取消</el-button>
        <el-button type="primary" @click="updateStatus" :loading="statusDialog.loading" size="large">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 工单详情对话框 -->
    <el-dialog 
      v-model="detailsDialog.visible" 
      title="工单详情" 
      width="700px"
      class="details-dialog"
    >
      <div v-if="detailsDialog.data" class="details-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="工单ID">
            <el-tag type="info">{{ detailsDialog.data.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="标题">
            {{ detailsDialog.data.title }}
          </el-descriptions-item>
          <el-descriptions-item label="类型">
            <el-tag :type="getCategoryType(detailsDialog.data.category)">
              {{ getCategoryLabel(detailsDialog.data.category) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailsDialog.data.status)">
              {{ getStatusLabel(detailsDialog.data.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="客户ID">
            {{ detailsDialog.data.customerId }}
          </el-descriptions-item>
          <el-descriptions-item label="表具ID">
            {{ detailsDialog.data.meterId }}
          </el-descriptions-item>
          <el-descriptions-item label="处理人">
            {{ detailsDialog.data.assignedTo || '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(detailsDialog.data.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ detailsDialog.data.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailsDialog.visible=false" size="large">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Tools,
  Search,
  RefreshRight,
  Plus,
  List,
  User,
  EditPen,
  UserFilled,
  Refresh,
  View
} from '@element-plus/icons-vue'
import { workOrderApi } from '@/services/api'

// 响应式变量
const list = ref([])
const loading = ref(false)
const filterStatus = ref('')
const filterCategory = ref('')
const filterAssigned = ref('')

// 表单引用
const createFormRef = ref(null)
const assignFormRef = ref(null)
const statusFormRef = ref(null)

// 对话框状态
const createDialog = reactive({ 
  visible: false, 
  loading: false,
  form: { 
    customerId: null, 
    meterId: null, 
    title: '', 
    description: '', 
    category: 'REPAIR' 
  } 
})

const assignDialog = reactive({ 
  visible: false, 
  loading: false,
  id: null, 
  form: { assignedTo: '' } 
})

const statusDialog = reactive({ 
  visible: false, 
  loading: false,
  id: null, 
  currentStatus: '',
  form: { status: '' } 
})

const detailsDialog = reactive({ 
  visible: false, 
  data: null 
})

// 表单验证规则
const createRules = reactive({
  customerId: [
    { required: true, message: '请输入客户ID', trigger: 'blur' },
    { type: 'number', message: '客户ID必须为数字', trigger: 'blur' }
  ],
  meterId: [
    { required: true, message: '请输入表具ID', trigger: 'blur' },
    { type: 'number', message: '表具ID必须为数字', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入工单标题', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择工单类型', trigger: 'change' }
  ]
})

const assignRules = reactive({
  assignedTo: [
    { required: true, message: '请输入处理人姓名', trigger: 'blur' }
  ]
})

const statusRules = reactive({
  status: [
    { required: true, message: '请选择新状态', trigger: 'change' }
  ]
})

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'NEW': '新建',
    'ASSIGNED': '已派单',
    'IN_PROGRESS': '处理中',
    'DONE': '已完成',
    'CLOSED': '已关闭'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'NEW': 'info',
    'ASSIGNED': 'primary',
    'IN_PROGRESS': 'warning',
    'DONE': 'success',
    'CLOSED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取类型标签
const getCategoryLabel = (category) => {
  const categoryMap = {
    'REPAIR': '报修',
    'INSPECTION': '安检',
    'OTHER': '其他'
  }
  return categoryMap[category] || category
}

// 获取类型样式
const getCategoryType = (category) => {
  const typeMap = {
    'REPAIR': 'danger',
    'INSPECTION': 'warning',
    'OTHER': 'info'
  }
  return typeMap[category] || 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 重置筛选条件
const resetFilters = () => {
  filterStatus.value = ''
  filterCategory.value = ''
  filterAssigned.value = ''
  loadList()
}

// 加载工单列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await workOrderApi.list({
      status: filterStatus.value || undefined,
      category: filterCategory.value || undefined
    })
    if (!res.success) throw new Error(res.message)
    let data = res.data || []
    if (filterAssigned.value) {
      data = data.filter(item => (item.assignedTo || '').includes(filterAssigned.value))
    }
    list.value = data
  } catch (err) {
    ElMessage.error(err.message || '加载工单列表失败')
  } finally {
    loading.value = false
  }
}

// 打开新建工单对话框
const openCreate = () => {
  createDialog.form = { 
    customerId: null, 
    meterId: null, 
    title: '', 
    description: '', 
    category: 'REPAIR' 
  }
  createDialog.visible = true
}

// 创建工单
const createWorkOrder = async () => {
  if (!createFormRef.value) return
  
  try {
    await createFormRef.value.validate()
    createDialog.loading = true
    
    const res = await workOrderApi.create(createDialog.form)
    if (!res.success) throw new Error(res.message)
    
    ElMessage.success('创建成功')
    createDialog.visible = false
    loadList()
  } catch (err) {
    if (err.name !== 'ElFormValidateError') {
      ElMessage.error(err.message || '创建工单失败')
    }
  } finally {
    createDialog.loading = false
  }
}

// 打开派单对话框
const openAssign = (row) => {
  assignDialog.id = row.id
  assignDialog.form = { assignedTo: row.assignedTo || '' }
  assignDialog.visible = true
}

// 派单
const assignWorkOrder = async () => {
  if (!assignFormRef.value) return
  
  try {
    await assignFormRef.value.validate()
    assignDialog.loading = true
    
    const res = await workOrderApi.assign(assignDialog.id, assignDialog.form)
    if (!res.success) throw new Error(res.message)
    
    ElMessage.success('派单成功')
    assignDialog.visible = false
    loadList()
  } catch (err) {
    if (err.name !== 'ElFormValidateError') {
      ElMessage.error(err.message || '派单失败')
    }
  } finally {
    assignDialog.loading = false
  }
}

// 打开更新状态对话框
const openStatus = (row) => {
  statusDialog.id = row.id
  statusDialog.currentStatus = row.status
  statusDialog.form = { status: row.status }
  statusDialog.visible = true
}

// 更新状态
const updateStatus = async () => {
  if (!statusFormRef.value) return
  
  try {
    await statusFormRef.value.validate()
    statusDialog.loading = true
    
    const res = await workOrderApi.updateStatus(statusDialog.id, statusDialog.form)
    if (!res.success) throw new Error(res.message)
    
    ElMessage.success('更新成功')
    statusDialog.visible = false
    loadList()
  } catch (err) {
    if (err.name !== 'ElFormValidateError') {
      ElMessage.error(err.message || '更新状态失败')
    }
  } finally {
    statusDialog.loading = false
  }
}

// 查看工单详情
const viewDetails = (row) => {
  detailsDialog.data = row
  detailsDialog.visible = true
}

// 初始化
onMounted(() => {
  loadList()
})
</script>

<style scoped>
.workorders-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.page-title .el-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #409EFF;
}

.page-title h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  color: #606266;
  margin-left: 36px;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.search-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.search-inputs {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-select {
  width: 160px;
}

.filter-input {
  width: 200px;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.search-button {
  width: 100px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .search-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-inputs {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-select, .filter-input {
    width: 100%;
  }

  .search-actions {
    margin-left: 0;
    justify-content: space-between;
  }

  .search-button {
    width: auto;
    flex: 1;
  }
}

.search-card :deep(.el-card__header) {
  padding: 18px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #ebeef5;
}

.search-card :deep(.el-card__body) {
  padding: 20px;
}

/* --- 表格区域样式 --- */
.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-card :deep(.el-card__header) {
  padding: 18px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-title .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.workorders-table {
  width: 100%;
}

.workorders-table :deep(.el-table__header) th {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

.workorders-table :deep(.el-table__row) {
  transition: background-color 0.2s;
}

.workorders-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.assigned-to {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #409EFF;
}

.not-assigned {
  color: #909399;
  font-style: italic;
}

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.table-actions .el-button {
  padding: 5px 8px;
  margin-left: 0;
}

/* --- 对话框样式 --- */
.create-dialog :deep(.el-dialog__body),
.assign-dialog :deep(.el-dialog__body),
.status-dialog :deep(.el-dialog__body),
.details-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
}

.details-content {
  line-height: 1.6;
}

/* Element Plus 组件样式覆盖 */
.search-card :deep(.el-input__wrapper),
.table-card :deep(.el-input__wrapper) {
  border-radius: 4px;
}
</style>