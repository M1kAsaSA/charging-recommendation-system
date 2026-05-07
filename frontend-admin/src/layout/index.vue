<template>
  <el-container class="app-wrapper">
    <el-aside width="210px" class="sidebar-container">
      <div class="logo">
        <el-icon :size="24" color="#409eff" style="margin-right: 10px"><FirstAidKit /></el-icon>
        <span>充电桩管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>监控大屏</template>
        </el-menu-item>
        
        <el-sub-menu index="/station">
          <template #title>
            <el-icon><Location /></el-icon>
            <span>电站管理</span>
          </template>
          <el-menu-item index="/station/list">电站列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/pile">
          <template #title>
            <el-icon><Lightning /></el-icon>
            <span>电桩管理</span>
          </template>
          <el-menu-item index="/pile/list">设备档案</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <template #title>订单监控</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="navbar">
        <div class="right-menu">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              管理员 admin
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)

const logout = () => {
  // TODO: 调用退出接口，清理Token
  router.push('/login')
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  width: 100%;
}
.sidebar-container {
  background-color: #304156;
  transition: width 0.28s;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  background-color: #2b3643;
}
.el-menu-vertical {
  border-right: none;
}
.navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.right-menu {
  margin-right: 20px;
  cursor: pointer;
}
</style>
