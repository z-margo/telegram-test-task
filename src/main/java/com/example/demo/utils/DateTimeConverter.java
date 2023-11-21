package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Slf4j
public class DateTimeConverter {

    public static LocalTime parseStringTime(String time) {
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private DateTimeConverter() {
    }
}
