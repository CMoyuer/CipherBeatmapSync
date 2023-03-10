'use strict'

// ========================== 消息传递 ==========================
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.event === "upload2vr") {
        uploadBeatmapToVR(message.filename, message.base64).then(() => {
            console.log("上传到VR一体机成功!")
        }).catch(err => {
            console.log("上传到VR一体机失败: " + err)
        })
    }
})

/**
 * 
 * @param {string} fileName 文件名称
 * @param {string} base64 曲谱ZIP的Base64
 */
async function uploadBeatmapToVR(fileName, base64) {
    // 获取VR一体机的局域网IP地址
    let lan_info = await fetch("https://api.moyuer.cc/lan_ddns").then(res => res.json())
    if (lan_info.code != 200)
        throw "找不到VR设备的IP地址!"
    let lan_ip = lan_info.data.lan_ip

    // 上传文件到VR一体机
    let formData = new FormData()
    formData.append("name", fileName)
    formData.append("base64", base64)

    let upload_result = await fetch("http://" + lan_ip + ":25521/upload_beatmap",
        {
            method: "POST",
            body: formData
        }
    ).then(res => res.json())
    if (upload_result.code != 200)
        throw upload_result.message
}