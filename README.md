# 《闪韵灵镜》谱面同步助手
“谱面同步助手”是基于[**闪韵灵镜 谱面编辑器**](https://cipher-editor-cn.picovr.com/)进行开发的功能拓展插件

旨在给作谱者带来便捷

如有侵犯官方权益，请联系本人进行删除

## 功能介绍
 - 本工具只支持[**Chrome浏览器**](https://www.google.com/chrome/)
 - 需要在浏览器中安装扩展，且在pico一体机中安装app后使用
 - 修改谱面编辑器的“下载”按钮，为其增加“同步到VR一体机”的功能

## 安装说明
### 准备材料
1. 前往[**下载页面**](https://github.com/CMoyuer/CipherBeatmapSync/releases/latest)，下载前两个附件（zip + apk）

### 安装浏览器拓展程序
1. 将刚才准备的zip解压到一个喜欢的地方，比如 D:\ChromeExtension\CipherEditorBeatmapSync
2. 打开Chrome浏览器，地址栏输入并访问 chrome://extensions/ ，打开浏览器拓展程序页面
3. 打开页面右上角的**开发者模式**

![image](https://user-images.githubusercontent.com/51113234/224472244-3cdabf2d-32dc-418b-a4e4-304dbe3359f5.png)

5. 点击左上角**加载已解压的拓展程序**，选择刚才解压到的目录
6. 出现以下扩展程序，就安装成功了

![image](https://user-images.githubusercontent.com/51113234/224472430-11f274ce-bdbb-4e3f-9afb-a9236f55db31.png)

### 安装Cipher助手到VR上
1. 使用数据线，将apk文件复制到自己喜欢的目录
2. 在VR中，使用文件管理器安装app

## 使用说明
1. 在vr中打开“Cipher谱面同步助手”
2. 点击app左下角的“启动”按钮
3. 在pc端谱面编辑器中，点击下载谱面
4. 弹窗提示“是否同步到一体机”，点击“是”
5. 自动同步到VR当中

## 常见问题
**1. VR中已打开app并点击启动了，操作时却提示“同步失败：找不到VR设备的IP地址！”该怎么办？**
 - 检查VR与电脑连接是否处于同一个局域网
 - 检查电脑是否使用了代理/VPN，可以将域名 api.moyuer.cc 添加到绕过规则中（个人服务器，求别打）
 
**2. VR摘下来一段时间后，就无法同步了**
 - VR一体机都带有休眠功能，可参考[B站相关视频](https://search.bilibili.com/all?keyword=pico4%20%E4%BC%91%E7%9C%A0)关闭

**3. APP切到后台一段时间后，就没法同步了？**
 - 安卓系统有自动杀后台服务的功能，这里使用了[保活组件](https://github.com/fanqieVip/keeplive)，但不一定奏效
 - 该问题暂时无解

**4. 同一公网IP下，为什么有多台VR同时使用该工具，会出现问题？**
 - 本工具使用根据公网IP记录VR端局域网IP的方式来快速获取到VR端IP，所以同一公网IP下只能有一台VR使用本工具
 - 正在寻找更好的解决方案

## 更新记录

**v1.0.0 2023-3-11**
 - 首次发布
