package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.QrCodeRequest;
import com.ms.qrcode.qrcode.dto.QrCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QrCodeConsumer {

    private final QrCodeService qrCodeService;
    private final KafkaTemplate<String, QrCodeResponse> kafkaTemplate;
    private static final String RESPONSE_TOPIC = "qrcode.gerar.response";

    @KafkaListener(topics = "qrcode.gerar.request", groupId = "qrcode-group")
    public void consumir(QrCodeRequest request) {
        log.info("Gerando QR Code: eventoId={}", request.eventoId());
        QrCodeResponse response;

        try {
            String url = qrCodeService.gerarUrlQrCode(
                    request.baseUrl(),
                    request.eventoId(),
                    request.tema()
            );

            response = new QrCodeResponse(
                    request.eventoId(), url, "SUCCESS", null
            );
        } catch (Exception e) {
            log.error("Erro ao gerar QR Code: eventoId={}", request.eventoId(), e);
            response = new QrCodeResponse(
                    request.eventoId(), null, "ERROR", e.getMessage()
            );
        }

        kafkaTemplate.send(RESPONSE_TOPIC, request.eventoId().toString(), response);
    }
}