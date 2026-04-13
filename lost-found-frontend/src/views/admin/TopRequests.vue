<!-- src/views/admin/TopRequests.vue -->
<template>
  <div class="top-requests">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToAdminHome">主页</span>
        <span @click="goToUserManage">用户管理</span>
        <span @click="goToReports">举报管理</span>
        <span class="active">置顶申请</span>
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
      <!-- 状态筛选 -->
      <div class="status-tabs">
        <button :class="{ active: statusFilter === 0 }" @click="statusFilter = 0">待审批</button>
        <button :class="{ active: statusFilter === 1 }" @click="statusFilter = 1">已通过</button>
        <button :class="{ active: statusFilter === 2 }" @click="statusFilter = 2">已拒绝</button>
      </div>

      <!-- 申请列表 -->
      <div class="request-list" v-loading="loading">
        <div v-for="req in filteredRequests" :key="req.id" class="request-card">
          <div class="request-header">
            <div class="request-type">
  <span class="type-badge">置顶申请</span>
  <router-link :to="`/post/${req.postId}/${req.itemType}`" class="request-title">
    {{ req.postTitle }}
  </router-link>
</div>
            <span class="request-time">{{ formatDateTime(req.createTime) }}</span>
          </div>
          <div class="request-content">
            <div class="request-detail">
              <span>申请人：{{ req.requesterName }}</span>
              <span>申请时长：{{ req.durationHours }} 小时</span>
              <span>帖子类型：{{ req.itemType === 0 ? '失物' : '拾物' }}</span>
            </div>
          </div>
          <div class="request-actions" v-if="req.status === 0">
            <el-button type="success" size="small" @click="approve(req.id, true)">通过</el-button>
            <el-button type="danger" size="small" plain @click="approve(req.id, false)">拒绝</el-button>
          </div>
          <div v-else class="request-status">
            <span :class="req.status === 1 ? 'approved' : 'rejected'">
              {{ req.status === 1 ? '已通过' : '已拒绝' }}
            </span>
            <span class="handle-time">处理时间：{{ formatDateTime(req.approveTime) }}</span>
          </div>
        </div>
        <div v-if="filteredRequests.length === 0 && !loading" class="empty">暂无置顶申请</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTopRequests, approveTopRequest } from '@/api'
import { formatDateTime } from '@/utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref(userStore.userInfo)
const showDropdown = ref(false)
const loading = ref(false)
const requestList = ref([])
const statusFilter = ref(0)

const filteredRequests = computed(() => {
  return requestList.value.filter(r => r.status === statusFilter.value)
})

const loadRequests = async () => {
  loading.value = true
  try {
    const res = await getTopRequests()
    if (Array.isArray(res)) {
      requestList.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      requestList.value = res.data
    } else {
      requestList.value = []
    }
  } finally {
    loading.value = false
  }
}

const approve = async (id, approved) => {
  await approveTopRequest(id, approved)
  ElMessage.success(approved ? '已通过置顶申请' : '已拒绝置顶申请')
  loadRequests()
}

const goToAdminHome = () => router.push('/admin')
const goToUserManage = () => router.push('/admin/users')
const goToReports = () => router.push('/admin/reports')
const goToStatistics = () => router.push('/admin/statistics')
const goToPersonalCenter = () => router.push('/personal-center')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadRequests()
})
</script>

<style scoped>
.request-title {
  font-weight: bold;
  color: #667eea;
  text-decoration: none;
  cursor: pointer;
}

.request-title:hover {
  text-decoration: underline;
}
.top-requests {
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
  max-width: 1000px;
  margin: 0 auto;
}
.status-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.status-tabs button {
  padding: 8px 24px;
  border: none;
  background: white;
  border-radius: 20px;
  cursor: pointer;
}
.status-tabs button.active {
  background: #667eea;
  color: white;
}
.request-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.request-card {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
}
.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}
.request-type {
  display: flex;
  align-items: center;
  gap: 12px;
}
.type-badge {
  background: #2196f3;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.request-title {
  font-weight: bold;
}
.request-time {
  color: #999;
  font-size: 12px;
}
.request-content {
  margin-bottom: 12px;
}
.request-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: #666;
}
.request-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
  border-top: 1px solid #eee;
}
.request-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #eee;
  font-size: 13px;
}
.approved {
  color: #4caf50;
}
.rejected {
  color: #f44336;
}
.handle-time {
  color: #999;
}
.empty {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 12px;
  color: #999;
}
</style>