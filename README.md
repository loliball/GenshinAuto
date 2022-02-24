# GenshinAuto
米游社原神自动签到
## 作为可执行文件使用
新建cookies.txt，每行一个cookie  
执行命令 ```java -jar GenshinAuto.main.jar```
## 作为库使用
Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url = uri("https://jitpack.io") }
	}
}
```
Step 2. Add the dependency
```
dependencies {
	implementation("com.github.WhichWho:GenshinAuto:+")
}
```
调用```genshin.AutoCheckin.checkin```传入cookie即可签到
## 其他的一些小工具
从网页端，客户端获取的cookie通常包含大量无用的追踪信息  
可以调用```genshin.AutoCheckin.distinctCookie```去除这些数据
