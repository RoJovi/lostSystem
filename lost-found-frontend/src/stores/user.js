import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const userInfo = ref(null)
    const userType = ref(null)
    const token = ref(localStorage.getItem('token') || '')
    
    // 初始化时从 localStorage 恢复 userInfo
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
        try {
            userInfo.value = JSON.parse(savedUserInfo)
        } catch (e) {}
    }
    
    // 恢复 userType
    const savedType = localStorage.getItem('userType')
    if (savedType !== null) {
        userType.value = parseInt(savedType)
    }
    
    function setUser(data) {
        if (data.adminNum !== undefined) {
            userInfo.value = {
                id: data.id,
                adminNum: data.adminNum,
                name: data.name,
                avatar: data.avatar || ''
            }
            userType.value = 1
        } else {
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
        localStorage.setItem('userId', data.id)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))  // 添加这一行
    }
    
    function logout() {
        userInfo.value = null
        userType.value = null
        token.value = ''
        localStorage.removeItem('token')
        localStorage.removeItem('userType')
        localStorage.removeItem('userId')
        localStorage.removeItem('userInfo')
    }
    
    return { userInfo, userType, token, setUser, logout }
})