<!-- src/views/UserProfile.vue -->
<template>
  <div class="user-profile">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goBack">← 返回</span>
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

    <div class="content" v-loading="loading">
      <div class="profile-header">
        <div class="profile-avatar">
          <img :src="targetUser?.avatar || '/default-avatar.png'" />
        </div>
        <div class="profile-info">
          <div class="profile-name">
            {{ targetUser?.nickname || targetUser?.username }}
            <span class="user-type" v-if="targetUser?.post_count >= 10">🌟 活跃用户</span>
          </div>
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-value">{{ targetUser?.post_count || 0 }}</span>
              <span class="stat-label">发布</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ targetUser?.comment_count || 0 }}</span>
              <span class="stat-label">评论</span>
            </div>
          </div>
        </div>
        <div class="profile-actions">
          <el-button 
            v-if="!isSelf && userType === 0"
            type="primary" 
            @click="showMessageDialog = true"
          >
            💬 私信
          </el-button>
          <el-dropdown v-if="!isSelf" trigger="click" @command="handleUserAction">
            <span class="more-btn">⋮</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="report">🚨 举报该用户</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="user-posts">
        <h3>{{ targetUser?.nickname || targetUser?.username }} 的帖子</h3>
        <div class="post-list">
          <div v-for="post in userPosts" :key="post.id" class="post-card" @click="goToDetail(post)">
            <div class="post-header">
              <span class="post-type" :class="post.type === 0 ? 'lost' : 'found'">
                {{ post.type === 0 ? '寻物' : '拾物' }}
              </span>
            </div>
            <h4>{{ post.title }}</h4>
            <p class="post-time">{{ formatTime(post.createTime) }}</p>
          </div>
          <div v-if="userPosts.length === 0" class="empty">暂无帖子</div>
        </div>
      </div>
    </div>

    <!-- 私信弹窗 -->
    <el-dialog v-model="showMessageDialog" title="发送私信" width="400px">
      <el-input
        v-model="messageContent"
        type="textarea"
        :rows="4"
        placeholder="请输入消息内容..."
      />
      <template #footer>
        <el-button @click="showMessageDialog = false">取消</el-button>
        <el-button type="primary" @click="sendMessageToUser">发送</el-button>
      </template>
    </el-dialog>

    <!-- 举报弹窗 -->
    <el-dialog v-model="showReportDialog" title="举报用户" width="400px">
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="举报原因" required>
          <el-select v-model="reportForm.reason" placeholder="请选择">
            <el-option label="虚假信息" value="虚假信息" />
            <el-option label="骚扰行为" value="骚扰行为" />
            <el-option label="恶意营销" value="恶意营销" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体描述" v-if="reportForm.reason === '其他'">
          <el-input v-model="reportForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReportDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUserReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo, getUserPosts, sendMessage, report, getMyPosts } from '@/api'
import { formatTime } from '@/utils/format'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userId = ref(parseInt(route.params.id))
const loading = ref(false)
const showDropdown = ref(false)
const showMessageDialog = ref(false)
const showReportDialog = ref(false)
const targetUser = ref(null)
const userPosts = ref([])
const messageContent = ref('')
const reportForm = ref({ reason: '', description: '' })

const currentUser = computed(() => userStore.userInfo)
const userType = computed(() => userStore.userType)
const isSelf = computed(() => currentUser.value?.id === userId.value)

const loadTargetUser = async () => {
  const res = await getUserInfo(userId.value)
  targetUser.value = res.data
}

const loadUserPosts = async () => {
  const res = await getUserPosts(userId.value)
  userPosts.value = res.data
}

const sendMessageToUser = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  try {
    await sendMessage({
      toUserId: userId.value,
      content: messageContent.value
    })
    ElMessage.success('发送成功')
    showMessageDialog.value = false
    messageContent.value = ''
  } catch (error) {}
}

const handleUserAction = (command) => {
  if (command === 'report') {
    showReportDialog.value = true
  }
}

const submitUserReport = async () => {
  const reason = reportForm.value.reason === '其他' 
    ? reportForm.value.description 
    : reportForm.value.reason
  await report({
    targetUserId: userId.value,
    reason: reason
  })
  ElMessage.success('举报已提交')
  showReportDialog.value = false
  reportForm.value = { reason: '', description: '' }
}

const goToDetail = (post) => {
  router.push(`/post/${post.id}/${post.type}`)
}

const goBack = () => {
  router.back()
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
  loadTargetUser()
  loadUserPosts()
})
</script>

<style scoped>
.user-profile {
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
.nav-links span {
  cursor: pointer;
  font-size: 16px;
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
.profile-header {
  background: white;
  border-radius: 20px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}
.profile-avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}
.profile-info {
  flex: 1;
}
.profile-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-type {
  font-size: 12px;
  background: #ff9800;
  color: white;
  padding: 2px 8px;
  border-radius: 20px;
}
.profile-stats {
  display: flex;
  gap: 24px;
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  display: block;
}
.stat-label {
  font-size: 12px;
  color: #999;
}
.profile-actions {
  display: flex;
  gap: 12px;
}
.more-btn {
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
}
.user-posts {
  background: white;
  border-radius: 20px;
  padding: 24px;
}
.user-posts h3 {
  margin-bottom: 20px;
}
.post-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.post-card {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.post-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.post-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}
.post-type.lost {
  background: #ffebee;
  color: #e74c3c;
}
.post-type.found {
  background: #e8f5e9;
  color: #4caf50;
}
.post-card h4 {
  margin: 8px 0;
  font-size: 14px;
}
.post-time {
  font-size: 12px;
  color: #999;
}
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  grid-column: span 3;
}
</style>