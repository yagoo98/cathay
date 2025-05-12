package com.example.demo.controller;

import com.example.demo.entity.Coin;
import com.example.demo.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping()
public class CoinController {
    String uri = "https://kengp3.github.io/blog/coindesk.json";

    @Autowired
    CoinService coinService;

    @GetMapping("/coinDesk")
    @ResponseBody
    public Map<String, Object> getCoinDesk() {
        Mono<String> coinDesk = coinService.getCoinDesk(uri);
        return coinService.monoStringToMap(coinDesk);
    }

    @GetMapping("/transformCoinDesk")
    @ResponseBody
    public Map<String, Object> getTransformCoinDesk() {
        Mono<String> coinDesk = coinService.getCoinDesk(uri);
        Map<String, Object> coinDeskMap = coinService.monoStringToMap(coinDesk);
        return coinService.transformData(coinDeskMap);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> search(@RequestBody Coin coin) {
        Optional<Coin> optionalCoin = coinService.searchCoin(coin.getName());
        if (optionalCoin.isPresent()) {
            return coinService.getResultMap(optionalCoin.get());
        } else {
            return coinService.getResultMap(null);
        }
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody Coin newCoin) {
        Coin savedCoin = coinService.createCoin(newCoin);
        return coinService.getResultMap(savedCoin);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> update(@RequestBody Coin updatedCoin) {
        Coin savedCoin = coinService.updateCoin(updatedCoin.getName(), updatedCoin.getRate());
        return coinService.getResultMap(savedCoin);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> delete(@RequestBody Coin deletedCoin) {
        deletedCoin = coinService.deleteCoin(deletedCoin.getName());
        return coinService.getResultMap(deletedCoin);
    }
}
