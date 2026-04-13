// src/utils/format.js

/**
 * 格式化时间显示
 * @param {string} time - 时间字符串
 * @returns {string} - 格式化后的时间
 */
export const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const hours = diff / (1000 * 60 * 60)
  
  if (hours < 1) {
    return `${Math.floor(diff / (1000 * 60))}分钟前`
  }
  if (hours < 24) {
    return `${Math.floor(hours)}小时前`
  }
  if (hours < 48) {
    return '昨天'
  }
  if (hours < 72) {
    return '前天'
  }
  return `${date.getMonth() + 1}/${date.getDate()}`
}

/**
 * 格式化完整日期时间
 */
export const formatDateTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
}

/**
 * 格式化浏览量
 */
export const formatViewCount = (count) => {
  if (count >= 10000) {
    return `${(count / 10000).toFixed(1)}w`
  }
  return count.toString()
}

/**
 * 截断文本
 */
export const truncateText = (text, length = 50) => {
  if (!text) return ''
  if (text.length <= length) return text
  return text.substring(0, length) + '...'
}