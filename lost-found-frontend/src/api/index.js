import request from '@/utils/request'

// ==================== 用户相关 ====================
// 用户登录（邮箱/手机号 + 密码）
export const userLogin = (data) => request.post('/user/login', data)
// 用户注册
export const userRegister = (data) => request.post('/user/register', data)
// 获取用户信息
export const getUserInfo = () => request.get('/user/info')
// 更新用户信息（昵称、头像、邮箱、手机号）
export const updateUserInfo = (data) => request.put('/user/info', data)
// 修改密码
export const updatePassword = (data) => request.put('/user/password', data)
// 获取其他用户的个人信息
export const getUserInfoById = (userId) => request.get(`/user/${userId}/info`)
// 获取其他用户的帖子列表
export const getUserPosts = (userId) => request.get(`/user/${userId}/posts`)

// ==================== 管理员相关 ====================
// 管理员登录
export const adminLogin = (data) => request.post('/admin/login', data)
// 获取管理员信息
export const getAdminInfo = () => request.get('/admin/info')
// 更新管理员信息
export const updateAdminInfo = (data) => request.put('/admin/info', data)
// 管理员修改密码
export const updateAdminPassword = (data) => request.put('/admin/password', data)

// ==================== 失物/拾物相关 ====================
// 发布失物
export const publishLost = (data) => request.post('/lost/create', data)
// 发布拾物
export const publishFound = (data) => request.post('/found/create', data)
// 获取首页列表（合并失物+拾物，支持筛选）
export const getFeedList = (params) => request.get('/feed/list', { params })
// 获取失物详情
export const getLostDetail = (id) => request.get(`/lost/${id}`)
// 获取拾物详情
export const getFoundDetail = (id) => request.get(`/found/${id}`)
// 删除自己的帖子
export const deletePost = (id, type) => request.delete(`/post/${type}/${id}`)
// 更新帖子（修改）
export const updatePost = (id, type, data) => request.put(`/post/${type}/${id}`, data)
// 设置已找回/已归还
export const setResolved = (id, type) => request.put(`/post/${type}/${id}/resolve`)
// 申请置顶
export const requestTop = (id, type, hours) => request.post('/top/request', { id, type, hours })
// 获取我的帖子列表
export const getMyPosts = () => request.get('/user/posts')

// ==================== 地点相关 ====================
// 获取地点列表（树形结构）
export const getLocations = () => request.get('/locations')
// 添加自定义地点
export const addLocation = (name, parentId) => request.post('/location', { name, parentId })

// ==================== AI相关 ====================
// AI补全描述
export const aiComplete = (type, title, description) => request.post('/ai/complete', { type, title, description })

// ==================== 评论相关 ====================
// 获取评论列表
export const getComments = (itemId, itemType) => request.get('/comments', { params: { itemId, itemType } })
// 发表评论
export const addComment = (data) => request.post('/comment', data)
// 删除评论
export const deleteComment = (id) => request.delete(`/comment/${id}`)

// ==================== 私信相关 ====================
// 发送私信
export const sendMessage = (data) => request.post('/message/send', data)
// 获取消息列表（收件箱+发件箱）
export const getMessageList = () => request.get('/message/list')
// 获取未读消息数
export const getUnreadCount = () => request.get('/message/unread-count')
// 标记消息已读
export const markMessageRead = (id) => request.put(`/message/${id}/read`)

// ==================== 举报相关 ====================
// 举报帖子或用户
export const report = (data) => request.post('/report', data)

// ==================== 管理员专用 ====================
// 获取所有用户列表
export const getAllUsers = (params) => request.get('/admin/users', { params })
// 封禁/解封用户
export const banUser = (userId, status) => request.put(`/admin/user/${userId}/ban`, { status })
// 删除用户
export const deleteUser = (userId) => request.delete(`/admin/user/${userId}`)
// 获取所有举报
export const getReports = (status) => request.get('/admin/reports', { params: { status } })
// 处理举报
export const handleReport = (id, action) => request.put(`/admin/report/${id}`, { action })
// 获取置顶申请列表
export const getTopRequests = (status) => request.get('/admin/top-requests', { params: { status } })
// 审批置顶申请
export const approveTopRequest = (id, approved) => request.put(`/admin/top-request/${id}`, { approved })
// 获取统计数据
export const getStatistics = () => request.get('/admin/statistics')

export default request