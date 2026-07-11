package com.example.nanny.stream;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe ring buffer of timestamped JPEG frames.
 * FrameGrabberTask writes, DetectionPipeline reads when saving a clip.
 */
public class FrameRingBuffer {

    public record TimestampedFrame(byte[] jpegData, long captureTimeMillis) {}

    private final int maxFrameCount;
    private final long maxAgeMillis;
    private final Deque<TimestampedFrame> buffer = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    public FrameRingBuffer(int maxFrameCount, long maxAgeMillis) {
        this.maxFrameCount = maxFrameCount;
        this.maxAgeMillis = maxAgeMillis;
    }

    /**
     * Add a frame. Called by FrameGrabberTask on every grabbed frame.
     */
    public void add(byte[] jpegData, long captureTimeMillis) {
        lock.lock();
        try {
            buffer.addLast(new TimestampedFrame(jpegData, captureTimeMillis));
            while (buffer.size() > maxFrameCount) {
                buffer.removeFirst();
            }
            evictExpired(captureTimeMillis);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Drain frames within [now - durationMs, now]. Called when saving a clip.
     * Returns a snapshot copy so encoding can proceed while the buffer keeps receiving frames.
     */
    public List<TimestampedFrame> drain(long durationMs) {
        long now = System.currentTimeMillis();
        long cutoff = now - durationMs;
        lock.lock();
        try {
            evictExpired(now);
            List<TimestampedFrame> result = new ArrayList<>();
            for (TimestampedFrame f : buffer) {
                if (f.captureTimeMillis >= cutoff) {
                    result.add(f);
                }
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return buffer.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    private void evictExpired(long now) {
        long cutoff = now - maxAgeMillis;
        while (!buffer.isEmpty() && buffer.peekFirst().captureTimeMillis < cutoff) {
            buffer.removeFirst();
        }
    }
}
