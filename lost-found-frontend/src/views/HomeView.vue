<template>
  <div class="home-container">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span :class="{ active: currentTab === 'home' }" @click="currentTab = 'home'">首页</span>
        <span :class="{ active: currentTab === 'myposts' }" @click="goToMyPosts">我的帖子</span>
        <span :class="{ active: currentTab === 'messages' }" @click="goToMessages">
          消息
          <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
        </span>
        <span :class="{ active: currentTab === 'publish' }" @click="goToPublish">发布</span>
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

    <!-- 筛选栏（仅首页显示） -->
    <div class="filters" v-if="currentTab === 'home'">
      <div class="filter-row">
        <div class="type-buttons">
          <button :class="{ active: filterType === 'all' }" @click="filterType = 'all'">全部</button>
          <button :class="{ active: filterType === 'lost' }" @click="filterType = 'lost'">丢失</button>
          <button :class="{ active: filterType === 'found' }" @click="filterType = 'found'">拾取</button>
        </div>
        <div class="search-box">
          <input v-model="searchKeyword" placeholder="按名称搜索" @keyup.enter="loadFeed" />
          <button @click="loadFeed">搜索</button>
        </div>
      </div>
      <div class="filter-row">
        <button class="sort-btn" @click="toggleSort">
          {{ sortOrder === 'desc' ? '时间倒序' : '时间正序' }} 🔽
        </button>
   <div class="location-select" @click="showLocationPicker = !showLocationPicker">
  <span>{{ selectedLocationName || '全部地点' }} ▼</span>
  <div class="location-dropdown" v-show="showLocationPicker">
    <div @click="handleNodeClick({ id: null, name: '' })">全部地点</div>
    <el-tree 
      :data="locationTree" 
      :props="{ label: 'name', children: 'children' }"
      @node-click="handleNodeClick"
    />
  </div>
</div>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="feed-list" v-loading="loading">
      <div class="post-grid">
        <div v-for="post in feedList" :key="post.id" class="post-card" @click="goToDetail(post)">
	<img v-if="post.imageUrl" :src="post.imageUrl" class="post-cover" />
          <div class="post-header">
            <span class="post-type" :class="post.type === 0 ? 'lost' : 'found'">
              {{ post.type === 0 ? '寻物' : '拾物' }}
            </span>
            <span v-if="post.isTop" class="top-badge">🔝 置顶</span>
          </div>
          <h3>{{ post.title }}</h3>
          <p class="post-meta">
            {{ post.userNickname }} · {{ formatTime(post.createTime) }}
          </p>
        </div>
      </div>
      <div v-if="feedList.length === 0 && !loading" class="empty">暂无帖子</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getFeedList, getUnreadCount, getLocations } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

const currentTab = ref('home')
const showDropdown = ref(false)
const filterType = ref('all')
const searchKeyword = ref('')
const sortOrder = ref('desc')
const selectedLocationId = ref(null)
const selectedLocationName = ref('')
const showLocationPicker = ref(false)
const loading = ref(false)
const feedList = ref([])
const unreadCount = ref(0)
const locationTree = ref([])

const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const hours = diff / (1000 * 60 * 60)
  if (hours < 24) return `${Math.floor(hours)}小时前`
  if (hours < 48) return '昨天'
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const loadFeed = async () => {
loading.value = true
  try {
    const res = await getFeedList({
      type: filterType.value,
      keyword: searchKeyword.value,
      sort: sortOrder.value,
      locationId: selectedLocationId.value
    })
    console.log('首页数据:', res)  // 调试
    // 修改这里：去掉 .data
    if (Array.isArray(res)) {
      feedList.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      feedList.value = res.data
    } else {
      feedList.value = []
    }
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  const res = await getUnreadCount()
  console.log('未读消息数:', res)  // 调试
  // 修改这里：兼容处理
  if (typeof res === 'number') {
    unreadCount.value = res
  } else if (res?.data !== undefined) {
    unreadCount.value = res.data
  } else {
    unreadCount.value = 0
  }
}

const loadLocations = async () => {
  const res = await getLocations()
  console.log('地点数据:', res)
  
  // 后端已经返回平铺数组，直接赋值
  if (Array.isArray(res)) {
    locationTree.value = res
  } else if (res?.data && Array.isArray(res.data)) {
    locationTree.value = res.data
  } else {
    locationTree.value = []
  }
  
  console.log('locationTree赋值后:', locationTree.value)
}

const toggleSort = () => {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  loadFeed()
}

const selectLocation = (id, name) => {
  selectedLocationId.value = id
  selectedLocationName.value = name || ''
  showLocationPicker.value = false
  loadFeed()
}

// 树形菜单点击处理
const handleNodeClick = (data) => {
  selectedLocationId.value = data.id
  selectedLocationName.value = data.name
  showLocationPicker.value = false
  loadFeed()
}

const goToDetail = (post) => {
  router.push(`/post/${post.id}/${post.type}`)
}

const goToMyPosts = () => {
  router.push('/my-posts')
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

watch([filterType, searchKeyword], () => {
  loadFeed()
})

onMounted(() => {
  loadFeed()
  loadUnreadCount()
  loadLocations()
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
.home-container {
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
  position: relative;
}
.nav-links span.active {
  color: #667eea;
  font-weight: bold;
}
.unread-badge {
  position: absolute;
  top: -8px;
  right: -16px;
  background: red;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
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
.filters {
  margin-top: 80px;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #eee;
}
.filter-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}
.type-buttons {
  display: flex;
  gap: 12px;
}
.type-buttons button, .sort-btn {
  padding: 6px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
}
.type-buttons button.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}
.search-box {
  display: flex;
  gap: 8px;
}
.search-box input {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  width: 200px;
}
.location-select {
  position: relative;
  cursor: pointer;
  padding: 6px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
}
.location-dropdown {
  position: absolute;
  top: 35px;
  left: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 10;
}
.location-dropdown div {
  padding: 8px 16px;
  cursor: pointer;
}
.location-dropdown div:hover {
  background: #f5f5f5;
}
.feed-list {
  padding: 24px;
  margin-top: 20px;
}
.post-grid {
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
</style>