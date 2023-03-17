<template>
	<el-container>
		<el-header class="title">
			闪韵灵镜 谱面同步助手
		</el-header>
		<el-main>
			<div v-show="dropTip" class="drop-tip">
				<el-icon>
					<upload-filled />
				</el-icon>
				<div class="tip">松开以添加到同步任务中</div>
			</div>
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
			<el-scrollbar class="list-box">
				<TransitionGroup name="list-box">
					<div v-for="(item, taskId) in taskInfoSets" :key="item" class="item-box"
						:class="taskStatus[taskId] === TASK_STAUS.SUCCESS ? 'item-box-success': taskStatus[taskId] === TASK_STAUS.FAIL ? 'item-box-failed' : 'item-box-default'">
						<el-container>
							<el-aside>
								<el-image :src="item.image" />
							</el-aside>
							<el-main class="song-name">
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
				<el-empty v-if="Object.keys(taskInfoSets) == 0" description="暂时没有任务">
					<div class="el-empty__description" style="height: auto;margin-top:0;">
						<p>可拖拽谱面压缩包文件到这里进行上传</p>
					</div>
				</el-empty>
			</el-scrollbar>
		</el-main>
	</el-container>

</template>

<script setup>
	import {
		Check,
		EditPen,
		CloseBold,
		Loading,
		Failed,
		UploadFilled
	} from '@element-plus/icons-vue'
	import {
		ElMessage
	} from 'element-plus'
	import axios from 'axios'
	import {
		onMounted,
		onBeforeUnmount,
		ref
	} from 'vue'
	import * as connStateManager from "../utils/ConnStateManager.js"
	import * as taskManager from "../utils/TaskManager.js"
	import * as cipher from "../utils/cipher_helper.js"
	import * as config from "../config/config.js"
	import {
		TASK_STAUS
	} from "../utils/TaskManager.js"

	// ========================================== 生命周期 ==========================================

	onMounted(() => {
		loadIPInfo()
		initTaskManager()
		connStateManager.onStateChange(onConnStateChange)
		taskManager.setOnStatusChanged(taskStatusChanged)
		initDragEvent()
		if (window.opener) {
			window.opener.postMessage({
				event: "syncweb-alive"
			}, "*")
		}
	})

	onBeforeUnmount(() => {

	})

	// =========================================== 拖拽添加文件 ===========================================
	let dropTip = ref(false)

	function initDragEvent() {
		// 禁用默认拖拽事件
		let doc = document.documentElement
		doc.addEventListener('drop', e => e.preventDefault())
		doc.addEventListener('dragleave', e => e.preventDefault())
		doc.addEventListener('dragenter', e => e.preventDefault())
		doc.addEventListener('dragover', e => e.preventDefault())
		// 拖拽事件
		let ele = document.querySelector('html')
		let lastenter = null
		ele.addEventListener('dragenter', (e) => {
			lastenter = e.target
			ele.classList.add('drop-area')
			dropTip.value = true
		})
		ele.addEventListener('dragleave', function(e) {
			if (lastenter != e.target) return
			ele.classList.remove('drop-area')
			dropTip.value = false
		})
		ele.addEventListener('drop', (e) => {
			ele.classList.remove('drop-area')
			dropTip.value = false
			let files = e.dataTransfer.files
			for (let i = 0; i < files.length; i++) {
				let file = files[i]
				if (!file.name.endsWith(".zip")) {
					ElMessage.error('只支持zip格式谱面')
					continue
				}
				cipher.getBaseInfo(files[i]).then(res => {
					res.event = "add_ciphermap"
					window.postMessage(res)
				}).catch(err => {
					console.error(err)
					console.warn(file.name, err)
					ElMessage.error('导入失败啦, \r\n看看控制台怎么回事吧')
				})
			}
		})
	}

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
		}else if(type === "error"){
			taskManager.stop()
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
			} else {
				console.log(res)
			}
		})
	}

	function startTaskProcess() {
		setIpAddressTip("info", "正在尝试连接VR端...")
		taskManager.setApiUrl(getAppUrl())
		connStateManager.setup(getAppUrl())
		connStateManager.start()
	}

	function stopTaskProcess() {
		connStateManager.stop()
		taskManager.stop()
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
		return "http://" + ipInput.value + ":" + config.APP_WEB_SERVER_PORT + "/" + api
	}
</script>

<style scoped>
	.el-main {
		padding: 15px;
	}

	.drop-area * {
		pointer-events: none;
	}

	.drop-tip {
		background-color: #000000A0;
		position: fixed;
		left: 0px;
		top: 0px;
		width: 100%;
		height: 100%;
		z-index: 1000;
		color: white;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.drop-tip .el-icon {
		font-size: 120px;
	}

	.title {
		font-size: 18px;
		font-weight: bold;
		color: white;
		padding: 10px;
		height: auto;
		background-image: -webkit-linear-gradient(top, #47a9f3, #2196f3);
		box-shadow: rgb(0 0 0 / 20%) 0px 2px 4px -1px, rgb(0 0 0 / 14%) 0px 4px 5px 0px, rgb(0 0 0 / 12%) 0px 1px 10px 0px;
	}

	.ipAddress .tip {
		margin-top: 5px;
	}

	.class-name {
		font-weight: bold;
		margin: 10px 0px;
	}

	.list-box {
		overflow: hidden;
		height: calc(100vh - 190px);
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
			margin-bottom: 10px;
		}

		100% {
			transform: translateX(400px);
			opacity: 0;
			margin-bottom: -52px;
		}
	}

	.item-box {
		border-radius: 8px;
		margin-bottom: 10px;
		overflow: hidden;
	}

	.item-box-default {
		background-color: #fafafa;
		border: 1px solid lightblue;
		color: gray;
	}

	.item-box-failed {
		background-color: var(--el-color-error-light-9);
		color: var(--el-color-error);
		border: 1px solid var(--el-color-error);
	}

	.item-box-success {
		background-color: var(--el-color-success-light-9);
		color: var(--el-color-success);
		border: 1px solid var(--el-color-success);
	}

	.item-box .el-image {
		max-width: 50px;
		margin-bottom: -4px;
	}

	.item-box .song-name {
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		font-size: 14px;
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
