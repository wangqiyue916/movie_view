<template>
  <el-dropdown
    v-if="userStore.isLogin"
    trigger="hover"
    :popper-class="dark ? 'user-dropdown-dark' : ''"
    @command="handleCommand"
  >
    <span class="user-trigger" :class="{ dark: dark }">
      <el-icon style="margin-right:4px"><User /></el-icon>
      {{ userStore.userInfo?.nickname }}
      <el-icon class="arrow"><ArrowDown /></el-icon>
    </span>

    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item disabled>
          <div class="user-info">
            <div class="nickname">{{ userStore.userInfo?.nickname }}</div>
            <div class="roles">
              <el-tag v-for="r in roleLabels" :key="r.code" size="small" :type="r.type">
                {{ r.label }}
              </el-tag>
            </div>
          </div>
        </el-dropdown-item>

        <el-dropdown-item
          v-if="userStore.hasRole('ADMIN') || userStore.hasRole('SUPER_ADMIN')"
          command="admin"
          :icon="Setting"
        >
          管理后台
        </el-dropdown-item>

        <el-dropdown-item
          v-if="userStore.hasRole('OFFICIAL')"
          command="official"
          :icon="OfficeBuilding"
        >
          电影商后台
        </el-dropdown-item>

        <el-dropdown-item divided command="logout" :icon="SwitchButton">
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <router-link v-else to="/login">登录</router-link>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ArrowDown, Setting, OfficeBuilding, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

defineProps<{ dark?: boolean }>()

const router = useRouter()
const userStore = useUserStore()

interface RoleLabel {
  code: string
  label: string
  type: '' | 'success' | 'warning' | 'danger' | 'info'
}

const ROLE_MAP: Record<string, RoleLabel> = {
  USER: { code: 'USER', label: '用户', type: '' },
  OFFICIAL: { code: 'OFFICIAL', label: '电影商', type: 'success' },
  ADMIN: { code: 'ADMIN', label: '管理员', type: 'warning' },
  SUPER_ADMIN: { code: 'SUPER_ADMIN', label: '超级管理员', type: 'danger' },
}

const roleLabels = computed<RoleLabel[]>(() =>
  userStore.roles.map((r) => ROLE_MAP[r] ?? { code: r, label: r, type: 'info' }),
)

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (cmd === 'admin') {
    router.push('/admin')
  } else if (cmd === 'official') {
    router.push('/official')
  }
}
</script>

<style scoped>
.user-trigger {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
  color: inherit;
}

.user-trigger.dark {
  color: #e8c16d;
}

.arrow {
  margin-left: 2px;
  font-size: 12px;
}

.user-info {
  text-align: center;
  min-width: 120px;
}

.nickname {
  font-weight: 600;
  margin-bottom: 4px;
}

.roles {
  display: flex;
  gap: 4px;
  justify-content: center;
}
</style>