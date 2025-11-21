<template>
  <svg class="feature-icon" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="gradWork" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#7ae39c" />
        <stop offset="100%" stop-color="#33c7ba" />
      </linearGradient>
    </defs>
    <rect x="12" y="18" width="40" height="28" rx="8" ry="8" fill="url(#gradWork)" />
    <path d="M22 30h8l3 4 7-10 8 12" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" class="check" />
    <circle cx="22" cy="46" r="2" fill="#fff" />
    <circle cx="32" cy="46" r="2" fill="#fff" />
    <circle cx="42" cy="46" r="2" fill="#fff" />
  </svg>
</template>

<style scoped>
.feature-icon { width: 48px; height: 48px; }
.check {
  stroke-dasharray: 60;
  stroke-dashoffset: 60;
  animation: draw 2s ease-in-out infinite;
}
@keyframes draw {
  0% { stroke-dashoffset: 60; }
  50% { stroke-dashoffset: 0; }
  100% { stroke-dashoffset: 60; }
}
</style>
