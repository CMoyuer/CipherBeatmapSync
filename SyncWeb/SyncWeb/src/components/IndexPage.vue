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
					<div v-for="(item,index) in ciphermapList" :key="item" class="item-box">
						<el-container>
							<el-aside>
								<el-image :src="item.image">
								</el-image>
							</el-aside>
							<el-main>
								{{item.name}}
							</el-main>
							<el-aside @click="removeTask(index)">
								<el-icon>
									<CloseBold v-if="item.state === 'idle'" />
									<Loading v-if="item.state === 'posting'" />
								</el-icon>
							</el-aside>
						</el-container>
					</div>
				</TransitionGroup>
				<el-empty v-if="ciphermapList.length == 0" description="暂时没有任务" />
			</div>
		</el-main>
	</el-container>

</template>

<script setup>
	import {
		Check,
		EditPen,
		CloseBold,
		Loading
	} from '@element-plus/icons-vue'
	import {
		onMounted,
		onUnmounted,
		ref
	} from 'vue'
	// ========================================== 参数设置 ==========================================

	const TASK_INTERVAL_TIME = 1500 // 每次任务间隔时间

	// ========================================== 生命周期 ==========================================

	onMounted(() => {
		loadIPInfo()
		initTaskManager()
		for (let i = 0; i < 10; i++) {
			window.postMessage({
				event: "add_ciphermap",
				name: "taasdadadasdasdsaasdasdashdiaiudnasidsk_" + i,
				id: "id_" + i,
				image: "https://lf-ciphercdn-cn.picovr.com/obj/lf-game-lf/gdl_app_405727/mapper/cover/backtoyou.png",
				file: "a"
			})
		}
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
			startTaskTimer()
		} else {
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
				startTaskTimer()
			} else {
				setIpAddressTip("warning", "IP地址的格式错啦，仔细检查一下吧！")
			}
		} else {
			// 解锁输入框
			ipInputState.lock.value = false
			stopTaskTimer()
			setIpAddressTip("info", "修改IP地址后，记得点右侧按钮确认哦~")
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

	/** @type {{value:[{id:string,name:string,image:string,file:Blob,state:string}]}} */
	const ciphermapList = ref([]) // 待传输的谱面列表
	/**
	 * 初始化任务控制
	 */
	function initTaskManager() {
		window.addEventListener("message", (res) => {
			let data = res.data
			if (!data || !data.event) return
			if (data.event === "add_ciphermap" && data.id && data.file) {
				ciphermapList.value.push({
					id: data.id,
					name: data.name,
					image: data.image,
					file: data.file,
					state: "idle",
				})
			}
		})
	}

	/**
	 * @param {Object} index
	 */
	function removeTask(index) {
		ciphermapList.value.splice(index, 1)
	}


	// ========================================== 定时任务 ==========================================

	let taskTimerHandle = 0 // 定时器句柄
	let taskLock = false // 任务锁

	/**
	 * 启动任务定时器
	 */
	function startTaskTimer() {
		if (taskTimerHandle > 0) return
		setIpAddressTip("info", "正在尝试连接VR端...")
		taskTimerHandle = setInterval(taskProcess, TASK_INTERVAL_TIME)
	}

	/**
	 * 停止任务定时器
	 */
	function stopTaskTimer() {
		if (taskTimerHandle <= 0) return
		clearInterval(taskTimerHandle)
		taskTimerHandle = 0
	}

	/**
	 * 处理任务
	 */
	function taskProcess() {
		// TODO
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
		background-color: #fafafa;
		margin-top: 10px;
		overflow: hidden;
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
		color: #666;
		font-weight: bold;
		font-size: 16px;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.item-box .el-icon {
		height: 100%;
		margin-right: 10px;
		color: red;
	}
</style>
