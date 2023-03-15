<template>
	<el-container>
		<el-header class="title">
			闪韵灵镜 谱面同步助手
		</el-header>
		<el-main>
			<div class="ipAddress">
				<el-input v-model="ipInput" :disabled="ipInputState.lock.value" placeholder="VR设备的局域网IP地址">
					<template #prepend>设备IP</template>
					<template #append>
						<el-button :icon="ipInputState.lock.value ? EditPen: Check" @click="onBtnIpClick" />
					</template>
				</el-input>
				<el-alert class="tip" :type="ipInputState.tipType.value" :title="ipInputState.tipMsg.value"
					:closable="false" />
			</div>
			<div class="class-name">任务列表</div>
			<div class="list-box">
				<TransitionGroup name="list-box">
					<div v-for="(item, taskId) in taskInfoSets" :key="item" class="item-box"
						:class="taskStatus[taskId] === TASK_STAUS.SUCCESS ? 'item-box-success': taskStatus[taskId] === TASK_STAUS.FAIL ? 'item-box-failed' : 'item-box-default'">
						<el-container>
							<el-aside>
								<el-image :src="item.image" />
							</el-aside>
							<el-main>
								{{item.name}}
							</el-main>
							<el-aside style="min-width: 16px;margin-right: 10px;">
								<el-icon
									v-show="taskStatus[taskId] !== TASK_STAUS.REMOVE && taskStatus[taskId] !== TASK_STAUS.ADD">
									<Loading v-if="taskStatus[taskId] === TASK_STAUS.POSTING" style="color: blue;" />
									<Check v-else-if="taskStatus[taskId] === TASK_STAUS.SUCCESS"
										style="color: green;" />
									<CloseBold v-else-if="taskStatus[taskId] === TASK_STAUS.FAIL" style="color: red;" />
								</el-icon>
							</el-aside>
						</el-container>
					</div>
				</TransitionGroup>
				<el-empty v-if="Object.keys(taskInfoSets) == 0" description="暂时没有任务" />
			</div>
		</el-main>
	</el-container>

</template>

