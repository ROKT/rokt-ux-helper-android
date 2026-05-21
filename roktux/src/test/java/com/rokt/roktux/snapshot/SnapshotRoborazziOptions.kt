package com.rokt.roktux.snapshot

import com.github.takahirom.roborazzi.RoborazziOptions

private const val SNAPSHOT_CHANGE_THRESHOLD = 0.002F

internal val snapshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(
        changeThreshold = SNAPSHOT_CHANGE_THRESHOLD,
    ),
)
