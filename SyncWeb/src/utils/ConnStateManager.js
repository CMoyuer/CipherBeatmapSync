import axios from 'axios'
const AGREEMENT_VERSION = "2.0.0" // 协议版本号，如果与VR端对应不上，则无法使用
const CONN_STATE_INTERVAL_TIME = 5 * 1000 // 每次判断VR端连通状态的间隔时间

/** @name 状态回调列表 @type [Function] */
let callbackList = []
let timerHandle = 0 // 定时器ID
let isRunning = false // 是否正在运行
let api_url = "" // VR设备API地址

// =============================== Public ===============================

/**
 * 设置参数
 * @param {string} url 设备Api地址
 */
export function setup(url) {
	api_url = url
}

/**
 * 开始连接状态监听
 */
export function start() {
	if (timerHandle > 0) return
	isRunning = true
	timerHandle = setInterval(updateConnectState, CONN_STATE_INTERVAL_TIME)
	updateConnectState()
}

/**
 * 停止连接状态监听
 */
export function stop() {
	if (timerHandle <= 0) return
	clearInterval(timerHandle)
	timerHandle = 0
	isRunning = false
}

/**
 * 打开状态变更回调
 * @param {Function} callback
 */
export function onStateChange(callback) {
	if (typeof callback !== "function") return
	if (callbackList.indexOf(callback) >= 0) return
	callbackList.push(callback)
}

/**
 * 关闭状态变更回调
 * @param {Function} callback
 */
export function offStateChange(callback) {
	if (typeof callback !== "function") {
		callbackList = []
	} else {
		let index = callbackList.indexOf(callback)
		if (index < 0) return
		callbackList.splice(index, 1)
	}
}

// =============================== Private ===============================

/**
 * 获取连接状态
 */
function updateConnectState() {
	axios.get(api_url + "version?t=" + new Date().getTime(), {
		timeout: 3000,
		headers: {
			"Access-Control-Allow-Origin": "*",
			// "Access-Control-Allow-Credentials": true
		}
	}).then(res => {
		if (!isRunning) return
		if (res.data && res.data.code >= 200 && res.data.code < 300) {
			if (res.data.data.agreement === AGREEMENT_VERSION) {
				onStateUpdate("success", "成功连接到VR端!")
				return
			}
		}
		onStateUpdate("error", "插件与APP版本不一致, 请更新插件和APP!")
	}).catch(err => {
		if (!isRunning) return
		console.error("连接VR端失败", err)
		onStateUpdate("error", "连接VR端失败，请检查IP地址和APP是否正常!")
	})
}

/**
 * 当状态变更时触发
 * @param {string} status 状态
 * @param {string} msg 提示信息
 */
function onStateUpdate(status, msg) {
	for (let index in callbackList) {
		try {
			callbackList[index](status, msg)
		} catch (err) {
			console.error(err)
		}
	}
}
