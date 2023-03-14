import {
	createApp
} from 'vue'
import App from './App.vue'

// document.addEventListener(
// 	"deviceready",
// 	function() {
// 		alert('Running cordova-' + cordova.platformId + '@' + cordova.version);
// 	}
// );

import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import locale from 'element-plus/lib/locale/lang/zh-cn'

const app = createApp(App)

app.use(ElementPlus, {
	locale
})

app.mount('#app')
