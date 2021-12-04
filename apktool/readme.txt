方法一
分两个过程，第一为破解APK的资源，第二为破解APK的代码（尽量还原）
https://blog.csdn.net/csh86277516/article/details/81325403
https://www.zhihu.com/question/29370382

破解APK的资源
1.cd到apktool.jar所在目录
java -jar apktool.jar d -f /e/Workspace/Android/Structure/wallpaper/myhealth/build/outputs/apk/release/Wallpaper_MyHealth.apk -o ../resolve

/e/Workspace/Android/Structure/wallpaper/myhealth/build/outputs/apk/release/Wallpaper_MyHealth.apk：apk所在路径
-o ../resolve：-o用于指定破解后的APK资源保存的目录，现为resolve（不指定的话，会默认在apktool.jar的同级目录生成一个和apk名称相同的
文件夹，如上为Wallpaper_MyHealth）

破解APK的代码（尽量还原）
1.Wallpaper_MyHealth.apk重命名为压缩文件，如Wallpaper_MyHealth.zip；
2.解压Wallpaper_MyHealth.zip；
3.把解压获取到的classes.dex拷贝到apktool/dex2jar-2.1目录；
4.cd到dex2jar-2.1目录；
5.执行命令：d2j-dex2jar classes.dex
此时会在当前目录生成对应的jar文件（格式：原文件名-dex2jar.jar），如：classes-dex2jar.jar
6.使用jd-gui工具打开新生成的jar包：java -jar ../jd-gui-1.6.6.jar classes-dex2jar.jar，即可查看相应的源码

方法二
使用集成框架：https://github.com/skylot/jadx
ubuntu,https://lindevs.com/install-jadx-on-ubuntu/
步骤如下：
cd Programs(没有的话，建一个)
查最新版本号
JADX_VERSION=$(curl -s "https://api.github.com/repos/skylot/jadx/releases/latest" | grep -Po '"tag_name": "v\K[0-9.]+')
下载
curl -Lo jadx.zip "https://github.com/skylot/jadx/releases/latest/download/jadx-${JADX_VERSION}.zip"
解压
unzip jadx.zip -d jadx
配置路径
echo 'export PATH=$PATH:~/Programs/jadx/bin' | sudo tee -a /etc/profile
生效配置文件
source /etc/profile
验证是否安装成功
jadx --version
成功的话会输入版本号

#录制终端
script -t 2>time.txt session.typescript
#结束录制
exit
#回放录制视频
scriptreplay -t time.txt session.typescript