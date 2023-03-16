import * as utils from "./utils.js"
/**
 * 添加谱面到Cipher
 * @param {Object} info
 * @param {string} info.name
 * @param {string} info.base64
 */
export async function addBeatmap(info) {
	// 保存zip
	let blob = utils.base64toBlob(info.base64, info.name + ".zip")
	await utils.saveBlobToPath(blob, cordova.file.externalDataDirectory, "", "temp.zip")
	// 解压
}
