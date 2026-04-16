<!-- src/views/Messages.vue -->
<template>
  <div class="messages-container">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToHome">首页</span>
        <span @click="goToMyPosts">我的帖子</span>
        <span class="active">消息</span>
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
      <div class="message-tabs">
        <button :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">全部</button>
        <button :class="{ active: activeTab === 'unread' }" @click="activeTab = 'unread'">
          未读
          <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
        </button>
      </div>

      <div class="message-list" v-loading="loading">
        <div 
  v-for="msg in filteredMessages" 
  :key="msg.id" 
  class="message-item"
  :class="{ unread: !msg.isRead }"
  @click="handleMessageClick(msg)"
>
  <!-- 头像：我发的显示我的头像，对方发的显示对方的头像 -->
  <div class="message-avatar" @click.stop="goToUserProfile(msg.fromUserId === currentUserId ? msg.toUserId : msg.fromUserId)">
    <img :src="msg.fromUserId === currentUserId ? (userInfo?.avatar || '/default-avatar.png') : (msg.fromUserAvatar || '/default-avatar.png')" />
  </div>
  
  <div class="message-content">
    <div class="message-header">
      <!-- 昵称：我发的显示"我"，对方发的显示对方昵称 -->
      <span class="from-user" @click.stop="goToUserProfile(msg.fromUserId === currentUserId ? msg.toUserId : msg.fromUserId)">
        <span v-if="msg.fromUserId === currentUserId">我</span>
        <span v-else>{{ msg.fromUserNickname || '用户' }}</span>
      </span>
      <span class="message-type" :class="msg.type">
        {{ msg.type === 'private' ? '私信' : '评论' }}
      </span>
      <span class="time">{{ formatTime(msg.createTime) }}</span>
    </div>
    <div class="message-preview">{{ msg.content }}</div>
    <div v-if="msg.type === 'comment'" class="message-source">
      来源：{{ msg.postTitle }}
    </div>
  </div>
  <div v-if="!msg.isRead" class="unread-dot"></div>
</div>
        <div v-if="filteredMessages.length === 0 && !loading" class="empty">
          暂无消息
        </div>
      </div>
    </div>

    <!-- 私信回复弹窗 -->
    <el-dialog v-model="showReplyDialog" title="回复私信" width="400px">
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容..."
      />
      <template #footer>
        <el-button @click="showReplyDialog = false">取消</el-button>
        <el-button type="primary" @click="sendReply">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMessageList, getUnreadCount, sendMessage, markMessageRead } from '@/api'
import { formatTime } from '@/utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo
const currentUserId = userStore.userInfo?.id;

const loading = ref(false)
const messageList = ref([])
const unreadCount = ref(0)
const activeTab = ref('all')
const showDropdown = ref(false)
const showReplyDialog = ref(false)
const replyContent = ref('')
const currentReplyTo = ref(null)

const filteredMessages = computed(() => {
  if (activeTab.value === 'unread') {
    return messageList.value.filter(m => !m.isRead)
  }
  return messageList.value
})

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessageList()
    console.log('消息列表:', res)  // 调试
    // 修改这里：去掉 .data
    if (Array.isArray(res)) {
      messageList.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      messageList.value = res.data
    } else {
      messageList.value = []
    }
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  const res = await getUnreadCount()
  console.log('未读消息数:', res)
  
  if (typeof res === 'number') {
    unreadCount.value = res
  } else if (res?.data !== undefined) {
    unreadCount.value = res.data
  } else {
    unreadCount.value = 0
  }
}

const handleMessageClick = async (msg) => {
  // 标记为已读
  if (!msg.isRead && msg.toUserId === currentUserId) {
    try {
      await markMessageRead(msg.id)
      msg.isRead = true
      await loadUnreadCount()
    } catch (e) {
      console.log('标记已读失败:', e)
    }
  }
  
  // 如果是私信，打开回复弹窗
  if (msg.type === 'private') {
    // 计算要回复给谁
    let replyToUserId = null;
    if (msg.fromUserId === currentUserId) {
      // 这是我发的消息，对方是 toUserId
      replyToUserId = msg.toUserId;
    } else {
      // 这是对方发的消息，回复给 fromUserId
      replyToUserId = msg.fromUserId;
    }
    
    // 不能回复自己
    if (replyToUserId === currentUserId) {
      ElMessage.warning('不能回复自己发送的消息');
      return;
    }
    
    currentReplyTo.value = replyToUserId;
    showReplyDialog.value = true;
  } else {
    // 评论，跳转到帖子详情
    router.push(`/post/${msg.postId}/${msg.postType}`);
  }
}

const sendReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await sendMessage({
      toUserId: currentReplyTo.value,
      content: replyContent.value
    })
    ElMessage.success('发送成功')
    showReplyDialog.value = false
    replyContent.value = ''
    loadMessages()
  } catch (error) {}
}

const goToHome = () => router.push('/home')
const goToMyPosts = () => router.push('/my-posts')
const goToPublish = () => router.push('/publish')
const goToPersonalCenter = () => router.push('/personal-center')
const goToUserProfile = (userId) => router.push(`/user/${userId}`)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadMessages()
  loadUnreadCount()
})
</script>

<style scoped>
.messages-container {
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
  max-width: 800px;
  margin: 0 auto;
}
.message-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.message-tabs button {
  padding: 8px 20px;
  border: none;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  position: relative;
}
.message-tabs button.active {
  background: #667eea;
  color: white;
}
.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: red;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 10px;
}
.message-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}
.message-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;
}
.message-item:hover {
  background: #f9f9f9;
}
.message-item.unread {
  background: #f0f7ff;
}
.message-avatar {
  margin-right: 12px;
}
.message-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}
.message-content {
  flex: 1;
}
.message-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.from-user {
  font-weight: bold;
  cursor: pointer;
}
.from-user:hover {
  color: #667eea;
}
.message-type {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}
.message-type.private {
  background: #e8f5e9;
  color: #4caf50;
}
.message-type.comment {
  background: #e3f2fd;
  color: #2196f3;
}
.time {
  font-size: 12px;
  color: #999;
}
.message-preview {
  color: #666;
  font-size: 14px;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.message-source {
  font-size: 12px;
  color: #999;
}
.unread-dot {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 10px;
  background: #f44336;
  border-radius: 50%;
}
.empty {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>