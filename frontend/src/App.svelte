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

  // load the game when the page first loads 
  onMount(loadGameState);

  //
  // API Routes to send to Java backend and wait on a return call
  //

  async function loadGameState() {
    await callApi('/game');
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
    if (!gameState.activeGame) {
      return;
    }

    await callApi('/game/reveal', {
      method: 'POST',
      body: JSON.stringify({ row: cell.row, col: cell.col })
    });
  }

  async function toggleFlag(cell) {
    if (!gameState.activeGame) {
      return;
    }

    await callApi('/game/flag', {
      method: 'POST',
      body: JSON.stringify({ row: cell.row, col: cell.col })
    });
  }

  async function callApi(path, options = {}) {
    loading = true;
    errorMessage = '';

    try {
      const response = await fetch(`${API_BASE}${path}`, {
        headers: {
          'Content-Type': 'application/json'
        },
        ...options
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message ?? 'Request failed.');
      }

      gameState = data;
    } catch (error) {
      errorMessage = error.message;
    } finally {
      loading = false;
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
        This page is responsible for communicating with the Java backend to run our Minesweeper game.
      </p>
    </div>
    <div class="status-card">
      <span class="status-label">Status</span>
      <strong>{gameState.activeGame ? gameState.status : 'WAITING_TO_START'}</strong>
      <p>
        Difficulty: {gameState.selectedDifficulty} <br />
        Timer: {gameState.elapsedTime}s
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
      <span>{gameState.mineCount} mines</span>
    </div>

    <button class="reset-button" on:click={resetGame} disabled={!gameState.activeGame || loading}>
      Reset Current Game
    </button>
  </section>

  <section class="board-panel">
    {#if gameState.activeGame}
      <div
        class="board"
        style:grid-template-columns={`repeat(${gameState.cols}, minmax(0, 1fr))`}
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
              F
            {:else if cell.revealed}
              {cell.adjacentMines}
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
</main>
