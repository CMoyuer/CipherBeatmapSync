export function base64toBlob(base64, filename) {
	let arr = base64.split(','),
		mime = arr[0].match(/:(.*?);/)[1],
		bstr = atob(arr[1]),
		n = bstr.length,
		u8arr = new Uint8Array(n);
	while (n--) {
		u8arr[n] = bstr.charCodeAt(n)
	}
	// return new File([u8arr], filename, {
	// 	type: mime
	// })
	return new Blob([u8arr], {
		type: mime
	})
}

/**
 * 创建目录
 * @param {Object} dirEntry
 * @param {string} dir
 */
function createDirectory(dirEntry, dir) {
	return new Promise((resolve, reject) => {
		if (dir.startsWith("/")) dir = dir.substring(1)
		let index = dir.indexOf("/")
		if (index < 0 && !dir) {
			resolve(dirEntry)
			return
		}
		let nDir = ""
		if (index < 0) {
			nDir = dir
		} else {
			nDir = dir.substring(0, index)
		}
		dirEntry.getDirectory(nDir, {
			create: true
		}, function(dirEntry) {
			if (index == -1) {
				resolve(dirEntry)
			} else {
				createDirectory(dirEntry, dir.substring(index + 1))
					.then(dirEntry => resolve(dirEntry))
					.catch(reject)
			}
		}, reject)
		return true
	})
}

/**
 * 保存文件到路径下
 * @param {Blob} blob
 * @param {string} dir
 * @param {string} fileName
 */
export function saveBlobToPath(blob, rootDir, dir, fileName) {
	return new Promise((resolve, reject) => {
		window.resolveLocalFileSystemURL(rootDir, dirEntry => {
			createDirectory(dirEntry, dir).then(dirEntry => {
				dirEntry.getFile(fileName, {
					create: true,
					exclusive: false
				}, function(fileEntry) {
					fileEntry.createWriter(function(fileWriter) {
						fileWriter.onwriteend = resolve
						fileWriter.onerror = reject
						fileWriter.write(blob)
					});
				}, reject)
			}, reject)
		}, reject)
		return true
	})
}
