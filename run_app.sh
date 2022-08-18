#!/bin/bash

module="view"
./gradlew :"${module}":uR
./gradlew :"${module}":iR

devices=()
pkg="com.suheng.structure.view"
aty_path="${pkg}/.activity.BlurActivity"
mapfile -t devices < <(adb devices | grep -w "device") #结果用数组保存
for device in "${devices[@]}"; do
  echo "device info: ${device}"

  read -ra ids <<<"${device// / }" #按空格拆分，结果用数组保存
  id=${ids[0]}
  echo "id: ${id}"

  adb -s "${id}" shell am start "${aty_path}"
done
