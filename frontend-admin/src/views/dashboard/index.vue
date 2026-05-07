<template>
  <div class="dashboard-container">
    <div class="header">
      <h2>看板概览 <span style="font-size:14px;color:#909399;font-weight:normal">(实时监控)</span></h2>
    </div>

    <!-- 顶部核心数据卡片 -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-blue">
            <el-icon size="48"><Location /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">运营站点数</div>
            <div class="card-panel-num">{{ stats.totalStations || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-green">
            <el-icon size="48"><Lightning /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总桩数</div>
            <div class="card-panel-num">{{ stats.totalPiles || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-red">
            <el-icon size="48"><DataAnalysis /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总营收</div>
            <div class="card-panel-num">￥{{ stats.totalRevenue || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-orange">
            <el-icon size="48"><Tickets /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">已结算订单</div>
            <div class="card-panel-num">{{ stats.totalOrders || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover" header="全网电桩实时运行状态 (动态分布)">
          <div ref="pieChartRef" style="height: 350px; width: 100%;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" header="异常告警日志 (模拟)">
          <ul class="alert-list">
            <li><el-tag size="small" type="danger" effect="plain">离线</el-tag> 桩 P1-01 [软件园A区] - 超过30分未心跳</li>
            <li><el-tag size="small" type="warning" effect="plain">超温</el-tag> 桩 P2-05 [星光谷站] - 设备温度达76℃</li>
            <li><el-tag size="small" type="info" effect="plain">异常</el-tag> 订单 O002 长时间未支付挂起</li>
            <li><el-tag size="small" type="danger" effect="plain">断电</el-tag> 桩 P012 [江景站] - 异常断电保护</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import { Location, Lightning, DataAnalysis, Tickets } from '@element-plus/icons-vue'

const stats = ref({
  totalStations: 0,
  totalPiles: 0,
  totalOrders: 0,
  totalRevenue: 0,
  idlePiles: 0,
  chargingPiles: 0,
  faultPiles: 0
})

const pieChartRef = ref(null)
let pieChart = null

const fetchStats = async () => {
  try {
    const res = await request.get('/dashboard/stats')
    if(res.code === 200) {
      stats.value = res.data
      initChart()
    }
  } catch(e) {
    console.error('获取大盘数据失败', e)
  }
}

const initChart = () => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    color: ['#67C23A', '#409EFF', '#F56C6C'], // 绿 蓝 红
    series: [
      {
        name: '运行状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: [
          { value: stats.value.idlePiles, name: '空闲中' },
          { value: stats.value.chargingPiles, name: '充电中' },
          { value: stats.value.faultPiles, name: '故障停用' }
        ]
      }
    ]
  }
  pieChart.setOption(option)
}

const handleResize = () => {
  if (pieChart) pieChart.resize()
}

onMounted(() => {
  fetchStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (pieChart) pieChart.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.header h2 {
  margin-top: 0;
  color: #303133;
}
.panel-group {
  margin-top: 18px;
}
.card-panel-col {
  margin-bottom: 32px;
}
:deep(.el-card__body) {
  display: block;
}
.card-panel-icon-wrapper {
  float: left;
  padding: 10px;
  border-radius: 6px;
  transition: all 0.3s ease-out;
}
.card-panel-icon-wrapper.icon-blue { color: #40c9c6; }
.card-panel-icon-wrapper.icon-green { color: #36a3f7; }
.card-panel-icon-wrapper.icon-red { color: #f4516c; }
.card-panel-icon-wrapper.icon-orange { color: #e6a23c; }

.card-panel-description {
  float: right;
  font-weight: bold;
  text-align: right;
}
.card-panel-text {
  line-height: 18px;
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 12px;
}
.card-panel-num {
  font-size: 20px;
  color: #666;
}

.alert-list {
  list-style: none;
  padding: 0;
  margin: 0;
  height: 350px;
  overflow-y: auto;
}
.alert-list li {
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  color: #606266;
}
</style>