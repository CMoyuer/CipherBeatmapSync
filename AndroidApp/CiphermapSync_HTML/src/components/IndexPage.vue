<template>
	<el-container style="height: 100%;">
		<el-header class="title">Cipher谱面同步助手</el-header>
		<el-main class="main-box">
			<el-button circle @click="btnStartClick" class="btn-start" :disabled="btnStartStatus.disabled"
				:icon="btnStartStatus.icon == 'loading' ? Loading : btnStartStatus.type == 'check' ? Check : SwitchButton"
				:type="btnStartStatus.type" />
			<div class="tip">{{tip}}</div>
		</el-main>
	</el-container>
</template>

<script setup>
	import {
		onMounted,
		onUnmounted,
		ref
	} from 'vue'
	import {
		Check,
		SwitchButton,
		Loading,
	} from '@element-plus/icons-vue'

	// =============================== Par ===============================

	// ============================== Event ==============================

	onMounted(() => {

	})

	// ============================ Permission ============================

	/**
	 * 检查必备权限
	 */
	function checkPermission() {
		return new Promise((resolve, reject) => {
			let permissions = cordova.plugins.permissions
			permissions.checkPermission(permissions.WRITE_EXTERNAL_STORAGE, status => {
				if (status.hasPermission) {
					resolve()
				} else {
					permissions.requestPermission(permissions.WRITE_EXTERNAL_STORAGE, status => {
						if (status.hasPermission) resolve()
					}, reject)
				}
			}, () => {
				permissions.requestPermission(permissions.WRITE_EXTERNAL_STORAGE, status => {
					if (status.hasPermission) resolve()
				}, reject)
			})
		})
	}

	// =============================== Event ===============================

	const btnStartStatus = ref({
		disabled: false,
		type: "primary",
		icon: "switch",
	})
	const tip = ref("服务未启动")

	let isRunning = false

	function btnStartClick(event) {
		event.target.blur()
		if (event.target.nodeName === 'SPAN') {
			event.target.parentNode.blur()
		}
		if (!isRunning) {
			ciphermap_sync_helper.bindCallback(webStatusChanged)
			checkPermission().then(() => {
				isRunning = true
				btnStartStatus.value.type = "primary"
				btnStartStatus.value.disabled = true
				btnStartStatus.value.icon = "loading"
				tip.value = "正在启动"
				ciphermap_sync_helper.start(null, err => {
					isRunning = false
					console.error(err)
					btnStartStatus.value.type = "danger"
					btnStartStatus.value.disabled = false
					btnStartStatus.value.icon = "SwitchButton"
					tip.value = "启动服务时发生错误"
				})
			})
		} else {
			btnStartStatus.value.type = "primary"
			btnStartStatus.value.disabled = true
			btnStartStatus.value.icon = "loading"
			tip.value = "正在关闭"
			ciphermap_sync_helper.stop()
		}
	}

	// =============================== Callback ===============================

	function webStatusChanged(res) {
		if (!res || !res.event) return
		if (res.event === "server_started") {
			// 服务器已启动
			isRunning = true
			btnStartStatus.value.type = "success"
			btnStartStatus.value.disabled = false
			btnStartStatus.value.icon = "check"
			tip.value = "服务已启动, 本机IP: 获取中"
			networkinterface.getWiFiIPAddress(address => {
				tip.value = "服务已启动, 本机IP: " + address.ip
			}, err => {
				console.error(err)
				tip.value = "服务已启动, 本机IP: 获取失败"
			})
		} else if (res.event === "server_stopped") {
			// 服务器已停止
			isRunning = false
			btnStartStatus.value.type = "primary"
			btnStartStatus.value.disabled = false
			btnStartStatus.value.icon = "SwitchButton"
			tip.value = "服务已停止"
		} else if (res.event === "server_exception") {
			// 服务器出现异常
			tip.value = "服务发生错误: " + res.message
		}
	}
</script>

<style scoped>
	.title {
		font-size: 18px;
		font-weight: bold;
		color: white;
		padding: 10px;
		height: auto;
		background-image: -webkit-linear-gradient(top, #47a9f3, #2196f3);
		box-shadow: rgb(0 0 0 / 20%) 0px 2px 4px -1px, rgb(0 0 0 / 14%) 0px 4px 5px 0px, rgb(0 0 0 / 12%) 0px 1px 10px 0px;
	}

	.main-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.btn-start {
		width: 40vh;
		height: 40vh;
		font-size: 16vh;

		box-shadow: #00000040 1px 1px 6px 2px;
	}

	.tip {
		margin-top: 20px;
		font-weight: bold;
		font-size: 18px;
		color: #888;
	}
</style>
