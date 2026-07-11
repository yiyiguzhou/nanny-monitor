# nanny-monitor

Spring Boot 婴儿监控系统：RTSP 拉流 → VLM 视觉分析 → 异常告警 + 视频片段存储。支持用户注册/登录（JWT）、角色权限（ADMIN/USER）、订阅支付（微信/支付宝/银行卡）、Dubbo RPC 服务暴露。

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL（默认 `192.168.1.38:3306/auto`）
- 阿里云 DashScope API Key（通义千问-VL）
- （可选）Nacos 注册中心（Dubbo 使用）
- （可选）微信支付商户号 / 支付宝 APPID（支付功能）

## 启动

```bash
cd /Users/wzhang/code/nanny-monitor
export DASHSCOPE_API_KEY=你的DashScopeKey
mvn spring-boot:run
```

默认端口：`8080`。启动时自动执行 `schema.sql` 建表（`IF NOT EXISTS`，幂等安全）。

## 配置

`src/main/resources/application.yml` 主要配置项：

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.1.38:3306/auto
    username: root
    password: root
  ai:
    openai:
      api-key: ${DASHSCOPE_API_KEY:}
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      chat:
        options:
          model: qwen-vl-plus

nanny:
  frame:
    interval-seconds: 5        # 抽帧间隔
  alert:
    window-size: 6             # 滑动窗口大小
    abnormal-threshold: 2      # 异常帧阈值
    cooldown-seconds: 300      # 告警冷却时间
  clip:
    duration-seconds: 30       # 视频片段时长
    fps: 15                    # 输出帧率

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret}
  expiration: 86400000         # Token 有效期（毫秒），默认 24h

subscription:
  monthly-price: 29.9          # 月付价格
  quarterly-price: 79.9        # 季付价格
  yearly-price: 299.0          # 年付价格

