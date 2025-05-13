package com.example.demo;

import com.example.demo.entity.Coin;
import com.example.demo.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CathayApplicationTests {
    @Autowired
    CoinService coinService;
    Coin usdCoin;
    Coin gbpCoin;
    Coin eurCoin;

    @Test
    void contextLoads() {
    }


    @BeforeEach
    void setUp() {
        usdCoin = new Coin("USD", "USD", new BigDecimal("101935.90"), LocalDateTime.now(), null);
        gbpCoin = new Coin("GBP", "GBP", new BigDecimal("77479.62"), LocalDateTime.now(), null);
        eurCoin = new Coin("EUR", "EUR", new BigDecimal("92115.92"), LocalDateTime.now(), null);

        Optional<Coin> oldUsdCoin = coinService.searchCoin(usdCoin.getName());
        Optional<Coin> oldGbpCoin = coinService.searchCoin(gbpCoin.getName());
        Optional<Coin> oldEurCoin = coinService.searchCoin(eurCoin.getName());

        oldUsdCoin.ifPresent(coin -> coinService.deleteCoin(coin.getName()));
        oldGbpCoin.ifPresent(coin -> coinService.deleteCoin(coin.getName()));
        oldEurCoin.ifPresent(coin -> coinService.deleteCoin(coin.getName()));

        coinService.createCoin(usdCoin);
        coinService.createCoin(gbpCoin);
        coinService.createCoin(eurCoin);
    }

    @Test
    void testSearchCoin() {
        Optional<Coin> foundUsdCoin = coinService.searchCoin("USD");
        assertTrue(foundUsdCoin.isPresent());
        assertEquals("USD", foundUsdCoin.get().getName());
        assertEquals(new BigDecimal("101935.9000"), foundUsdCoin.get().getRate());

        Optional<Coin> notFoundCoin = coinService.searchCoin("XYZ");
        assertFalse(notFoundCoin.isPresent());
    }

    @Test
    void testCreateCoin() {
        Coin newCoin = new Coin("JPY", "JPY", new BigDecimal("145.0000"), LocalDateTime.now(), null);
        coinService.createCoin(newCoin);
        Optional<Coin> foundCoin = coinService.searchCoin(newCoin.getName());
        assertTrue(foundCoin.isPresent());
        assertEquals("JPY", foundCoin.get().getName());
        assertEquals(new BigDecimal("145.0000"), foundCoin.get().getRate());
    }

    @Test
    void testUpdateCoin() {
        BigDecimal newRate = new BigDecimal("105000.0000");
        coinService.updateCoin(usdCoin.getName(), newRate);

        Optional<Coin> updatedUsdCoin = coinService.searchCoin(usdCoin.getName());
        assertTrue(updatedUsdCoin.isPresent());
        assertEquals(newRate, updatedUsdCoin.get().getRate());
    }

    @Test
    void testDeleteCoin() {
        coinService.deleteCoin("GBP");
        Optional<Coin> deletedGbpCoin = coinService.searchCoin("GBP");
        assertFalse(deletedGbpCoin.isPresent());
    }

    @Test
    void testTransformData() {
        Map<String, Object> data = new HashMap<>();

        Map<String, String> time = new HashMap<>();
        time.put("updatedISO", "2024-09-02T07:07:20+00:00");
        data.put("time", time);

        Map<String, Map<String, Object>> bpi = new HashMap<>();

        Map<String, Object> usd = new HashMap<>();
        usd.put("rate", 57756.298);
        bpi.put("USD", usd);

        Map<String, Object> gbp = new HashMap<>();
        gbp.put("rate", 43984.02);
        bpi.put("GBP", gbp);

        Map<String, Object> eur = new HashMap<>();
        eur.put("rate", 52243.287);
        bpi.put("EUR", eur);

        data.put("bpi", bpi);

        Map<String, Object> result = coinService.transformData(data);

        assertEquals("2024/09/02 07:07:20", result.get("更新時間"));

        Map<String, Object> coinInfo = (Map<String, Object>) result.get("幣別相關資訊");
        assertNotNull(coinInfo);

        Map<String, Object> usdInfo = (Map<String, Object>) coinInfo.get("USD");
        assertEquals("美元", usdInfo.get("幣別中文名稱"));
        assertEquals(57756.298, usdInfo.get("匯率"));

        Map<String, Object> gbpInfo = (Map<String, Object>) coinInfo.get("GBP");
        assertEquals("英鎊", gbpInfo.get("幣別中文名稱"));
        assertEquals(43984.02, gbpInfo.get("匯率"));

        Map<String, Object> eurInfo = (Map<String, Object>) coinInfo.get("EUR");
        assertEquals("歐元", eurInfo.get("幣別中文名稱"));
        assertEquals(52243.287, eurInfo.get("匯率"));
    }
}
