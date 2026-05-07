<template>
  <div class="app-container" style="padding: 20px;">
    <!-- 搜索过滤 -->
    <div class="filter-container" style="margin-bottom: 20px;">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="桩编号">
          <el-input v-model="listQuery.pileCode" placeholder="模糊对应机身码" clearable style="width: 150px;" @keyup.enter="handleFilter" />
        </el-form-item>
        <el-form-item label="归属电站">
          <el-select v-model="listQuery.stationId" placeholder="选择站" clearable filterable style="width: 180px">
            <el-option v-for="item in stationOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="运行状态">
          <el-select v-model="listQuery.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="空闲" :value="0" />
            <el-option label="充电中" :value="1" />
            <el-option label="故障" :value="2" />
            <el-option label="离线(网络异常)" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button type="success" @click="handleCreate"><el-icon><Plus /></el-icon> 新增设备</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格展示 -->
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column label="设备ID" prop="id" align="center" width="80" />
      <el-table-column label="机器识别码" prop="pileCode" align="center" min-width="150" />
      <el-table-column label="归属电站" prop="stationName" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" prop="type" align="center" width="100">
        <template #default="{row}">
          <el-tag :type="row.type === 1 ? 'info' : ''">
            {{ row.type === 1 ? '慢充(交流)' : '快充(直流)' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="额定功率" prop="power" align="center" width="120">
        <template #default="{row}">{{ row.power }} kW</template>
      </el-table-column>
      <el-table-column label="通讯状态" prop="status" align="center" width="120">
        <template #default="{row}">
          <el-tag v-if="row.status === 0" type="success">空闲</el-tag>
          <el-tag v-else-if="row.status === 1" type="warning">充电中</el-tag>
          <el-tag v-else-if="row.status === 2" type="danger">设备故障</el-tag>
          <el-tag v-else type="info">离线</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作(云端介入)" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="handleUpdate(row)"> 配置 </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)"> 报废注销 </el-button>
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
    <el-dialog :title="dialogStatus === 'create' ? '新电桩入网登记' : '电桩运行参数下发'" v-model="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-position="right" label-width="120px" style="width: 400px; margin-left:30px;">
        <el-form-item label="归属电站" prop="stationId">
          <el-select v-model="temp.stationId" placeholder="绑定站点" clearable filterable style="width: 100%">
            <el-option v-for="item in stationOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备识别码(SN)" prop="pileCode">
          <el-input v-model="temp.pileCode" :disabled="dialogStatus === 'update'" placeholder="出厂唯一码" />
        </el-form-item>
        <el-form-item label="充电流派" prop="type">
          <el-radio-group v-model="temp.type">
            <el-radio :label="1">交流慢充机</el-radio>
            <el-radio :label="2">直流快充机</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="单枪功率" prop="power">
          <el-input v-model.number="temp.power" type="number">
            <template #append>kW</template>
          </el-input>
        </el-form-item>
        <el-form-item label="强制干预" v-if="dialogStatus === 'update'">
          <el-select v-model="temp.status" placeholder="状态下发">
            <el-option label="复位至空闲" :value="0" />
            <el-option label="标记为故障维修" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false"> 取消 </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()" :loading="btnLoading"> 下发配置 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchPilePage, savePile, updatePile, delPile } from '@/api/pile'
import { fetchAllStations } from '@/api/station'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

// 状态和数据池
const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const stationOptions = ref([])

const listQuery = reactive({
  page: 1,
  size: 10,
  pileCode: undefined,
  stationId: undefined,
  status: undefined
})

const dialogFormVisible = ref(false)
const dialogStatus = ref('')
const btnLoading = ref(false)
const dataForm = ref(null)

const resetTemp = () => {
  return {
    id: undefined,
    stationId: undefined,
    pileCode: '',
    type: 1,
    power: 7,
    status: 0
  }
}

const temp = ref(resetTemp())

// 表单校验
const rules = {
  stationId: [{ required: true, message: '请绑定设备所属的充电站', trigger: 'change' }],
  pileCode: [{ required: true, message: '请扫码或输入机器唯一的通讯识别码', trigger: 'blur' }],
  power: [{ required: true, type: 'number', message: '必须填写正确的功率数值', trigger: 'blur' }]
}

// 获取站点绑定字典
const getStations = async () => {
  const res = await fetchAllStations()
  stationOptions.value = res.data
}

// 主查询流水
const getList = async () => {
  listLoading.value = true
  try {
    const res = await fetchPilePage(listQuery)
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
        await savePile(temp.value)
        ElMessage.success('入网成功')
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
        await updatePile(temp.value)
        ElMessage.success('配置下线成功')
        dialogFormVisible.value = false
        getList()
      } finally {
        btnLoading.value = false
      }
    }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('注销后不可恢复，确定要该设备下线吗?', '拆除警告', {
    confirmButtonText: '强制注销',
    cancelButtonText: '取消',
    type: 'error'
  }).then(async () => {
    await delPile(id)
    ElMessage.success('设备已从云端注销')
    getList()
  }).catch(() => {})
}

// 初始化钩子
onMounted(() => {
  getStations()
  getList()
})
</script>
