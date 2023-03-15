import {
	v4 as uuidv4
} from 'uuid'

import {
	blobToBase64
} from "./utils.js"

/**
 * 获取曲谱的基本信息
 * @param {File} file
 */
export async function getBaseInfo(file) {
	let zip = await JSZip.loadAsync(file)
	let image = ""
	let mapInfo = ""
	for (let fileName in zip.files) {
		if (fileName.indexOf('/') >= 0) continue
		if (fileName.endsWith(".png")) {
			image = await blobToBase64(await zip.file(fileName).async("blob"))
		} else if (fileName.endsWith(".yaml")) {
			mapInfo = await zip.file(fileName).async("string")
		}
	}

	let name = mapInfo.match(/songName:\s\"(.*)\"/)[1]
	let base64 = await blobToBase64(file)

	return {
		id: uuidv4(),
		name,
		image,
		base64
	}
}
