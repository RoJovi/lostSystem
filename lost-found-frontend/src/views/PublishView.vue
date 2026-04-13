<!-- src/views/PublishView.vue -->
<template>
  <div class="publish-container">
    <!-- 顶栏 -->
    <div class="header">
      <div class="nav-links">
        <span @click="goToHome">首页</span>
        <span @click="goToMyPosts">我的帖子</span>
        <span @click="goToMessages">消息</span>
        <span class="active">发布</span>
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
      <div class="publish-options">
        <div class="publish-card lost" @click="selectType(0)">
          <div class="emoji">(Ｔ▽Ｔ)</div>
          <div class="title">我东西丢了</div>
          <div class="desc">发布失物信息，让好心人帮你找</div>
        </div>
        <div class="publish-card found" @click="selectType(1)">
          <div class="emoji">(｡･ω･｡)</div>
          <div class="title">我找到东西了</div>
          <div class="desc">发布拾物信息，物归原主</div>
        </div>
      </div>
    </div>

    <!-- 发布表单弹窗 -->
    <el-dialog 
      v-model="showFormDialog" 
      :title="publishType === 0 ? '发布失物信息' : '发布拾物信息'"
      width="550px"
      destroy-on-close
    >
      <PublishForm 
        :type="publishType" 
        @success="handlePublishSuccess"
        @cancel="showFormDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import PublishForm from './PublishForm.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

const showDropdown = ref(false)
const showFormDialog = ref(false)
const publishType = ref(0)

const selectType = (type) => {
  publishType.value = type
  showFormDialog.value = true
}

const handlePublishSuccess = () => {
  showFormDialog.value = false
  ElMessage.success('发布成功')
  router.push('/home')
}

const goToHome = () => router.push('/home')
const goToMyPosts = () => router.push('/my-posts')
const goToMessages = () => router.push('/messages')
const goToPersonalCenter = () => router.push('/personal-center')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>


<style scoped>
.publish-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: rgba(255,255,255,0.95);
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
  padding: 100px 24px 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  box-sizing: border-box;
}
.publish-options {
  display: flex;
  gap: 40px;
  justify-content: center;
  flex-wrap: wrap;
}
.publish-card {
  width: 300px;
  padding: 40px 24px;
  background: white;
  border-radius: 24px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}
.publish-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.publish-card.lost:hover {
  background: #ffebee;
}
.publish-card.found:hover {
  background: #e8f5e9;
}
.emoji {
  font-size: 64px;
  margin-bottom: 16px;
}
.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
}
.desc {
  color: #666;
  font-size: 14px;
}
</style>