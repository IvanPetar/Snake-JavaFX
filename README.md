# Snake JavaFX

A Snake game built with JavaFX as a university project at Algebra Bernays University. It runs as a single-player game or as a **two-player networked game**. The project shows several Java techniques: **sockets, RMI, JNDI, serialization, XML (DOM + DTD) and reflection**.

## Features

- **Single-player mode**: the classic Snake game
- **Two-player network mode**: two game instances connect over TCP sockets
  - Player 1 hosts the game and runs the game loop
  - Player 2 sends its moves to Player 1 and receives the game state back
- **RMI chat**: a real-time chat between the two players through a shared RMI server
- **JNDI configuration**: ports and host name are read from `conf/app.conf`
- **Save / Load**: the game state is saved with Java serialization (`game/snake.dat`)
- **XML move log**: snake moves are written to `xml/snakeMoves.xml`, which follows the `xml/dtd/gameMoves.dtd` structure
- **Documentation generator**: uses reflection to scan the project's classes and writes an HTML report (`doc/documentation.html`)

## Tech Stack

| Area | Technologies |
|------|--------------|
| Language | Java |
| UI | JavaFX 21 (FXML) |
| Networking | TCP sockets, Java RMI |
| Configuration | JNDI (file system context) |
| Persistence | Java serialization, XML (DOM, DTD) |
| Other | Lombok, SLF4J, reflection |
| Build | Maven |

## Project Structure

```
├── conf/app.conf                 # Ports and host name (read through JNDI)
├── xml/dtd/gameMoves.dtd         # DTD for the XML move log
├── lib/                          # fscontext JAR (JNDI file system provider)
└── src/main/java/hr/algebra/snakejavafx/
    ├── SnakeApplication.java     # Entry point; reads the player type from the command line
    ├── SnakeController.java      # UI controller and game loop
    ├── engine/                   # Game logic (movement, food, collisions, winner)
    ├── model/                    # GameState, Position, Direction, PlayerType
    ├── input/                    # Keyboard handling
    ├── rmi/                      # RMI chat server and remote service
    ├── jndi/                     # Configuration reader
    └── utils/                    # Networking, chat, save/load, XML, documentation
```

## Configuration

`conf/app.conf`:

```properties
player.one.server.port=1980
player.two.server.port=1989
rmi.server.port=1099
host.name=localhost
```

## Running the Game

The application takes **one command-line argument**, the player type:

| Argument | Mode |
|----------|------|
| `SINGLE_PLAYER` | Single-player game |
| `PLAYER_1` | Multiplayer, host |
| `PLAYER_2` | Multiplayer, client |

### Single player

Run `SnakeApplication` with the program argument `SINGLE_PLAYER`. In IntelliJ, use **Run → Edit Configurations → Program arguments**.

### Multiplayer

Start these in order:

1. **RMI server**: run `hr.algebra.snakejavafx.rmi.RmiServer`
2. **Player 1**: run `SnakeApplication` with `PLAYER_1`
3. **Player 2**: run `SnakeApplication` with `PLAYER_2`
4. In Player 1's window, choose **Game → Start Game**

## Controls

| Player | Keys |
|--------|------|
| Single player / Player 1 | `W` `A` `S` `D` |
| Player 2 | Arrow keys |

## Menu

- **Game → Start Game / New / Save / Load**
- **Documentation → Generate**: creates `doc/documentation.html`

> The `game/` and `doc/` folders must exist in the project root for saving the game and generating the documentation.

## Author

**Ivan Petar Šolić**
