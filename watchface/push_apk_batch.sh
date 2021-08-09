#!/bin/bash

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
  vianneyhalter
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
