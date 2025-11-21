<template>
  <svg class="feature-icon" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="gradUser" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" stop-color="#7c9dff" />
        <stop offset="100%" stop-color="#5ad8ff" />
      </linearGradient>
    </defs>
    <circle class="pulse" cx="32" cy="24" r="12" fill="url(#gradUser)" />
    <circle cx="32" cy="22" r="6" fill="#fff" />
    <path d="M14 54c2-10 10-16 18-16s16 6 18 16" fill="none" stroke="url(#gradUser)" stroke-width="3" stroke-linecap="round" />
  </svg>
</template>

<style scoped>
.feature-icon { width: 48px; height: 48px; }
.pulse { animation: pulse 2.5s infinite; }
@keyframes pulse {
  0% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 0.4; transform: scale(1.08); }
  100% { opacity: 0.8; transform: scale(1); }
}
</style>
