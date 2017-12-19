package com.sess.tx.msg.format

import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.packager.Packager
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.types.Type

/**
 * Created by kevin on 7/11/17.
 */
data class FieldFormat(val name: String,
                  val field: String,
                  val desc: String,
                  val ftype: Type,
                  val charset: CHARSET,
                  val min: Int,
                  val max: Int,
                  val mandatory: Boolean?,
                  val default: String?,
                  val packager: Packager) : Format(name, desc) {

    fun newField(vararg args: Any) : Field = ftype.newField(name, min, max, charset, field, default, args)

    fun minLength(): Int = packager.lengthUsed + ftype.calcByteLength(min)

    fun maxLength(): Int = packager.lengthUsed + ftype.calcByteLength(max)
}