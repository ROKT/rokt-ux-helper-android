#!/bin/bash
# Merges the inner layout and outer layout into partner_experiences.json
# Build and install the demo app into the available device/emulator
# Start the app

# Function to check if an emulator is running and booted
is_emulator_running() {
    # Get a list of all running emulator/device instances and their states.
    if ! adb devices -l >/tmp/result.txt; then
        echo "Error: adb devices -l command failed"
        return 1
    fi
    if ! tail -n +2 /tmp/result.txt >/tmp/result2.txt; then
        echo "Error: tail command failed"
        return 1
    fi
    if ! grep -v offline /tmp/result2.txt >/tmp/result.txt; then
        echo "Error: grep command failed"
        return 1
    fi
    rm /tmp/result2.txt

    emulator_info=$(cat /tmp/result.txt)
    rm /tmp/result.txt

    if [[ -z ${emulator_info} ]]; then
        echo "No emulator is running."
        return 1
    fi

    echo "Emulator is running."
    return 0
}

# Function to start the default emulator
start_default_emulator() {
    echo "Starting default emulator..."
    if ! emulator -list-avds >/tmp/result.txt; then
        echo "Error: emulator -list-avds command failed"
        return 1
    fi
    emulator_name=$(head -n 1 /tmp/result.txt)

    if [[ -z ${emulator_name} ]]; then
        echo "Error: No AVDs found. Please create an AVD using the AVD Manager."
        return 1
    fi
    rm /tmp/result.txt
    #Use the correct path to call emulator command.
    "${ANDROID_HOME}/emulator/emulator" -avd "${emulator_name}" &
    if ! emulator -avd "${emulator_name}"; then
        echo "Error: emulator -avd command failed"
        return 1
    fi
    local pid=$!

    # Use the correct path to call adb command.
    if ! timeout 60 "${ANDROID_HOME}/platform-tools/adb" -s emulator-"${pid}" wait-for-device shell "if ! while [[ -z $(getprop sys.boot_completed || true) ]]; do sleep 1; done; then exit 1; fi"; then
        echo "Error: Emulator did not boot within the timeout period."
        # Clean up if there is an error
        kill "${pid}"
        return 1
    fi

    echo "Emulator started with name: ${emulator_name} and PID: ${pid}"
    return 0
}

# Check if an emulator is running
if ! is_emulator_running; then
    # Start the default emulator if not running
    start_default_emulator
else
    echo "An emulator is already running. No need to start a new one."
fi

python3 ./tools/copy_to.py ./demoapp/src/main/assets/inner_layout.json ./demoapp/src/main/assets/partner_experiences.json i
python3 ./tools/copy_to.py ./demoapp/src/main/assets/outer_layout.json ./demoapp/src/main/assets/partner_experiences.json
./gradlew demoapp:installDebug
adb shell am start -n com.rokt.demoapp/com.rokt.demoapp.ui.screen.MainActivity
