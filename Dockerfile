FROM ubuntu:jammy-20230425

HEALTHCHECK NONE

# Set up environment
RUN apt-get update && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends \
    openjdk-17-jdk=17.0.12+7-1ubuntu2~22.04 \
    wget=1.21.2-2ubuntu1.1 \
    unzip=6.0-26ubuntu3.2 \
    && \
    groupadd --gid=1002 rokt && \
    useradd --uid=1001 --gid=rokt --create-home rokt && \
    rm -rf /var/lib/apt/lists/*

# Set up Java environment
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV JDK_HOME=${JAVA_HOME}
ENV JRE_HOME=${JDK_HOME}

# Set up Android environment
ENV ANDROID_HOME=/home/rokt/android-sdk
ENV ANDROID_SDK_ROOT=${ANDROID_HOME}
ENV CMDLINE_TOOLS_ROOT=${ANDROID_HOME}/cmdline-tools/latest/bin
ENV PATH "${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/platform-tools/bin:${PATH}"

RUN SDK_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" && \
	mkdir -p ${ANDROID_HOME}/cmdline-tools && \
	mkdir ${ANDROID_HOME}/platforms && \
	mkdir ${ANDROID_HOME}/ndk && \
	wget -O /tmp/cmdline-tools.zip -t 5 "${SDK_TOOLS_URL}" -q && \
	unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools && \
	rm /tmp/cmdline-tools.zip && \
	mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest

SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "tools" && \
    echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "platform-tools" && \
    echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "build-tools;33.0.3" && \
    echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "build-tools;34.0.0" && \
    echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "platforms;android-33" && \
    echo y | ${CMDLINE_TOOLS_ROOT}/sdkmanager "platforms;android-34"

USER rokt
