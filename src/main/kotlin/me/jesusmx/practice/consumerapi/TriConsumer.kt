package me.jesusmx.practice.consumerapi

import java.util.*

fun interface TriConsumer<X, Y, Z> {
    fun accept(x: X, y: Y, z: Z)
    fun andThen(after: TriConsumer<in X, in Y, in Z>): TriConsumer<X, Y, Z>? {
        Objects.requireNonNull(after)
        return TriConsumer { x: X, y: Y, z: Z ->
            accept(x, y, z)
            after.accept(x, y, z)
        }
    }
}