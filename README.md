# 校园AI失物招领平台

> 在校园里，“丢东西”和“捡东西”几乎是每天都在发生的小剧情。这个平台就是为解决这个问题而生的。

## 项目简介

本项目是一个基于Spring Boot + Vue3的校园失物招领平台，支持失物/拾物发布、浏览搜索、评论私信、举报置顶等功能，并引入AI大模型辅助用户填写物品描述、分析失物趋势。

### 核心功能

**用户端**

- 邮箱/手机号注册登录，JWT Token认证
- 发布失物/拾物信息（支持图片上传）
- 首页列表浏览，支持按类型、时间、地点筛选和关键词搜索
- 帖子详情查看、评论、举报
- 用户间私信、未读消息通知
- 申请帖子置顶（需管理员审批）

**管理员端**

- 用户管理：查看、封禁/解封、删除用户
- 举报管理：处理帖子/用户举报
- 置顶审批：通过/拒绝置顶申请
- 平台统计：数据卡片 + ECharts图表 + AI周报

**AI辅助**

- 发布时自动补全物品描述（调用大模型API）
- 管理端AI周报：分析失物高发区域和热门物品类型

### 技术栈

| 层级     | 技术                        |
| :------- | :-------------------------- |
| 后端框架 | Spring Boot 3.5.12          |
| ORM      | MyBatis 3.0.5               |
| 前端框架 | Vue 3.5.32                  |
| UI组件库 | Element Plus 2.13.6         |
| 状态管理 | Pinia 3.0.4                 |
| 构建工具 | Vite 8.0.4                  |
| 数据库   | MySQL 8.0                   |
| AI模型   | 硅基流动 qwen-flash-free    |
| 其他     | JWT、BCrypt、ECharts、Axios |

## 项目结构

text

```
lostSystem/
├── lost-found-back/            # 后端代码
│   ├── src/main/java/com/jovi/
│   │   ├── controller/         # 15个Controller
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # 数据访问层
│   │   ├── pojo/               # 实体类、DTO
│   │   ├── config/             # 配置类（拦截器、跨域）
│   │   └── utils/              # 工具类（JWT、加密）
│   └── src/main/resources/
│       ├── mapper/             # MyBatis XML映射文件
│       └── application.yml     # 配置文件
│
├── lost-found-frontend/        # 前端代码
│   ├── src/
│   │   ├── api/                # API接口层
│   │   ├── views/              # 页面组件
│   │   │   ├── admin/          # 管理员页面
│   │   │   └── ...             # 普通用户页面
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia状态管理
│   │   └── utils/              # 工具函数
│   └── package.json
│
└── README.md
```



## 数据库设计

共10张表：`user`、`admin`、`location`、`lost_item`、`found_item`、`comment`、`message`、`notification`、`report`、`top_request`



## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Node.js 18+（前端）

### 后端启动

数据库脚本

