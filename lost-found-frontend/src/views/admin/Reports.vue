<!-- src/views/admin/Reports.vue -->
<template>
  <div class="reports-manage">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToAdminHome">主页</span>
        <span @click="goToUserManage">用户管理</span>
        <span class="active">举报管理</span>
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
      <!-- 状态筛选 -->
      <div class="status-tabs">
        <button :class="{ active: statusFilter === 0 }" @click="statusFilter = 0">待处理</button>
        <button :class="{ active: statusFilter === 1 }" @click="statusFilter = 1">已通过</button>
        <button :class="{ active: statusFilter === 2 }" @click="statusFilter = 2">已驳回</button>
      </div>

      <!-- 举报列表 -->
      <div class="report-list" v-loading="loading">
        <div v-for="report in filteredReports" :key="report.id" class="report-card">
          <div class="report-header">
            <div class="report-type">
              <span class="type-badge">举报</span>
              <span class="report-reason">{{ report.reason }}</span>
            </div>
            <span class="report-time">{{ formatDateTime(report.createTime) }}</span>
          </div>
          <div class="report-content">
            <div class="report-detail">
              <span>举报人：{{ report.reporterName }}</span>
              <span>被举报内容：</span>
              <router-link :to="`/post/${report.itemId}/${report.itemType}`" class="post-link">
                {{ report.postTitle }}
              </router-link>
            </div>
            <div v-if="report.description" class="report-description">
              补充说明：{{ report.description }}
            </div>
</div>
    <div class="report-actions" v-if="report.status === 0">
      <el-button type="success" size="small" @click="handleReportAction(report.id, 'approve')">通过举报</el-button>
      <el-button type="danger" size="small" plain @click="handleReportAction(report.id, 'reject')">驳回举报</el-button>
      <el-button type="danger" size="small" @click="banUserAndReport(report)">封禁用户并删除</el-button>
          </div>
	
          <div v-else class="report-status">
            <span :class="report.status === 1 ? 'approved' : 'rejected'">
              {{ report.status === 1 ? '已通过' : '已驳回' }}
            </span>
            <span class="handle-time">处理时间：{{ formatDateTime(report.handleTime) }}</span>
          </div>
        </div>
        <div v-if="filteredReports.length === 0 && !loading" class="empty">暂无举报</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getReports, handleReport, banUser, deletePost } from '@/api'
import { formatDateTime } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref(userStore.userInfo)
const showDropdown = ref(false)
const loading = ref(false)
const reportList = ref([])
const statusFilter = ref(0)

const filteredReports = computed(() => {
  return reportList.value.filter(r => r.status === statusFilter.value)
})

const loadReports = async () => {
  loading.value = true
  try {
    const res = await getReports()
    // 兼容处理
    if (Array.isArray(res)) {
      reportList.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      reportList.value = res.data
    } else {
      reportList.value = []
    }
  } finally {
    loading.value = false
  }
}
const handleReportAction = async (id, action) => {
  await handleReport(id, action)
  ElMessage.success(action === 'approve' ? '已通过举报，帖子将被删除' : '已驳回举报')
  loadReports()
}

const banUserAndReport = async (report) => {
  ElMessageBox.confirm('确定要封禁该用户并删除其所有内容吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await banUser(report.userId, 0)
    await deletePost(report.itemId, report.itemType)
    await handleReport(report.id, 'approve')
    ElMessage.success('已封禁用户并删除违规内容')
  }).catch(() => {})
}

const goToAdminHome = () => router.push('/admin')
const goToUserManage = () => router.push('/admin/users')
const goToTopRequests = () => router.push('/admin/top-requests')
const goToStatistics = () => router.push('/admin/statistics')
const goToPersonalCenter = () => router.push('/personal-center')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.reports-manage {
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
.report-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.report-card {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
}
.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}
.report-type {
  display: flex;
  align-items: center;
  gap: 12px;
}
.type-badge {
  background: #ff9800;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.report-reason {
  font-weight: bold;
}
.report-time {
  color: #999;
  font-size: 12px;
}
.report-content {
  margin-bottom: 12px;
}
.report-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 14px;
  color: #666;
}
.post-link {
  color: #667eea;
  text-decoration: none;
}
.post-link:hover {
  text-decoration: underline;
}
.report-description {
  margin-top: 8px;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
}
.report-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
  border-top: 1px solid #eee;
}
.report-status {
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