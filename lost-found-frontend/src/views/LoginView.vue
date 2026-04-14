<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 顶部切换按钮 -->
      <div class="tab-switch">
        <button 
          :class="{ active: loginType === 'user' }" 
          @click="loginType = 'user'"
        >👤 用户登录</button>
        <button 
          :class="{ active: loginType === 'admin' }" 
          @click="loginType = 'admin'"
        >🔧 管理员登录</button>
      </div>

      <!-- 用户登录表单 -->
      <form v-if="loginType === 'user'" @submit.prevent="handleUserLogin">
        <input 
          type="text" 
          v-model="userForm.account" 
          placeholder="邮箱 / 手机号"
          required
        />
        <input 
          type="password" 
          v-model="userForm.password" 
          placeholder="密码"
          required
        />
        <button type="submit" class="login-btn">登录</button>
        <div class="register-tip">
          还没有账号？<a @click="showRegisterDialog = true">注册一个</a>
        </div>
      </form>

      <!-- 管理员登录表单 -->
      <form v-else @submit.prevent="handleAdminLogin">
        <input 
          type="text" 
          v-model="adminForm.adminNum" 
          placeholder="工号"
          required
        />
        <input 
          type="password" 
          v-model="adminForm.password" 
          placeholder="密码"
          required
        />
        <button type="submit" class="login-btn">登录</button>
        <!-- 管理员没有注册提示 -->
      </form>
    </div>

   <!-- 注册弹窗 -->
<el-dialog v-model="showRegisterDialog" title="注册账号" width="400px">
  <el-form :model="registerForm" label-width="80px" @submit.prevent="handleRegister">
    <el-form-item label="昵称" required>
      <el-input v-model="registerForm.nickname" placeholder="请输入昵称（可修改）" />
    </el-form-item>
    <el-form-item label="邮箱" required>
      <el-input v-model="registerForm.email" placeholder="example@xx.com" />
    </el-form-item>
    <el-form-item label="手机号" required>
      <el-input v-model="registerForm.phone" placeholder="13800138000" />
    </el-form-item>
    <el-form-item label="密码" required>
      <el-input v-model="registerForm.password" type="password" show-password />
    </el-form-item>
    <el-form-item label="确认密码" required>
      <el-input v-model="registerForm.confirmPassword" type="password" show-password />
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="showRegisterDialog = false">取消</el-button>
    <el-button type="primary" @click="handleRegister" native-type="button">注册</el-button>
  </template>
</el-dialog>
</div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userLogin, adminLogin, userRegister } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginType = ref('user')
const showRegisterDialog = ref(false)

const userForm = ref({ account: '', password: '' })
const adminForm = ref({ adminNum: '', password: '' })
const registerForm = ref({
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const handleUserLogin = async () => {
  try {
    const res = await userLogin(userForm.value)
    userStore.setUser(res)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    // 错误已由拦截器处理
  }
}

const handleAdminLogin = async () => {
  try {
    const res = await adminLogin(adminForm.value)
    userStore.setUser(res)
    ElMessage.success('登录成功')
    router.push('/admin')
  } catch (error) {
    // 错误已由拦截器处理
  }
}

const handleRegister = async () => {
  // 昵称校验
  if (!registerForm.value.nickname) {
    ElMessage.error('请输入昵称')
    return
  }
  // 邮箱校验
  if (!registerForm.value.email) {
    ElMessage.error('请输入邮箱')
    return
  }
  // 手机号校验
  if (!registerForm.value.phone) {
    ElMessage.error('请输入手机号')
    return
  }
  // 密码校验
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  if (registerForm.value.password.length < 6) {
    ElMessage.error('密码长度不能小于6位')
    return
  }
  
  try {
    // 注意：参数里的 username 也要改成 nickname
    await userRegister({
      nickname: registerForm.value.nickname,
      email: registerForm.value.email,
      phone: registerForm.value.phone,
      password: registerForm.value.password
    })
    ElMessage.success('注册成功，请登录')
    showRegisterDialog.value = false
    // 清空表单
    registerForm.value = {
      nickname: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: ''
    }
  } catch (error) {
    // 错误已由拦截器处理
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}
.tab-switch {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 2px solid #eee;
}
.tab-switch button {
  flex: 1;
  padding: 10px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #666;
}
.tab-switch button.active {
  color: #667eea;
  border-bottom: 2px solid #667eea;
  margin-bottom: -2px;
}
input {
  width: 100%;
  padding: 12px;
  margin-bottom: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}
.login-btn {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
.register-tip {
  text-align: center;
  margin-top: 16px;
  color: #666;
}
.register-tip a {
  color: #667eea;
  cursor: pointer;
}
</style>