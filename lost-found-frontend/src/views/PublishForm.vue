<!-- src/views/PublishForm.vue -->
<template>
  <div class="publish-form">
    <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
      <el-form-item label="物品名称" prop="title" required>
        <el-input v-model="form.title" placeholder="请输入物品名称" />
      </el-form-item>

      <el-form-item label="地点" prop="locationId" required>
        <el-cascader
          v-model="form.locationId"
          :options="locationTree"
          :props="{ value: 'id', label: 'name', checkStrictly: true }"
          placeholder="请选择地点"
          clearable
          @change="handleLocationChange"
        />
      </el-form-item>

      <el-form-item label="时间" prop="time" required>
        <el-date-picker
          v-model="form.time"
          type="datetime"
          placeholder="选择时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          :shortcuts="dateShortcuts"
        />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <div class="description-wrapper">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请描述物品特征..."
          />
          <el-button 
            class="ai-btn"
            :type="hasUsedAI ? 'info' : 'primary'"
            :loading="aiLoading"
            @click="handleAIComplete"
          >
            {{ hasUsedAI ? '✨ 重新描述' : '🤖 AI补充' }}
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="图片">
        <div class="upload-wrapper">
          <img v-if="form.imageUrl" :src="form.imageUrl" class="preview-image" />
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
          >
            <el-button type="success" :icon="Upload">上传图片</el-button>
          </el-upload>
        </div>
        <div class="tip">图片为选填，支持jpg/png格式，不超过10MB</div>
      </el-form-item>
    </el-form>

    <div class="form-actions">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="primary" @click="submitForm" :loading="submitting">发布</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { publishLost, publishFound, getLocations, aiComplete } from '@/api'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  type: {
    type: Number,
    required: true,
    default: 0  // 0-失物 1-拾物
  }
})

const emit = defineEmits(['success', 'cancel'])

const formRef = ref(null)
const submitting = ref(false)
const aiLoading = ref(false)
const hasUsedAI = ref(false)
const locationTree = ref([])

// 日期快捷选项
const dateShortcuts = [
  { text: '今天', value: new Date() },
  { text: '昨天', value: () => {
    const date = new Date()
    date.setDate(date.getDate() - 1)
    return date
  }},
  { text: '3天前', value: () => {
    const date = new Date()
    date.setDate(date.getDate() - 3)
    return date
  }}
]

const form = reactive({
  title: '',
  locationId: null,
  time: '',
  description: '',
  imageUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
  locationId: [{ required: true, message: '请选择地点', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间', trigger: 'change' }]
}

const uploadUrl = '/api/upload'
const uploadHeaders = {
  token: localStorage.getItem('token') || ''
}

const loadLocations = async () => {
  const res = await getLocations()
  locationTree.value = res.data
}

const handleLocationChange = (val) => {
  form.locationId = val[val.length - 1]
}

const handleUploadSuccess = (res) => {
  form.imageUrl = res.data
  ElMessage.success('上传成功')
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const handleAIComplete = async () => {
  if (!form.title) {
    ElMessage.warning('请先输入物品名称')
    return
  }
  aiLoading.value = true
  try {
    const res = await aiComplete(props.type, form.title, form.description)
    form.description = res.data
    hasUsedAI.value = true
    ElMessage.success('AI已补充描述')
  } catch (error) {
    ElMessage.error('AI服务繁忙，请稍后重试')
  } finally {
    aiLoading.value = false
  }
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = {
      title: form.title,
      locationId: form.locationId,
      time: form.time,
      description: form.description,
      imageUrl: form.imageUrl || null
    }
    
    if (props.type === 0) {
      await publishLost(data)
    } else {
      await publishFound(data)
    }
    emit('success')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadLocations()
})
</script>

<style scoped>
.publish-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}
.description-wrapper {
  width: 100%;
}
.ai-btn {
  margin-top: 8px;
  width: 100%;
}
.upload-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}
.preview-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}
.tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}
</style>