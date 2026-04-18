<!-- src/views/PostDetail.vue -->
<template>
  <div class="post-detail-container" v-loading="loading">
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

<div class="header-placeholder"></div> 

    <div class="post-content" v-if="post">
      <!-- 顶部：发帖人头像和昵称 -->
      <div class="post-author">
        <div class="author-left" @click="goToUserProfile(post.userId)">
          <img :src="post.userAvatar || '/default-avatar.png'" class="author-avatar" />
          <span class="author-name">{{ post.userNickname || post.username }}</span>
        </div>
        <!-- 右上角三点菜单 -->
<el-dropdown trigger="click" @command="handleMenuCommand">
  <span class="more-btn">⋮</span>
  <template #dropdown>
    <el-dropdown-menu>
      <!-- 管理员：只有删除 -->
      <template v-if="isAdmin">
        <el-dropdown-item command="delete">🗑️ 删除</el-dropdown-item>
      </template>

      <!-- 自己的帖子 -->
      <template v-else-if="isMyPost">
        <el-dropdown-item command="edit">✏️ 修改</el-dropdown-item>
        <el-dropdown-item command="delete">🗑️ 删除</el-dropdown-item>
        
        <el-dropdown-item v-if="post.status === 0" command="resolve">
          {{ post.type === 0 ? '✅ 设置为已找回' : '✅ 设置为已归还' }}
        </el-dropdown-item>
        <el-dropdown-item v-else command="resolve" disabled>
          {{ post.type === 0 ? '已找回' : '已归还' }}
        </el-dropdown-item>
        
 <el-dropdown-item v-if="post.status === 0 && post.type === 0" command="top">
    📌 申请置顶
  </el-dropdown-item>
  <el-dropdown-item v-else-if="post.type === 1" command="top" disabled>
    📌 拾物不可置顶
  </el-dropdown-item>
  <el-dropdown-item v-else command="top" disabled>
    📌 已找回不可置顶
  </el-dropdown-item>
</template>

      <!-- 别人的帖子 -->
      <template v-else>
        <el-dropdown-item command="report">🚨 举报</el-dropdown-item>
      </template>
    </el-dropdown-menu>
  </template>
</el-dropdown>

      </div>

      <!-- 图片 -->
      <div class="post-image" v-if="post.imageUrl">
        <img :src="post.imageUrl" @click="previewImage(post.imageUrl)" />
      </div>

      <!-- 标题 -->
      <h1 class="post-title">
        【{{ post.type === 0 ? '丢失' : '拾到' }}】{{ post.title }}
      </h1>

      <!-- 描述 -->
      <div class="post-description">{{ post.description }}</div>

      <!-- 地点和时间 -->
      <div class="post-info">
        <div class="info-item">📍 {{ post.locationName }}</div>
        <div class="info-item">
          🕐 {{ post.type === 0 ? '丢失时间：' : '拾到时间：' }}{{ formatDateTime(post.lostTime || post.foundTime) }}
        </div>
        <div class="info-item">👁️ {{ formatViewCount(post.viewCount) }} 次浏览</div>
        <div class="info-item">💬 {{ commentList.length }} 条评论</div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>评论区</h3>
        <div class="comment-input" v-if="!isAdmin">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="发表评论..."
          />
          <el-button type="primary" @click="submitComment" :loading="commentLoading">发表</el-button>
        </div>
        <div class="comment-list">
          <div v-for="comment in commentList" :key="comment.id" class="comment-item">
            <img :src="comment.userAvatar || '/default-avatar.png'" class="comment-avatar" />
            <div class="comment-body">
              <div class="comment-user">
                <span class="username" @click="goToUserProfile(comment.userId)">{{ comment.userNickname }}</span>
                <span class="time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>
          <div v-if="commentList.length === 0" class="empty-comments">暂无评论，快来抢沙发~</div>
        </div>
      </div>
    </div>

    <!-- 修改帖子弹窗 -->
    <el-dialog v-model="showEditDialog" :title="'修改' + (post?.type === 0 ? '失物' : '拾物')" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="物品名称" required>
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="地点" required>
  <el-cascader
    v-model="editForm.locationId"
    :options="locationTree"
    :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }"
    placeholder="请选择地点"
    clearable
    style="width: 100%"
  />
        </el-form-item>
        <el-form-item label="时间" required>
          <el-date-picker
            v-model="editForm.time"
            type="datetime"
            placeholder="选择时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="4" />
        </el-form-item>
