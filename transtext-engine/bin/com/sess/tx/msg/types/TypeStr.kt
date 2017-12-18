package com.sess.tx.msg.types

import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.impl.FieldStr

/**
 * Created by kevin on 7/10/17.
 */
class TypeStr:Type {

    override fun calcByteLength(length: Int): Int = length

    override fun newField(id: String, min: Int, max: Int, charset: CHARSET, transformId:String, default:String?, vararg args: Any): Field {
        return FieldStr(id, min, max, charset, transformId, default )
    }

}