var fs = require('fs');
var path = require('path');

module.exports = function (context) {
	const platformRoot = path.join(
      context.opts.projectRoot,
      "platforms/android"
    )
	
	let rootGradle = platformRoot + "/build.gradle";
	let rootGradleText = fs.readFileSync(rootGradle, 'utf8');
	
	let libs = ["classpath 'com.yanzhenjie.andserver:plugin:2.1.11'"]
	for(let i = 0;i < libs.length;i++)
		rootGradleText = rootGradleText.replace(libs[i], "")
	rootGradleText = rootGradleText.replaceAll(/\n\s{1,}\n/g, "\n")
	fs.writeFileSync(rootGradle, rootGradleText, 'utf8');
};