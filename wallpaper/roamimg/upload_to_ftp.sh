#!/bin/bash

readonly isManualModify=$(echo "$1" | grep -Eiw "y|yes") #传参y或yes，表示已经手动修改版本号。i：忽略大小写

#---------------- 获取版本号 ----------------
readonly VERSION_FILE="./build.gradle"
if [ -e $VERSION_FILE ]; then
  version_name=$(grep -w "versionName" $VERSION_FILE | grep -Eo '([0-9]\.){3}[a-z]')
  if [ "$version_name" ]; then
    echo "version_name: $version_name"
  else
    echo "获取版本号失败，程序退出！"
    exit
  fi
else
  echo $VERSION_FILE" 文件不存在，程序退出！"
  exit
fi
#---------------- 修改版本号 ----------------
function makeQuotMod() {
  add=$1
  shift=$2
  quot=$((add / shift)) #商数
  mod=$((add % shift))  #余数

  read -ra array <<<"$quot $mod" #返回数组，第一数是商，第第二个数是余数
  echo "${array[*]}"
}
function modifyVersionName() {
  readonly A_ASCII=$(printf "%d" \'a)

  readonly ven=${version_name//./} #1.0.0.a->100a
  readonly apl=$(echo "$ven" | grep -Eo '[a-z]') #截取a
  readonly apl_ascii=$(printf "%d" \'"$apl") #字符转ASCII码
  readonly new_apl_ascii=$((apl_ascii + 1)) #ASCII码加1得到新字符
  #echo "apl_ascii: $apl_ascii, new_apl_ascii: $new_apl_ascii"
  read -ra array <<<"$(makeQuotMod $((new_apl_ascii - A_ASCII)) 26)" #取余数，保证新字符落在a-z之间
  new_apl=$(echo $((A_ASCII + array[1])) | awk '{printf("%c", $1)}') #ASCII码转字符
  #echo "apl: $apl, new_apl: $new_apl"

  readonly num=$(echo "$ven" | grep -Eo '[0-9]+') #截取100
  readonly new_num=$((num + array[0]))
  #反转插入"."，以保证第一位可以一直递增显示，再反转得到新版本号
  new_version_name=$(echo "$new_num$new_apl" | rev | sed 's/\(.\)\(.\)\(.\)/\1.\2.\3./' | rev) #rev：字符反转
  echo "new_version_name: $new_version_name"
  sed -i.bak 's/'"$version_name"'/'"$new_version_name"'/' $VERSION_FILE
  version_name=$new_version_name
}
if [ ! "$isManualModify" ]; then #如果已经手动修改号，就不需要自动修改版本号了，以避免重复修改
  modifyVersionName
fi

#---------------- 获取版本编码 ----------------
readonly version_code=$(grep "versionCode" $VERSION_FILE | grep -Eo '[0-9]{8}')
echo "version_code: $version_code"
#---------------- 修改版本编码 ----------------
function modifyVersionCode() {
  readonly new_version_code=$((version_code + 1))
  echo "new_version_code: $new_version_code"
  sed -i.bak 's/'"$version_code"'/'"$new_version_code"'/' $VERSION_FILE
}
modifyVersionCode

#---------------- 编译打包 ----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"
./../../gradlew ":wallpaper:${module_dir}:aR"
# shellcheck disable=SC2181
if [ $? -ne 0 ]; then
  echo "APK打包失败，程序退出！"
  exit
fi

#---------------- 获取APK名称 ----------------
readonly MK_FILE="Android.mk"
if [ -e $MK_FILE ]; then
  readonly apk_name=$(grep -w "LOCAL_MODULE" $MK_FILE | grep -Eo 'WizFace[[:alnum:]_]*')
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
zip -j "${SIGNED_APK_PATH}/$zip_file" $MK_FILE "${SIGNED_APK_PATH}/${apk_name}.apk"
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
