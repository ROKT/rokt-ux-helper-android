#!/bin/bash
# Merges the inner layout and outer layout into partner_experiences.json
# Build and install the demo app into the available device/emulator
# Start the app

python3 ./tools/copy_to.py ./demoapp/src/main/assets/inner_layout.json ./demoapp/src/main/assets/partner_experiences.json i
python3 ./tools/copy_to.py ./demoapp/src/main/assets/outer_layout.json ./demoapp/src/main/assets/partner_experiences.json
./gradlew demoapp:installDebug
adb shell am start -n com.rokt.demoapp/com.rokt.demoapp.ui.screen.MainActivity
