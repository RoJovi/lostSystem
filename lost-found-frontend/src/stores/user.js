import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const userInfo = ref(null)
    const userType = ref(null) // 0-普通用户 1-管理员
    const token = ref(localStorage.getItem('token') || '')
    
    function setUser(data) {
        // 根据是否有 adminNum 判断是管理员还是普通用户
        if (data.adminNum !== undefined) {
            // 管理员
            userInfo.value = {
                id: data.id,
                adminNum: data.adminNum,
                name: data.name,
                avatar: data.avatar || ''
            }
            userType.value = 1
        } else {
            // 普通用户
            userInfo.value = {
                id: data.id,
                nickname: data.nickname,
                email: data.email,
                phone: data.phone,
                avatar: data.avatar || ''
            }
            userType.value = 0
        }
        token.value = data.token
        localStorage.setItem('token', data.token)
        localStorage.setItem('userType', userType.value)
    }
    
    function logout() {
        userInfo.value = null
        userType.value = null
        token.value = ''
        localStorage.removeItem('token')
        localStorage.removeItem('userType')
    }
    
    // 页面刷新时从 localStorage 恢复用户类型
    const savedType = localStorage.getItem('userType')
    if (savedType !== null) {
        userType.value = parseInt(savedType)
    }
    
    return { userInfo, userType, token, setUser, logout }
})