package com.sess.tx.msg.field.packager

import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.FieldSet
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.types.TypeNum

data class LengthVar(override val mask: Byte,val varType: TypeNum,
                     val varLen: Int,
                     val varCharset: CHARSET = CHARSET.CHARSET_NONE, val transformId:String, val default:String?) : Packager {

    init {
        if (varType == null) throw MsgException("<$this> invalid VAR Type value (isEmpty)")
        if (varLen <= 0) throw MsgException("<$this.getClass> invalid VAR Len value (<= 0")
    }

    override val lengthUsed: Int = varType.calcByteLength(varLen)

    override fun unpack(field: FieldSet, from: Array<Byte?>, fromPos: Int): Int {
        val flen: FieldSet = varType.newField("FLEN", varLen, varLen, varCharset, transformId, default) as FieldSet

        val npos: Int = fromPos + flen.unpack(from, fromPos, varLen)
        val nlen: Int = flen.str().toInt()

        return npos + field.unpack(from, npos, nlen)
    }

    override fun pack(field: FieldSet, to: Array<Byte?>, toPos: Int): Int {
        val flen: FieldSet = varType.newField("FLEN", varLen, varLen, varCharset, transformId, default) as FieldSet

        flen.set(field.length().toString())

        val npos: Int = toPos + flen.pack(to, toPos)
        return npos + field.pack(to, npos)
    }
}