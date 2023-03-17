import {
	createApp
} from 'vue'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import locale from 'element-plus/lib/locale/lang/zh-cn'

const app = createApp(App)

app.use(ElementPlus, {
	locale
})

app.mount('#app')

// ======================= Event ======================= 

document.addEventListener(
	"deviceready",
	function() {
		// alert('Running cordova-' + cordova.platformId + '@' + cordova.version)
		document.addEventListener("backbutton", eventBackButton, false);
	}
);

function eventBackButton() {
	// window.plugins.ToastPlugin.show_short('再点击一次退出!');
	// 注销返回键
	document.removeEventListener("backbutton", eventBackButton, false)
	// 1.5秒后重新注册
	window.setTimeout(() => {
		document.addEventListener("backbutton", eventBackButton, false)
	}, 1500)
}
