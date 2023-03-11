'use strict'

/** @type {import("./lib/jquery-3.6.0.min.js")} */
const $ = window.jQuery

function blobToBase64(blob, callback) {
	const fileReader = new FileReader();
	fileReader.onload = (e) => {
		callback(e.target.result)
	}
	fileReader.readAsDataURL(blob)
}
// ========================== 消息传递 ==========================

window.addEventListener("message", (event) => {
	if (!event.data || !event.data.event) return
	if (event.data.event === "upload2vr") {
		let file = event.data.blob
		let filename = event.data.filename
		blobToBase64(file, base64 => {
			if (base64.startsWith("data:application/zip;base64,")) {
				chrome.runtime.sendMessage(
					{ event: "upload2vr", filename, base64 },
					(res) => {
						if (res.succ) {
							alert("已同步谱面到一体机上!")
						} else {
							alert("同步失败: " + res.msg)
						}
					}
				)
			} else {
				alert("谱面格式错误, 无法同步到一体机!")
			}
		})
	}
}, false)
