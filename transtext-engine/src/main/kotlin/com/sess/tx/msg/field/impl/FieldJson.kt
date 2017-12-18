package com.sess.tx.msg.field.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.GsonJsonProvider
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider
import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.format.charset.CharsetByte
import com.sess.tx.msg.impl.MsgJson
import com.sess.tx.msg.types.Type
import com.sess.tx.msg.types.TypeByte
import java.util.*

/**
 * Created by kevin on 7/10/17.
 */
data class FieldJson(override var id: String,
                     override val min: Int,
                     override val max: Int,
                     val charset: CHARSET = CHARSET.CHARSET_NONE,
                     override var tranformId:String,
                     override val default:String?) : BaseField(id, min, max, tranformId, default) {


    protected val t: Type = TypeByte()
    protected var _length: Int = max

    override fun isNumeric(): Boolean = false

    override fun isset(): Boolean = _length != 0

    override fun data(): Array<Byte?> {
//        if (_length == 0) throw MsgException("$this.data fields is not set")
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

    override fun str(): String {
        var str=""
        for( b in _data){
            if(b != null)
                str += String(byteArrayOf(b))
        }
        return str
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int {
        val json:JsonObject = if(to.filterNotNull().size <= 0) Gson().fromJson("{}", JsonObject::class.java) else Gson().fromJson(String( to.filterNotNull().toByteArray() ), JsonObject::class.java)
        if (json.isJsonNull) throw MsgException("<$this.pack> json format not corrected")

        val configuration = Configuration.defaultConfiguration()
        configuration.jsonProvider(GsonJsonProvider())
        configuration.mappingProvider(GsonMappingProvider())
        configuration.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)

        val jsonPath = if(to.filterNotNull().size <= 0) JsonPath.using(configuration).parse("{}") else JsonPath.using(configuration).parse(String( to.filterNotNull().toByteArray() ))
        if(_data.lastOrNull() != null) {
            val seg = id.split(".")
            var index = 0
            var node = ""
            val obj = JsonObject()
            var tmpObj = JsonObject()
            for(key in seg){
                node += ".${key}"
                if(index == 0) {
                    json.add(key, obj)
                } else {
                    if (index < seg.size) {
                        tmpObj = JsonObject()
                    } else {
                        tmpObj.addProperty(key, _data.toString())
                    }
                    obj.add(key, tmpObj)
                }
                index++
            }

        }
        jsonPath.jsonString()
        val bytes =  GsonBuilder().create().toJson(json).toByteArray()
        if(to.size > bytes.size){
            for(i in 0 until to.size) {
                if( i < bytes.size) {
                    to[i] = bytes[i]
                } else {
                    to[i] = ' '.toByte()
                }
            }

        }else {
            for (i in 0 until bytes.size) {
                if (i < to.size) {
                    to[i] = bytes[i]
                }
            }
        }
        _length = bytes.size
        return _length
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int {

        var element:String? = null

        var obj: JsonObject = MsgJson.threadLocalValue.get()
        if (obj.isJsonNull) throw MsgException("<$this.pack> json format not corrected")
        val seg = id.split(".")
        var index=0
        for (key in seg) {
            index++
            if (obj != null) {
                if(index < seg.size) {
                    obj = obj.get(key).asJsonObject
                } else {
                    element = obj.get(key)?.asString
                }
            }
        }
        if(element != null) {
            _length = set(element.toByteArray().toTypedArray())
            _data = arrayOfNulls(_length)
            for (i in 0.._length-1){
                _data[i] = element.toCharArray()[i].toByte()
            }
        }

        return _length
    }

    override fun default(): Int {
        for (i in 0 until min) _data[i] = 0x00
        _length = min
        return _length
    }
    override fun set(value: String): Int {
        return set(value.toByteArray().toTypedArray())
    }
    fun set(value: Array<Byte>, cs: CHARSET = CHARSET.CHARSET_NONE): Int {
        _length = 0
        _data = arrayOfNulls(value.size)
//        if (value?.size < min) throw MsgException("<$this.set> value length less than minimum (${value.size} < $min)")
//        if (value?.size > max) throw MsgException("<$this.set> value length greater than maximum (${value.size} > $max)")

        if ( charset == CHARSET.CHARSET_NONE || charset == cs) {
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

        _length = value.size
        return _length
    }

    override fun unset(): Unit {_length = 0}

    override fun toString(): String = "${this.javaClass.name}[$id]"

}