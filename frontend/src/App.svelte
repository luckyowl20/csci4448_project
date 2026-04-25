<script>
  import { onMount } from 'svelte';

  // where api requests go / api root route
  const API_BASE = 'http://localhost:7070/api';

  // frontend local state derived from java game state returning a JSON of this
  let gameState = {
    selectedDifficulty: 'Easy',
    activeGame: false,
    status: 'NOT_STARTED',
    elapsedTime: 0,
    rows: 0,
    cols: 0,
    mineCount: 0,
    cells: [],
    difficulties: []
  };
  let loading = false;
  let errorMessage = '';
  let timerInterval;

  // load the game when the page first loads 
  onMount(loadGameState);

  //
  // API Routes to send to Java backend and wait on a return call
  //

  async function loadGameState() {
    await callApi('/game');
  }

  async function pollTimer() {
    try {
      const response = await fetch(`${API_BASE}/game`, {
        headers: { 'Content-Type': 'application/json' }
      });
      const data = await response.json();
      if (response.ok) {
        gameState = data;
      }
    } catch {
    }
  }

  async function startGame(difficulty) {
    await callApi('/game/start', {
      method: 'POST',
      body: JSON.stringify({ difficulty: difficulty.name })
    });
  }

  async function resetGame() {
    await callApi('/game/reset', { method: 'POST' });
  }

  async function revealCell(cell) {
    if (!gameState.activeGame) return;
    try {
      const response = await fetch(`${API_BASE}/game/reveal`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ row: cell.row, col: cell.col })
      });
      const data = await response.json();
      if (response.ok) {
        gameState = data;
        updateTimerInterval();
      }
    } catch {}
  }

  async function toggleFlag(cell) {
    if (!gameState.activeGame) return;
    try {
      const response = await fetch(`${API_BASE}/game/flag`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ row: cell.row, col: cell.col })
      });
      const data = await response.json();
      if (response.ok) {
        gameState = data;
        updateTimerInterval();
      }
    } catch {}
  }

  async function callApi(path, options = {}) {
    loading = true;
    errorMessage = '';
    try {
      const response = await fetch(`${API_BASE}${path}`, {
        headers: { 'Content-Type': 'application/json' },
        ...options
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message ?? 'Request failed.');
      }
      gameState = data;
      updateTimerInterval();
    } catch (error) {
      errorMessage = error.message;
    } finally {
      loading = false;
    }
  }

  function updateTimerInterval() {
    clearInterval(timerInterval);
    if (gameState.status === 'IN_PROGRESS') {
      timerInterval = setInterval(pollTimer, 1000);
    }
  }
</script>

<svelte:head>
  <title>Minesweeper Prototype</title>
  <meta
    name="description"
    content="Minimal Svelte UI shell for a Java-backed Minesweeper project."
  />
</svelte:head>

<main class="app-shell">
  <section class="hero">
    <div>
      <p class="eyebrow">CSCI 4448 Project</p>
      <h1>Minesweeper in Java</h1>
      <p class="lede">
        A full-stack Minesweeper implementation using Java, OOP design patterns, and a Svelte frontend.
      </p>
    </div>
    <div class="status-card">
      <span class="status-label">Status</span>
      <strong>
        {#if gameState.status === 'NOT_STARTED' && !gameState.activeGame}
          🎮 Ready to Play
        {:else if gameState.status === 'NOT_STARTED'}
          🎮 Ready
        {:else if gameState.status === 'IN_PROGRESS'}
          🎮 In Progress
        {:else if gameState.status === 'WON'}
          🏆 You Won!
        {:else if gameState.status === 'LOST'}
          💥 Game Over
        {/if}
      </strong>
      <p>
        Difficulty: {gameState.selectedDifficulty} <br />
        {#if gameState.status === 'IN_PROGRESS' || gameState.status === 'WON' || gameState.status === 'LOST'}
          Timer: {gameState.elapsedTime}s
        {/if}
      </p>
      {#if errorMessage}
        <p class="error-text">{errorMessage}</p>
      {/if}
    </div>
  </section>

  <section class="controls">
    <div class="difficulty-group">
      <span class="group-label">Difficulty</span>
      <div class="button-row">
        {#each gameState.difficulties as difficulty}
          <button
            class:selected={difficulty.name === gameState.selectedDifficulty}
            on:click={() => startGame(difficulty)}
            disabled={loading}
          >
            {difficulty.name}
          </button>
        {/each}
      </div>
    </div>

    <div class="difficulty-meta">
      <span>{gameState.rows || '-'}x{gameState.cols || '-'}</span>
      <span>💣 {gameState.mineCount - gameState.cells.filter(c => c.flagged).length} left</span>    </div>

    <button class="reset-button" on:click={resetGame} disabled={!gameState.activeGame || loading}>
      Start New Game
    </button>
  </section>

  <section class="board-panel">
    {#if gameState.activeGame}
      <div
              class="board"
              style:grid-template-columns={`repeat(${gameState.cols}, minmax(0, 1fr))`}
              style:max-width={`${gameState.cols * 42}px`}
      >
        {#each gameState.cells as cell}
          <button
            class="cell"
            class:revealed={cell.revealed}
            class:flagged={cell.flagged}
            title={`Cell ${cell.row}, ${cell.col}`}
            on:click={() => revealCell(cell)}
            on:contextmenu|preventDefault={() => toggleFlag(cell)}
            disabled={loading}
          >
            {#if cell.flagged}
              🚩
            {:else if cell.revealed && cell.mine && gameState.status === 'LOST'}
              💥
            {:else if cell.revealed}
              <span class="num-{cell.adjacentMines}">{cell.adjacentMines === 0 ? '' : cell.adjacentMines}</span>
            {/if}
          </button>
        {/each}
      </div>
    {:else}
      <div class="empty-board">
        <p>Press a difficulty button to create a board from the Java backend.</p>
        <p>Left click reveals a cell. Right click toggles a flag.</p>
      </div>
    {/if}
  </section>
  {#if gameState.status === 'WON' || gameState.status === 'LOST'}
    <div class="overlay">
      <div class="overlay-card">
        {#if gameState.status === 'WON'}
          <h2>🏆 You Won!</h2>
          <p>Completed in {gameState.elapsedTime}s on {gameState.selectedDifficulty} Difficulty</p>
        {:else}
          <h2>💥 Game Over</h2>
          <p>Better luck next time!</p>
        {/if}
        <button class="overlay-btn" on:click={resetGame}>Play Again</button>
      </div>
    </div>
  {/if}
</main>
