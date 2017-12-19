package com.sess.tx.msg.field.impl

import com.google.gson.*
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

data class FieldArray(override var id: String,
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
        var json: JsonObject = if(to.filterNotNull().size <= 0) Gson().fromJson("{}", JsonObject::class.java) else Gson().fromJson(String( to.filterNotNull().toByteArray() ), JsonObject::class.java)
        if (json.isJsonNull) throw MsgException("<$this.pack> json format not corrected")

        packLoop(json, json, id, id.substringBefore("["), -1)

        _length = json.toString().toByteArray().size
        return _length
    }

    private fun packLoop(toObj: JsonObject, fromObj: JsonObject, path: String, id: String, index: Int) : Any{
//        val jsonValue = if(index >= 0) (fromObj as JsonArray).get(index).asJsonObject.get(id) else (fromObj as JsonObject).get(id)
        if(id == null) return toObj
        else {
            val jsonValue = JsonObject()
        }
        val nextPath = """(?<=${id})(?s)(.*$)""".toRegex().find(path)?.groupValues?.get(0)
        val nextId = if(nextPath?.split("""[\.|\]](\w*)""".toRegex())?.size!! > 2 )
                                """(\.|\])(.*?)(\.|\[)""".toRegex().find(nextPath!!)?.groupValues?.get(2)
                            else
                                """[\.|\]](\w*)""".toRegex().find(nextPath)?.groupValues?.get(1)
        if(fromObj.isJsonArray) {
            val nextIndex = "[\\d]".toRegex().find(nextPath!!)?.groupValues?.get(0)?.toInt()
            (fromObj as JsonArray).set(index, jsonValue as JsonElement)
            return packLoop(toObj, jsonValue as JsonArray, nextPath, nextId!!, nextIndex!!)
        }else {
            toObj.add(id, jsonValue)
            return packLoop(toObj, jsonValue as JsonObject, nextPath!!, nextId!!, -1)
        }
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int, length: Int): Int {

        var obj: JsonObject = MsgJson.threadLocalValue.get()
        if (obj.isJsonNull) throw MsgException("<$this.pack> json format not corrected")
        var value = ""
        try {
            value = unpackLoop(obj, id, id.substringBefore("["), -1) as String
        }catch (e:Exception ){
            print(id)
        }

        _data = value.toByteArray().toTypedArray() as Array<Byte?>
        _length = value.length
        return _length
    }

    private fun unpackLoop(fromObj: Any, path: String, id: String, index: Int) : Any{

        val jsonValue = if(index >= 0) (fromObj as JsonArray).get(index).asJsonObject.get(id) else (fromObj as JsonObject).get(id)
        if(jsonValue.isJsonPrimitive) return jsonValue.toString()
        else {
            val nextPath = """(?<=${id})(?s)(.*$)""".toRegex().find(path)?.groupValues?.get(0)
            val nextId = if(nextPath?.split("""[\.|\]](\w*)""".toRegex())?.size!! > 2 )
                                    """(\.|\])(.*?)(\.|\[)""".toRegex().find(nextPath!!)?.groupValues?.get(2)
                                 else
                                    """[\.|\]](\w*)""".toRegex().find(nextPath)?.groupValues?.get(1)
            if(jsonValue.isJsonArray) {
                val nextIndex = "[\\d]".toRegex().find(nextPath!!)?.groupValues?.get(0)?.toInt()
                return unpackLoop(jsonValue as JsonArray, nextPath, nextId!!, nextIndex!!)
            }else {
                return unpackLoop(jsonValue as JsonObject, nextPath!!, nextId!!, -1)
            }
        }
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