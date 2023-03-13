import { createApp } from 'vue'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './styles/dark/css-vars.css'
import locale from 'element-plus/lib/locale/lang/zh-cn'

const app = createApp(App)

app.use(ElementPlus, { locale })

app.mount('#app')
