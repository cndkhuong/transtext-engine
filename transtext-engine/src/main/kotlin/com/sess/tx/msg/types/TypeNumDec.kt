package com.sess.tx.msg.types

import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.impl.FieldNumStr
import com.sess.tx.msg.format.charset.CHARSET

class TypeNumDec : TypeNum() {

    override fun calcByteLength(length: Int): Int = if (length % 2 == 0) length / 2 else (length + 1) / 2

    override fun newField(id: String, min: Int, max: Int, charset: CHARSET, transformId:String, default:String?, vararg args: Any): Field {
        return FieldNumStr (id, min, max, charset, transformId, default)
    }



}