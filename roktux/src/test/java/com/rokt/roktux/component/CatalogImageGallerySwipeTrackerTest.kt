package com.rokt.roktux.component

import com.rokt.roktux.event.RoktUserInteractionAction
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CatalogImageGallerySwipeTrackerTest {

    @Test
    fun `clears explicit target after non-scrolling page jump`() {
        val tracker = CatalogImageGallerySwipeTracker()

        tracker.markExplicitNavigationTarget(1)
        tracker.markStateSyncTarget(1)

        assertThat(
            tracker.onPagerScrollChanged(
                isScrollInProgress = false,
                settledPage = 1,
                currentPage = 1,
            ),
        ).isNull()

        tracker.onPagerScrollChanged(
            isScrollInProgress = true,
            settledPage = 0,
            currentPage = 0,
        )

        assertThat(
            tracker.onPagerScrollChanged(
                isScrollInProgress = false,
                settledPage = 1,
                currentPage = 1,
            ),
        ).isEqualTo(RoktUserInteractionAction.MainImageSwipeLeft)
    }

    @Test
    fun `suppresses scrolling page jumps only once`() {
        val tracker = CatalogImageGallerySwipeTracker()

        tracker.markStateSyncTarget(1)
        tracker.onPagerScrollChanged(
            isScrollInProgress = true,
            settledPage = 0,
            currentPage = 0,
        )

        assertThat(
            tracker.onPagerScrollChanged(
                isScrollInProgress = false,
                settledPage = 1,
                currentPage = 1,
            ),
        ).isNull()

        tracker.onPagerScrollChanged(
            isScrollInProgress = true,
            settledPage = 0,
            currentPage = 0,
        )

        assertThat(
            tracker.onPagerScrollChanged(
                isScrollInProgress = false,
                settledPage = 1,
                currentPage = 1,
            ),
        ).isEqualTo(RoktUserInteractionAction.MainImageSwipeLeft)
    }

    @Test
    fun `reports swipe direction from scroll start and end page`() {
        val tracker = CatalogImageGallerySwipeTracker()

        tracker.onPagerScrollChanged(
            isScrollInProgress = true,
            settledPage = 1,
            currentPage = 1,
        )

        assertThat(
            tracker.onPagerScrollChanged(
                isScrollInProgress = false,
                settledPage = 0,
                currentPage = 0,
            ),
        ).isEqualTo(RoktUserInteractionAction.MainImageSwipeRight)
    }
}
