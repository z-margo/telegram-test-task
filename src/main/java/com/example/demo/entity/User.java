package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalTime;


@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    @Temporal(TemporalType.TIME)
    private LocalTime timeOfStart;
    @Temporal(TemporalType.TIME)
    private LocalTime timeOfFinish;
    private Long chatId;
    private Long telegramUserId;
    private Integer percent;

}
