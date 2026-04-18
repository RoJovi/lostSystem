<!-- src/views/admin/Statistics.vue -->
<template>
  <div class="statistics">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToAdminHome">主页</span>
        <span @click="goToUserManage">用户管理</span>
        <span @click="goToReports">举报管理</span>
        <span @click="goToTopRequests">置顶申请</span>
        <span class="active">平台统计</span>
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
      <!-- 数据卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
          <div class="stat-label">总帖子数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalLost || 0 }}</div>
          <div class="stat-label">丢失帖子</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalFound || 0 }}</div>
          <div class="stat-label">拾取帖子</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalResolved || 0 }}</div>
          <div class="stat-label">已找回/已归还</div>
        </div>
      </div>
<!-- 新增：活跃用户时间段卡片 -->
<div class="stats-grid" style="margin-top: 20px;">
  <div class="stat-card">
    <div class="stat-value">{{ stats.activeUsersLast7Days || 0 }}</div>
    <div class="stat-label">近7天活跃用户</div>
  </div>
  <div class="stat-card">
    <div class="stat-value">{{ stats.activeUsersLast30Days || 0 }}</div>
    <div class="stat-label">近30天活跃用户</div>
  </div>
  <div class="stat-card">
    <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
    <div class="stat-label">总用户数</div>
  </div>
  <div class="stat-card">
    <div class="stat-value">{{ stats.pendingReports || 0 }}</div>
    <div class="stat-label">待处理举报</div>
  </div>
</div>
      <!-- 图表区域 -->
      <div class="chart-row">
        <div class="chart-card">
          <h3>帖子类型分布</h3>
          <div ref="postChartRef" class="chart"></div>
        </div>
        <div class="chart-card">
          <h3>用户活跃度分布</h3>
          <div ref="userChartRef" class="chart"></div>
        </div>
      </div>

      <!-- 地点统计 -->
      <div class="chart-card full-width">
        <h3>失物高发区域 TOP 5</h3>
        <div ref="locationChartRef" class="chart" style="height: 300px"></div>
      </div>
<!-- AI 总结卡片 -->
<div class="ai-summary-card">
  <div class="summary-header">
    <h3>🤖 AI 周报分析</h3>
    <el-button type="primary" @click="generateAISummary" :loading="aiLoading">
      {{ aiLoading ? '生成中...' : '生成报告' }}
    </el-button>
  </div>
  <div v-if="aiSummary" class="summary-content">
    {{ aiSummary }}
  </div>
  <div v-else class="summary-placeholder">
    点击「生成报告」获取 AI 智能分析
  </div>
</div>

    </div>
  </div>

</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getStatistics } from '@/api'
import * as echarts from 'echarts'
import { getAIStatistics } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const adminInfo = ref(userStore.userInfo)
const showDropdown = ref(false)
const stats = ref({})
const postChartRef = ref(null)
const userChartRef = ref(null)
const locationChartRef = ref(null)

let postChart = null
let userChart = null
let locationChart = null

const loadStatistics = async () => {
  const res = await getStatistics()
  stats.value = res
  renderCharts()
}

const renderCharts = () => {
  // 帖子类型分布饼图
  if (postChartRef.value) {
    if (postChart) postChart.dispose()
    postChart = echarts.init(postChartRef.value)
    postChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { top: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: '55%',
        data: [
          { name: '丢失帖子', value: stats.value.totalLost || 0, itemStyle: { color: '#e74c3c' } },
          { name: '拾取帖子', value: stats.value.totalFound || 0, itemStyle: { color: '#4caf50' } },
          { name: '已找回/已归还', value: stats.value.totalResolved || 0, itemStyle: { color: '#ff9800' } }
        ],
        emphasis: { scale: true }
      }]
    })
  }

  // 用户活跃度分布饼图
  if (userChartRef.value) {
    if (userChart) userChart.dispose()
    userChart = echarts.init(userChartRef.value)
    const totalUsers = stats.value.totalUsers || 0
    const activeUsers = stats.value.activeUsers || 0
    const inactiveUsers = totalUsers - activeUsers
    userChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { top: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: '55%',
        data: [
          { name: '活跃用户', value: activeUsers, itemStyle: { color: '#667eea' } },
          { name: '僵尸用户', value: inactiveUsers, itemStyle: { color: '#999' } }
        ],
        emphasis: { scale: true }
      }]
    })
  }

  // 地点统计柱状图
  if (locationChartRef.value && stats.value.topLocations) {
    if (locationChart) locationChart.dispose()
    locationChart = echarts.init(locationChartRef.value)
    locationChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: { type: 'category', data: stats.value.topLocations.map(l => l.name), axisLabel: { rotate: 30 } },
      yAxis: { type: 'value', name: '丢失数量' },
      series: [{
        type: 'bar',
        data: stats.value.topLocations.map(l => l.count),
        itemStyle: { color: '#667eea', borderRadius: [4, 4, 0, 0] }
      }]
    })
  }
}

// 监听窗口大小变化
watch(() => [postChartRef.value, userChartRef.value, locationChartRef.value], () => {
  setTimeout(() => {
    if (postChart) postChart.resize()
    if (userChart) userChart.resize()
    if (locationChart) locationChart.resize()
  }, 100)
})

const aiLoading = ref(false)
const aiSummary = ref('')

const generateAISummary = async () => {
  aiLoading.value = true
  try {
    const res = await getAIStatistics()
    aiSummary.value = res
  } catch (error) {
    ElMessage.error('生成失败')
  } finally {
    aiLoading.value = false
  }
}

const goToAdminHome = () => router.push('/admin')
const goToUserManage = () => router.push('/admin/users')
const goToReports = () => router.push('/admin/reports')
const goToTopRequests = () => router.push('/admin/top-requests')
const goToPersonalCenter = () => router.push('/personal-center')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics {
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
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
}
.stat-label {
  color: #999;
  font-size: 14px;
  margin-top: 8px;
}
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.chart-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}
.chart {
  width: 100%;
  height: 300px;
}
.full-width {
  grid-column: span 2;
}
.ai-summary-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-top: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.summary-header h3 {
  margin: 0;
}
.summary-content {
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}
.summary-placeholder {
  color: #999;
  text-align: center;
  padding: 40px;
}
</style>