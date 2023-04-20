# 《闪韵灵境》谱面同步助手 V2
“谱面同步助手”是基于[**闪韵灵境 谱面编辑器**](https://cipher-editor-cn.picovr.com/)进行开发的功能拓展插件

旨在给作谱者带来便捷

如有侵犯官方权益，请联系本人进行删除

![ezgif-3-bd996cf02b](https://user-images.githubusercontent.com/51113234/233357793-019da48a-ccd0-4f29-8969-00d50382d4cd.gif)
![ezgif-3-7870bd9f4b](https://user-images.githubusercontent.com/51113234/233359990-0ea84380-b411-4afd-a728-2f92d95d6ed7.gif)

## 功能介绍
 - 为谱面编辑器增加“同步”按钮，快速同步在编谱子的数据到一体机上
 - 支持多文件拖拽同步功能，一次上传多个谱面到VR一体机
 - 支持VR端连接状态监测功能
 - 支持传输失败重试功能

## V2版本差异
 - V2版本几乎所有代码推翻重做，需要卸载原1.0版本的Chrome浏览器拓展程序后使用
 - 移除Chrome扩展程序、改用油猴插件，以支持其他浏览器（如：Firefox、Safari、Microsoft、Opera、Maxthon等）
 - 浏览器增加同步小窗口，支持VR端连接状态监测、同步任务队列、自动重试、拖拽文件同步等功能
 - 使用Cordova混合开发方式重做了App

### 安装浏览器脚本
1. 先在浏览器上安装一个[脚本管理器](https://greasyfork.org/zh-CN/help/installing-user-scripts)（比如Tampermonkey）
2. 点击[这里](https://greasyfork.org/zh-CN/scripts/462205)安装脚本
3. 打开[谱面编辑器](https://cipher-editor-cn.picovr.com/)，看到右上角显示【同步助手】，就代表安装成功了

![image](https://user-images.githubusercontent.com/51113234/226379351-55407f06-4877-4e4f-8993-580a9227c590.png)

### 安装谱面同步助手到VR上
1. 前往[**下载页面**](../../releases/latest)下载APK文件
1. 使用数据线，将apk文件复制到VR设备中，自己找得到的目录下
2. 在VR设备中，使用文件管理器打开并安装app

## 使用说明

1. 在VR设备中打开“闪韵谱面同步助手”
2. 点击正中间的“启动”按钮
3. 在PC端谱面编辑器中，点击打开【同步助手】
4. 浏览器弹出小窗，填写在APP中看到的IP地址，并点击确认
5. 点击谱面更多-同步按钮，或拖拽谱面压缩包将会自动同步到VR当中

## 常见问题
**1. VR摘下来长时间自动休眠后，就无法同步了**
 - VR一体机都带有休眠功能，可参考[B站相关视频](https://search.bilibili.com/all?keyword=pico4%20%E4%BC%91%E7%9C%A0)关闭

**2. APP切到后台一段时间后，就没法同步了？**
 - 安卓系统有自动杀后台服务的功能，这里使用了[保活组件](https://github.com/fanqieVip/keeplive)，但不一定奏效
 - 如果没法同步，可切换到APP重试

## 更新记录

**v2.1.0 2023-4-20**
 - 支持编辑器中的谱面直接同步到一体机中

**v2.0.0 2023-3-20**
 - 全部推翻重做

**v1.0.0 2023-3-11**
 - 首次发布
