package com.rokt.roktux.snapshot

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.RoborazziOptions

private const val SNAPSHOT_CHANGE_THRESHOLD = 0.002F
private const val SNAPSHOT_PIXEL_DISTANCE_THRESHOLD = 0.03F

internal val snapshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(
        changeThreshold = SNAPSHOT_CHANGE_THRESHOLD,
        imageComparator = SimpleImageComparator(maxDistance = SNAPSHOT_PIXEL_DISTANCE_THRESHOLD),
    ),
)
