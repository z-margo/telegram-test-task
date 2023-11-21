package com.example.demo.service;

public final class Constants {
    public static final String START_DESCRIPTION = "Starts the bot";

    public static final String CHAT_STATES = "chatStates";

    public static final String WELCOME_MESSAGE = """
            Hi! This is a bot to notify you about currency changes.
            Let's make some settings first.
            Please, enter time to start notifications!
            """;

    public static final String WELCOME_ERROR_MESSAGE = "Sorry... Bot is full of users. Please, try to join later... ";

    public static final String STOP_MESSAGE = """
            Thank you for using bot. See you soon!
            Press /start to start using bot again
            """;
    public static final String UNEXPECTED_ERROR = "I did not expect that.";
    public static final String SUCCESS_MESSAGE = "You've been successfully subscribed for the notifications!";
    public static final String UNSUBCSCRIBED_MESSAGE = "You've been successfully unsubscribed from the notifications!";

    public static final String TIME_TO_FINISH_MESSAGE = "Great! Enter time to finish:";

    public static final String ERROR_TIME_MESSAGE = "Time is incorrect. Please try again";
    public static final String SET_PERCENT_MESSAGE = "Great! Enter % for notifications:";

    public static final String PERCENT_ERROR_MESSAGE = "Percent value is incorrect. Please try again";

    private Constants(){}
}
