package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.utils.DateTimeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsersToNotify() {
        int hours = LocalDateTime.now().getHour();
        int minutes = LocalDateTime.now().getMinute();

        String time = String.format("%s:%s:00", convertInt(hours), convertInt(minutes));
        LocalTime currentTime = DateTimeConverter.parseStringTime(time);
        log.info("Current time {}",currentTime);

       return userRepository.findAllByTimeOfStartBeforeAndTimeOfFinishAfter(currentTime);
    }

    private String convertInt(int val){
        if (val < 10) {
            return String.format("0%d", val);
        }
        return String.valueOf(val);
    }

    public void createUser(Long telegramUserId, String userName, Long chatId) {
        Optional<User> userOpt = userRepository.findByTelegramUserId(telegramUserId);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = new User();
            user.setTelegramUserId(telegramUserId);
            user.setUsername(userName);
        }
        user.setChatId(chatId);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        Optional<User> userOpt = userRepository.findByChatId(user.getChatId());
        if (userOpt.isPresent()) {
            User dbUser = prepareUserToUpdate(userOpt.get(), user);
            userRepository.save(dbUser);
        }

    }

    private User prepareUserToUpdate(User dbUser, User updUser) {
        LocalTime timeToStart = updUser.getTimeOfStart();
        LocalTime timeToFinish = updUser.getTimeOfFinish();
        Integer percent = updUser.getPercent();

        if (timeToStart != null) {
            dbUser.setTimeOfStart(timeToStart);
        }
        if (timeToFinish != null) {
            dbUser.setTimeOfFinish(timeToFinish);
        }
        if (percent != null) {
            dbUser.setPercent(percent);
        }
        return dbUser;
    }
}