dubbo:
  registry:
    address: ${DUBBO_REGISTRY_ADDRESS:nacos://127.0.0.1:8848}
```

### 支付配置（可选）

```bash
# 微信支付
export WECHAT_MERCHANT_ID=your_merchant_id
export WECHAT_API_V3_KEY=your_api_v3_key
export WECHAT_PRIVATE_KEY_PATH=/path/to/apiclient_key.pem

# 支付宝
export ALIPAY_APP_ID=your_app_id
export ALIPAY_PRIVATE_KEY=your_private_key
export ALIPAY_PUBLIC_KEY=your_alipay_public_key
```

未配置时支付接口返回 `UNCONFIGURED` 状态，不会报错。

## 认证

所有 API（除 `/api/auth/**` 和 `/api/payment/callback/**`）需要在 Header 中携带 JWT Token：

```
Authorization: Bearer <token>
```

角色：

| 角色 | 说明 |
|------|------|
| ADMIN | 可查看/管理所有摄像头 |
| USER | 只能查看/管理自己的摄像头 |

## 订阅

启动摄像头实时流检测需要有效订阅。订阅类型：

| 计划 | 周期 | 默认价格 |
|------|------|---------|
| MONTHLY | 30 天 | ¥29.9 |
| QUARTERLY | 90 天 | ¥79.9 |
| YEARLY | 365 天 | ¥299.0 |

支付方式：微信支付、支付宝、银行卡。

### 调试模式

本地开发时无需接入真实支付，直接激活订阅：

```bash
curl -X POST http://localhost:8080/api/payment/debug/activate \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"plan":"MONTHLY"}'
```

响应：

```json
{"status":"SUCCESS","message":"订阅已激活","plan":"MONTHLY","amount":29.9}
```

## REST API

### 用户注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456","role":"ADMIN"}'
```

响应：

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": { "id": 1, "username": "admin", "role": "ADMIN" }
}
```

### 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

### 查询订阅状态

```bash
curl http://localhost:8080/api/subscription/status \
  -H 'Authorization: Bearer <token>'
```

响应：

```json
{
  "active": true,
  "plan": "MONTHLY",
  "startDate": "2026-07-01",
  "endDate": "2026-07-31",
  "daysRemaining": 25
}
```

### 创建支付订单

```bash
curl -X POST http://localhost:8080/api/payment/create \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"method":"WECHAT","plan":"MONTHLY"}'
```

支付回调（由支付网关调用，无需 Token）：

```
POST /api/payment/callback/wechat    # 微信支付回调
POST /api/payment/callback/alipay    # 支付宝回调
POST /api/payment/callback/bankcard  # 银行卡回调
```

---

以下接口需要携带 Token：

### 1. 注册摄像头

```bash
curl -X POST http://localhost:8080/api/cameras \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"id":"cam-001","name":"客厅摄像头","rtspUrl":"rtsp://user:pass@192.168.1.10:554/stream1"}'
```

> 摄像头自动绑定到当前用户。ADMIN 可查看全部，USER 只看自己的。

### 2. 查看摄像头列表

```bash
curl http://localhost:8080/api/cameras \
  -H 'Authorization: Bearer <token>'
```

### 3. 启动摄像头检测 ⚠️ 需要订阅

```bash
curl -X POST http://localhost:8080/api/cameras/cam-001/start \
  -H 'Authorization: Bearer <token>'
```

> 未订阅返回 `403 {"error":"需要有效订阅才能使用此功能","code":"SUBSCRIPTION_REQUIRED"}`

### 4. 停止摄像头检测

```bash
curl -X POST http://localhost:8080/api/cameras/cam-001/stop \
  -H 'Authorization: Bearer <token>'
```

### 5. 查看检测记录

```bash
curl http://localhost:8080/api/cameras/cam-001/detections \
  -H 'Authorization: Bearer <token>'
```

### 6. 查看告警记录

```bash
curl http://localhost:8080/api/cameras/cam-001/alerts \
  -H 'Authorization: Bearer <token>'
```

### 7. 手动上传截图测试 VLM

```bash
curl -X POST http://localhost:8080/api/detect/image \
  -H 'Authorization: Bearer <token>' \
  -F 'file=@/path/to/frame.jpg'
```

## WebSocket 报警订阅

STOMP endpoint：`/ws`

主题：

- 全局报警：`/topic/alerts`
- 单摄像头报警：`/topic/cameras/{cameraId}/alerts`

## 架构说明

- **JavaCV/FFmpeg** 从 RTSP 拉流，默认每 5 秒抽一帧 JPEG。
- **Spring AI + 通义千问-VL** 分析每帧：检测喂奶、婴儿在场、看护人在场、异常行为。
- **滑动窗口评估**：窗口内 ≥2 帧异常且高置信度 → 触发 `ABNORMAL_FEEDING` 告警；婴儿在场但看护人连续缺席 → 触发 `CAREGIVER_ABSENT` 告警。
- **视频片段存储**：异常告警触发时，自动将前 30 秒帧缓冲编码为 H.264 MP4 存入数据库（`video_clip` 表）。
- **实时推送**：告警通过 WebSocket 实时推送到前端。
- **用户权限**：Spring Security + JWT，摄像头按用户隔离。
- **订阅支付**：策略模式支持微信/支付宝/银行卡，支付回调自动激活订阅。
- **Dubbo RPC**：CameraService / AlertRecordService / DetectionRecordService 暴露为 Dubbo provider。

## 数据库表

| 表 | 说明 |
|------|------|
| `user` | 用户（username, password, role） |
| `user_camera` | 用户-摄像头关联 |
| `user_subscription` | 订阅记录（plan, start_date, end_date, status） |
| `payment_record` | 支付记录（amount, method, status, transaction_id） |
| `camera` | 摄像头配置 |
| `detection_record` | VLM 检测结果 |
| `alert_record` | 告警记录 |
| `video_clip` | 异常告警视频片段（MP4 LONGBLOB） |

## 隐私建议

- 所有外部 VLM 请求走 HTTPS。
- 视频片段存储在数据库中，生产环境建议定期清理或迁移至对象存储。
- 如合规要求更高，应改为本地/私有化视觉模型推理。