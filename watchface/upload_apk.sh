#!/bin/bash

#小版本变化规则：a->z->ba->zz->caa->...，类似0->9->10->99->100->...

readonly isManualModify=$(echo "$1" | grep -Eiw "y|yes") #传参y或yes(忽略大小写)，表示已经手动修改版本号。

#---------------- 获取module名称 ----------------
readonly module_dir=$(pwd | awk -F "/" '{print $NF}')
echo "module: $module_dir"

#---------------- 获取APK名称 ----------------
readonly MK_FILE="Android.mk"
if [ -e $MK_FILE ]; then
  readonly apk_name=$(grep -w "LOCAL_MODULE" $MK_FILE | grep -Eo 'WizFace[[:alnum:]_]*')
  if [ "$apk_name" ]; then
    echo "apk_name: $apk_name"
  else
    echo "获取APK名称失败，程序退出！"
    exit 1
  fi
else
  echo "$MK_FILE 文件不存在，程序退出！"
  exit 1
fi

#---------------- 获取版本号 ----------------
readonly VERSION_FILE="./build.gradle"
if [ -e $VERSION_FILE ]; then
  version_name=$(grep -w "versionName" $VERSION_FILE | grep -Eo '([0-9]\.){3}[a-z]+')
  if [ "$version_name" ]; then
    echo "version_name: $version_name"
  else
    echo "获取版本号失败，程序退出！"
    exit 1
  fi
else
  echo $VERSION_FILE"文件不存在，程序退出！"
  exit 1
fi

#---------------- 修改版本号 ----------------
readonly LOWER_CASE_A_ASCII=$(printf "%d" \'a)
readonly NUMBER_SHIFT=26
alphabet=()
for ((i = 0; i < NUMBER_SHIFT; i++)); do
  ch=$(echo $((i + LOWER_CASE_A_ASCII)) | awk '{printf("%c", $1)}')
  alphabet[i]=$ch
done
readonly length=${#alphabet[*]}
#echo "alphabet: ${alphabet[*]}, length: $length"

function getElementIndex() {
  local args=$1
  local str=${args[*]}
  #echo "str: $str, ${#str[*]}"
  local item=$2
  #echo "item: $item"
  index=0
  for element in $str; do
    if [ "$item" = "$element" ]; then
      echo $index
      return
    fi
    index=$((index + 1))
  done

  echo -1
}
#letter="h"
#index=$(getElementIndex "${alphabet[*]}" "$letter")
#echo "letter: $letter, alphabet index: $index"

function makePow() { #幂运算
  local base=$1     #底数
  local exponent=$2 #幂

  echo $((base ** exponent))
}
#pow_result=$(makePow 3 4)
#echo "pow_result: $pow_result"

function obtainUnits() { #计算新字母值
  local number=$1 #数值
  local shift=$2  #数的进制

  until ((number == 0)); do
    letters=${alphabet[$((number % shift))]}$letters
    number=$((number / shift))
  done

  echo "$letters"
}

function modifyVersionName() {
  readonly old_letter=$(echo "$version_name" | grep -Eo '[a-z]+')
  readonly old_letter_len=${#old_letter}
  #echo "all_letter: $old_letter, len: $old_letter_len"

  for ((i = 0; i < old_letter_len; i++)); do
    exponent=$((old_letter_len - 1 - i))
    pow=$(makePow NUMBER_SHIFT $exponent)
    #echo "pow 26 $exponent: $pow"

    letter=${old_letter:$i:1}
    index=$(getElementIndex "${alphabet[*]}" "$letter")
    #echo "letter: $letter, letters index: $i, alphabet index: $index"

    sum=$((sum + index * pow))
  done
  #echo "sum: $sum"

  readonly new_letter=$(obtainUnits $((sum + 1)) NUMBER_SHIFT)
  #echo "new_letter: $new_letter"

  new_version_name="${version_name//$old_letter/$new_letter}"
  echo "new_version_name: $new_version_name"
}

#---------------- 获取版本编码 ----------------
readonly version_code=$(grep "versionCode" $VERSION_FILE | grep -Eo '[0-9]+')
echo "version_code: $version_code"
#---------------- 修改版本编码 ----------------
function modifyVersionCode() {
  readonly new_version_code=$((version_code + 1))
  echo "new_version_code: $new_version_code"
}

if [ ! "$isManualModify" ]; then #如果已经手动修改版本，就不需要自动修改了，避免重复修改
  modifyVersionName
  modifyVersionCode
  #修改build.gradle之前备份一份，用于打包或压缩失败的情况时它的还原
  sed -i.bak 's/'"$version_code"'/'"$new_version_code"'/;s/'"$version_name"'/'"$new_version_name"'/' $VERSION_FILE
  version_name=$new_version_name
fi

function restoreGradleFile() { #还原build.gradle文件，用于打包或压缩等失败的情况。
  if [ -e "${VERSION_FILE}.bak" ]; then
    mv "${VERSION_FILE}.bak" "${VERSION_FILE}"
  fi
}

#---------------- 编译打包 ----------------
#./../../gradlew ":watchface:${module_dir}:apkSign"
./../../gradlew ":watchface:${module_dir}:aR"
sign_result=$?
if [ $sign_result -eq 0 ]; then
  if [ -e "${VERSION_FILE}.bak" ]; then
    rm "${VERSION_FILE}.bak" #如果打包成功，则删除备件文件
  fi
else
  echo "APK打包失败，程序退出！"
  restoreGradleFile #如果打包失败，则还原build.gradle文件
  exit 1
fi

#---------------- zip压缩 ----------------
readonly ZIP_FILE_PATH="./build"
readonly SIGNED_APK_DIR="../apks"
readonly BUILD_APK_PATH="./build/outputs/apk/release/${module_dir}-release-unsigned.apk"
readonly SIGNED_APK_PATH="${SIGNED_APK_DIR}/${apk_name}.apk"
cp "${BUILD_APK_PATH}" "${SIGNED_APK_PATH}"
readonly zip_file="${apk_name}_AA_V${version_name}.zip"
zip -j "${ZIP_FILE_PATH}/${zip_file}" ${MK_FILE} "${SIGNED_APK_PATH}"
zip_result=$?
if [ $zip_result != 0 ]; then
  echo "文件压缩失败，程序退出！"
  restoreGradleFile
  exit 1
fi
echo "zip_file: $zip_file"

#---------------- 上传到服务器 ----------------
function uploadToFtp() {
  ftp -v -n 192.168.115.231 <<EOF
user romimgs_r_w mI9H#cDSWBEzMclB
binary
hash

cd /apk-signed-for-watch || exit 1
if [ ! -d "$apk_name" ];then
    mkdir "$apk_name"
fi
cd "$apk_name" || exit 1

lcd $ZIP_FILE_PATH
prompt
put $zip_file
close
bye
EOF
}
#uploadToFtp
