package com.sess.tx.msg.field.impl

import com.sess.tx.msg.MsgException
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.charset.CharsetByte
import com.sess.tx.msg.types.Type
import com.sess.tx.msg.types.TypeNumStr
import java.util.*

/**
 * Created by kevin on 7/10/17.
 */
data class FieldNumStr(override var id: String,
                       override val min: Int,
                       override val max: Int,
                       val charset: CHARSET = CHARSET.CHARSET_NONE,
                       override var tranformId:String,
                       override val default:String?) : BaseField(id, min, max, tranformId, default){
    protected val t: Type = TypeNumStr()
    protected var _length: Int = max

    override fun isNumeric(): Boolean = true

    override fun isset(): Boolean = _length != 0

    override fun data(): Array<Byte?> {
        if (_length == 0) throw MsgException("<$this.data> fields is not set")

        var b: Array<Byte?> = arrayOfNulls<Byte>(_length)

        b = Arrays.copyOf(_data, 0)
        return b
    }

    override fun length(): Int = _length

    override fun lengthOfByte(): Int = t.calcByteLength(_length)

    override fun str(fmt: Char): String {
        if (_length == 0) throw MsgException("<$this.byte> fields is not set")
        return when (fmt) {
            'x', 'X' -> "x|" + _data.take(_length).map { it -> "%02x".format(it) }.toString()
            'b', 'B' -> "b|" + _data.take(_length).map { it -> "%8s".format(Integer.toBinaryString(it!!.toInt() and 0xff)).replace(' ', '0') }.toString()
            else -> str()
        }
    }

    override fun str(): String {
        if (_length == 0) throw MsgException("<$this.str> fields is not set")
        return when (charset ) {
            CHARSET.CHARSET_EBCDIC -> _data.take(_length).map{it -> CharsetByte.e2a[it!!.toInt() and 0xff]}.toString()
            else -> _data.take(_length).toString()
        }
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int {
        if (toPos + _length > to.size) throw MsgException("<$this.pack> overflow buffer value")

        for (i: Int in 0 until _length) {
            val idx: Int = toPos + i
            to[idx] = _data[i] ?: 0x00
        }

        return _length
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int {
        if (fromPos + length > from.size) throw MsgException("<$this.unpack> overflow buffer value")
        if (length < min) throw MsgException("<$this.unpack> unpack length less than minimum ($length < $min)")
        if (length > max) throw MsgException("<$this.unpack> unpack length greater than maximum ($length > $max)")
        _data = arrayOfNulls(length)
        when (charset) {
            CHARSET.CHARSET_EBCDIC ->
                for (i: Int in 0 until length) {
                    val idx: Int = fromPos + i
                    if (from[idx]!! < 0xf0.toByte() || from[idx]!! > 0xf9.toByte()) throw MsgException("<$this.unpack> value with invalid content at $i")
                    _data[i] = from[idx]
                }

            else ->
            for (i: Int in 0 until length) {
                val idx: Int = fromPos + i
                if (from[idx]!! < '0'.toByte() || from[idx]!! > '9'.toByte())
                    throw MsgException("<$this.unpack> value with invalid content at $i")
                _data[i] = from[idx]
            }

        }

        _length = length
        return _length
    }

    override fun default(): Int {
        when(charset) {
            CHARSET. CHARSET_EBCDIC -> for (i in 0 until min) _data[i] = 0xf0.toByte()
            CHARSET. CHARSET_ASCII  -> for (i in 0 until min) _data[i] = 0x30.toByte()
            CHARSET. CHARSET_NONE   -> for (i in 0 until min) _data[i] = '0'.toByte()
        }
        _length = min
        return _length
    }

    fun set(value: Array<Byte>, cs: CHARSET = CHARSET.CHARSET_NONE): Int {
        _length = 0

        if (value.size < min) throw MsgException("<$this.set> value length less than minimum (${value.size} < $min)")
        if (value.size < min) throw MsgException("<$this.set> value length greater than maximum (${value.size} > $max)")

        when(charset) {
            CHARSET.CHARSET_ASCII ->
                when (cs) {
                    CHARSET.CHARSET_ASCII ->
                        for (i: Int in value.indices) {
                            if (value[i]!! < 0x30.toByte() || value[i]!! > 0x39.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                            _data[i] = value[i]
                        }
                    CHARSET.CHARSET_EBCDIC ->
                        for (i: Int in value.indices) {
                            if (value[i]!! < 0xf0.toByte() || value[i]!! > 0xf9.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                            _data[i] = CharsetByte.e2a[value[i]!!.toInt()]
                        }
                    CHARSET.CHARSET_NONE ->
                        for (i: Int in value.indices) {
                            if (value[i]!! < '0'.toByte() || value[i]!! > '9'.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                            _data[i] = value[i]
                        }
            }

            CHARSET.CHARSET_EBCDIC ->
            when(cs) {
                CHARSET.CHARSET_ASCII ->
                for (i: Int in value.indices) {
                    if (value[i]!! < 0x30.toByte() || value[i]!! > 0x39.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = CharsetByte.a2e[value[i]!!.toInt()]
                }
                CHARSET.CHARSET_EBCDIC ->
                for (i: Int in value.indices) {
                    if (value[i]!! < 0xf0.toByte() || value[i]!! > 0xf9.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = value[i]
                }
                CHARSET.CHARSET_NONE ->
                for (i: Int in value.indices) {
                    if (value[i]!! < '0'.toByte() || value[i]!! > '9'.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = CharsetByte.a2e[value[i]!!.toInt()]
                }
            }

            CHARSET.CHARSET_NONE ->
            when(cs) {
                CHARSET.CHARSET_ASCII ->
                for (i: Int in value.indices) {
                    if (value[i]!! < 0x30.toByte() || value[i]!! > 0x39.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = value[i]
                }
                CHARSET. CHARSET_EBCDIC ->
                for (i: Int in value.indices) {
                    if (value[i]!! < 0xf0.toByte() || value[i]!! > 0xf9.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = CharsetByte.e2a[value[i]!!.toInt()]
                }
                CHARSET.CHARSET_NONE ->
                for (i: Int in value.indices) {
                    if (value[i]!! < '0'.toByte() || value[i]!! > '9'.toByte()) throw MsgException("<$this.set> value with invalid content $cs at $i")
                    _data[i] = value[i]
                }
            }
        }

        _length = value.size
        return _length
    }

    override fun set(value: String): Int {
        val v: String = if (min != max) {
            if (value.length < min) throw MsgException("<$this.set> value length less than minimum (${value.length} < $min)")
            if (value.length > max) throw MsgException("<$this.set> value length greater than maximum (${value.length} > $max)")
            value
        }
        else {
            if (value.length > max) throw MsgException("<$this.set> value length greater than maximum (${value.length} > $max)")

            val n: Int = max - value.length
            if (n != 0) {
                val value1: String = ("0".repeat(n)) + value
                value1
            }
            else {
                value
            }
        }

        return set(v.toByteArray().toTypedArray(), CHARSET.CHARSET_NONE)
    }

    override fun unset(): Unit {_length = 0}

    override fun toString(): String = "${this.javaClass.name}[$id]"

}