package com.lbapp.LBcalc.forex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class ForexAdapter {

    public List<FxRateDto> getFxRatesFrom(URI uri) {
        InputStream inputStream = getFrom(uri);
        return parseFxRatesFrom(inputStream);
    }

    protected InputStream getFrom(URI uri) {
        requireNonNull(uri, "Uri can not be null");
        InputStream inputStream = null;
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .setHeader("Content-type", "application/xml")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
            inputStream = response.body();
        } catch (Exception ignored) {
        }
        return inputStream;
    }

    protected List<FxRateDto> parseFxRatesFrom(InputStream inputStream) {
        requireNonNull(inputStream, "InputStream of fx rates can not be null");
        try {
            return new XmlMapper().readValue(inputStream, new TypeReference<List<FxRateDto>>() {
            });
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
