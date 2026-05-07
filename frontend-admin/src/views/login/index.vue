<template>
  <div class="login-container">
    <el-card class="login-box" shadow="always">
      <div class="title">
        <el-icon :size="28" color="#409EFf"><Lightning /></el-icon>
        <span>充电桩管理后台</span>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入管理员账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({ username: 'admin', password: 'password' })

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入管理员账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
}

const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 模拟接口调用延时
      setTimeout(() => {
        // TODO: 替换为真实的API Auth调用并存储Token
        localStorage.setItem('admin_token', 'mock_jwt_token')
        ElMessage.success('登录成功')
        router.push('/')
        loading.value = false
      }, 1000)
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2d3a4b;
}

.login-box {
  width: 400px;
  max-width: 90%;
  padding: 20px;
  border-radius: 8px;
}

.title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 30px;
}

.title span {
  margin-left: 10px;
}
</style>
