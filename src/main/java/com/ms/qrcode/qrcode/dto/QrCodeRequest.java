package com.ms.qrcode.qrcode.dto;

public record QrCodeRequest(
        Long eventoId,
        String baseUrl,
        String tema
) {}