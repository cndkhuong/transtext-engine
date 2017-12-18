package com.sess.tx.msg.impl

// Fixed `com.sess.tx.msg.impl.batch()` and sliding `com.sess.tx.msg.impl.slide()` windows suited for lists and sequences.

fun <T> Iterable<T>.batch(size: Int): Sequence<List<T>> {
    return BatchingSequence(this, size)
}

fun <T> Sequence<T>.batch(size: Int): Sequence<List<T>> {
    return BatchingSequence(this.asIterable(), size)
}

fun <T> Iterable<T>.slide(size: Int,
                          step: Int = 1): Sequence<List<T>> {
    return SlidingSequence(this, size, step)
}

fun <T> Sequence<T>.slide(size: Int,
                          step: Int = 1): Sequence<List<T>> {
    return SlidingSequence(this.asIterable(), size, step)
}

internal class BatchingSequence<out T>(val source: Iterable<T>,
                                       val batchSize: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        private val iterator = if (batchSize > 0) source.iterator() else emptyList<T>().iterator()

        override fun computeNext() = when {
            iterator.hasNext() -> {
                val next = iterator.asSequence().take(batchSize).toList()
                setNext(next)
            }
            else -> done()
        }
    }
}

internal class SlidingSequence<out T>(val source: Iterable<T>,
                                      val slideSize: Int,
                                      val slideStep: Int) : Sequence<List<T>> {
    override fun iterator(): Iterator<List<T>> = object : AbstractIterator<List<T>>() {
        private val iterator = if (slideSize > 0) source.iterator() else emptyList<T>().iterator()
        private var buffer = listOf<T>()

        override fun computeNext() = when {
            iterator.hasNext() -> {
                buffer = buffer.drop(slideStep).let {
                    it + iterator.asSequence().take(slideSize - it.size)
                }
                setNext(buffer)
                
                //val next = buffer.drop(slideStep) + iterator.asSequence()
                //    .take(slideSize - Math.max(buffer.size - slideStep, 0))
                //buffer = next
                //setNext(next)
            }
            else -> done()
        }
    }
}
