# Boomerang Australia

## Description
A multiplayer, terminal-based implementation of the Boomerang Australia
card game using Java and TCP. Supports 2–4 human players and bots combined.

## Requirements
- Java JDK 22
- IntelliJ IDEA (for unit testing)
- JUnit 5.11.4

## How to compile

```bash
javac -d out $(find src -name "*.java")
```

## How to run

### Host (server and host client)

```bash
java -cp out BoomerangAustralia 2 0
```

The first argument specifies the number of human players and the second
specifies the number of bots. The total must be between 2 and 4,
with at least one human player.

### Guest (client)

```bash
java -cp out BoomerangAustralia <IP>
```

Replace `<IP>` with the host computer's IP address, no IP needed if we run guest on same computer as the host.

The game starts once all required clients have connected.

## How to run the unit tests

Right-click the `test` directory in IntelliJ IDEA and select Run All Tests.

JUnit dependency: `org.junit.jupiter:junit-jupiter:5.11.4`