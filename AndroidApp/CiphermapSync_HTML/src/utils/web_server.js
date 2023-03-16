import * as cipher from "./cipher_helper.js"

const WEB_SERVER_PORT = 25521
const AGREEMENT_VERSION = "2.0.0"

/** @name 请求信息 @typedef REQUESTI_INFO @type {{requestId:string, method:string, path: string, headers:string, body:string}} */

// ========================= Public =========================

/**
 * 初始化
 */
export function init() {

}

/**
 * 启动服务器
 * @param {Function} succ
 * @param {Function} fail
 */
export function start(succ, fail) {
	webserver.onRequest(onWebRequest)
	webserver.start(succ, fail, WEB_SERVER_PORT)
}

/**
 * 停止服务器
 * @param {Function} succ
 * @param {Function} fail
 */
export function stop() {
	webserver.stop()
}

// ========================= Response =========================

/**
 * 发送成功回调(JSON)
 * @param {string} requestId
 * @param {Object} data
 */
function sendSuccessResponseJSON(requestId, data) {
	let response = {
		code: 200,
		data,
		message: "success"
	}
	webserver.sendResponse(requestId, {
		status: 200,
		body: JSON.stringify(response),
		headers: {
			'Content-Type': "application/json;charset=utf-8;"
		}
	})
}

/**
 * 发送成功回调(空内容)
 * @param {string} requestId
 */
function sendSuccessResponseEmpty(requestId) {
	webserver.sendResponse(requestId, {
		status: 200,
		body: "",
		headers: {
			'Content-Type': "text/html;",
			'Access-Control-Allow-Origin': '*',
			'Access-Control-Allow-Headers': '*'
		}
	})
}

/**
 * 发送错误回调
 * @param {string} requestId
 * @param {number} errCode
 */
function sendErrorResponse(requestId, errCode) {
	webserver.sendResponse(requestId, {
		status: errCode,
		body: "",
		headers: {
			'Content-Type': "text/html;"
		}
	})
}

// ========================= Request =========================

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 */
function onWebRequest(req) {
	console.log(req)
	try {
		if (req.method === "GET") {
			if (onGetRequest(req)) return
		} else if (req.method === "POST") {
			if (onPostRequest(req)) return
		} else if (req.method === "OPTIONS") {
			if (onOptionsRequest(req)) return
		}
		sendErrorResponse(req.requestId, 404)
	} catch (e) {
		sendErrorResponse(req.requestId, 500)
		console.error(e)
	}
}

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onGetRequest(req) {
	if (req.path === "/version") {
		sendSuccessResponseJSON(req.requestId, {
			agreement: AGREEMENT_VERSION
		})
	} else {
		return false
	}
	return true
}

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onPostRequest(req) {
	if (req.path === "/upload_beatmap") {
		let info = JSON.parse(req.body)
		if (!info.base64 || !info.name) {
			sendErrorResponse(req.requestId, 400)
		} else {
			cipher.addBeatmap(info).then(() => {
				sendSuccessResponseJSON(req.requestId, {})
			}).catch(err => {
				console.error(err)
				sendErrorResponse(req.requestId, 500)
			})
		}
	} else {
		return false
	}
	return true
}

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onOptionsRequest(req) {
	sendSuccessResponseEmpty(req.requestId)
	return true
}
