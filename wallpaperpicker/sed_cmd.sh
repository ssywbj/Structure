#!/bin/bash

#---------------- 获取版本号和编码 ----------------
readonly VERSION_FILE="./build.gradle"
readonly version_name=$(grep "versionName" $VERSION_FILE | grep -Eo '[0-9]+\.([0-9]\.){2}[a-z]')
echo "version_name: $version_name"
readonly version_code=$(grep "versionCode" $VERSION_FILE | grep -Eo '[0-9]{8}')
echo "version_code: $version_code"

#---------------- 修改版本号和编码 ----------------
readonly new_version_code=$((version_code + 1))
echo "new_version_code: $new_version_code"
#sed -i '' 's/'"$version_code"'/'"$new_version_code"'/' $VERSION_FILE #mac
#sed -i 's/'"$version_code"'/'"$new_version_code"'/' $VERSION_FILE

echo "------- 字符拆分：方法1 ------"
#array=(${version_name//./ }) #最后一个"/"后面接一个空格
#echo "${array[@]}"
#echo "${array[3]}"
read -ra array4 <<<"${version_name//./ }" #最后一个"/"后面接一个空格
echo "${array4[@]}"
echo "${array4[1]}"
echo "------- 字符拆分：方法2, IFS --------"
#OLD_IFS="$IFS"
#IFS="."
#array2=($version_name)
#IFS="$OLD_IFS"
#for var in "${array2[@]}"; do
#  echo "$var"
#done
echo "------- IFS read -ra--------"
IFS='.' read -ra array3 <<<"$version_name"
for i in "${array3[@]}"; do
  echo "$i"
done

echo "------- 字符与ASCII码互转 --------"
i=65
ch=$(echo $i | awk '{printf("%c", $1)}')
echo "ch: $ch"
((i++))
ch=$(echo $i | awk '{printf("%c", $1)}')
echo "ch: $ch"

ascii=$(printf "%d" \'a)
echo "ascii: $ascii"
ascii=$(printf "%d" "'b")
echo "ascii: $ascii"

echo "------- 字母运算：a+0=a，a+1=b，... ，a+25=z;a代表0，b代表1，...，z代表25，逢26进1，如：a+26=ba -------"
readonly SHIFT=26
function basicCalc() {
  ascii=97 #ascii码97代表小写字母a
  ch=$(echo $ascii | awk '{printf("%c", $1)}')
  echo "ch: $ch"
  ch=$(echo $((ascii + 1)) | awk '{printf("%c", $1)}')
  echo "a+1: $ch"
  ch=$(echo $((ascii + 25)) | awk '{printf("%c", $1)}')
  echo "a+25: $ch"
  ch=$(echo $((ascii + 26)) | awk '{printf("%c", $1)}')
  echo "a+26: $ch"
}
#basicCalc

function alphabetCalc() {
  add=${1}
  quot=$((add / SHIFT))
  mod=$((add % SHIFT))

  read -ra array <<<"$quot $mod"
  echo "${array[*]}"
}
#array=($(alphabetCalc 0))
read -ra array <<<"$(alphabetCalc 0)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 1)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 25)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 26)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 27)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 51)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 52)"
echo "quot: ${array[0]}, mod: ${array[1]}"
read -ra array <<<"$(alphabetCalc 53)"
echo "quot: ${array[0]}, mod: ${array[1]}"

read -ra array <<<"${version_name//./ }"
echo "${array[@]}"
