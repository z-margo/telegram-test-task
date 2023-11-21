package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserState;
import com.example.demo.service.Constants;
import com.example.demo.utils.DateTimeConverter;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalTime;
import java.util.Map;

public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        this.chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToStart(MessageContext ctx, int maxUsers) {
        SendMessage message = new SendMessage();
        message.setChatId(ctx.chatId());
        if (chatStates.size() < maxUsers) {
            message.setText(Constants.WELCOME_MESSAGE);
            sender.execute(message);
            chatStates.put(ctx.chatId(), UserState.AWAITING_TIME_START);
        } else {
            message.setText(Constants.WELCOME_ERROR_MESSAGE);
            sender.execute(message);
        }

    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Constants.STOP_MESSAGE);
        sender.execute(sendMessage);
        chatStates.remove(chatId);
    }

    public User replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
            return null;
        }
        User user = new User();
        switch (chatStates.get(chatId)) {
            case AWAITING_TIME_START -> user.setTimeOfStart(replyToSetTimeStart(chatId, message));
            case AWAITING_TIME_FINISH -> user.setTimeOfFinish(replyToSetTimeFinish(chatId, message));
            case AWAITING_PERCENT -> user.setPercent(replyToSetPercent(chatId, message));
            case SUBSCRIBED -> replyToSubscribed(chatId);
            case UNSUBSCRIBED -> replyToUnsubscribed(chatId);
            default -> unexpectedMessage(chatId);
        }
        return user;
    }

    public void sendUpdateMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Constants.UNEXPECTED_ERROR);
        sender.execute(sendMessage);
    }

    public void replyToSubscribed(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Constants.SUCCESS_MESSAGE);
        sender.execute(sendMessage);
    }

    public void replyToUnsubscribed(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Constants.UNSUBCSCRIBED_MESSAGE);
        sender.execute(sendMessage);
    }

    private LocalTime replyToSetTimeStart(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = message.getText();

        LocalTime time = DateTimeConverter.parseStringTime(text);

        if (time != null) {
            sendMessage.setText(Constants.TIME_TO_FINISH_MESSAGE);
            sender.execute(sendMessage);
            chatStates.put(chatId, UserState.AWAITING_TIME_FINISH);
        } else {
            sendMessage.setText(Constants.ERROR_TIME_MESSAGE);
            sender.execute(sendMessage);
        }
        return time;
    }


    private LocalTime replyToSetTimeFinish(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = message.getText();

        LocalTime time = DateTimeConverter.parseStringTime(text);

        if (time != null) {
            sendMessage.setText(Constants.SET_PERCENT_MESSAGE);
            sender.execute(sendMessage);
            chatStates.put(chatId, UserState.AWAITING_PERCENT);
        } else {
            sendMessage.setText(Constants.ERROR_TIME_MESSAGE);
            sender.execute(sendMessage);
        }
        return time;
    }

    private Integer replyToSetPercent(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = message.getText();
        Integer percent;
        try {
            percent = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            percent = null;
        }

        if (percent != null && percent > 0 && percent < 100) {
            sendMessage.setText(Constants.SUCCESS_MESSAGE);
            sender.execute(sendMessage);
            chatStates.put(chatId, UserState.SUBSCRIBED);

        } else {
            sendMessage.setText(Constants.PERCENT_ERROR_MESSAGE);
            sender.execute(sendMessage);
        }
        return percent;
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }

}
