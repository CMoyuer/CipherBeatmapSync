// =========================== 压缩包 ===========================

/**
 * 解压文件到目标文件夹
 * @param {string} srcPath
 * @param {string} tarDir
 */
export function unzip(srcPath, tarDir) {
	return new Promise((resolve, reject) => {
		zip.unzip(srcPath, tarDir, (result) => {
			if (result === 0) {
				resolve()
			} else {
				reject("Unzip failed!")
			}
		})
		return true
	})
}

// =========================== 文件操作 ===========================

/**
 * 保存Blob资源为文件
 * @param {Blob} blob
 * @param {string} rootDir
 * @param {string} dir
 * @param {string} fileName
 */
export async function saveBlobFile(blob, rootDir, dir, fileName) {
	let dirEntry = await _getDirEntry(rootDir)
	// 创建父级目录
	let dirList = dir.split("/")
	for (let i = 0; i < dirList.length; i++) {
		let path = dirList[i]
		if (!path) continue
		dirEntry = await _getDirectory(dirEntry, path, {
			create: true
		})
	}
	// 创建文件
	let fileEntry = await _getFile(dirEntry, fileName, {
		create: true,
		exclusive: false
	})
	await _writeFile(fileEntry, blob)
}

/**
 * 删除指定文件
 * @param {string} rootDir
 * @param {string} dir
 * @param {string} fileName
 */
export async function removeFile(rootDir, dir, fileName) {
	try {
		let dirEntry = await _getDirEntry(rootDir)
		dirEntry = await _getDirectory(dirEntry, dir, {})
		let fileEntry = await _getFile(dirEntry, fileName, {})
		await _deleteFile(fileEntry)
	} catch (e) {
		if (e.code === 1) return
		throw e
	}
}

/**
 * 删除指定文件夹
 * @param {string} rootDir
 * @param {string} dir
 */
export async function removeDirectory(rootDir, dir) {
	try {
		let dirEntry = await _getDirEntry(rootDir)
		dirEntry = await _getDirectory(dirEntry, dir, {})
		await _deleteDirectory(dirEntry)
	} catch (e) {
		if (e.code === 1) return
		throw e
	}
}

// =========================== Async Function ===========================

/**
 * 获取DirEntry
 * @param {string} rootDir
 */
function _getDirEntry(rootDir) {
	return new Promise((resolve, reject) => {
		window.resolveLocalFileSystemURL(rootDir, resolve, reject)
		return true
	})
}

/**
 * 获取目录
 * @param {Object} dirEntry
 * @param {string} name
 * @param {Object} options
 */
function _getDirectory(dirEntry, name, options) {
	return new Promise((resolve, reject) => {
		dirEntry.getDirectory(name, options, resolve, reject)
		return true
	})
}

/**
 * 获取文件
 * @param {Object} dirEntry
 * @param {string} name
 * @param {Object} options
 */
function _getFile(dirEntry, name, options) {
	return new Promise((resolve, reject) => {
		dirEntry.getFile(name, options, resolve, reject)
		return true
	})
}

/**
 * 写文件数据
 * @param {Object} fileEntry
 * @param {Blob} blob
 */
function _writeFile(fileEntry, blob) {
	return new Promise((resolve, reject) => {
		fileEntry.createWriter(fileWriter => {
			fileWriter.onwriteend = resolve
			fileWriter.onerror = reject
			fileWriter.write(blob)
		})
		return true
	})
}

/**
 * 删除文件
 * @param {Object} entry
 */
function _deleteFile(entry) {
	return new Promise((resolve, reject) => {
		entry.remove(resolve, reject)
		return true
	})
}

/**
 * 删除文件夹
 * @param {Object} entry
 */
function _deleteDirectory(entry) {
	return new Promise((resolve, reject) => {
		entry.removeRecursively(resolve, reject)
		return true
	})
}
