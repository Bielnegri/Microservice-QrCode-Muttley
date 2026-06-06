package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.TipoQrCode;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class QrCodeService {

    private static final String QUICKCHART_BASE = "https://quickchart.io/qr";

    public String gerarUrlQrCode(String baseUrl, Long eventoId, String eventoTema, TipoQrCode tipo) {
        String urlDestino = switch (tipo) {
            case INSCRICAO    -> baseUrl + "/eventos/" + eventoId + "/inscricao";
            case CONFIRMACAO  -> baseUrl + "/eventos/" + eventoId + "/confirmar-presenca";
        };

        String encoded = URLEncoder.encode(urlDestino, StandardCharsets.UTF_8);
        return QUICKCHART_BASE + "?text=" + encoded + "&size=300&margin=2";
    }

    public byte[] baixarQrCode(String qrCodeUrl) throws Exception {
        return new java.net.URL(qrCodeUrl).openStream().readAllBytes();
    }
}