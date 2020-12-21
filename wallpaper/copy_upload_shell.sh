#!/bin/bash

echo "请输入module名称："
read -r module
echo "module: $module"

if [ -e "$module" ]; then
  cp upload_to_ftp.sh "$module"
  cd "$module" || exit
  ./upload_to_ftp.sh
  rm upload_to_ftp.sh
  cd ..
else
  echo "$module module不存在！"
  exit
fi
