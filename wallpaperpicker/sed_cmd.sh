#!/bin/bash

#---------------- 获取版本号和编码 ----------------
readonly VERSION_FILE="./build.gradle"
readonly version_name=$(grep "versionName" $VERSION_FILE | grep -Eo '([0-9]{1,2}\.){3}[a-z]{1,2}')
echo "version_name: $version_name"
readonly version_code=$(grep "versionCode" $VERSION_FILE | grep -Eo '[0-9]{8}')
echo "version_code: $version_code"

readonly new_version_code=$((version_code+1))
echo "new_version_code: $new_version_code"
#sed -i '' 's/'"$version_code"'/'"$new_version_code"'/' $VERSION_FILE #mac
sed -i 's/'"$version_code"'/'"$new_version_code"'/' $VERSION_FILE
#new_version_name=$((version_name+1))
#echo "new_version_name: $new_version_name"

array=(${version_name//./ }) #最后一个"/"后面接一个空格
echo "${array[@]}"
echo "${array[3]}"
read -ra array4 <<< "${version_name//./ }" #最后一个"/"后面接一个空格
echo "${array4[@]}"
echo "${array4[1]}"
echo "------- IFS --------"
OLD_IFS="$IFS"
IFS="."
array2=($version_name)
IFS="$OLD_IFS"
for var in "${array2[@]}";do
  echo "$var"
done
echo "------- IFS read -ra--------"
IFS='.' read -ra array3 <<< "$version_name"
for i in "${array3[@]}";do
    echo "$i"
done

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
