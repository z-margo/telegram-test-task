# Task: create a Telegram bot that will notify user about cryptocurrency price changes.
* User will start at some time, for example at 8 am. +
* After that, if some cryptocurrency becomes more expensive or cheaper by more than N percent since 8 am, user should be notified with message in Telegram.
* URL to check prices for crypto: https://api.mexc.com/api/v3/ticker/price. +
* Algorithm should make a request and refresh application state every S seconds. +
* User should have ability to restart algorithm. +
* Telegram bot can support K users, K + 1 user should be notified that bot is not available. +
* Technology to use: Spring Boot. Database is by candidate decision. Task should take 4 hours maximum.
* Task can be unfinished if it is taking more time. The point is not to have bot at the end of task but to see how candidate will show himself.


# Implementation details:

The APP starts and update currency changes each 30 sec (by default). Compares previous and current values and collects changes to a list.
If the list is not empty -> the app gets users to notify (by current time, FYI timezone is not checked), filters changes by user's params and sends notification messages (if user is active = didn't stop using the bot)
FYI: max size of the telegram message is 4096 characters. If the message is longer the app splits it to several parts

 1. Find the bot with name `currencyChangeTest` (https://t.me/currency_change_test_bot)
 2. Type `/start` and follow the instructions. You have to set time of start and finish notifications and percent
 3. Type `/stop` to stop using the bot.
 4. Type `/start` to restart using the bot. In case of K + 1 user bot would send a message: "Sorry... Bot is full of users. Please, try to join later... "

`currency-change.upd.milliseconds` - time in milliseconds to retrieve data from the API, default value is 30 sec (= 30000 ms)
FYI: fixedDelay logic is used!
`currency-change.max_users` - max number of users to join the bot at the same time

### The app compare previous and current values (not since the time defined by the user) and doesn't keep it in the DB. 
#### How to implement the logic:

1. Save all currency values to DB with current time (by schedule).
2. Get users to notify by time parameter and calculate changes the same way as implemented where **previous val is currency by the time to start**.


# Technologies:

Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL, Maven, Telegram API, Lombok, Apache commons libs

