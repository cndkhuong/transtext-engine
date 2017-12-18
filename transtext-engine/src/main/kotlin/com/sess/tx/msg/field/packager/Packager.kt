package com.sess.tx.msg.field.packager

import com.sess.tx.msg.field.FieldSet

/**
 * Created by kevin on 7/10/17.
 */
interface Packager {
    val mask:Byte
    val lengthUsed:Int

    fun unpack(field: FieldSet, from: Array<Byte?>, fromPos: Int): Int
    fun pack(field: FieldSet, to: Array<Byte?>, toPos: Int): Int
}