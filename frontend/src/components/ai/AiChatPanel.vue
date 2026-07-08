<template>
  <Transition name="panel-slide">
    <div v-if="aiStore.visible" class="ai-chat-panel">
      <div class="panel-header">
        <span class="header-title">AI 电影助手</span>
        <button class="close-btn" @click="aiStore.close">✕</button>
      </div>

      <div class="messages-area" ref="messagesRef">
        <div v-if="aiStore.messages.length === 0" class="empty-state">
          <div class="welcome-text">你好！我是电影助手，可以问我关于电影、演员、导演、周边等问题～</div>
          <div class="quick-chips">
            <span class="chip" @click="sendQuick('推荐好看的科幻片')">推荐好看的科幻片</span>
            <span class="chip" @click="sendQuick('星际穿越的导演是谁')">星际穿越的导演是谁</span>
            <span class="chip" @click="sendQuick('有什么热门周边')">有什么热门周边</span>
          </div>
        </div>

        <div
          v-for="msg in aiStore.messages"
          :key="msg.id"
          class="message-item"
          :class="msg.role === 'USER' ? 'msg-user' : 'msg-assistant'"
        >
          <div class="message-bubble" :class="msg.role === 'USER' ? 'bubble-user' : 'bubble-assistant'">
            <div class="message-text">{{ msg.content }}</div>
            <div v-if="msg.role === 'ASSISTANT' && msg.relatedType" class="related-card">
              <router-link
                v-if="msg.relatedType === 'MOVIE'"
                :to="`/movies/${msg.relatedId}`"
                class="related-link"
              >
                查看电影详情 →
              </router-link>
              <router-link
                v-else-if="msg.relatedType === 'NEWS'"
                :to="`/news/${msg.relatedId}`"
                class="related-link"
              >
                查看资讯 →
              </router-link>
              <a
                v-else-if="msg.relatedType === 'VIDEO'"
                :href="`/videos`"
                class="related-link"
              >
                查看视频 →
              </a>
              <router-link
                v-else-if="msg.relatedType === 'MERCHANDISE'"
                :to="`/merchandise/${msg.relatedId}`"
                class="related-link"
              >
                查看周边 →
              </router-link>
            </div>
          </div>
        </div>

        <div v-if="aiStore.loading" class="typing-indicator">
          <span class="dot" />
          <span class="dot" />
          <span class="dot" />
          AI 正在思考...
        </div>
      </div>

      <div class="input-area">
        <textarea
          v-model="inputText"
          class="input-textarea"
          placeholder="输入你的问题..."
          rows="1"
          @keydown.enter.exact.prevent="handleSend"
        />
        <button class="send-btn" :disabled="!inputText.trim() || aiStore.loading" @click="handleSend">
          发送
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { useAiStore } from '@/stores/aiStore'
import { aiApi } from '@/api/aiApi'

const aiStore = useAiStore()
const inputText = ref('')
const messagesRef = ref<HTMLElement | null>(null)

async function scrollToBottom() {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || aiStore.loading) return

  if (!aiStore.sessionId) {
    try {
      const session = await aiApi.createSession()
      aiStore.sessionId = session.id
    } catch {
      return
    }
  }

  inputText.value = ''
  aiStore.setLoading(true)

  try {
    const reply = await aiApi.sendMessage(aiStore.sessionId, text)
    // The backend returns the assistant message; we simulate the user message locally
    const userMsg = {
      id: Date.now(),
      sessionId: aiStore.sessionId,
      role: 'USER' as const,
      content: text,
      relatedType: null,
      relatedId: null,
      createdAt: new Date().toISOString(),
    }
    aiStore.addMessage(userMsg)
    aiStore.addMessage(reply)
    await scrollToBottom()
  } catch {
    // 发送失败，忽略
  } finally {
    aiStore.setLoading(false)
  }
}

function sendQuick(text: string) {
  inputText.value = text
  handleSend()
}

watch(() => aiStore.messages.length, () => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-chat-panel {
  position: fixed;
  right: 24px;
  bottom: 170px;
  z-index: 999;
  width: 380px;
  max-height: 520px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.14);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  opacity: 0.8;
  transition: opacity 150ms;
}

.close-btn:hover {
  opacity: 1;
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px 12px;
}

.welcome-text {
  font-size: 14px;
  color: #6b7280;
  text-align: center;
  line-height: 1.7;
}

.quick-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.chip {
  display: inline-block;
  padding: 6px 14px;
  background: #f3f4f6;
  border-radius: 20px;
  font-size: 13px;
  color: #374151;
  cursor: pointer;
  transition: background 150ms, color 150ms;
}

.chip:hover {
  background: #667eea;
  color: #fff;
}

.message-item {
  display: flex;
  max-width: 85%;
}

.msg-user {
  align-self: flex-end;
}

.msg-assistant {
  align-self: flex-start;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.bubble-user {
  background: #667eea;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.bubble-assistant {
  background: #f3f4f6;
  color: #1f2937;
  border-bottom-left-radius: 4px;
}

.related-card {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.bubble-assistant .related-card {
  border-top-color: #d1d5db;
}

.related-link {
  font-size: 13px;
  color: #a78bfa;
  text-decoration: none;
  font-weight: 500;
}

.bubble-assistant .related-link {
  color: #667eea;
}

.related-link:hover {
  text-decoration: underline;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 0;
  font-size: 13px;
  color: #9ca3af;
}

.typing-indicator .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #9ca3af;
  animation: typing-blink 1.4s ease-in-out infinite;
}

.typing-indicator .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing-blink {
  0%, 80%, 100% { opacity: 0.2; }
  40% { opacity: 1; }
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #e5e7eb;
}

.input-textarea {
  flex: 1;
  resize: none;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 14px;
  line-height: 1.5;
  outline: none;
  font-family: inherit;
  transition: border-color 150ms;
  max-height: 80px;
}

.input-textarea:focus {
  border-color: #667eea;
}

.send-btn {
  padding: 8px 18px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 150ms;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: transform 280ms ease, opacity 280ms ease;
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  transform: translateX(40px);
  opacity: 0;
}
</style>
