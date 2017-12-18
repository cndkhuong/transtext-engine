package com.sess.tx.msg.types

import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.impl.FieldNumStr
import com.sess.tx.msg.format.charset.CHARSET

/**
 * Created by kevin on 7/11/17.
 */
class TypeNumStr : Type {
    override fun calcByteLength(length: Int): Int = length

    override fun newField(id: String, min: Int, max: Int, charset: CHARSET, transformId:String, default:String?, vararg args:Any ): Field =
            FieldNumStr(id, min, max, charset, transformId, default )

}