package com.example.demo.service;

import com.example.demo.dto.CurrencyDto;
import com.example.demo.entity.Currency;
import com.example.demo.repo.CurrencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${currency-change.url}")
    private String url;

    @Autowired
    private CurrencyRepository currencyRepository;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, BigDecimal> getCurrency() {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {

            List<CurrencyDto> currencyList = convertStringToObjList(response.getBody());
            return convertListToMap(currencyList);
        }
        return new HashMap<>();
    }

    private List<CurrencyDto> convertStringToObjList(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Map<String, BigDecimal> convertListToMap(List<CurrencyDto> currencies) {
        Map<String, BigDecimal> values = new HashMap<>();
        currencies.forEach(el -> values.put(el.symbol(), el.price()));

        return values;
    }

    // new logic

    public Map<String, BigDecimal> getCurrencyFromAPI() {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK && ObjectUtils.isNotEmpty(response.getBody())) {
            LocalTime currentTime = LocalTime.now();

            Currency currency = currencyRepository.findByTimeIs(currentTime).orElse(new Currency());
            currency.setData(response.getBody());
            currency.setTime(currentTime);
            currencyRepository.save(currency);

            List<CurrencyDto> currencyList = convertStringToObjList(currency.getData());
            return convertListToMap(currencyList);
        }
        return new HashMap<>();
    }

    public Map<String, BigDecimal> getUserPreviousCurrencyList(LocalTime time) {
        List<Currency> list = currencyRepository.findByTimeAfter(time);
        Currency currency = list.get(0);
        List<CurrencyDto> currencyList = convertStringToObjList(currency.getData());
        return convertListToMap(currencyList);
    }
}
