#!/bin/bash

#----------------- 编译打包 -----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"
./../../gradlew :watchface":${module_dir}":apkSign
#./../../gradlew :watchface":${module_dir}":aR

#---------------- 获取APK名称 ----------------
readonly apk_name=$(grep -w "LOCAL_MODULE" Android.mk | grep -Eo 'WizFace[[:alnum:]_]*')
echo "apk_name: $apk_name"

#---------------- push到指定目录 ----------------
adb root
adb remount

#adb push "./build/outputs/apk/release/${module_dir}-release-unsigned.apk" /system/etc/wiz_home/plugin_watch
#adb push "../apks/${apk_name}.apk" /data/wiz_system/wiz_home/plugin_watch
adb push "../apks/${apk_name}.apk" /system/etc/wiz_home/plugin_watch

#readonly home_pkg="com.wiz.watch.home.single"
readonly home_pkg="com.wiz.watch.home"
adb shell pm clear $home_pkg #清桌面数据若没有自动回到表盘页，则需要按home键回到表盘页
sleep 2
#删除shared_prefs后长按删除表盘，此时桌面会报错，然后点击重启桌面即可。若还是不生效，可考虑延长休眠时间
adb shell rm -r /data/data/$home_pkg/shared_prefs/WizHome.xml