<!-- src/views/MyPosts.vue -->
<template>
  <div class="my-posts-container">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToHome">首页</span>
        <span class="active">我的帖子</span>
        <span @click="goToMessages">消息</span>
        <span @click="goToPublish">发布</span>
      </div>
<el-dropdown trigger="click">
  <div class="user-info">
    <img :src="userInfo?.avatar || '/default-avatar.png'" class="avatar" />
    <span class="nickname">{{ userInfo?.nickname || '用户' }}</span>
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
      <div class="post-list" v-loading="loading">
        <div v-for="post in myPosts" :key="post.id" class="post-card" @click="goToDetail(post)">
	   <img v-if="post.imageUrl" :src="post.imageUrl" class="post-cover" />
          <div class="post-header">
            <span class="post-type" :class="post.type === 0 ? 'lost' : 'found'">
              {{ post.type === 0 ? '寻物' : '拾物' }}
            </span>
            <span v-if="post.isTop" class="top-badge">🔝 置顶</span>
            <span :class="['status', getStatusClass(post.status)]">
              {{ getStatusText(post.type, post.status) }}
            </span>
          </div>
          <h3>{{ post.title }}</h3>
          <p class="post-meta">
            {{ formatTime(post.createTime) }} · 👁️ {{ post.viewCount }}次浏览
          </p>
        </div>
        <div v-if="myPosts.length === 0 && !loading" class="empty">暂无帖子，去发布一条吧~</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMyPosts } from '@/api'
import { formatTime } from '@/utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

const loading = ref(false)
const myPosts = ref([])
const showDropdown = ref(false)

const getStatusText = (type, status) => {
  if (status === 0) return '进行中'
  if (type === 0) return '已找回'
  return '已归还'
}

const getStatusClass = (status) => {
  return status === 0 ? 'ongoing' : 'resolved'
}

const loadMyPosts = async () => {
  loading.value = true
try {
    const res = await getMyPosts()
    console.log('=== getMyPosts 调试 ===')
    console.log('res 完整内容:', res)
    console.log('res 类型:', typeof res)
    console.log('res 是数组吗?', Array.isArray(res))
    console.log('res.data:', res?.data)
    console.log('=====================')
    
    // 根据实际返回结构赋值
    if (Array.isArray(res)) {
      myPosts.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      myPosts.value = res.data
    } else {
      myPosts.value = []
    }
  } finally {
    loading.value = false
  }
}

const goToDetail = (post) => {
  router.push(`/post/${post.id}/${post.type}`)
}

const goToHome = () => {
  router.push('/home')
}

const goToMessages = () => {
  router.push('/messages')
}

const goToPublish = () => {
  router.push('/publish')
}

const goToPersonalCenter = () => {
  router.push('/personal-center')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadMyPosts()
})
</script>

<style scoped>
.post-cover {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.my-posts-container {
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
.dropdown div:hover {
  background: #f5f5f5;
}
.content {
  padding: 80px 24px 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.post-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.post-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}
.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}
.post-type {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.post-type.lost {
  background: #ffebee;
  color: #e74c3c;
}
.post-type.found {
  background: #e8f5e9;
  color: #4caf50;
}
.top-badge {
  color: #ff9800;
  font-size: 12px;
}
.status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
.status.ongoing {
  background: #e3f2fd;
  color: #2196f3;
}
.status.resolved {
  background: #e8f5e9;
  color: #4caf50;
}
.post-card h3 {
  margin: 8px 0;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.post-meta {
  color: #999;
  font-size: 12px;
}
.empty {
  text-align: center;
  padding: 60px;
  color: #999;
  grid-column: span 3;
}
</style>