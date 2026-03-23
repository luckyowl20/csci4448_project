package minesweeper.api;

import io.javalin.Javalin;
import minesweeper.api.dto.CellActionRequest;
import minesweeper.api.dto.DifficultyRequest;
import minesweeper.api.dto.ErrorResponse;
import minesweeper.api.dto.HealthResponse;
import minesweeper.domain.board.BoardFactory;
import minesweeper.domain.difficulty.EasyDifficulty;
import minesweeper.domain.timer.GameTimer;
import minesweeper.game.GameController;
import minesweeper.game.GameStatsObserver;

/**
 * Bootstraps the HTTP API for the Minesweeper application.
 *
 * <p>This class is intentionally thin. It should only be responsible for:
 * <ul>
 *     <li>constructing the top-level application objects</li>
 *     <li>registering Javalin routes</li>
 *     <li>mapping HTTP request bodies into DTO classes</li>
 *     <li>mapping exceptions into JSON error responses</li>
 * </ul>
 *
 * <p>When adding a new API feature, the normal workflow is:
 * <ol>
 *     <li>define or reuse a request/response DTO in {@code minesweeper.api.dto}</li>
 *     <li>register a new route here</li>
 *     <li>delegate the work to {@link GameService}</li>
 *     <li>keep domain/game logic out of the route handler</li>
 * </ol>
 */
public class GameApiServer {
    /**
     * Starts the Javalin server and registers all current routes.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        GameService gameService = new GameService(
                new GameController(
                        new BoardFactory(),
                        GameTimer.INSTANCE,
                        new EasyDifficulty(),
                        java.util.List.of(new GameStatsObserver())
                )
        );

        Javalin.start(config -> {
            config.jetty.port = 7070;
            config.bundledPlugins.enableCors(cors ->
                    cors.addRule(rule -> rule.anyHost())
            );

            // Simple server liveness check used by the frontend and local troubleshooting.
            config.routes.get("/api/health", ctx -> ctx.json(new HealthResponse("ok")));

            // Returns the full game snapshot that drives the Svelte UI.
            config.routes.get("/api/game", ctx -> ctx.json(gameService.getState()));

            // Creates a new board using the requested difficulty.
            config.routes.post("/api/game/start", ctx -> {
                DifficultyRequest request = ctx.bodyAsClass(DifficultyRequest.class);
                ctx.json(gameService.startGame(request.difficulty()));
            });

            // Recreates the current game using the controller's selected difficulty.
            config.routes.post("/api/game/reset", ctx -> ctx.json(gameService.resetGame()));

            // Applies a reveal action to one cell and returns the updated game state.
            config.routes.post("/api/game/reveal", ctx -> {
                CellActionRequest request = ctx.bodyAsClass(CellActionRequest.class);
                ctx.json(gameService.revealCell(request.row(), request.col()));
            });

            // Toggles a flag on one cell and returns the updated game state.
            config.routes.post("/api/game/flag", ctx -> {
                CellActionRequest request = ctx.bodyAsClass(CellActionRequest.class);
                ctx.json(gameService.flagCell(request.row(), request.col()));
            });

            // These handlers keep UI error handling consistent by always returning JSON.
            config.routes.exception(IllegalArgumentException.class, (exception, ctx) -> {
                ctx.status(400).json(new ErrorResponse(exception.getMessage()));
            });
            config.routes.exception(IllegalStateException.class, (exception, ctx) -> {
                ctx.status(400).json(new ErrorResponse(exception.getMessage()));
            });
            config.routes.exception(IndexOutOfBoundsException.class, (exception, ctx) -> {
                ctx.status(400).json(new ErrorResponse(exception.getMessage()));
            });
        });
    }
}
