package com.sess.tx.msg.field.packager

import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.FieldSet

/**
 * Created by kevin on 7/11/17.
 */
data class LengthFixed(override val mask: Byte) : Packager {
    override val lengthUsed: Int = 0

    override fun unpack(field: FieldSet, from: Array<Byte?>, fromPos: Int): Int {
        if (field.min != field.max)
            throw MsgException("<${this.javaClass.name}.unpack> ${field.id} not fixed length fields")

        return fromPos + field.unpack(from, fromPos, field.min)
    }

    override fun pack(field: FieldSet, to: Array<Byte?>, toPos: Int): Int {
        if (field.min != field.max)
            throw MsgException("<${this.javaClass.name}.unpack> pack ${field.id} failed: fields is not fixed length fields")

        return toPos + field.pack(to, toPos)
    }
}
