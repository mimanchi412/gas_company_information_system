<template>
  <svg class="feature-icon" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="gradBill" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stop-color="#ffb84d" />
        <stop offset="100%" stop-color="#ff7b5f" />
      </linearGradient>
    </defs>
    <rect x="14" y="10" width="36" height="44" rx="6" ry="6" fill="url(#gradBill)" />
    <line x1="22" y1="22" x2="42" y2="22" class="line" />
    <line x1="22" y1="30" x2="36" y2="30" class="line" />
    <line x1="22" y1="38" x2="42" y2="38" class="line" />
    <circle cx="32" cy="48" r="3" fill="#fff" />
  </svg>
</template>

<style scoped>
.feature-icon { width: 48px; height: 48px; }
.line {
  stroke: #fff;
  stroke-width: 3;
  stroke-linecap: round;
  animation: dash 2s ease-in-out infinite;
}
@keyframes dash {
  0% { stroke-dasharray: 0 40; }
  50% { stroke-dasharray: 40 0; }
  100% { stroke-dasharray: 0 40; }
}
</style>
