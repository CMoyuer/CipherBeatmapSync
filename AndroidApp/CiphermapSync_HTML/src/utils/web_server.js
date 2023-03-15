const WEB_SERVER_PORT = 25521
const AGREEMENT_VERSION = "2.0.0"

/** @name 请求信息 @typedef REQUESTI_INFO @type {{requestId:string, method:string, path: string, headers:string, body:string}} */

// ========================= Public =========================

/**
 * 初始化
 */
export function init() {
	webserver.onRequest(onWebRequest)
}

/**
 * 启动服务器
 * @param {Function} succ
 * @param {Function} fail
 */
export function start(succ, fail) {
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

// ========================= Private =========================

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 */
function onWebRequest(req) {
	console.log(req)

	if (req.method === "GET") {
		if (onGetRequest(req)) return
	} else if (req.method === "POST") {
		if (onPostRequest(req)) return
	} else if (req.method === "OPTIONS") {
		if (onOptionsRequest(req)) return
	}

	sendErrorResponse404()
}

// ========================= HTTP =========================

/**
 * Web请求回调
 * @param {REQUESTI_INFO} req
 * @return {boolean}
 */
function onGetRequest(req) {
	if (req.path === "/version") {
		sendSuccessResponseJSON(req.requestId, {
			agreement: config.AGREEMENT_VERSION
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
	return false
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
			'Access-Control-Allow-Origin': '*'
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
		headers: {
			'Access-Control-Allow-Origin': '*'
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
		headers: {
			'Access-Control-Allow-Origin': '*'
		}
	})
}
