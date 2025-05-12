package com.example.demo;

import com.example.demo.entity.Coin;
import com.example.demo.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
}
