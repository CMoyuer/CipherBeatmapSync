import * as utils from "./utils.js"
import * as fileManager from "./file_manager.js"
/**
 * 添加谱面到Cipher
 * @param {Object} info
 * @param {string} info.name
 * @param {string} info.base64
 */
export async function addBeatmap(info) {
	// 保存zip
	let zipFileName = new Date().getTime() + "-" + parseInt(Math.random() * 1000) + ".zip"
	let zipBlob = utils.base64toBlob(info.base64)
	await fileManager.saveBlobFile(zipBlob, cordova.file.externalDataDirectory, "", zipFileName)
	try {
		// 解压
		// let rootDir = cordova.file.externalDataDirectory
		// let tarDir = info.name
		let rootDir = cordova.file.externalRootDirectory
		let tarDir = "Android/data/com.bytedance.cipher/files/Ciphermap/CustomLevels/" + info.name
		await fileManager.removeDirectory(rootDir, tarDir)
		await fileManager.unzip(cordova.file.externalDataDirectory + zipFileName,
			rootDir + tarDir)
	} catch (e) {
		throw e
	} finally {
		// 删除缓存
		try {
			await fileManager.removeFile(cordova.file.externalDataDirectory, "", zipFileName)
		} catch (e) {}
	}
}
