package com.ms.qrcode.qrcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.qrcode.qrcode.dto.QrCodeRequest;
import com.ms.qrcode.qrcode.dto.QrCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class QrCodeConsumer {

    private final QrCodeService qrCodeService;
    private final KafkaTemplate<String, QrCodeResponse> kafkaTemplate;
    private static final String RESPONSE_TOPIC = "qrcode.gerar.response";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "qrcode.gerar.request", groupId = "qrcode-group")
    public void consumir(String payload) throws JsonProcessingException {
        QrCodeRequest request = objectMapper.readValue(payload, QrCodeRequest.class);

        log.info("Gerando QR Code: eventoId={}, tipo={}", request.eventoId(), request.tipo());
        QrCodeResponse response;

        try {
            String url = qrCodeService.gerarUrlQrCode(
                    request.baseUrl(),
                    request.eventoId(),
                    request.tema(),
                    request.tipo()
            );

            response = new QrCodeResponse(
                    request.eventoId(), url, "SUCCESS", null, request.tipo()
            );
        } catch (Exception e) {
            log.error("Erro ao gerar QR Code: eventoId={}, tipo={}", request.eventoId(), request.tipo(), e);
            response = new QrCodeResponse(
                    request.eventoId(), null, "ERROR", e.getMessage(),
                    request.tipo());
        }

        String chave = request.eventoId() + "-" + request.tipo().name();
        kafkaTemplate.send(RESPONSE_TOPIC, chave, response);
    }
}