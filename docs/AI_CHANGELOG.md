# AI 变更记录

本项目所有由 AI（Claude Code）完成的变更记录。

---

## 2026-07-10 — 视频片段存储

**需求**: 检测到哺乳异常时，自动保存 30 秒视频片段到数据库

**新增文件**:
- `domain/VideoClip.java` — 视频片段实体（cameraId, alertRecordId, alertType, clipData LONGBLOB, durationSeconds, createdAt）
- `repository/VideoClipRepository.java`
- `stream/FrameRingBuffer.java` — 线程安全环形缓冲区，存 JPEG 帧

**修改文件**:
- `stream/FrameGrabberTask.java` — 每帧都缓冲到 ring buffer，不再只保留 5s 采样帧
- `stream/StreamManager.java` — 每个摄像头创建/管理 FrameRingBuffer
- `detection/DetectionPipeline.java` — ABNORMAL_FEEDING 告警时 drain 30s 帧 → H.264 编码 → 存入 video_clip
- `application.yml` — 新增 nanny.clip.* 配置

**技术要点**: JPEG 帧缓冲（~50KB/帧，450帧 ≈ 22.5MB），JavaCV FFmpegFrameRecorder 编码 H.264 MP4，ReentrantLock 线程安全

---

## 2026-07-11 — HSF → Dubbo 迁移

**需求**: 将 HSF 服务改为 Apache Dubbo

**修改文件**:
- `pom.xml` — pandora-hsf-spring-boot-starter → dubbo-spring-boot-starter
- `application.yml` — spring.hsf.* → dubbo.*（Nacos 注册中心、随机端口）
- `NannyMonitorApplication.java` — @EnableHSF → @EnableDubbo
- `service/impl/*.java` — @HSFProvider → @DubboService

**技术要点**: 保持 REST + Dubbo 共存，接口不变，Dubbo 3.3.0 + Nacos 2.3.0

---

## 2026-07-12 — 用户注册、角色权限

**需求**: 用户注册/登录（JWT），ADMIN/USER 角色，摄像头按用户隔离

**新增文件 (10)**:
- `domain/User.java`, `domain/UserCamera.java`
- `repository/UserRepository.java`, `repository/UserCameraRepository.java`
- `mapper/UserMapper.java`, `mapper/UserCameraMapper.java`
- `security/JwtUtils.java`, `security/JwtAuthFilter.java`, `security/SecurityConfig.java`
- `controller/AuthController.java`

**修改文件**:
- `pom.xml` — +spring-boot-starter-security, +jjwt
- `application.yml` — +jwt.secret, +jwt.expiration
- `service/CameraService.java` — 方法增加 userId 参数
- `service/impl/CameraServiceImpl.java` — register 写入 user_camera；listAll 按用户过滤；start/stop 校验所有权
- `controller/CameraController.java` — 从 SecurityContext 获取当前用户

**技术要点**: Spring Security + JWT (jjwt 0.12.5)，BCrypt 密码加密，HandlerInterceptor 检查订阅，user_camera 关联表

---

## 2026-07-13 — 订阅支付系统

**需求**: 包月/季/年订阅，微信/支付宝/银行卡支付，订阅期内才能启动摄像头

**新增文件 (14)**:
- `domain/UserSubscription.java`, `domain/PaymentRecord.java`
- `repository/UserSubscriptionRepository.java`, `repository/PaymentRecordRepository.java`
- `mapper/UserSubscriptionMapper.java`, `mapper/PaymentRecordMapper.java`
- `service/SubscriptionService.java`, `service/PaymentService.java`
- `payment/PaymentStrategy.java`, `payment/WechatPayStrategy.java`, `payment/AlipayStrategy.java`, `payment/BankCardStrategy.java`, `payment/DebugPaymentStrategy.java`
- `controller/PaymentController.java`, `controller/SubscriptionController.java`
- `security/SubscriptionInterceptor.java`

**修改文件**:
- `pom.xml` — +wechatpay-apache-httpclient, +alipay-sdk-java
- `application.yml` — +payment.wechat.*, +payment.alipay.*, +subscription.* 价格
- `schema.sql` — +user_subscription, +payment_record 表
- `security/SecurityConfig.java` — 放行 /api/payment/callback/**，注册 SubscriptionInterceptor

**技术要点**: 策略模式（微信/支付宝/银行卡/调试），支付回调自动激活订阅，SubscriptionInterceptor 拦截 start 接口

---

## 2026-07-14 — 数据库建表 + 架构文档

**数据库建表**:
- 通过 `schema.sql` + `spring.sql.init.mode: always` 自动建表
- 8 张表：user, user_camera, user_subscription, payment_record, camera, detection_record, alert_record, video_clip

**架构文档**:
- `docs/architecture.md` — 系统分层架构、数据流、部署、模块依赖、技术栈
- `docs/business-architecture.md` — 业务全景、角色权限、订阅/监控流程、ER 图、告警类型

**README 更新**:
- 完整 API 文档（含认证、订阅、支付、调试模式）
- 配置说明、架构说明、数据库表清单

---

## 变更统计

| 类别 | 数量 |
|------|------|
| 新增文件 | 42 |
| 修改文件 | 18 |
| 新增数据库表 | 8 |
| 新增 API 端点 | 12 |
| 新增配置项 | 20+ |