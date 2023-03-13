<template>
	<el-container>
		<el-header class="title">
			同步助手
		</el-header>
		<el-main>
			<div class="ipAddress">
				<el-input v-model="ipInput" :disabled="ipInputState.lock.value" placeholder="请输入VR设备的局域网IP">
					<template #prepend>设备IP</template>
					<template #append>
						<el-button :icon="ipInputState.lock.value ? EditPen: Check" @click="onBtnIpClick" />
					</template>
				</el-input>
				<el-alert class="tip" v-show="ipInputState.tipShow.value" :type="ipInputState.tipType.value"
					:title="ipInputState.tipMsg.value" :closable="false" />
			</div>
		</el-main>
	</el-container>

</template>

<script setup>
	import {
		Check,
		EditPen
	} from '@element-plus/icons-vue'
	import {
		ref
	} from 'vue'

	// IP地址输入框
	const ipInput = ref("")
	const ipInputState = {
		lock: ref(false),
		tipShow: ref(false),
		tipType: ref("warning"),
		tipMsg: ref("")
	}

	function onBtnIpClick() {
		if (!ipInputState.lock.value) {
			// 保存IP并锁定输入
			let ip = ipInput.value
			if (/((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}/.test(ip)) {
				ipInputState.tipShow.value = false
				console.log(ip)
				ipInputState.lock.value = true
			} else {
				ipInputState.tipType.value = "warning"
				ipInputState.tipMsg.value = "请输入正确的IP地址"
				ipInputState.tipShow.value = true
			}
		} else {
			// 解锁输入框
			ipInputState.lock.value = false
			ipInputState.tipShow.value = false
		}
	}
</script>

<style scoped>
	.title {
		font-size: 18px;
		font-weight: bold;
		background-color: #192230;
		padding: 10px;
		height: auto;
		box-shadow: rgb(0 0 0 / 20%) 0px 2px 4px -1px, rgb(0 0 0 / 14%) 0px 4px 5px 0px, rgb(0 0 0 / 12%) 0px 1px 10px 0px;
	}

	.ipAddress .tip {
		margin-top: 5px;
	}
</style>
