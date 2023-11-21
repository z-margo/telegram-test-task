package com.example.demo.repo;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelegramUserId(Long id);

    Optional<User> findByChatId(Long id);

    @Query("select u from User u where u.timeOfStart <= :time and u.timeOfFinish >= :time")
    List<User> findAllByTimeOfStartBeforeAndTimeOfFinishAfter(LocalTime time);
}
