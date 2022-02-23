# GenshinAuto
米游社原神自动签到
## 作为可执行文件使用
新建cookies.txt，每行一个cookie
执行命令```java -jar GenshinAuto.main.jar```
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
