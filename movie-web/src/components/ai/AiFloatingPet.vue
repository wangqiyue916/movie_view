<template>
  <div class="ai-floating-pet" @click="handleClick">
    <span class="pet-icon">🤖</span>
    <span v-if="!aiStore.hasSession && aiStore.visible" class="notification-dot" />
  </div>
</template>

<script setup lang="ts">
import { useAiStore } from '@/stores/aiStore'
import { aiApi } from '@/api/aiApi'

const aiStore = useAiStore()

async function handleClick() {
  aiStore.toggle()
  if (aiStore.visible && !aiStore.sessionId) {
    try {
      const session = await aiApi.createSession()
      aiStore.sessionId = session.id
    } catch {
      // 创建会话失败时继续，后续发消息时会处理
    }
  }
}
</script>

<style scoped>
.ai-floating-pet {
  position: fixed;
  bottom: 100px;
  right: 24px;
  z-index: 1000;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  animation: pet-bounce 2s ease-in-out infinite;
  transition: transform 180ms ease, box-shadow 180ms ease;
}

.ai-floating-pet:hover {
  transform: scale(1.12);
  box-shadow: 0 6px 22px rgba(102, 126, 234, 0.6);
}

.pet-icon {
  font-size: 28px;
  line-height: 1;
}

.notification-dot {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ef4444;
  border: 2px solid #fff;
  animation: dot-pulse 1.6s ease-in-out infinite;
}

@keyframes pet-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

@keyframes dot-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.4; }
}
</style>
