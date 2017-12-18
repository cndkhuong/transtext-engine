package com.sess.tx.msg.types

import com.sess.tx.msg.field.Field
import com.sess.tx.msg.format.charset.CHARSET

/**
 * Created by kevin on 7/10/17.
 */
interface Type {
    fun calcByteLength(length: Int): Int

    fun newField(id: String, min: Int, max: Int, charset: CHARSET = CHARSET.CHARSET_NONE, transformId:String, default:String?, vararg args:Any): Field
}