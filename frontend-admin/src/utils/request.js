import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api/v1/admin',
  timeout: 10000 // 请求超时时间
})

// request 拦截器
request.interceptors.request.use(
  config => {
    // 自动在请求头带上 Token
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// response 拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || '系统异常')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    ElMessage.error('网络请求失败')
    return Promise.reject(error)
  }
)

export default request
