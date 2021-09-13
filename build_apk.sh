#!/bin/bash

#手动生成并签名apk脚本，适用于非idea构建的android项目

readonly ANDROID_JAR_PATH="${ANDROID_HOME}/platforms/android-30/android.jar"
readonly SDKLIB_JAR_PATH="${ANDROID_HOME}/tools/lib/sdklib-26.0.0-dev.jar"

#readonly STORE_FILE_PATH="./zhipu-keystore.jks"
#readonly STORE_PWD="Zhipu@520"
#readonly KEY_PWD="Zhipu@520_"
#readonly KEY_ALIAS="modules_apply"

readonly STORE_FILE_PATH=~/.android/debug.keystore #带~的路径不加双引号括号起来，免得会找不到宿主目录
readonly STORE_PWD="android"
readonly KEY_PWD="android"
readonly KEY_ALIAS="androiddebugkey"

readonly project_name=$(pwd | awk -F "/" '{print $NF}')
echo "project name: $project_name"
readonly project_pkg=$(grep -w "package" AndroidManifest.xml | grep -Eo '([a-z]+[\.]){2,}[a-zA-Z]+')
readonly src_path=${project_pkg//.//}
echo "pkg:${project_pkg}, src path:${src_path}"

readonly gen_dir="gen"
if [ ! -d "$gen_dir" ];then
    mkdir "$gen_dir"
fi
aapt package -f -M AndroidManifest.xml -I "${ANDROID_JAR_PATH}" -S res/ -J ${gen_dir}/ -m

readonly bin_dir="bin"
if [ ! -d "$bin_dir" ];then
    mkdir "$bin_dir"
fi
javac -target 1.8 -bootclasspath "${ANDROID_JAR_PATH}" -d ${bin_dir} src/"${src_path}"/*.java ${gen_dir}/"${src_path}"/*.java

dx --dex --output=${bin_dir}/classes.dex ${bin_dir}

aapt package -f -M AndroidManifest.xml -S res -I "${ANDROID_JAR_PATH}" -F ${bin_dir}/resources.ap_

readonly apk_dir="./apk"
if [ ! -d "$apk_dir" ];then
    mkdir "$apk_dir"
fi
java -cp "${SDKLIB_JAR_PATH}" com.android.sdklib.build.ApkBuilderMain ${apk_dir}/temp.apk -v -u -z ${bin_dir}/resources.ap_ -f ${bin_dir}/classes.dex -rf ./src

jarsigner -verbose -keystore "${STORE_FILE_PATH}" -storepass ${STORE_PWD} -keypass ${KEY_PWD} -signedjar ${apk_dir}/signed.apk ${apk_dir}/temp.apk ${KEY_ALIAS}

zipalign -f 4 ${apk_dir}/signed.apk ${apk_dir}/"${project_name}".apk

rm ${apk_dir}/signed.apk ${apk_dir}/temp.apk

adb install -r -t ${apk_dir}/"${project_name}".apk
