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