package com.sess.tx.msg.field

/**
 * Created by kevin on 7/10/17.
 */
interface Field {
    var id: String
    var tranformId: String
    val default: String?

    val min: Int
    val max: Int

    fun isNumeric(): Boolean

    fun isset(): Boolean
    fun data(): Array<Byte?>
    fun length(): Int

    fun lengthOfByte(): Int

    fun str(): String
    fun str(fmt: Char): String
}