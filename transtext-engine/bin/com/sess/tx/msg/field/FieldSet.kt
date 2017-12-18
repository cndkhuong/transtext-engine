package com.sess.tx.msg.field

/**
 * Created by kevin on 7/10/17.
 */
interface FieldSet : Field {
    fun pack(to: Array<Byte?>, toPos: Int): Int
    fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int

    fun default(): Int
    fun set(value: String): Int
    fun unset(): Unit
}