# Backend



## What is this project?

This is the server backend for the Proud Detectives App.
As of version 0.0.1_alpha it supports the following features:
- client server communication
- support for different message types
    - ChatMessage
    - CluesGuessesStateMessage
    - EndGameMessage
    - JoinGameMessage
    - ReadyMessage
    - RegisterMessage
    - StartGameMessage
- database persistence for players, chat messages and games


## How to install
1. Clone this repository
2. Make sure Maven and Java (v17) are installed on your PC
3. run `mvn clean install` in the root directory of your project in any console that supports maven.
4. This creates a target directory with a `detectives-backend-ws.jar`
5. Copy this `.jar` to the target location and start the server in a terminal with `java -jar detectives-backend-ws.jar`