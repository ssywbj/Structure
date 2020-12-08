#!/bin/bash

#---------------- 编译打包 ----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"
./../gradlew ":${module_dir}":aR

#---------------- 获取版本号 ----------------
readonly VERSION_NAME_IN_FILE="./build.gradle"
readonly version_name=$(grep -w "versionName" $VERSION_NAME_IN_FILE | grep -Eo '([0-9]\.){3}[a-z]{1,2}')
echo "version_name: $version_name"

#---------------- 获取APK名称 ----------------
readonly MK_FILE="./Android.mk"
#readonly apk_name=$(grep -w "LOCAL_MODULE" $MK_FILE | grep -Eo 'WizFace[[:alnum:]_]*')
readonly apk_name="WallpaperPicker"
echo "apk_name: $apk_name"

#---------------- zip压缩 ----------------
readonly SIGNED_APK_PATH="./build/outputs/apk/release"
readonly zip_file="${apk_name}_AA_V${version_name}.zip"
zip -j "${SIGNED_APK_PATH}/$zip_file" $MK_FILE "${SIGNED_APK_PATH}/${apk_name}.apk"
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
