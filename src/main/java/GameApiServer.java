import io.javalin.Javalin;

public class GameApiServer {
    public static void main(String[] args) {
        GameService gameService = new GameService(
                new GameController(new BoardFactory(), GameTimer.INSTANCE, new EasyDifficulty())
        );

        Javalin.start(config -> {
            config.jetty.port = 7070;
            config.bundledPlugins.enableCors(cors ->
                    cors.addRule(rule -> rule.anyHost())
            );

            config.routes.get("/api/health", ctx -> ctx.json(new HealthResponse("ok")));
            config.routes.get("/api/game", ctx -> ctx.json(gameService.getState()));
            config.routes.post("/api/game/start", ctx -> {
                DifficultyRequest request = ctx.bodyAsClass(DifficultyRequest.class);
                ctx.json(gameService.startGame(request.difficulty()));
            });
            config.routes.post("/api/game/reset", ctx -> ctx.json(gameService.resetGame()));
            config.routes.post("/api/game/reveal", ctx -> {
                CellActionRequest request = ctx.bodyAsClass(CellActionRequest.class);
                ctx.json(gameService.revealCell(request.row(), request.col()));
            });
            config.routes.post("/api/game/flag", ctx -> {
                CellActionRequest request = ctx.bodyAsClass(CellActionRequest.class);
                ctx.json(gameService.flagCell(request.row(), request.col()));
            });

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
