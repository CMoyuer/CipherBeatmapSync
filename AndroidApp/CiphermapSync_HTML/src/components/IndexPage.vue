<template>
	<el-container style="height: 100%;">
		<el-header class="title">Cipher谱面同步助手</el-header>
		<el-main>
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
	import * as webServer from '../utils/web_server.js'

	// =============================== Par ===============================

	// =============================== Event ===============================

	onMounted(() => {
		webServer.init()
	})

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
			isRunning = true
			btnStartStatus.value.type = "primary"
			btnStartStatus.value.disabled = true
			btnStartStatus.value.icon = "loading"
			tip.value = "正在启动"
			webServer.start(() => {
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
			}, err => {
				isRunning = false
				console.error(err)
				alert(JSON.stringify(err))
				btnStartStatus.value.type = "danger"
				btnStartStatus.value.disabled = false
				btnStartStatus.value.icon = "SwitchButton"
				tip.value = "启动服务时发生错误"
			}, 25521)
		} else {
			isRunning = false
			webServer.stop()
			btnStartStatus.value.type = "primary"
			btnStartStatus.value.disabled = false
			btnStartStatus.value.icon = "SwitchButton"
			tip.value = "服务已停止"
		}
	}
</script>

<style scoped>
	.title {
		background-color: #66b1ff;
		font-size: 18px;
		font-weight: bold;
		height: auto;
		color: white;
		padding: 10px;
	}

	.btn-start {
		margin-top: 80px;
		width: 40vw;
		height: 40vw;
		font-size: 16vw;
	}

	.tip {
		margin-top: 20px;
		font-weight: bold;
		font-size: 18px;
		color: #888;
	}
</style>
