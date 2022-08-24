package net.frozenorb.potpvp.kt.command.data.processor

interface Processor<T, R> {

    fun process(type: T): R

}