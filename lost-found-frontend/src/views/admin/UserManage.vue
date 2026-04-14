<!-- src/views/admin/UserManage.vue -->
<template>
  <div class="user-manage">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToAdminHome">主页</span>
        <span class="active">用户管理</span>
        <span @click="goToReports">举报管理</span>
        <span @click="goToTopRequests">置顶申请</span>
        <span @click="goToStatistics">平台统计</span>
      </div>
      <el-dropdown trigger="click">
  <div class="user-info">
    <img :src="userInfo?.avatar || '/default-avatar.png'" class="avatar" />
    <span class="nickname">{{ userInfo?.nickname || userInfo?.username }}</span>
    <span class="arrow">▼</span>
  </div>
  <template #dropdown>
    <el-dropdown-menu>
      <el-dropdown-item @click="goToPersonalCenter">个人中心</el-dropdown-item>
      <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
    </el-dropdown-menu>
  </template>
</el-dropdown>
    </div>

    <div class="content">
      <!-- 筛选栏 -->
      <div class="filters">
        <div class="filter-buttons">
          <button :class="{ active: userFilter === 'all' }" @click="userFilter = 'all'">全部用户</button>
          <button :class="{ active: userFilter === 'active' }" @click="userFilter = 'active'">活跃用户</button>
          <button :class="{ active: userFilter === 'inactive' }" @click="userFilter = 'inactive'">僵尸用户</button>
        </div>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="按用户名/昵称搜索"
            prefix-icon="Search"
            clearable
            @keyup.enter="loadUsers"
            @clear="loadUsers"
          />
        </div>
      </div>

      <!-- 用户列表 -->
      <div class="user-list" v-loading="loading">
        <div v-for="user in filteredUsers" :key="user.id" class="user-card">
          <div class="user-avatar">
            <img :src="user.avatar || '/default-avatar.png'" />
          </div>
          <div class="user-info">
            <div class="user-name">
              {{ user.nickname || user.username }}
              <span class="user-username">@{{ user.username }}</span>
            </div>
            <div class="user-detail">
              <span>📧 {{ user.email }}</span>
              <span v-if="user.phone">📱 {{ user.phone }}</span>
            </div>
            <div class="user-stats">
              <span>📝 {{ user.post_count }} 篇帖子</span>
              <span>💬 {{ user.comment_count }} 条评论</span>
              <span>🕐 注册于 {{ formatDate(user.createTime) }}</span>
            </div>
          </div>
          <div class="user-actions">
            <span class="user-status" :class="user.status === 1 ? 'active' : 'banned'">
              {{ user.status === 1 ? '正常' : '已封禁' }}
            </span>
            <el-button 
              :type="user.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="toggleBan(user)"
            >
              {{ user.status === 1 ? '封禁' : '解封' }}
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              plain
              @click="confirmDelete(user)"
            >
              删除
            </el-button>
          </div>
        </div>
        <div v-if="filteredUsers.length === 0 && !loading" class="empty">暂无用户</div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400px">
      <p>确定要删除用户 "{{ deleteTarget?.nickname || deleteTarget?.username }}" 吗？</p>
      <p class="warning">此操作将同时删除该用户的所有帖子和评论，且不可恢复！</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="deleteUser">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllUsers, banUser, deleteUser } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref(userStore.userInfo)
const showDropdown = ref(false)
const loading = ref(false)
const userList = ref([])
const userFilter = ref('all')
const searchKeyword = ref('')
const showDeleteDialog = ref(false)
const deleteTarget = ref(null)

const filteredUsers = computed(() => {
  let result = userList.value
  
  // 按活跃度筛选
  if (userFilter.value === 'active') {
    result = result.filter(u => u.post_count + u.comment_count >= 5)
  } else if (userFilter.value === 'inactive') {
    result = result.filter(u => u.post_count + u.comment_count === 0)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    result = result.filter(u => 
      u.username?.toLowerCase().includes(kw) || 
      u.nickname?.toLowerCase().includes(kw)
    )
  }
  
  return result
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    if (Array.isArray(res)) {
      userList.value = res
    } else if (res?.data && Array.isArray(res)) {
      userList.value = res
    } else {
      userList.value = []
    }
  } finally {
    loading.value = false
  }
}

const toggleBan = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  await banUser(user.id, newStatus)
  user.status = newStatus
  ElMessage.success(newStatus === 1 ? '已解封用户' : '已封禁用户')
}

const confirmDelete = (user) => {
  deleteTarget.value = user
  showDeleteDialog.value = true
}

const deleteUserConfirm = async () => {
  await deleteUser(deleteTarget.value.id)
  ElMessage.success('删除成功')
  showDeleteDialog.value = false
  loadUsers()
}

const formatDate = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

const goToAdminHome = () => router.push('/admin')
const goToReports = () => router.push('/admin/reports')
const goToTopRequests = () => router.push('/admin/top-requests')
const goToStatistics = () => router.push('/admin/statistics')
const goToPersonalCenter = () => router.push('/personal-center')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
  min-height: 100vh;
  background: #f0f2f5;
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
  flex: 1;
}
.nav-links span {
  cursor: pointer;
  font-size: 16px;
}
.nav-links span.active {
  color: #667eea;
  font-weight: bold;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  position: relative;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.dropdown {
  position: absolute;
  top: 50px;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
  min-width: 120px;
  z-index: 10;
}
.dropdown div {
  padding: 10px 16px;
  cursor: pointer;
}
.content {
  padding: 80px 24px 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
}
.filter-buttons {
  display: flex;
  gap: 12px;
}
.filter-buttons button {
  padding: 6px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
}
.filter-buttons button.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}
.search-box {
  width: 250px;
}
.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.user-card {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-avatar img {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
}
.user-info {
  flex: 1;
}
.user-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 6px;
}
.user-username {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  margin-left: 8px;
}
.user-detail {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}
.user-stats {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}
.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-status {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
}
.user-status.active {
  background: #e8f5e9;
  color: #4caf50;
}
.user-status.banned {
  background: #ffebee;
  color: #f44336;
}
.empty {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 12px;
  color: #999;
}
.warning {
  color: #f44336;
  font-size: 12px;
  margin-top: 8px;
}
</style>