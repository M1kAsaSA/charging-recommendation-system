<template>
  <div class="app-container" style="padding: 20px;">
    <!-- 搜索过滤 -->
    <div class="filter-container" style="margin-bottom: 20px;">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="站点名称">
          <el-input v-model="listQuery.name" placeholder="支持模糊匹配" clearable style="width: 200px;" @keyup.enter="handleFilter" />
        </el-form-item>
        <el-form-item label="运营状态">
          <el-select v-model="listQuery.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="运营中" :value="1" />
            <el-option label="停运维护" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button type="success" @click="handleCreate"><el-icon><Plus /></el-icon> 新增站点</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格展示 -->
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column label="ID" prop="id" align="center" width="80" />
      <el-table-column label="站点名称" prop="name" align="center" min-width="150" />
      <el-table-column label="地址" prop="address" align="center" min-width="200" show-overflow-tooltip />
      <el-table-column label="总功率 (kW)" prop="powerCapacity" align="center" width="120" />
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template #default="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '运营中' : '已停运' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" prop="createTime" align="center" width="160" />
      
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="handleUpdate(row)"> 编辑 </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)"> 删除 </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="listQuery.page"
        v-model:page-size="listQuery.size"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="getList"
        @current-change="getList"
      />
    </div>

    <!-- 对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新增电站档案' : '编辑电站档案'" v-model="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="站点名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="temp.address" />
        </el-form-item>
        <el-form-item label="经纬度">
          <el-col :span="11">
            <el-input v-model="temp.longitude" placeholder="经度" />
          </el-col>
          <el-col class="line" :span="2" style="text-align: center;">-</el-col>
          <el-col :span="11">
            <el-input v-model="temp.latitude" placeholder="纬度" />
          </el-col>
        </el-form-item>
        <el-form-item label="额定功率" prop="powerCapacity">
          <el-input v-model.number="temp.powerCapacity" type="number" />
        </el-form-item>
        <el-form-item label="运营状态">
          <el-switch v-model="temp.status" active-text="运营中" :active-value="1" inactive-text="停运" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false"> 取消 </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()" :loading="btnLoading"> 提交 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchStationPage, saveStation, updateStation, delStation } from '@/api/station'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

// 列表查询相关变量
const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const listQuery = reactive({
  page: 1,
  size: 10,
  name: undefined,
  status: undefined
})

// 对话框相关变量
const dialogFormVisible = ref(false)
const dialogStatus = ref('')
const btnLoading = ref(false)
const dataForm = ref(null)

const resetTemp = () => {
  return {
    id: undefined,
    name: '',
    address: '',
    longitude: undefined,
    latitude: undefined,
    powerCapacity: 0,
    status: 1
  }
}

const temp = ref(resetTemp())

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请填写站点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请填写详细地址', trigger: 'blur' }],
  powerCapacity: [{ required: true, type: 'number', message: '必须为数字值', trigger: 'blur' }]
}

const getList = async () => {
  listLoading.value = true
  try {
    const res = await fetchStationPage(listQuery)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    listLoading.value = false
  }
}

const handleFilter = () => {
  listQuery.page = 1
  getList()
}

const handleCreate = () => {
  temp.value = resetTemp()
  dialogStatus.value = 'create'
  dialogFormVisible.value = true
  if (dataForm.value) {
    dataForm.value.clearValidate()
  }
}

const handleUpdate = (row) => {
  temp.value = Object.assign({}, row)
  dialogStatus.value = 'update'
  dialogFormVisible.value = true
  if (dataForm.value) {
    dataForm.value.clearValidate()
  }
}

const createData = () => {
  dataForm.value.validate(async (valid) => {
    if (valid) {
      btnLoading.value = true
      try {
        await saveStation(temp.value)
        ElMessage.success('创建成功')
        dialogFormVisible.value = false
        getList()
      } finally {
        btnLoading.value = false
      }
    }
  })
}

const updateData = () => {
  dataForm.value.validate(async (valid) => {
    if (valid) {
      btnLoading.value = true
      try {
        await updateStation(temp.value)
        ElMessage.success('更新成功')
        dialogFormVisible.value = false
        getList()
      } finally {
        btnLoading.value = false
      }
    }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该站点信息吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await delStation(id)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

// 页面加载完成执行获取列表数据
onMounted(() => {
  getList()
})
</script>
