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
            <div class="card-panel-num">128</div>
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
            <div class="card-panel-num">1,024</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-red">
            <el-icon size="48"><DataAnalysis /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日总营收</div>
            <div class="card-panel-num">￥ 23,450.00</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <el-card shadow="hover">
          <div class="card-panel-icon-wrapper icon-orange">
            <el-icon size="48"><WarningFilled /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">告警设备</div>
            <div class="card-panel-num">3</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区(占位) -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover" header="电量趋势图 (模拟数据)">
          <div style="height: 300px; background: #fafafa; display: flex; justify-content: center; align-items: center; color: #a8abb2">
            [Echarts 图表预留区域：展示近七天用电量趋势]
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" header="异常告警日志">
          <ul class="alert-list">
            <li><el-tag size="small" type="danger" effect="plain">离线</el-tag> 桩编号 P1-01 [软件园A区] - 超过30分未心跳</li>
            <li><el-tag size="small" type="warning" effect="plain">超温</el-tag> 桩编号 P2-05 [星光谷站] - 设备温度达86℃</li>
            <li><el-tag size="small" type="info" effect="plain">异常</el-tag> 订单 O002 长时间未支付挂起</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 未来引入Echarts处理图表逻辑
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px !important;
}
.card-panel-icon-wrapper {
  padding: 10px;
  border-radius: 6px;
  transition: all 0.3s ease-out;
}
.card-panel-icon-wrapper.icon-blue { color: #40c9c6; }
.card-panel-icon-wrapper.icon-green { color: #36a3f7; }
.card-panel-icon-wrapper.icon-red { color: #f4516c; }
.card-panel-icon-wrapper.icon-orange { color: #e6a23c; }

.card-panel-description {
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
  height: 300px;
  overflow-y: auto;
}
.alert-list li {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  color: #606266;
}
</style>
