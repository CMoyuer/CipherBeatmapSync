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

window.addEventListener("message", (event) => {
	if (!event.data || !event.data.event) return
	if (event.data.event !== "upload2vr") return
	let filename = event.data.filename
	let zipFile = new File([event.data.blob], filename, { type: "application/zip" })
	blobToBase64(zipFile, base64 => {
		chrome.runtime.sendMessage({ event: "upload2vr", filename, base64 })
	})
}, false)