<script setup>
	import {
		Check,
		EditPen,
		CloseBold,
		Loading,
		Failed
	} from '@element-plus/icons-vue'
	import {
		ElMessage
	} from 'element-plus'
	import axios from 'axios'
	import {
		onMounted,
		onUnmounted,
		ref
	} from 'vue'
	import * as connStateManager from "../utils/ConnStateManager.js"
	import * as taskManager from "../utils/TaskManager.js"
	import {
		TASK_STAUS
	} from "../utils/TaskManager.js"

	// ========================================== 参数设置 ==========================================

	const APP_WEB_SERVER_PORT = 25521 // APP端Web服务器端口

	// ========================================== 生命周期 ==========================================

	onMounted(() => {
		loadIPInfo()
		initTaskManager()
		for (let i = 0; i < 2; i++) {
			window.postMessage({
				event: "add_ciphermap",
				name: "taasdadadasdasdsaasdasdashdiaiudnasidsk_" + i,
				id: "id_" + i,
				image: "https://lf-ciphercdn-cn.picovr.com/obj/lf-game-lf/gdl_app_405727/mapper/cover/backtoyou.png",
				base64: "a"
			})
		}
		connStateManager.onStateChange(onConnStateChange)
	})

	onUnmounted(() => {
		// Do Nothing
	})

	// =========================================== IP地址 ===========================================

	const KEY_LOCAL_DEVICE_IP = "cipher_sync_device_ip"

	const ipInput = ref("") // IP地址输入框
	const ipInputState = {
		lock: ref(false), // 输入框是否锁定
		tipType: ref("warning"), // IP地址提示类型
		tipMsg: ref("") // IP地址提示内容
	}

	/**
	 * 加载缓存的IP地址
	 */
	function loadIPInfo() {
		let ip = localStorage.getItem(KEY_LOCAL_DEVICE_IP)
		if (ip) {
			ipInput.value = ip
			ipInputState.lock.value = true
			startTaskProcess()
		} else {
			stopTaskProcess()
			ipInput.value = ""
			ipInputState.lock.value = false
			setIpAddressTip("info", "使用前请先输入VR端的IP地址~")
		}
	}

	/**
	 * IP地址右边按钮事件
	 */
	function onBtnIpClick() {
		if (!ipInputState.lock.value) {
			// 保存IP并锁定输入
			let ip = ipInput.value
			if (/((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}/.test(ip)) {
				ipInputState.lock.value = true
				localStorage.setItem(KEY_LOCAL_DEVICE_IP, ip)
				startTaskProcess()
			} else {
				setIpAddressTip("warning", "IP地址的格式错啦，仔细检查一下吧！")
			}
		} else {
			// 解锁输入框
			ipInputState.lock.value = false
			stopTaskProcess()
			setIpAddressTip("info", "修改IP地址后，记得点右侧按钮确认哦~")
		}
	}

	let lastConnType = "info"
	/**
	 * 当设备连接状态变更
	 * @param {string} type
	 * @param {string} msg
	 */
	function onConnStateChange(type, msg) {
		setIpAddressTip(type, msg)
		if (type === "success") {
			taskManager.start()
		}
	}

	/**
	 * 设置IP提示栏提示内容
	 * @param {string} type 提示类型
	 * @param {string} msg 提示内容
	 */
	function setIpAddressTip(type, msg) {
		ipInputState.tipType.value = type
		ipInputState.tipMsg.value = msg
	}

	// ========================================== 任务控制 ==========================================

	function startTaskProcess() {
		setIpAddressTip("info", "正在尝试连接VR端...")
		taskManager.setup(getAppUrl(), taskStatusChanged)
		connStateManager.setup(getAppUrl())
		connStateManager.start()
	}

	function stopTaskProcess() {
		connStateManager.stop()
		taskManager.stop()
	}

	// ========================================== 任务控制 ==========================================

	/** @type {{value:{taskId:taskManager.TaskInfo}}} */
	const taskInfoSets = ref({})
	/** @type {{value:{string:boolean}}} */
	const taskStatus = ref({})

	/**
	 * 初始化任务控制
	 */
	function initTaskManager() {
		window.addEventListener("message", (res) => {
			let data = res.data
			if (!data || !data.event) return
			if (data.event === "add_ciphermap") {
				delete data.event
				taskManager.addTask(data)
			}
		})
	}

	/**
	 * 任务状态回调
	 * @param {Object} taskId 任务ID
	 * @param {string} status 任务状态(TASK_STAUS)
	 * @param {taskManager.TaskInfo} 任务信息(taskInfo)
	 */
	function taskStatusChanged(taskId, status, taskInfo) {
		// console.log(status)
		if (status === TASK_STAUS.ADD) {
			taskInfoSets.value[taskId] = taskInfo
			taskStatus.value[taskId] = status
		} else if (status === TASK_STAUS.REMOVE) {
			delete taskInfoSets.value[taskId]
			delete taskStatus.value[taskId]
		} else {
			taskStatus.value[taskId] = status
		}

		if (status === TASK_STAUS.FAIL) {
			ElMessage.error(taskInfo._errMsg)
		}
	}

	// ========================================== 其他 ==========================================
	/**
	 * 获取App端的API链接
	 */
	function getAppUrl(api = "") {
		return "http://" + ipInput.value + ":" + APP_WEB_SERVER_PORT + "/" + api
	}
</script>

<style scoped>
	.el-main {
		padding: 15px;
	}

	.title {
		font-size: 18px;
		font-weight: bold;
		background-color: #409EFF;
		color: white;
		padding: 10px;
		height: auto;
		box-shadow: rgb(0 0 0 / 20%) 0px 2px 4px -1px, rgb(0 0 0 / 14%) 0px 4px 5px 0px, rgb(0 0 0 / 12%) 0px 1px 10px 0px;
	}

	.ipAddress .tip {
		margin-top: 5px;
	}

	.class-name {
		font-weight: bold;
		margin-top: 10px;
	}

	.list-box {
		overflow: hidden;
	}

	.list-box-enter-active {
		animation: list-box-in .6s;
	}

	.list-box-leave-active {
		animation: list-box-out .6s;
	}

	@keyframes list-box-in {
		0% {
			opacity: 0;
		}

		50% {}

		100% {
			opacity: 1;
		}
	}

	@keyframes list-box-out {
		0% {}

		50% {
			transform: translateX(400px);
			margin-bottom: 0;
		}

		100% {
			transform: translateX(400px);
			opacity: 0;
			margin-bottom: -62px;
		}
	}

	.item-box {
		border-radius: 8px;
		border: 1px solid lightblue;
		margin-top: 10px;
		overflow: hidden;
	}

	.item-box-default {
		background-color: #fafafa;
		color: gray;
	}

	.item-box-failed {
		background-color: #ffcecf;
		color: red;
	}

	.item-box-success {
		background-color: #55ff7f;
		color: green;
	}

	.item-box .el-image {
		max-width: 50px;
		margin-bottom: -4px;
	}

	.item-box .el-aside {
		width: auto;
	}

	.item-box .el-main {
		margin: auto;
		padding: 8px 10px;
		font-weight: bold;
		font-size: 16px;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.item-box .el-icon {
		height: 100%;
	}
</style>
