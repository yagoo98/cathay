package com.example.demo.service;

import com.example.demo.entity.Coin;
import com.example.demo.repo.CoinRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoinService {
    @Autowired
    WebClient webClient;
    @Autowired
    CoinRepository coinRepository;

    public Mono<String> getCoinDesk(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        String.format("HTTP Error %s %s",
                                                response.statusCode().value(),
                                                response.statusCode().getReasonPhrase())
                                ))))
                .bodyToMono(String.class);
    }

    public Map<String, Object> monoStringToMap(Mono<String> monoString) {
        return monoString.flatMap(jsonString -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
                };
                Map<String, Object> data = objectMapper.readValue(jsonString, typeReference);
                return Mono.just(data);
            } catch (IOException e) {
                return Mono.error(new RuntimeException("Error parsing JSON", e));
            }
        }).block();
    }

    public Map<String, Object> transformData(Map<String, Object> data) {
        Map<String, String> time = (Map<String, String>) data.get("time");
        String updatedISO = time.get("updatedISO");
        String formattedTime = formatLocalDateTime(updatedISO);

        Map<String, Map<String, Object>> bpi = (Map<String, Map<String, Object>>) data.get("bpi");
        Map<String, Object> coinInfo = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : bpi.entrySet()) {
            String coinCode = entry.getKey();
            Object rate = entry.getValue().get("rate");
            String chineseName;
            switch (coinCode) {
                case "USD":
                    chineseName = "美元";
                    break;
                case "GBP":
                    chineseName = "英鎊";
                    break;
                case "EUR":
                    chineseName = "歐元";
                    break;
                default:
                    chineseName = "未知";
            }

            Map<String, Object> info = new HashMap<>();
            info.put("幣別", coinCode);
            info.put("幣別中文名稱", chineseName);
            info.put("匯率", rate);
            coinInfo.put(coinCode, info);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("更新時間", formattedTime);
        result.put("幣別相關資訊", coinInfo);
        return result;
    }

    private String formatLocalDateTime(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public Optional<Coin> searchCoin(String name) {
        return coinRepository.findByName(name);
    }

    public Coin createCoin(Coin coin) {
        return coinRepository.findByName(coin.getName()).orElseGet(() -> coinRepository.save(coin));
    }

    public Coin updateCoin(String name, String nameZH, BigDecimal rate) {
        return coinRepository.findByName(name)
                .map(coin -> {
                    coin.setNameZH(nameZH);
                    coin.setRate(rate);
                    return coinRepository.save(coin);
                })
                .orElse(null);
    }

    public Coin deleteCoin(String name) {
        return coinRepository.findByName(name)
                .map(coin -> {
                    coinRepository.delete(coin);
                    return coin;
                })
                .orElse(null);
    }

    public Map<String, Object> entityToMap(Coin coin) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("幣別", coin.getName());
        dataMap.put("幣別中文名稱", coin.getNameZH());
        dataMap.put("匯率", coin.getRate());
        return dataMap;
    }

    public Map<String, Object> getResultMap(Coin coin) {
        Map<String, Object> result = new HashMap<>();
        if (coin != null) {
            result.put("status", "success");
            result.put("data", entityToMap(coin));
            return result;
        }
        result.put("status", "fail");
        result.put("data", null);
        return result;
    }
}
