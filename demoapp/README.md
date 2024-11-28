# Rokt UX Helper Demo App Android

## Overview

This demo app showcases the integration of the Rokt UX SDK into an Android application. It utilizes the `networkhelper` module to retrieve experiences from the Rokt backend and the `roktux` module to handle UI rendering and user interactions.

```kotlin
dependencies {
   implementation(projects.roktux)
   implementation(projects.networkhelper)
}
```

## Resident Experts

-   Thomson Thomas - <thomson.thomas@rokt.com>
-   Sahil Suri - <sahil.suri@rokt.com>
-   Lewis Krishnamurti - <lewis.raj.krishnamurti@rokt.com>

## Requirements

-   The latest version of [Android Studio](https://developer.android.com/studio).
-   Android 5.0 (API level 21) and above
-   Android Gradle Plugin 8.1.2
-   Gradle 8.9+

### Configure the app

To run the demo app, you'll have to:

1. Add a file local.properties in the root project (this file should NOT be under version control to protect your keys)
2. Add below lines to local.properties that looks like

```text
BASE_URL=YOUR_BASE_URL, where YOUR_BASE_URL is the base url to your or Rokt backend.
VIEW_NAME=YOUR_VIEW_NAME, where YOUR_VIEW_NAME is the view name you get from Rokt.
ROKT_TAG_ID=YOUR_TAG_ID, where YOUR_TAG_ID is the tag id you get from Rokt.
ROKT_PUB_ID=YOUR_PUB_ID, where YOUR_PUB_ID is the pub id you get from Rokt.
ROKT_SECRET=YOUR_SECRET, where YOUR_SECRET is the secret you get from Rokt.
ROKT_CLIENT_UNIQUE_ID=YOUR_CLIENT_UNIQUE_ID, where YOUR_CLIENT_UNIQUE_ID is the client unique id you get from Rokt.
```

## Features

### Interactive Tutorials

The demo app provides a comprehensive set of interactive tutorials that guide you through the process of integrating Rokt UX into your app. These tutorials cover various configurations and use cases, allowing you to learn by doing.

**Location:** `/src/main/java/com/rokt/demoapp/ui/screen/tutorials`

### QR Scanner Layout Builder (Internal Tool)

This tool, intended for internal Rokt testing, allows you to create and preview layouts by scanning QR codes.

**Location:** `demoapp/src/main/java/com/rokt/demoapp/ui/screen/layout`

### Custom Layout Builder (Internal Tool)

This feature enables you to test custom layouts using custom attributes.

**Location:** `demoapp/src/main/java/com/rokt/demoapp/ui/screen/custom`
