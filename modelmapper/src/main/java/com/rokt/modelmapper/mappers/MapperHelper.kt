package com.rokt.modelmapper.mappers

import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.StatelessStylingBlock
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

// Helper function to create BasicStateStylingBlock
internal fun <T, R> BasicStateStylingBlock<T>.toBasicStateStylingBlock(
    transform: (T) -> R?,
): BasicStateStylingBlock<R?> {
    return BasicStateStylingBlock(
        default = transform(this.default),
        pressed = this.pressed?.let(transform),
    )
}

internal fun <E> ImmutableList<StatelessStylingBlock<E>>?.toBasicStateStylingBlock(): ImmutableList<BasicStateStylingBlock<E>>? =
    this?.map {
        BasicStateStylingBlock(
            default = it.default,
        )
    }?.toImmutableList()
