# nanny-monitor

Spring Boot MVP：摄像头 RTSP/RTMP 拉流抽帧，调用通义千问-VL 判断保姆是否正在给小孩喂奶，并通过数据库记录和 WebSocket 推送报警。

## 环境要求

- JDK 17+
- Maven 3.8+
- 阿里云 DashScope API Key

## 启动

```bash
cd /Users/wzhang/code/nanny-monitor
export DASHSCOPE_API_KEY=你的DashScopeKey
mvn spring-boot:run
```

默认端口：`8080`

H2 控制台：`http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:file:./data/nanny-monitor;MODE=MySQL;DATABASE_TO_LOWER=TRUE`
- username: `sa`
- password: 空

## 配置

`src/main/resources/application.yml`

```yaml
nanny:
  frame:
    interval-seconds: 5
  vlm:
    endpoint: https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
    api-key: ${DASHSCOPE_API_KEY:}
    model: qwen-vl-plus
```

如需更强模型，可把 `qwen-vl-plus` 改为你账号可用的 `qwen-vl-max` 或其他 Qwen-VL 模型。

## REST API

### 1. 注册摄像头

```bash
curl -X POST http://localhost:8080/api/cameras \
  -H 'Content-Type: application/json' \
  -d '{"id":"cam-001","name":"客厅摄像头","rtspUrl":"rtsp://user:pass@192.168.1.10:554/stream1"}'
```

### 2. 启动摄像头检测

```bash
curl -X POST http://localhost:8080/api/cameras/cam-001/start
```

### 3. 停止摄像头检测

```bash
curl -X POST http://localhost:8080/api/cameras/cam-001/stop
```

### 4. 查看最近检测结果

```bash
curl http://localhost:8080/api/cameras/cam-001/detections
```

### 5. 查看最近报警

```bash
curl http://localhost:8080/api/cameras/cam-001/alerts
```

### 6. 手动上传截图测试 VLM

```bash
curl -X POST http://localhost:8080/api/detect/image \
  -F 'file=@/path/to/frame.jpg'
```

## WebSocket 报警订阅

STOMP endpoint：`/ws`

主题：

- 全局报警：`/topic/alerts`
- 单摄像头报警：`/topic/cameras/{cameraId}/alerts`

## MVP 说明

- 当前使用 JavaCV/FFmpegFrameGrabber 直接从 RTSP/RTMP 抽帧。
- 每路摄像头一个抽帧任务，默认 5 秒抽一帧。
- VLM 调用异步执行，不阻塞拉流线程。
- 报警采用滑动窗口 + 冷却时间，避免单帧误判触发报警风暴。
- 默认 H2 文件数据库，后续可替换为 MySQL。

## 隐私建议

这是婴幼儿家庭监控场景，生产前建议：

- 不保存原始截图或视频帧，只保存结构化检测结果。
- 所有外部 VLM 请求走 HTTPS。
- 明确用户授权、数据保留周期和删除机制。
- 如果合规要求更高，应改为本地/私有化视觉模型推理。
