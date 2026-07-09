import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/global.css'
import App from './App.vue'
import router from './router'

/* 注入暗黑下拉面板全局样式 */
if (!document.getElementById('dropdown-dark-css')) {
  const s = document.createElement('style')
  s.id = 'dropdown-dark-css'
  s.textContent =
    '.el-popper.user-dropdown-dark{background:#1a1a2e!important;border:1px solid rgba(214,176,95,.24)!important;border-radius:8px!important;padding:4px 0!important}' +
    '.user-dropdown-dark .el-dropdown-menu{background:#1a1a2e!important}' +
    '.user-dropdown-dark .el-dropdown-menu__item{color:#d4c5a0!important;line-height:36px!important}' +
    '.user-dropdown-dark .el-dropdown-menu__item:not(.is-disabled):hover{background:rgba(214,176,95,.14)!important;color:#f3d58c!important}' +
    '.user-dropdown-dark .el-dropdown-menu__item.is-disabled{color:#a09070!important}' +
    '.user-dropdown-dark .el-dropdown-menu__item--divided{border-top-color:rgba(214,176,95,.2)!important}'
  document.head.appendChild(s)
}

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')

