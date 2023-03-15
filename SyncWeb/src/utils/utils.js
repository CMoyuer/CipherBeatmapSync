/**
 * 将Blob转换为Base64
 * @param {Blob} blob
 * @returns {Promise}
 */
export function blobToBase64(blob) {
	return new Promise(function(resolve, reject) {
		const fileReader = new FileReader();
		fileReader.onload = (e) => {
			resolve(e.target.result)
		}
		fileReader.readAsDataURL(blob)
	})
}
