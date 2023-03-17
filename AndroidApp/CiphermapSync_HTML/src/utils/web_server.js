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
			'Content-Type': "application/json;charset=utf-8;",
			'Access-Control-Allow-Origin': '*',
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
			'Access-Control-Allow-Headers': '*',
		}
	})
}

/**
 * 发送错误回调
 * @param {string} requestId
 * @param {number} errCode
 * @param {string} errMsg
 */
function sendErrorResponse(requestId, errCode, errMsg) {
	let response = {
		code: errCode,
		data: {},
		message: errMsg
	}
	webserver.sendResponse(requestId, {
		status: 200,
		body: JSON.stringify(response),
		headers: {
			'Content-Type': "application/json;charset=utf-8;",
			'Access-Control-Allow-Origin': '*',
		}
	})
}

/**
 * 发送404回调
 * @param {string} requestId
 */
function sendErrorResponse404(requestId) {
	webserver.sendResponse(requestId, {
		status: 404,
		body: "",
		headers: {
			'Content-Type': "text/html;",
			'Access-Control-Allow-Origin': '*',
		}
	})
}

// ========================= Request =========================

/**
 * 处理Web请求
 * @param {REQUESTI_INFO} req
 */
function onWebRequest(req) {
	try {
		if (req.method === "GET") {
			if (onGetRequest(req)) return
		} else if (req.method === "POST") {
			if (onPostRequest(req)) return
		} else if (req.method === "OPTIONS") {
			if (onOptionsRequest(req)) return
		}
		sendErrorResponse404(req.requestId)
	} catch (e) {
		sendErrorResponse(req.requestId, 500, e)
		console.error(e)
	}
}

/**
 * 处理Get请求
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
 * 处理Post请求
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onPostRequest(req) {
	if (req.path === "/upload_beatmap") {
		let info = JSON.parse(req.body)
		if (!info.base64 || !info.name) {
			sendErrorResponse(req.requestId, 400, "parameter error")
		} else {
			cipher.addBeatmap(info).then(() => {
				sendSuccessResponseJSON(req.requestId, {})
			}).catch(err => {
				console.error(err)
				sendErrorResponse(req.requestId, 500, err)
			})
		}
	} else {
		return false
	}
	return true
}

/**
 * 处理Options请求
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onOptionsRequest(req) {
	sendSuccessResponseEmpty(req.requestId)
	return true
}
