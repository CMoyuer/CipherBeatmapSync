import axios from 'axios'
import {
	v4 as uuidv4
} from 'uuid'

import * as config from "../config/config.js"

/** @name 任务信息 @typedef TaskInfo  @type {{_taskId:string|undefined, _status:number|undefined, _errMsg:string|undefined, id:string, name:string, base64:string, image:string}} */

export const TASK_STAUS = {
	ADD: 0,
	POSTING: 1,
	SUCCESS: 2,
	FAIL: 3,
	REMOVE: 4
}

// 域名链接
let api_url = "http://127.0.0.1/"
/** @name 任务ID列表 @type [string] */
let taskIds = []
/** @name 任务集合 @type {{taskId:TaskInfo}} */
let taskSets = {}
/** @name 状态回调 @type {(taskId:string, status:number, taskInfo:TaskInfo)=>{}} */
let statusCallback = (taskId, status, taskInfo) => {}
// 任务处理定时器ID
let taskProcessHandle = 0
// =============================== Public ===============================

/**
 * 设置设备Api地址
 * @param {string} url 设备Api地址
 */
export function setApiUrl(url) {
	api_url = url
}

/**
 * 设置状态变化回调
 * @param {Function|undefined} onStatusChanged 设备Api地址
 */
export function setOnStatusChanged(onStatusChanged) {
	statusCallback = typeof(onStatusChanged) == "function" ? onStatusChanged : () => {}
}


/**
 * 开始处理任务
 */
export function start() {
	if (taskProcessHandle > 0) return
	processTaskQueue()
	taskProcessHandle = setInterval(processTaskQueue, config.TASK_INTERVAL_TIME)
}

/**
 * 停止处理任务
 */
export function stop() {
	if (taskProcessHandle <= 0) return
	clearInterval(taskProcessHandle)
	taskProcessHandle = 0
}

/**
 * 添加任务
 * @param {TaskInfo} taskInfo 任务信息
 */
export function addTask(taskInfo) {
	let taskId = uuidv4()
	taskInfo._uuid = taskId
	taskSets[taskId] = taskInfo
	taskIds.push(taskId)
	changeTaskStatus(taskId, TASK_STAUS.ADD)
	return taskId
}

// =============================== Private ===============================

let taskLock = false

function processTaskQueue() {
	if (taskLock || taskIds.length == 0) return
	let taskId = taskIds[0]
	/** @type {TaskInfo} */
	let taskInfo = taskSets[taskId]
	if (taskInfo._status == TASK_STAUS.ADD || taskInfo._status == TASK_STAUS.FAIL) {
		taskLock = true
		changeTaskStatus(taskId, TASK_STAUS.POSTING)
		postFile(taskId).then(() => {
			changeTaskStatus(taskId, TASK_STAUS.SUCCESS)
			setTimeout(() => {
				changeTaskStatus(taskId, TASK_STAUS.REMOVE)
				delete taskSets[taskId]
				let index = taskIds[taskId]
				taskIds.splice(index, 1)
				taskLock = false
			}, 500)
		}).catch((err) => {
			console.error("Post file failed:", err)
			taskInfo._errMsg = err
			changeTaskStatus(taskId, TASK_STAUS.FAIL)
			setTimeout(() => {
				taskLock = false
			}, 3000)
		})
	}
}

/**
 * 上传文件
 * @param {string} taskId
 */
async function postFile(taskId) {
	let taskInfo = taskSets[taskId]
	let body = {
		name: taskInfo.name,
		base64: taskInfo.base64
	}
	let res = await axios.post(api_url + "upload_beatmap", body, {
		timeout: 10 * 1000,
		headers: {
			"Content-Type": "application/json;charset=utf-8;"
		}
	})
	if (res.data.code < 200 || res.data.code >= 300)
		throw res.data.message
}

/**
 * @param {string} taskId
 * @param {number} status 
 */
function changeTaskStatus(taskId, status) {
	if (!taskSets.hasOwnProperty(taskId)) return
	taskSets[taskId]._status = status
	statusCallback(taskId, status, taskSets[taskId])
}
