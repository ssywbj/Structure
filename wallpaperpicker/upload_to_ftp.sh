#!/bin/bash

#---------------- 编译打包 ----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"
./../gradlew ":${module_dir}:aR"
# shellcheck disable=SC2181
if [ $? -ne 0 ]; then
  echo "APK打包失败，程序退出！"
  exit
fi

#---------------- 获取版本号 ----------------
readonly VERSION_NAME_IN_FILE="./build.gradle"
if [ -e $VERSION_NAME_IN_FILE ]; then
  readonly version_name=$(grep -w "versionName" $VERSION_NAME_IN_FILE | grep -Eo '([0-9]\.){3}[a-z]{1,2}')
  if [ "$version_name" ]; then
    echo "version_name: $version_name"
  else
    echo "获取版本号失败，程序退出！"
    exit
  fi
else
  echo $VERSION_NAME_IN_FILE" 文件不存在，程序退出！"
  exit
fi

#---------------- 获取APK名称 ----------------
readonly MK_FILE="Android.mk"
if [ -e $MK_FILE ]; then
  readonly apk_name=$(grep -w "LOCAL_MODULE" $MK_FILE | grep -Eo 'Wbj[[:alnum:]_]*')".apk"
  if [ "$apk_name" ]; then
    echo "apk_name: $apk_name"
  else
    echo "获取APK名称失败，程序退出！"
    exit
  fi
else
  echo "$MK_FILE 文件不存在，程序退出！"
  exit
fi

#---------------- zip压缩 ----------------
readonly SIGNED_APK_PATH="./build/outputs/apk/release"
readonly zip_file="${apk_name}_AA_V${version_name}.zip"
zip -j "${SIGNED_APK_PATH}/$zip_file" $MK_FILE "${SIGNED_APK_PATH}/${apk_name}"
# shellcheck disable=SC2181
if [ $? != 0 ]; then
  echo "文件压缩失败，程序退出！"
  exit
fi
echo "zip_file: $zip_file"

#---------------- 上传到服务器 ----------------
function uploadToFtp() {
  ftp -v -n 192.168.115.231 <<EOF
user romimgs_r_w mI9H#cDSWBEzMclB
binary
hash

cd /apk-signed-for-watch || exit
if [ ! -d "$apk_name" ];then
    mkdir "$apk_name"
fi
cd "$apk_name" || exit

lcd $ZIP_FILE_PATH
prompt
put $zip_file
close
bye
EOF
}
#uploadToFtp
