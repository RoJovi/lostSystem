<!-- src/views/admin/AdminHome.vue -->
<template>
  <div class="admin-home">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span class="active">主页</span>
        <span @click="goToUserManage">用户管理</span>
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
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
            <div class="stat-label">总帖子数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🚨</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingReports || 0 }}</div>
            <div class="stat-label">待处理举报</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📌</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingTopRequests || 0 }}</div>
            <div class="stat-label">待审批置顶</div>
          </div>
        </div>
      </div>

      <!-- 近期动态 -->
      <div class="recent-section">
        <div class="section-header">
          <h3>近期举报</h3>
          <el-button type="text" @click="goToReports">查看更多 →</el-button>
        </div>
        <div class="recent-list">
          <div v-for="report in recentReports" :key="report.id" class="recent-item">
            <div class="recent-info">
              <span class="recent-type">举报</span>
              <span>{{ report.reason }}</span>
              <span class="recent-time">{{ formatTime(report.createTime) }}</span>
            </div>
            <el-button size="small" type="primary" @click="handleReport(report.id, 'approve')">处理</el-button>
          </div>
          <div v-if="recentReports.length === 0" class="empty">暂无举报</div>
        </div>
      </div>

      <div class="recent-section">
        <div class="section-header">
          <h3>置顶申请</h3>
          <el-button type="text" @click="goToTopRequests">查看更多 →</el-button>
        </div>
        <div class="recent-list">
          <div v-for="req in recentTopRequests" :key="req.id" class="recent-item">
            <div class="recent-info">
              <span class="recent-type">置顶申请</span>
              <span>帖子：{{ req.postTitle }}</span>
              <span class="recent-time">{{ formatTime(req.createTime) }}</span>
            </div>
<div class="recent-actions">
  <el-button size="small" type="success" @click="approveTop(req.id, true)">通过</el-button>
  <el-button size="small" type="danger" @click="approveTop(req.id, false)">拒绝</el-button>
</div>
          </div>
          <div v-if="recentTopRequests.length === 0" class="empty">暂无置顶申请</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getStatistics, getReports, getTopRequests, handleReport, approveTopRequest } from '@/api'
import { formatTime } from '@/utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref(userStore.userInfo)
const showDropdown = ref(false)
const stats = ref({})
const recentReports = ref([])
const recentTopRequests = ref([])

const loadStatistics = async () => {
  const res = await getStatistics()
  stats.value = res
}

const loadRecentReports = async () => {
  const res = await getReports(0)
  recentReports.value = Array.isArray(res) ? res.slice(0, 5) : (res?.data?.slice(0, 5) || [])
}

const loadRecentTopRequests = async () => {
  const res = await getTopRequests(0)
  recentTopRequests.value = Array.isArray(res) ? res.slice(0, 5) : (res?.data?.slice(0, 5) || [])
}

const handleReportAction = async (id, action) => {
  await handleReport(id, action)
  ElMessage.success('处理成功')
  loadRecentReports()
  loadStatistics()
}

const approveTop = async (id, approved) => {
  await approveTopRequest(id, approved)
  ElMessage.success(approved ? '已通过置顶申请' : '已拒绝置顶申请')
  loadRecentTopRequests()
  loadStatistics()
}

const goToUserManage = () => router.push('/admin/users')
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
  loadStatistics()
  loadRecentReports()
  loadRecentTopRequests()
})
</script>

<style scoped>
.admin-home {
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
  max-width: 1200px;
  margin: 0 auto;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.stat-icon {
  font-size: 40px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
.stat-label {
  color: #999;
  font-size: 14px;
}
.recent-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.section-header h3 {
  margin: 0;
}
.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}
.recent-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.recent-type {
  background: #e3f2fd;
  color: #2196f3;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.recent-time {
  color: #999;
  font-size: 12px;
}
.recent-actions {
  display: flex;
  gap: 8px;
}
.empty {
  text-align: center;
  padding: 30px;
  color: #999;
}
</style>