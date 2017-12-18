package com.sess.tx.msg.types

import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.impl.FieldByte
import com.sess.tx.msg.format.charset.CHARSET

/**
 * Created by kevin on 7/12/17.
 */
class TypeByte : Type {
    override fun calcByteLength(length: Int): Int = length

    override fun newField(id: String, min: Int, max: Int, charset: CHARSET, transformId:String, default:String?, vararg args: Any): Field =
            FieldByte(id, min, max, transformId, default)
}