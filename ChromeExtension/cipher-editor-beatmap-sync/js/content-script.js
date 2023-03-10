'use strict'

/** @type {import("./lib/jquery-3.6.0.min.js")} */
const $ = window.jQuery

window.addEventListener("message", (event) => {
	if (!event.data || !event.data.event) return
	if (event.data.event !== "upload2vr") return
	// 拿到压缩包信息
	let zipBlob = event.data.blob
	let fileName = event.data.filename
	console.log("upload2vr", fileName, zipBlob)
	uploadBeatmapToVR(zipBlob, fileName)
}, false);

/**
 * 同步方式发起请求
 * @param {object} par 
 * @returns 
 */
function ajax(par) {
	return new Promise((resolve, reject) => {
		par.success = resolve
		par.error = (req, status, err) => {
			reject(err)
		}
		$.ajax(par)
	})
}

/**
 * 
 * @param {Blob} zipBlob 曲谱ZIP
 * @param {string} fileName 文件名称
 */
async function uploadBeatmapToVR(zipBlob, fileName) {
	// 获取VR一体机的局域网IP地址
	let lan_info = await ajax({
		url: "https://api.moyuer.cc/lan_ddns",
		type: "get",
		timeout: 5000,
	})
	if (lan_info.code != 200)
		throw ("找不到VR设备的IP地址!")
	let lan_ip = lan_info.data.lan_ip
	// 上传文件到VR一体机
	let upload_result = await ajax({
		url: "https://" + lan_ip + ":25521/upload_beatmap",
		type: "post",
		body: {
			file: zipBlob,
			name: fileName
		},
		timeout: 5000,
	})
	console.log(upload_result)
}