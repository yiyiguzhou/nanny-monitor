package com.example.nanny.ai;

import com.example.nanny.dto.VlmResult;

public interface VlmClient {
    VlmResult detect(byte[] jpegFrame);
}
