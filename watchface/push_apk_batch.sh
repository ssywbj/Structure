#!/bin/bash

#modules=(
#  auspiciousdragon
#  classicpointer
#  digitalbeat
#  dynamic
#  facingchallenges
#  healthdata
#  healthycircle
#  joker
#  myhealth
#  numberpointer
#  peach
#  photo
#  plugindata
#  qrs217
#  roamingclock
#  shuttle
#  simplenumber
#  simplepointer
#  sundiary
#  svendandersen
#  timerace
#  x-sports
#)

modules=(
  sundiary
  x-sports
)

readonly sh_file="push_apk.sh"
for module in ${modules[*]}; do
  if [ -e "$module" ]; then
    if [ -e "$sh_file" ]; then
      cp $sh_file "$module"
      cd "$module" || exit
      ./$sh_file
      rm $sh_file
      cd ..
    else
      echo "$sh_file 文件不存在！"
    fi
  else
    echo "$module module不存在！"
  fi
done
