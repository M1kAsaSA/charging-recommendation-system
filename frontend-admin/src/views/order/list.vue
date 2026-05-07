<template>
  <div class="order-container">
    <el-card shadow="hover">
      <!-- 搜索筛选条件 -->
      <template #header>
        <div class="card-header">
          <span>订单监控中心</span>
          <div class="search-actions">
            <el-input 
              v-model="queryForm.orderNo" 
              placeholder="搜索订单号" 
              clearable 
              style="width: 200px; margin-right: 10px;"
            />
            <el-select 
              v-model="queryForm.payStatus" 
              placeholder="支付状态" 
              clearable 
              style="width: 150px; margin-right: 10px;"
            >
              <el-option label="已支付" :value="1" />
              <el-option label="未支付" :value="0" />
            </el-select>
            <el-button type="primary" @click="searchOrders">查询</el-button>
            <el-button @click="resetForm">重置</el-button>
          </div>
        </div>
      </template>

      <!-- 订单列表表格 -->
      <el-table 
        :data="orderList" 
        style="width: 100%; margin-top: 20px;" 
        stripe 
        :loading="tableLoading"
        :default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column prop="orderNo" label="订单号" width="150" show-overflow-tooltip />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="stationName" label="充电站" width="180" show-overflow-tooltip />
        <el-table-column prop="pileCode" label="电桩编号" width="120" />
        <el-table-column prop="totalPower" label="充电电量(kWh)" width="130" align="center" />
        <el-table-column prop="totalAmount" label="消费金额(元)" width="130" align="center">
          <template #default="scope">
            <el-tag type="success">¥{{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'warning'">
              {{ scope.row.payStatus === 1 ? '已支付' : '未支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="充电开始" width="180" />
        <el-table-column prop="endTime" label="充电结束" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              link 
              type="primary" 
              size="small"
              @click="viewDetails(scope.row)"
            >
              详情
            </el-button>
            <el-button 
              link 
              type="danger" 
              size="small"
              v-if="scope.row.payStatus === 0"
              @click="handleRefund(scope.row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <el-pagination 
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right;"
        @size-change="searchOrders"
        @current-change="searchOrders"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="订单详情" 
      width="600px"
    >
      <el-descriptions 
        v-if="selectedOrder" 
        :column="1" 
        border
      >
        <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedOrder.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ selectedOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="充电站">{{ selectedOrder.stationName }}</el-descriptions-item>
        <el-descriptions-item label="电桩编号">{{ selectedOrder.pileCode }}</el-descriptions-item>
        <el-descriptions-item label="充电电量">{{ selectedOrder.totalPower }} kWh</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ selectedOrder.totalAmount ? selectedOrder.totalAmount.toFixed(2) : '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="selectedOrder.payStatus === 1 ? 'success' : 'warning'">
            {{ selectedOrder.payStatus === 1 ? '已支付' : '未支付' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="充电开始">{{ selectedOrder.startTime }}</el-descriptions-item>
        <el-descriptions-item label="充电结束">{{ selectedOrder.endTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ selectedOrder.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const queryForm = ref({
  orderNo: '',
  payStatus: null
})

const orderList = ref([])
const tableLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const selectedOrder = ref(null)

// 获取订单列表
const fetchOrderList = async () => {
  tableLoading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      orderNo: queryForm.value.orderNo || undefined,
      payStatus: queryForm.value.payStatus
    }
    
    // 移除 undefined 的参数
    Object.keys(params).forEach(key => params[key] === undefined && delete params[key])
    
    const response = await request.get('/order/page', { params })
    if (response.code === 200) {
      const data = response.data
      orderList.value = data.records || []
      total.value = data.total || 0
    } else {
      ElMessage.error('获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表出错:', error)
    ElMessage.error('获取订单列表出错')
  } finally {
    tableLoading.value = false
  }
}

// 查询订单
const searchOrders = () => {
  currentPage.value = 1
  fetchOrderList()
}

// 重置表单
const resetForm = () => {
  queryForm.value = {
    orderNo: '',
    payStatus: null
  }
  currentPage.value = 1
  fetchOrderList()
}

// 查看详情
const viewDetails = (order) => {
  selectedOrder.value = order
  detailVisible.value = true
}

// 处理退款（演示）
const handleRefund = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认要为订单 ${order.orderNo} 进行退款吗？`,
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里可以调用后端退款接口
    ElMessage.success('退款申请已提交，请等待审核')
    fetchOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退款出错:', error)
    }
  }
}

// 初始化
onMounted(() => {
  fetchOrderList()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
}

:deep(.el-pagination) {
  display: flex;
  justify-content: flex-end;
}
</style>
