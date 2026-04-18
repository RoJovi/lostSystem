import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['token'] = token
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器 - 适配后端 Result 类 { code, msg, data }
request.interceptors.response.use(
    response => {
        const res = response.data
        
	if (res.code === 403 || (res.msg && res.msg.includes('封禁'))) {
    		ElMessage.error(res.msg || '账号已被封禁')
   		 localStorage.removeItem('token')
   		 localStorage.removeItem('userType')
   		 setTimeout(() => {
        		window.location.href = '/login'
    		}, 1000)
   		 return Promise.reject(new Error(res.msg))
	}
        // 后端统一返回格式
        if (res.code !== 200) {
            if (res.code === 401) {
                ElMessage.error(res.msg || '登录已过期，请重新登录')
                localStorage.removeItem('token')
                localStorage.removeItem('userType')
                window.location.href = '/login'
                return Promise.reject(new Error(res.msg))
            }
            ElMessage.error(res.msg || '请求失败')
            return Promise.reject(new Error(res.msg))
        }
        
        // 关键：返回 data 部分，调用方直接拿到业务数据
        return res.data
    },
    error => {
        const message = error.response?.data?.msg || error.message || '网络错误'
        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default request