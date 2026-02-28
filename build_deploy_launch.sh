#!/bin/bash
# Automated script to build, deploy, launch, and execute the app on EM45

set -e

WORKDIR="$(cd "$(dirname "$0")" && pwd)"
cd "$WORKDIR"

BASE_APP_ID=$(grep -m1 'applicationId[[:space:]]*"' app/build.gradle | sed -E 's/.*applicationId[[:space:]]*"([^"]+)".*/\1/')
DEBUG_SUFFIX=$(awk '
  /buildTypes[[:space:]]*\{/ { inBuildTypes=1 }
  inBuildTypes && /debug[[:space:]]*\{/ { inDebug=1; next }
  inDebug && /applicationIdSuffix[[:space:]]*"/ {
    if (match($0, /"[^"]+"/)) {
      print substr($0, RSTART + 1, RLENGTH - 2)
      exit
    }
  }
  inDebug && /\}/ { inDebug=0 }
' app/build.gradle)

if [ -z "$BASE_APP_ID" ]; then
  echo "Could not resolve applicationId from app/build.gradle"
  exit 1
fi

APP_ID="${BASE_APP_ID}${DEBUG_SUFFIX}"

# 1. Build the APK
echo "Building APK..."
./gradlew assembleDebug

# 2. Find the APK path
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
  echo "APK not found at $APK_PATH"
  exit 1
fi

echo "APK built at $APK_PATH"

# 3. Find connected device
DEVICE=$(adb devices | awk 'NR==2 {print $1}')
if [ -z "$DEVICE" ]; then
  echo "No device connected."
  exit 1
fi

echo "Deploying APK to device $DEVICE..."
adb -s "$DEVICE" install -r "$APK_PATH"

echo "Launching app..."
adb -s "$DEVICE" shell monkey -p "$APP_ID" -c android.intent.category.LAUNCHER 1

echo "App launched on $DEVICE."
