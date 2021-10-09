# Transactions

Hello there!

This is one of my tasks at skillbox.ru. 

The task was to learn how to use multithreading and avoid deadlock situations and race conditions. 

During the test, the account could be blocked by the security service for attempted fraud. Security service can only process one operation at a time.

The following cases were tested:

- transfers between random accounts at the same time. The amount of money in the bank should not change.
- transfers between two accounts. In the nonproper multithreaded program can occur deadlock situation.

The list of used instruments is below:

- Multithreading
- Maven
- JUnit
- Lombok
