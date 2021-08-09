#!/bin/bash

#批量上传apk到ftp服务器，脚本功能：自/手动修改版本、打包、压缩、上传、统计结果等
#Linux或Mac环境下执行："./upload_apk_batch.sh"或"./upload_apk_batch.sh y"

#modules=(
#  auspiciousdragon
#  calendar
#  classicpointer
#  dataplugin
#  digitalbeat
#  dynamic
#  elevator
#  facingchallenges
#  healthdata
#  healthycircle
#  healthylife
#  joker
#  luxury
#  maze
#  mexico
#  myhealth
#  numberpointer
#  numberstack
#  peach
#  photo
#  plugindata
#  pointerplugin
#  qrs217
#  retroelectronic
#  roamingclock
#  scale123
#  shuttle
#  simplenumber
#  simplepointer
#  soberlyonyx
#  spaceman
#  sportstalent
#  sundiary
#  svendandersen
#  timerace
#  traverse
#  vianneyhalter
#  x-sports
#)

modules=(
  sundiary
)

readonly isManualModify=$(echo "$1" | grep -Eiw "y|yes") #传参y或yes(忽略大小写)，表示已经手动修改版本号。
readonly sh_file="upload_apk.sh"
success_count=0
for module in ${modules[*]}; do
  if [ -e "$module" ]; then
    if [ -e "$sh_file" ]; then
      cp $sh_file "$module"
      cd "$module" || exit
      ./$sh_file "$isManualModify"
      result=$?
      if [ $result == 0 ]; then
        success_count=$((success_count + 1))
      else
        fail_array[fail_count]=$module
        fail_count=$((fail_count + 1))
      fi

      rm $sh_file
      cd ..
    else
      echo "$sh_file 文件不存在！"
    fi
  else
    echo "$module module不存在！"
    exit
  fi
done

echo "---------------------- 执行结果 -----------------------"
echo -e "总共：${#modules[*]}个，\033[34m成功：${success_count}个\033[0m"
fail_array_len=${#fail_array[*]}
if [ "$fail_array_len" != 0 ]; then
  echo -e "\033[31m失败：${fail_array_len}个，为：${fail_array[*]}\033[0m"
fi
