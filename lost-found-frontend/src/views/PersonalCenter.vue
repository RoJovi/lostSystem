<template>
  <div class="personal-center">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <template v-if="userType === 1">
          <span @click="goToAdminHome">主页</span>
          <span @click="goToUserManage">用户管理</span>
          <span @click="goToReports">举报管理</span>
          <span @click="goToTopRequests">置顶申请</span>
          <span @click="goToStatistics">平台统计</span>
        </template>
        <template v-else>
          <span @click="goToHome">首页</span>
          <span @click="goToMyPosts">我的帖子</span>
          <span @click="goToMessages">消息</span>
          <span @click="goToPublish">发布</span>
        </template>
      </div>
      <el-dropdown trigger="click">
        <div class="user-info">
          <img :src="userAvatar" class="avatar" />
          <span class="nickname">{{ userDisplayName }}</span>
          <span class="arrow">▼</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>个人中心</el-dropdown-item>
            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="content">
      <div class="profile-card">
        <!-- 头像区域：在名字上面 -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img :src="profileAvatar" class="profile-avatar" />
            <!-- 只有普通用户显示上传按钮 -->
            <div class="avatar-upload" v-if="userType === 0" @click="triggerAvatarUpload">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
          <input
            type="file"
            ref="avatarInput"
            style="display: none"
            accept="image/jpeg,image/png"
            @change="uploadAvatar"
          />
        </div>

        <!-- 信息区域 -->
        <div class="info-section">
          <!-- 普通用户显示：昵称、邮箱、手机号 -->
          <template v-if="userType === 0">
            <div class="info-row">
              <span class="label">昵称</span>
              <el-input v-model="form.nickname" placeholder="未设置" @input="markChanged" />
            </div>
            <div class="info-row">
              <span class="label">邮箱</span>
              <el-input v-model="form.email" placeholder="未设置" @input="markChanged" />
            </div>
            <div class="info-row">
              <span class="label">手机号</span>
              <el-input v-model="form.phone" placeholder="未设置" @input="markChanged" />
            </div>
          </template>
          
          <!-- 管理员显示：姓名、工号、联系电话 -->
          <template v-else>
            <div class="info-row">
              <span class="label">姓名</span>
              <el-input v-model="form.name" placeholder="未设置" @input="markChanged" />
            </div>
            <div class="info-row">
              <span class="label">工号</span>
              <el-input v-model="form.adminNum" disabled />
            </div>
            <div class="info-row">
              <span class="label">联系电话</span>
              <el-input v-model="form.phone" placeholder="未设置" @input="markChanged" />
            </div>
          </template>
        </div>

        <div class="action-buttons">
          <el-button 
            :type="hasChanged ? 'primary' : 'info'"
            :disabled="!hasChanged"
            @click="saveInfo"
          >
            保存修改
          </el-button>
          <el-button @click="showPasswordDialog = true">修改密码</el-button>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码" required>
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, updatePassword, getAdminInfo, updateAdminInfo, updateAdminPassword } from '@/api'
import { Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userType = userStore.userType

// 顶栏显示用的计算属性
const userAvatar = computed(() => {
  if (userType === 1) return '/default-avatar.png'
  return userStore.userInfo?.avatar || '/default-avatar.png'
})

const userDisplayName = computed(() => {
  if (userType === 1) return userStore.userInfo?.name || '管理员'
  return userStore.userInfo?.nickname || userStore.userInfo?.username || '用户'
})

// 页面内头像
const profileAvatar = computed(() => {
  if (userType === 1) return '/default-avatar.png'
  return form.avatar || '/default-avatar.png'
})

const goToAdminHome = () => router.push('/admin')
const goToUserManage = () => router.push('/admin/users')
const goToReports = () => router.push('/admin/reports')
const goToTopRequests = () => router.push('/admin/top-requests')
const goToStatistics = () => router.push('/admin/statistics')
const goToHome = () => router.push('/home')
const goToMyPosts = () => router.push('/my-posts')
const goToMessages = () => router.push('/messages')
const goToPublish = () => router.push('/publish')

const showPasswordDialog = ref(false)
const hasChanged = ref(false)
const avatarInput = ref(null)

const form = reactive({
  nickname: '',
  email: '',
  phone: '',
  name: '',
  adminNum: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const markChanged = () => {
  hasChanged.value = true
}

const loadUserInfo = async () => {
  if (userType === 1) {
    const res = await getAdminInfo()
    form.name = res.name || ''
    form.adminNum = res.adminNum || ''
    form.phone = res.phone || ''
    form.avatar = ''
  } else {
    const res = await getUserInfo()
    form.nickname = res.nickname || ''
    form.email = res.email || ''
    form.phone = res.phone || ''
    form.avatar = res.avatar || ''
  }
}

const saveInfo = async () => {
  try {
    if (userType === 1) {
      await updateAdminInfo({
        name: form.name,
        phone: form.phone
      })
      userStore.userInfo = { ...userStore.userInfo, name: form.name, phone: form.phone }
    } else {
      await updateUserInfo({
        nickname: form.nickname,
        email: form.email,
        phone: form.phone,
        avatar: form.avatar
      })
      userStore.userInfo = { ...userStore.userInfo, ...form }
    }
    ElMessage.success('保存成功')
    hasChanged.value = false
  } catch (error) {}
}

const triggerAvatarUpload = () => {
  avatarInput.value.click()
}

const uploadAvatar = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const res = await fetch('/api/upload', {
      method: 'POST',
      headers: { token: localStorage.getItem('token') },
      body: formData
    })
    const data = await res.json()
    if (data.code === 200) {
      form.avatar = data.data
      hasChanged.value = true
      ElMessage.success('头像上传成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('密码长度不能小于6位')
    return
  }
  try {
    if (userType === 1) {
      await updateAdminPassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
    } else {
      await updatePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
    }
    ElMessage.success('密码修改成功，请重新登录')
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error) {}
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.personal-center {
  min-height: 100vh;
  background: #f5f5f5;
}
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 100;
}
.nav-links {
  display: flex;
  gap: 32px;
}
.nav-links span {
  cursor: pointer;
  font-size: 16px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.content {
  padding: 80px 24px 24px;
  max-width: 800px;
  margin: 0 auto;
}
.profile-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-section {
  margin-bottom: 30px;
}
.avatar-wrapper {
  position: relative;
}
.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #667eea;
}
.avatar-upload {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #667eea;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
}
.info-section {
  width: 100%;
  margin-bottom: 30px;
}
.info-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}
.info-row .label {
  width: 80px;
  font-weight: bold;
  color: #333;
}
.info-row .el-input {
  flex: 1;
}
.action-buttons {
  display: flex;
  gap: 16px;
}
</style>