package com.example.nanny.stream;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public class FrameGrabberTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(FrameGrabberTask.class);

    private final String cameraId;
    private final String streamUrl;
    private final int intervalSeconds;
    private final Consumer<byte[]> onFrame;
    private volatile boolean running = true;

    public FrameGrabberTask(String cameraId, String streamUrl, int intervalSeconds, Consumer<byte[]> onFrame) {
        this.cameraId = cameraId;
        this.streamUrl = streamUrl;
        this.intervalSeconds = intervalSeconds;
        this.onFrame = onFrame;
    }

    @Override
    public void run() {
        while (running) {
            try {
                grabLoop();
            } catch (Exception e) {
                log.warn("Stream interrupted. cameraId={}, error={}", cameraId, e.getMessage());
                sleep(3_000);
            }
        }
        log.info("Frame grabber stopped. cameraId={}", cameraId);
    }

    private void grabLoop() throws Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamUrl);
             Java2DFrameConverter converter = new Java2DFrameConverter()) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setOption("stimeout", "5000000");
            grabber.start();

            long lastCaptureAt = 0;
            log.info("Frame grabber started. cameraId={}, url={}", cameraId, maskUrl(streamUrl));

            while (running) {
                Frame frame = grabber.grabImage();
                if (frame == null) {
                    continue;
                }

                long now = System.currentTimeMillis();
                if (now - lastCaptureAt < intervalSeconds * 1000L) {
                    continue;
                }
                lastCaptureAt = now;

                BufferedImage image = converter.convert(frame);
                if (image != null) {
                    onFrame.accept(toJpeg(image));
                }
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    private byte[] toJpeg(BufferedImage image) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", output);
            return output.toByteArray();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    private String maskUrl(String url) {
        return url == null ? "" : url.replaceAll("://([^:/@]+):([^@]+)@", "://$1:***@");
    }
}