<!-- 添加图片上传 -->
    <el-form-item label="图片">
      <div class="edit-upload-wrapper">
        <img v-if="editForm.imageUrl" :src="editForm.imageUrl" class="edit-preview-image" />
        <div v-else class="edit-image-placeholder">暂无图片</div>
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleEditUploadSuccess"
          :on-error="handleEditUploadError"
        >
          <el-button size="small" type="primary">上传新图片</el-button>
        </el-upload>
        <el-button v-if="editForm.imageUrl" size="small" type="danger" @click="removeEditImage">删除图片</el-button>
      </div>
      <div class="tip">支持jpg/png格式，不超过10MB</div>
    </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 举报弹窗 -->
    <el-dialog v-model="showReportDialog" title="举报" width="400px">
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="举报原因" required>
          <el-select v-model="reportForm.reason" placeholder="请选择">
            <el-option label="虚假信息" value="虚假信息" />
            <el-option label="垃圾广告" value="垃圾广告" />
            <el-option label="骚扰信息" value="骚扰信息" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体描述" v-if="reportForm.reason === '其他'">
          <el-input v-model="reportForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReportDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>

    <!-- 置顶申请弹窗 -->
    <el-dialog v-model="showTopDialog" title="申请置顶" width="400px">
      <el-form label-width="100px">
        <el-form-item label="置顶时长">
          <el-radio-group v-model="topHours">
            <el-radio :label="24">24小时</el-radio>
            <el-radio :label="48">48小时</el-radio>
            <el-radio :label="72">72小时</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <span class="tip">申请后需要管理员审核，审核通过后帖子将在首页置顶</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTopDialog = false">取消</el-button>
        <el-button type="primary" @click="submitTopRequest">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer v-if="previewVisible" :url-list="[previewUrl]" @close="previewVisible = false" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user' 
import { 
  getLostDetail, getFoundDetail, deletePost, updatePost, setResolved,
  getComments, addComment, report, requestTop, getLocations
} from '@/api'
import { formatTime, formatDateTime, formatViewCount } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

//选择用户类型
const userType = userStore.userType
const isAdmin = userType === 1

const postId = ref(parseInt(route.params.id))
const postType = ref(parseInt(route.params.type)) // 0-失物 1-拾物
const loading = ref(false)
const post = ref(null)
const commentList = ref([])
const newComment = ref('')
const commentLoading = ref(false)
const locationTree = ref([])

// 弹窗状态
const showEditDialog = ref(false)
const showReportDialog = ref(false)
const showTopDialog = ref(false)
const showDropdown = ref(false)
const previewVisible = ref(false)
const previewUrl = ref('')
const topHours = ref(24)

// 表单
const editForm = ref({
  title: '',
  locationId: null,
  time: '',
  description: ''
})
const reportForm = ref({
  reason: '',
  description: ''
})

// 计算属性
const isMyPost = computed(() => post.value && post.value.userId === userStore.userInfo?.id)

const loadPost = async () => {
  loading.value = true
  try {
    if (postType.value === 0) {
      const res = await getLostDetail(postId.value)
      post.value = res  // 去掉 .data
    } else {
      const res = await getFoundDetail(postId.value)
      post.value = res  // 去掉 .data
    }
    console.log('帖子数据:', post.value)  // 调试用
  } finally {
    loading.value = false
  }
}
const loadComments = async () => {
  const res = await getComments(postId.value, postType.value)
  console.log('评论数据:', res)  // 调试
  // 兼容多种返回格式
  if (Array.isArray(res)) {
    commentList.value = res
  } else if (res?.data && Array.isArray(res.data)) {
    commentList.value = res
  } else {
    commentList.value = []
  }
}

const loadLocations = async () => {
  const res = await getLocations()
  console.log('地点数据:', res)  // 调试
  if (Array.isArray(res)) {
    locationTree.value = res
  } else if (res?.data && Array.isArray(res.data)) {
    locationTree.value = res
  } else {
    locationTree.value = []
  }
}
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentLoading.value = true
  try {
    await addComment({
      itemId: postId.value,
      itemType: postType.value,
      content: newComment.value
    })
    ElMessage.success('评论成功')
    newComment.value = ''
    loadComments()
  } finally {
    commentLoading.value = false
  }
}

const handleMenuCommand = (command) => {
   // 管理员只能删除
  if (isAdmin) {
    if (command === 'delete') {
      handleDelete()
    }
    return
  }
  
  // 普通用户的逻辑
switch (command) {
    case 'edit':
      openEditDialog()
      break
    case 'delete':
      handleDelete()
      break
    case 'resolve':
      handleResolve()
      break
    case 'top':
      showTopDialog.value = true
      break
    case 'report':
      showReportDialog.value = true
      break
  }
}

// 平铺地点列表（用于下拉框）
const flatLocationTree = computed(() => {
  const flatten = (items, result = []) => {
    if (!items) return result
    items.forEach(item => {
      result.push({ id: item.id, name: item.name })
      if (item.children && item.children.length) {
        flatten(item.children, result)
      }
    })
    return result
  }
  return flatten(locationTree.value)
})

const findPath = (tree, targetId, path = []) => {
  if (!tree) return null
  for (const node of tree) {
    const newPath = [...path, node.id]
    if (node.id === targetId) {
      return newPath
    }
    if (node.children && node.children.length) {
      const result = findPath(node.children, targetId, newPath)
      if (result) return result
    }
  }
  return null
}

