### Design Patterns

### 1. Singleton (GameTimer)
GameTimer is implemented as an enum Singleton, which ensures that only one timer instance exists
across the entire application. It is accessed via `GameTimer.INSTANCE` and implements the `ITimer`
interface so that it can be injected as a dependency.

### 2. Observer (GameObserver + Board)
The Observer pattern is used to notify interested parties when the game state changes.
`GameObserver` is an interface with methods, such as `onGameWon()`, `onGameLost()`, and `onCellRevealed()`.
`Board` maintains a list of observers and notifies them when an event occurs, which decouples the game
logic from the UI and the timer.

### 3. Strategy (IDifficulty)
The Strategy pattern is used to represent difficulty levels. `EasyDifficulty`, `MediumDifficulty`, and 
`HardDifficulty` each implement `IDifficulty`, which encapsulates their own grid size and mine count.
This helps to avoid if-else chains and also allows difficulty to be swapped out polymorphically.

### 4. Factory (BoardFactory)
`BoardFactory` creates and initializes `IBoard` instances based on a given `IDifficulty` strategy.
This decouples the board creation from the rest of the application and also centralizes configuration
logic in just one place.

## Current App Structure

- `src/main/java`
  - Core game logic and the Javalin backend API
- `frontend`
  - Svelte + Vite frontend

## Prerequisites

Install these tools before running the project:

- Java `21+`
- Node.js LTS
- `npm` (included with Node.js)

You do not need to install Gradle separately because the project uses the Gradle wrapper.

## First-Time Setup

### Backend

From the project root:

```powershell
./gradlew build
```

### Frontend

From the `frontend` directory:

```powershell
npm install
```

## Running The App

You need two terminals open.

### Terminal 1: Start the Java API

From the project root:

```powershell
./gradlew run
```

The API will start on:

```text
http://localhost:7070
```

Health check:

```text
http://localhost:7070/api/health
```

### Terminal 2: Start the Svelte UI

From the `frontend` directory:

```powershell
npm run dev
```

The UI will start on:

```text
http://localhost:5173/
```

Open `http://localhost:5173/` in your browser to view the UI.

## Quick Start

1. Install Java `21+` and Node.js LTS.
2. From the project root, run:

```powershell
./gradlew run
```

3. In a second terminal, go to `frontend` and run:

```powershell
npm install
npm run dev
```

4. Open:

```text
http://localhost:5173/
```

## If You Do Not See The UI

If `http://localhost:5173/` is blank or does not load:

- Make sure the frontend terminal is still running `npm run dev`
- Make sure the backend terminal is still running `./gradlew run`
- Check the backend health endpoint at `http://localhost:7070/api/health`
- If `npm` is not recognized, reopen the terminal after installing Node.js
- Make sure you opened the UI URL `http://localhost:5173/` and not the API port

## How The UI Talks To Java

The Svelte app does not contain the real game logic. It calls the Java backend over HTTP.

Current flow:

- Click a difficulty button in the UI
- Svelte sends a request to the Javalin API
- Javalin calls `GameController`
- `GameController` uses `BoardFactory` to create or reset the board
- The API returns JSON describing the current game state
- Svelte re-renders from that JSON

This keeps the game logic in Java and uses Svelte only as the presentation layer.

## Current API Endpoints

- `GET /api/health`
- `GET /api/game`
- `POST /api/game/start`
- `POST /api/game/reset`
- `POST /api/game/reveal`
- `POST /api/game/flag`

Example request body to start a game:

```json
{
  "difficulty": "Easy"
}
```

Example request body to reveal or flag a cell:

```json
{
  "row": 0,
  "col": 0
}
```

## Notes For Teammates

- If the frontend shows a fetch error, make sure the backend is running on port `7070`
- If `npm` is not recognized, install Node.js LTS and reopen the terminal
- If `./gradlew run` fails, verify Java is installed and available on `PATH`
- Open the UI at `http://localhost:5173/`
- Use `http://localhost:7070/api/health` to confirm the Java backend is running

## Recommended Next Step

The current API is intentionally minimal. The next backend milestone should be making the board fully
implement Minesweeper rules, then expanding the API responses to expose richer board state to the UI.
