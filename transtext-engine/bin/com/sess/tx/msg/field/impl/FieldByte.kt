package com.sess.tx.msg.field.impl

import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.types.Type
import com.sess.tx.msg.types.TypeByte
import java.util.*

/**
 * Created by kevin on 7/10/17.
 */
data class FieldByte(override var id: String,
                     override val min: Int,
                     override val max: Int,
                     override var tranformId:String,
                     override var default:String?) : BaseField(id, min, max, tranformId, default) {

        protected val t: Type = TypeByte()
        protected var _length: Int = max

        override fun isNumeric(): Boolean = false

        override fun isset(): Boolean = _length != 0

        override fun data(): Array<Byte?> {
            if (_length == 0) throw MsgException("$this.data fields is not set")
            var b: Array<Byte?> = arrayOfNulls<Byte>(_length)
            b = Arrays.copyOf(_data, _length)
            return b
        }

        override fun length(): Int = _length

        override fun lengthOfByte(): Int = t.calcByteLength(_length)

        override fun str(fmt: Char): String {
            if (_length == 0) throw MsgException("<$this.str> fields is not set")
            return when(fmt){
                'x' , 'X' -> "x|" + _data.take(_length).map{ it -> "%02x".format(it) }.toString()
                'b' , 'B' -> "b|" + _data.take(_length).map{ it -> "%8s".format(Integer.toBinaryString(it!!.toInt() and 0xff)).replace(' ', '0') }.toString()
                else -> throw MsgException("<$this.str> invalid format character")
            }
        }

        override fun str(): String = str('x')

        override fun pack(to: Array<Byte?>, toPos: Int): Int {
            if (toPos + _length > to.size) throw MsgException("<$this.pack> overflow buffer value")

            for (i: Int in 0 until _length) {
                val idx: Int = toPos + i
                to[idx] = data()[i]!!
            }

            return _length
        }

        override fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int {
            if (fromPos + length > from.size) throw MsgException("<$this.unpack> overflow buffer value")
            if (length < min) throw MsgException("<$this.unpack> unpack length less than minimum ($length < $min)")
            if (length > max) throw MsgException("<$this.unpack> unpack length greater than maximum ($length > $max)")
            _data = arrayOfNulls(length)
            _data = Arrays.copyOfRange(from, fromPos, fromPos + length)

            _length = length
            return _length
        }

        override fun default(): Int {
            for (i in 0 until min) _data[i] = 0x00
            _length = min
            return _length
        }

        override fun set(value: String): Int = TODO()

        override fun unset(): Unit {_length = 0}

        override fun toString(): String = "${this.javaClass.name}[$id]"
    }