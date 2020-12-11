#!/bin/bash

#---------------- 编译打包 ----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"
./../gradlew ":${module_dir}:aR"
# shellcheck disable=SC2181
if [ $? -ne 0 ]
then
    echo "APK打包失败，程序退出！"
    exit
fi

#---------------- 获取APK名称 ----------------
readonly MK_FILE="Android.mk"
if [ -e $MK_FILE ];then
    readonly apk_name=$(grep -w "LOCAL_MODULE" $MK_FILE | grep -Eo 'Wbj[[:alnum:]_]*')".apk"
    if [ "$apk_name" ];then
        echo "apk_name: $apk_name"
    else
        echo "获取APK名称失败，程序退出！"
        exit
    fi
else
    echo "$MK_FILE 文件不存在，程序退出！"
    exit
fi

#---------------- 谷歌签名 ----------------
readonly SIGN_TOOL_PATH="sign-google"
readonly APK_PATH="build/outputs/apk/release/$apk_name"
readonly SIGNED_APK_PATH="build/outputs/apk/release/Gle_WallpaperPick.apk"
java -jar $SIGN_TOOL_PATH/signapk.jar $SIGN_TOOL_PATH/security-google/platform.x509.pem $SIGN_TOOL_PATH/security-google/platform.pk8 "$APK_PATH" "$SIGNED_APK_PATH"
# shellcheck disable=SC2181
if [ $? == 0 ];then
    echo "APK谷歌签名成功"
else
    echo "APK谷歌签名失败"
fi
