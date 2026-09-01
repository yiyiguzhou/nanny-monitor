package com.example.nanny.detection;

import com.example.nanny.ai.FeedingPrompt;
import com.example.nanny.alert.AlertService;
import com.example.nanny.domain.AlertRecord;
import com.example.nanny.domain.DetectionRecord;
import com.example.nanny.domain.VideoClip;
import com.example.nanny.dto.VlmResult;
import com.example.nanny.repository.DetectionRecordRepository;
import com.example.nanny.repository.VideoClipRepository;
import com.example.nanny.stream.FrameRingBuffer;
import com.example.nanny.stream.StreamManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class DetectionPipeline {
    private static final Logger log = LoggerFactory.getLogger(DetectionPipeline.class);

    private final ChatClient chatClient;
    private final SlidingWindowEvaluator evaluator;
    private final AlertService alertService;
    private final DetectionRecordRepository recordRepository;
    private final VideoClipRepository videoClipRepository;
    private final StreamManager streamManager;
    private final ObjectMapper objectMapper;
    private final int clipDurationSeconds;
    private final int clipFps;

    public DetectionPipeline(ChatClient chatClient,
                             SlidingWindowEvaluator evaluator,
                             AlertService alertService,
                             DetectionRecordRepository recordRepository,
                             VideoClipRepository videoClipRepository,
                             @Lazy StreamManager streamManager,
                             ObjectMapper objectMapper,
                             @Value("${nanny.clip.duration-seconds:30}") int clipDurationSeconds,
                             @Value("${nanny.clip.fps:15}") int clipFps) {
        this.chatClient = chatClient;
        this.evaluator = evaluator;
        this.alertService = alertService;
        this.recordRepository = recordRepository;
        this.videoClipRepository = videoClipRepository;
        this.streamManager = streamManager;
        this.objectMapper = objectMapper;
        this.clipDurationSeconds = clipDurationSeconds;
        this.clipFps = clipFps;
    }

    @Async("vlmExecutor")
    public void process(String cameraId, byte[] jpegFrame) {
        try {
            VlmResult result = detect(jpegFrame);
            DetectionRecord record = toRecord(cameraId, result);
            recordRepository.insert(record);

            AlertRule.Decision decision = evaluator.feed(cameraId, result);
            if (decision.shouldAlert()) {
                AlertRecord alertRecord = alertService.raise(cameraId, decision.type(), decision.reason());
                saveClipIfWorthy(cameraId, decision.type(), alertRecord.getId());
            }

            log.info("Detection result. cameraId={}, feeding={}, baby={}, caregiver={}, abnormal={}, confidence={}, desc={}",
                cameraId,
                result.feeding(),
                result.babyPresent(),
                result.caregiverPresent(),
                result.abnormal(),
                result.confidence(),
                result.description());
        } catch (Exception e) {
            log.warn("Detection pipeline failed. cameraId={}, error={}", cameraId, e.getMessage(), e);
        }
    }

    private void saveClipIfWorthy(String cameraId, String alertType, Long alertRecordId) {
        if (!"ABNORMAL_FEEDING".equals(alertType)) {
            return;
        }

        FrameRingBuffer buffer = streamManager.getRingBuffer(cameraId);
        if (buffer == null || buffer.isEmpty()) {
            log.warn("No frame buffer for camera {}, skipping clip", cameraId);
            return;
        }

        try {
            List<FrameRingBuffer.TimestampedFrame> frames = buffer.drain(clipDurationSeconds * 1000L);
            if (frames.size() < 2) {
                log.warn("Too few frames ({}) for clip on camera {}", frames.size(), cameraId);
                return;
            }

            byte[] mp4Bytes = encodeToMp4(frames);
            double duration = (double) frames.size() / clipFps;

            VideoClip clip = new VideoClip(cameraId, alertRecordId, alertType, mp4Bytes, duration);
            videoClipRepository.insert(clip);

            log.info("Video clip saved. cameraId={}, alertType={}, frames={}, duration={}s, size={}KB",
                cameraId, alertType, frames.size(), String.format("%.1f", duration), mp4Bytes.length / 1024);
        } catch (Exception e) {
            log.warn("Failed to save video clip. cameraId={}, error={}", cameraId, e.getMessage(), e);
        }
    }

    private byte[] encodeToMp4(List<FrameRingBuffer.TimestampedFrame> frames) throws Exception {
        BufferedImage firstImage = ImageIO.read(new ByteArrayInputStream(frames.get(0).jpegData()));
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, width, height);
        recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setFrameRate(clipFps);
        recorder.setVideoBitrate(2000000);
        recorder.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        for (FrameRingBuffer.TimestampedFrame tf : frames) {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(tf.jpegData()));
            Frame frame = converter.convert(img);
            recorder.record(frame);
        }

        recorder.stop();
        recorder.release();
        return outputStream.toByteArray();
    }

    /**
     * Detect feeding status from a JPEG frame using the VLM via ChatClient.
     */
    public VlmResult detect(byte[] jpegFrame) {
        try {
            String raw = chatClient.prompt()
                .user(FeedingPrompt.userSpec(jpegFrame))
                .call()
                .content();
            String json = extractJson(raw);
            return objectMapper.readValue(json, VlmResult.class);
        } catch (Exception e) {
            log.warn("VLM detection failed: {}", e.getMessage(), e);
            return VlmResult.unknown();
        }
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        throw new IllegalArgumentException("Model did not return JSON: " + content);
    }

    private DetectionRecord toRecord(String cameraId, VlmResult result) {
        return new DetectionRecord(
            cameraId,
            result.feeding(),
            result.babyPresent(),
            result.caregiverPresent(),
            result.abnormal(),
            result.confidence(),
            result.description()
        );
    }
}