// 上传相关
const uploadUrl = '/api/upload'
const uploadHeaders = {
  token: localStorage.getItem('token') || ''
}

// 打开编辑弹窗时，把当前图片也存入 editForm
const openEditDialog = async () => {
  if (locationTree.value.length === 0) {
    await loadLocations()
  }
  
  editForm.value = {
    title: post.value.title,
    locationId: post.value.locationId,
    time: post.value.lostTime || post.value.foundTime,
    description: post.value.description || '',
    imageUrl: post.value.imageUrl || ''  // 添加图片字段
  }
  showEditDialog.value = true
}

// 上传成功回调
const handleEditUploadSuccess = (res) => {
  editForm.value.imageUrl = res.data
  ElMessage.success('图片上传成功')
}

// 上传失败回调
const handleEditUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 删除图片
const removeEditImage = () => {
  editForm.value.imageUrl = ''
  ElMessage.success('已删除图片')
}

const submitEdit = async () => {
  try {
	// 处理级联选择器的返回值
    let locationId = editForm.value.locationId
    if (Array.isArray(locationId)) {
      locationId = locationId[locationId.length - 1]  // 取最后一个（叶子节点）
    }
    const data = {
      title: editForm.value.title,
      locationId: locationId,
      time: editForm.value.time,
      description: editForm.value.description,
      imageUrl: editForm.value.imageUrl || null 
    }
    const typeStr = postType.value === 0 ? 'lost' : 'found'
    await updatePost(postId.value, typeStr,data)
    ElMessage.success('修改成功')
    showEditDialog.value = false
    loadPost()
  } catch (error) {
    // 错误已处理
  }
}

const handleDelete = async () => {
  ElMessageBox.confirm('确定要删除这条帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const typeStr = postType.value === 0 ? 'lost' : 'found'
    await deletePost(postId.value, typeStr)
    ElMessage.success('删除成功')
    router.back()
  }).catch(() => {})
}

const handleResolve = async () => {
  const typeStr = postType.value === 0 ? 'lost' : 'found'
  await setResolved(postId.value, typeStr)
  ElMessage.success(postType.value === 0 ? '已标记为已找回' : '已标记为已归还')
  loadPost()
}

const submitReport = async () => {
  const reason = reportForm.value.reason === '其他' 
    ? reportForm.value.description 
    : reportForm.value.reason
  await report({
    itemId: postId.value,
    itemType: postType.value,
    reason: reason
  })
  ElMessage.success('举报已提交，管理员会尽快处理')
  showReportDialog.value = false
  reportForm.value = { reason: '', description: '' }
}

const submitTopRequest = async () => {
  await requestTop(postId.value, postType.value, topHours.value)
  ElMessage.success('置顶申请已提交，请等待管理员审核')
  showTopDialog.value = false
}

const previewImage = (url) => {
  previewUrl.value = url
  previewVisible.value = true
}

const goBack = () => {
  router.back()
}

const goToPersonalCenter = () => {
  router.push('/personal-center')
}

const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadPost()
  loadComments()
  loadLocations()

// 调试权限
  setTimeout(() => {
    console.log('=== 权限调试 ===')
    console.log('post.userId:', post.value?.userId)
    console.log('currentUserId:', userStore.userInfo?.id)
    console.log('isMyPost:', isMyPost.value)
  }, 1000)
})
</script>

<style scoped>
.post-detail-container {
  height: 70px;
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
.header + * {
  margin-top: 0 !important;
  padding-top: 60px !important;
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
.edit-upload-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.edit-preview-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}
.edit-image-placeholder {
  width: 80px;
  height: 80px;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}
.tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
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
.post-content {
  max-width: 800px;
  margin: 30px auto 40px;
  background: white;
  border-radius: 16px;
  padding: 24px;
}
.post-author {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.author-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}
.author-name {
  font-weight: bold;
  font-size: 16px;
}
.more-btn {
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
}
.post-image {
  margin-bottom: 20px;
  text-align: center;
}
.post-image img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  cursor: pointer;
}
.post-title {
  font-size: 24px;
  margin-bottom: 16px;
}
.post-description {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  line-height: 1.6;
}
.post-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 16px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
  color: #666;
}
.comment-section {
  margin-top: 24px;
}
.comment-input {
  margin: 16px 0;
}
.comment-list {
  margin-top: 20px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.comment-body {
  flex: 1;
}
.comment-user {
  margin-bottom: 8px;
}
.comment-user .username {
  font-weight: bold;
  cursor: pointer;
  margin-right: 12px;
}
.comment-user .time {
  color: #999;
  font-size: 12px;
}
.comment-content {
  line-height: 1.5;
}
.empty-comments {
  text-align: center;
  color: #999;
  padding: 40px;
}
.tip {
  color: #999;
  font-size: 12px;
}
</style>