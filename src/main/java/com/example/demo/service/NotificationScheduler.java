package com.example.demo.service;

import com.example.demo.config.TelegramBot;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationScheduler {
    private final CurrencyService currencyService;
    private final UserService userService;
    private final TelegramBot bot;
    private Map<String, BigDecimal> prevCurrencyValues = new HashMap<>();

    public NotificationScheduler(CurrencyService currencyService, UserService userService, TelegramBot bot) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.bot = bot;
    }

    // old logic
   // @Scheduled(fixedDelayString = "${currency-change.upd.milliseconds}")
    public void getCurrencyChanges() {
        log.info("Get currency updates...");
        Map<String, BigDecimal> currentCurrencyValues = currencyService.getCurrency();

        Map<String, Double> changes = new HashMap<>();
        if (ObjectUtils.isNotEmpty(prevCurrencyValues)) {

            Set<String> symbols = prevCurrencyValues.keySet();
            symbols.stream()
                    .filter(s -> prevCurrencyValues.get(s).compareTo(currentCurrencyValues.get(s)) != 0)
                    .forEach(s -> calculateCurrencyChange(changes, s, prevCurrencyValues.get(s),
                            currentCurrencyValues.get(s)));
            if (ObjectUtils.isNotEmpty(changes)) {
                log.info("There are some currency changes");
                // get users by parameters
                List<User> users = userService.getUsersToNotify();
                log.info("Users to notify: {}", users.size());
                notifyUsers(changes, users);
            }
        }

        prevCurrencyValues = currentCurrencyValues;
    }

    // new logic NOT READY YET
    @Scheduled(fixedDelayString = "${currency-change.upd.milliseconds}")
    public void newLogic() {
        Map<String, BigDecimal> currCurrencyValues = currencyService.getCurrencyFromAPI(); // save to DB every 30 sec
        // find users for notifications by the time
        List<User> users = userService.getUsersToNotify();
        users.forEach(user -> {
            // get prev val
            Map<String, BigDecimal> prCurrencyValues = currencyService.getUserPreviousCurrencyList(user.getTimeOfStart());
            Map<String, Double> changes = new HashMap<>();

            Set<String> symbols = prCurrencyValues.keySet();
            symbols.stream()
                    .filter(s -> prCurrencyValues.get(s).compareTo(currCurrencyValues.get(s)) != 0)
                    .forEach(s -> calculateCurrencyChangeNew(changes, s, prCurrencyValues.get(s),
                            currCurrencyValues.get(s), user.getPercent()));

            if (ObjectUtils.isNotEmpty(changes)) {
                log.info("There are some currency changes");
                notifyUsers(changes, List.of(user));
            }
        });
    }

    private void notifyUsers(Map<String, Double> changes, List<User> users) {
        if (ObjectUtils.isNotEmpty(changes)) {
            users.forEach(user -> {
                // list of changes not for all users
                // collect notifications for the user!!!
                String notificationMessage = createNotificationMessage(changes, user.getPercent());
                String[] messageArr = notificationMessage.split("(?<=\\G.{4000})");
                List<String> messages = Arrays.asList(messageArr);
                bot.sendUpdateMessage(user.getChatId(),
                        "Hey! There is an info about the currency changes: ");
                messages.forEach(m -> bot.sendUpdateMessage(user.getChatId(), m));
            });
        }

    }

    private String createNotificationMessage(Map<String, Double> changes, Integer percent) {
        return changes.keySet().stream()
                .filter(key -> changes.get(key) >= percent)
                .map(key -> key + " -> " + changes.get(key))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private void calculateCurrencyChange(Map<String, Double> changes, String symbol, BigDecimal prevVal,
                                         BigDecimal curVal) {
        BigDecimal result = (prevVal.divide(curVal, 2, RoundingMode.HALF_EVEN)
                .subtract(new BigDecimal(1)))
                .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_EVEN);
        double res = result.setScale(0, RoundingMode.HALF_EVEN).abs().doubleValue();
        // collect all possible changes
        if (res > 0) {
            changes.put(symbol, res);
        }

    }

    private void calculateCurrencyChangeNew(Map<String, Double> changes, String symbol, BigDecimal prevVal,
                                         BigDecimal curVal, int userPercent) {
        BigDecimal result = (prevVal.divide(curVal, 2, RoundingMode.HALF_EVEN)
                .subtract(new BigDecimal(1)))
                .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_EVEN);
        double res = result.setScale(0, RoundingMode.HALF_EVEN).abs().doubleValue();
        // collect all possible changes
        if (res >= userPercent) {
            changes.put(symbol, res);
        }

    }
}