```sql
-- 1. 用户表 (user) 
-- =====================================================
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱（登录用，唯一）',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号（登录用，唯一）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
  `nickname` VARCHAR(50) NOT NULL DEFAULT '萌新用户' COMMENT '昵称（显示用，必填，有默认值）',
  `avatar` VARCHAR(255) NOT NULL DEFAULT '/default-avatar.png' COMMENT '头像URL（有默认头像）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-封禁 1-正常',
  `is_active` TINYINT DEFAULT 0 COMMENT '是否活跃用户：0-否 1-是',
  `post_count` INT DEFAULT 0 COMMENT '发布次数',
  `comment_count` INT DEFAULT 0 COMMENT '评论次数',
  `last_active_time` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- =====================================================
-- 2. 管理员表 (admin)
-- =====================================================
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `admin_num` VARCHAR(50) NOT NULL COMMENT '工号（登录用，唯一）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_num` (`admin_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- =====================================================
-- 3. 地点表 (location)
-- =====================================================
CREATE TABLE `location` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '地点名称',
  `parent_id` INT DEFAULT 0 COMMENT '父级ID，0表示顶级',
  `is_custom` TINYINT DEFAULT 0 COMMENT '0-系统预置 1-用户自定义',
  `user_id` INT DEFAULT NULL COMMENT '自定义地点的创建者ID（is_custom=1时有效）',
  `use_count` INT DEFAULT 0 COMMENT '被使用的次数（用于热门排序）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_parent` (`parent_id`),
  INDEX `idx_use_count` (`use_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地点表';

-- =====================================================
-- 4. 失物表 (lost_item)
-- =====================================================
CREATE TABLE `lost_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '发布者ID',
  `title` VARCHAR(100) NOT NULL COMMENT '物品名称',
  `location_id` INT NOT NULL COMMENT '地点ID（关联location表）',
  `location_name` VARCHAR(100) DEFAULT NULL COMMENT '地点名称快照（冗余，防止地点被删后显示异常）',
  `lost_time` DATETIME NOT NULL COMMENT '丢失时间',
  `description` TEXT COMMENT '物品描述（半公开字段）',
  `contact_verify` VARCHAR(255) DEFAULT NULL COMMENT '核验字段（仅发布者+管理员可见）',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  `top_expire_time` DATETIME DEFAULT NULL COMMENT '置顶过期时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-进行中 1-已找回 2-已关闭',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_location_id` (`location_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_is_top_expire` (`is_top`, `top_expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物表';

-- =====================================================
-- 5. 拾物表 (found_item)
-- =====================================================
CREATE TABLE `found_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '发布者ID',
  `title` VARCHAR(100) NOT NULL COMMENT '物品名称',
  `location_id` INT NOT NULL COMMENT '地点ID（关联location表）',
  `location_name` VARCHAR(100) DEFAULT NULL COMMENT '地点名称快照',
  `found_time` DATETIME NOT NULL COMMENT '拾取时间',
  `description` TEXT COMMENT '物品描述（半公开字段）',
  `contact_verify` VARCHAR(255) DEFAULT NULL COMMENT '核验字段（仅发布者+管理员可见）',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-进行中 1-已归还 2-已关闭',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_location_id` (`location_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拾物表';

-- =====================================================
-- 6. 评论表 (comment)
-- =====================================================
CREATE TABLE `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '评论人ID',
  `item_id` INT NOT NULL COMMENT '物品ID（关联lost_item或found_item）',
  `item_type` TINYINT NOT NULL COMMENT '物品类型：0-失物 1-拾物',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_item` (`item_id`, `item_type`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- =====================================================
-- 7. 私聊消息表 (message)
-- =====================================================
CREATE TABLE `message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `from_user_id` INT NOT NULL COMMENT '发送者ID',
  `to_user_id` INT NOT NULL COMMENT '接收者ID',
  `content` VARCHAR(500) NOT NULL COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_from_user` (`from_user_id`),
  INDEX `idx_to_user` (`to_user_id`),
  INDEX `idx_to_user_read` (`to_user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私聊消息表';

-- =====================================================
-- 8. 通知表 (notification) - 新增
-- =====================================================
CREATE TABLE `notification` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '接收者ID',
  `from_user_id` INT DEFAULT NULL COMMENT '触发者ID（谁评论/谁发私信）',
  `type` TINYINT NOT NULL COMMENT '通知类型：1-评论 2-私信 3-系统',
  `content` VARCHAR(500) NOT NULL COMMENT '通知内容',
  `target_id` INT DEFAULT NULL COMMENT '关联的帖子ID（评论时使用）',
  `is_read` TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- =====================================================
-- 9. 举报表 (report) - 修正版
-- =====================================================
CREATE TABLE `report` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `reporter_id` INT NOT NULL COMMENT '举报人ID',
  `target_user_id` INT DEFAULT NULL COMMENT '被举报的用户ID（举报用户时使用）',
  `item_id` INT DEFAULT NULL COMMENT '被举报的物品ID（举报帖子时使用）',
  `item_type` TINYINT DEFAULT NULL COMMENT '物品类型：0-失物 1-拾物',
  `report_type` TINYINT DEFAULT 0 COMMENT '举报类型：0-举报帖子 1-举报用户',
  `reason` VARCHAR(255) NOT NULL COMMENT '举报理由',
  `status` TINYINT DEFAULT 0 COMMENT '处理状态：0-待处理 1-已通过 2-已驳回',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- =====================================================
-- 10. 置顶申请表 (top_request)
-- =====================================================
CREATE TABLE `top_request` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `item_id` INT NOT NULL COMMENT '物品ID',
  `item_type` TINYINT NOT NULL COMMENT '物品类型：0-失物 1-拾物',
  `requester_id` INT NOT NULL COMMENT '申请人ID',
  `duration_hours` INT DEFAULT 24 COMMENT '申请置顶时长（小时）',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审批 1-已通过 2-已拒绝',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='置顶申请表';

-- 预置管理员账号（密码需要BCrypt加密，'admin123'的加密结果）
INSERT INTO `10001` (admin_num, password, name)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员');--
```



```
# 1. 导入数据库
# 2. 修改 application.yml 中的数据库连接和AI API Key
# 3. 运行
```



### 前端启动

bash

```
cd lost-found-frontend
npm install
npm run dev
```



### 默认账号

| 角色     | 账号          | 密码     |
| :------- | :------------ | :------- |
| 普通用户 | 自行注册      | -        |
| 管理员   | 10001（工号） | admin123 |



## 项目截图

## 首页

![image-20260419175650631](C:\Users\JOVI2\AppData\Roaming\Typora\typora-user-images\image-20260419175650631.png)

## 发布帖子

![image-20260419175957942](C:\Users\JOVI2\AppData\Roaming\Typora\typora-user-images\image-20260419175957942.png)

## AI功能

![image-20260419180244587](C:\Users\JOVI2\AppData\Roaming\Typora\typora-user-images\image-20260419180244587.png)

## 管理员统计

  ![image-20260419180313403](C:\Users\JOVI2\AppData\Roaming\Typora\typora-user-images\image-20260419180313403.png)

## 遇到的坑与解决

开发过程中踩了不少坑，记录几个印象深刻的：

- **管理员查看用户主页误判为“自己”**：管理员和用户id都是2，判断逻辑要排除管理员
- **置顶帖子不过期**：加了定时任务每小时检查过期
- **AI调用超时**：设置30秒超时，降级返回默认文案

详细记录见项目报告和每日进度文档。

## 后续计划

- 实时聊天（WebSocket）
- 热点数据缓存（Redis）
- 认领流程（信息分级+核验）
- 多模态AI（图片识别）

## 致谢

感谢QG工作室提供的项目机会和指导

## 许可证

仅供学习交流使用

------

**作者**：【RoJovi】
**项目时间**：2026年4月
**GitHub**：https://github.com/RoJovi/lostSystem
