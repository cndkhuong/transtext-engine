package com.sess.tx.msg.field.impl

import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.format.charset.CharsetByte
import com.sess.tx.msg.types.Type
import com.sess.tx.msg.types.TypeStr
import java.util.*

/**
 * Created by kevin on 7/10/17.
 */
data class FieldStr(override var id: String,
               override val min: Int,
               override val max: Int,
               val charset: CHARSET = CHARSET.CHARSET_NONE,
                override var tranformId:String,
                override val default:String?) : BaseField(id, min, max, tranformId, default) {
    protected val t: Type = TypeStr()
    protected var _length: Int = max

    override fun isNumeric(): Boolean = false

    override fun isset(): Boolean = _length != 0

    override fun data(): Array<Byte?> {
        if (_length == 0) throw MsgException("<$this.data> fields is not set")
        var b: Array<Byte?> = arrayOfNulls<Byte>(_length)

        b = Arrays.copyOf(_data, _length)
        return b
    }

    override fun length(): Int = _length

    override fun lengthOfByte(): Int = t.calcByteLength(_length)

    override fun str(fmt: Char): String {
        if (_length == 0) throw MsgException("<$this.byte> fields is not set")
        return when (fmt) {
            'x' ,'X' -> "x|" + _data.take(_length).map{it -> "%02x".format(it)}.toString()
            'b' ,'B' -> "b|" + _data.take(_length).map{it -> "%8s".format(Integer.toBinaryString( it!!.toInt() and 0xff)  ).replace(' ', '0') }.toString()
            else -> str()
        }
    }

    override fun str(): String {
        if (_length == 0) throw MsgException("<$this.str> fields is not set")
        return when(charset) {
            CHARSET.CHARSET_EBCDIC -> _data.take(_length).map{ it  -> CharsetByte.e2a[it!!.toInt() and 0xff]}.toString()
            else ->
                if(_data.first() == null ) "" else {
                    var str=""
                    for(b in _data){
                        if(b != null)
                            str += String(byteArrayOf(b))
                    }
                    return str
                }
        }
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int  {
        if (toPos + _length > to.size) throw MsgException("<$this.pack> overflow buffer value")

        for (i: Int in 0 until _length) {
            val idx: Int = toPos + i
            to[idx] = _data[i] ?: 0x00
        }

        return _length
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int {
        if (fromPos + length > from.size)
            throw MsgException("<$this.unpack> overflow buffer value")
        if (length < min)
            throw MsgException("<$this.unpack> unpack length less than minimum ($length < $min)")
        if (length > max)
            throw MsgException("<$this.unpack> unpack length greater than maximum ($length > $max)")

        _data = arrayOfNulls(length)
        set(Arrays.copyOfRange(from, fromPos, fromPos + length), CHARSET.CHARSET_EBCDIC)

        _length = length
        return length
    }

    override fun default() : Int {
        when(charset){
            CHARSET.CHARSET_EBCDIC -> for (i in 0 until min) _data[i] = 0x40.toByte()
            CHARSET.CHARSET_ASCII  -> for (i in 0 until min) _data[i] = 0x20.toByte()
            CHARSET.CHARSET_NONE   -> for (i in 0 until min) _data[i] = ' '.toByte()
        }
        _length = min
        return _length
    }

    fun set(value: Array<Byte>, cs: CHARSET = CHARSET.CHARSET_NONE): Int {
        _length = 0

        if (value.size < min) throw MsgException("<$this.set> value length less than minimum (${value.size} < $min)")
        if (value.size < min) throw MsgException("<$this.set> value length greater than maximum (${value.size} > $max)")

        if (charset == cs) {
            _data = Arrays.copyOf(value, value.size)
        }
        else {
            when(charset){
                CHARSET.CHARSET_EBCDIC -> for (i: Int in value.indices) _data[i] = CharsetByte.a2e[value[i].toInt() and 0xff]
                else ->
                if (cs == CHARSET.CHARSET_EBCDIC) {
                    for (i: Int in value.indices) _data[i] = CharsetByte.e2a[value[i].toInt() and 0xff]
                }
                else {
                    for (i: Int in value.indices) _data[i] = value[i]
                }
            }
        }

        _length = _data.size
        return _length
    }

    override fun set(value: String): Int = set(value.toByteArray().toTypedArray())

    override fun unset(): Unit {
        _length = 0
    }

    override fun toString() : String = "${this.javaClass.name}[$id]"
}