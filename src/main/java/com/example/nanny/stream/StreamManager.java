package com.example.nanny.stream;

import com.example.nanny.detection.DetectionPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Component
public class StreamManager {
    private static final Logger log = LoggerFactory.getLogger(StreamManager.class);

    private final DetectionPipeline detectionPipeline;
    private final Executor streamExecutor;
    private final int intervalSeconds;
    private final Map<String, FrameGrabberTask> tasks = new ConcurrentHashMap<>();

    public StreamManager(DetectionPipeline detectionPipeline,
                         @Qualifier("streamExecutor") Executor streamExecutor,
                         @Value("${nanny.frame.interval-seconds:5}") int intervalSeconds) {
        this.detectionPipeline = detectionPipeline;
        this.streamExecutor = streamExecutor;
        this.intervalSeconds = intervalSeconds;
    }

    public synchronized void start(String cameraId, String streamUrl) {
        stop(cameraId);
        FrameGrabberTask task = new FrameGrabberTask(
            cameraId,
            streamUrl,
            intervalSeconds,
            frame -> detectionPipeline.process(cameraId, frame)
        );
        tasks.put(cameraId, task);
        streamExecutor.execute(task);
        log.info("Stream task submitted. cameraId={}", cameraId);
    }

    public synchronized void stop(String cameraId) {
        FrameGrabberTask old = tasks.remove(cameraId);
        if (old != null) {
            old.stop();
            log.info("Stream task stopping. cameraId={}", cameraId);
        }
    }

    public boolean isRunning(String cameraId) {
        return tasks.containsKey(cameraId);
    }

    public Map<String, Boolean> statuses() {
        return tasks.keySet().stream().collect(java.util.stream.Collectors.toMap(id -> id, id -> true));
    }
}
