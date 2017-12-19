package com.sess.tx.msg.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sess.tx.msg.FieldNotFound
import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.FieldSet
import com.sess.tx.msg.field.impl.FieldArray
import com.sess.tx.msg.field.impl.FieldJson
import com.sess.tx.msg.field.impl.FieldStr
import com.sess.tx.msg.format.FieldFormat
import com.sess.tx.msg.format.MsgFormat

/**
 * Created by kevin on 7/11/17.
 */
class MsgJson : Msg {
    constructor(format: MsgFormat) : super(format) {
        init()
    }


    var _length: Int = 0

    fun init() {
        for (fmt in format.fieldFormats) {
            if (fmt.mandatory == null) throw MsgException("<$this> fields with mandatory = false not allowed")
            val field: Field = fmt.newField()
            fields += field
            fieldsByName.put(fmt.name, field)
            _length += fmt.maxLength()
        }
    }

    override fun length(): Int = _length

    override fun unset(fname: String): Unit {
        try {
            (fieldsByName[fname] as FieldSet).default()
        }
        catch(e:NoSuchElementException) {
            throw FieldNotFound("<$this.update>", fname)
        }catch (e:Exception){
            throw e
        }
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int): Int {
        var gson: JsonObject;
        if(MsgJson.threadLocalValue.get() == null){
            gson = Gson().fromJson(String(from.filterNotNull().toByteArray()), JsonObject::class.java)
            MsgJson.threadLocalValue.set(gson)
        }else{
            gson = MsgJson.threadLocalValue.get()
        }
        var npos: Int = fromPos

        for(i in fields.indices) {
            val field = fields[i]
            val fieldFormat = format.fieldFormats[i]
            if(field.id.contains("[]"))
                buildFields(gson, field, fieldFormat)
        }

        fields.removeIf{ it.id.contains("[]") }

        for(i in fields.indices) {
            if(fields[i].id.contains("[]")) {
                fields.removeAt(i)
            }else {
                npos = format.fieldFormats[i].packager.unpack(fields[i] as FieldSet, from, npos)
            }
        }

        _length = npos
        return npos
    }

    private fun buildFields(gson:JsonObject, field:Field, fieldFormat:FieldFormat){
        if(field.id.contains("[]")) {
            fields.remove(field)
            format.fieldFormats.remove(fieldFormat)
            val arr = gson.getAsJsonArray(field.id.substringBefore("["))
            var subPos: Int = 0
            for(elementValue in arr) {
                val idElement = field.id.replaceFirst("[]", "[${subPos}]")
                val newField = (field as FieldArray).copy(id = idElement)

                val newFieldFormat = fieldFormat.copy(name = idElement, field = idElement)

                if(newField.id.contains("[]")) {
                    fields.remove(field)
                    format.fieldFormats.remove(fieldFormat)
                    return buildFields(gson, newField, newFieldFormat)
                }else{
                    fields.add(newField)
                    format.fieldFormats.add(newFieldFormat)
                }
                subPos += 1
            }
        }
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int {
        var npos: Int = toPos
        for(i in fields.indices) {
            npos = format.fieldFormats[i].packager.pack(fields[i] as FieldSet, to, npos)
        }
        return npos
    }

    override fun toString(): String {
        var json = mutableMapOf<String, Any?>()
        for(f in fields){
            val b = f as BaseField
            val seg = f.id.split("""[\.|[\d\]|](\w*)""".toRegex())
            var index = 0
            var element =  mutableMapOf<String, Any?>()
            for(key in seg){
                if(index == 0) {
                    if(json.containsKey(key))
                        element = json.get(key) as MutableMap<String, Any?>
                    else {
                        element = mutableMapOf<String, Any?>()
                        json.put(key, element)
                    }
                }else {

                    if(index == seg.size-1)
                        element.put(key, String(f.data().filterNotNull().toByteArray()))
                    else {
                        element.put(key, if(element.containsKey(key)) element.get(key) else mutableMapOf<String, Any?>())
                        element = element.get(key) as MutableMap<String, Any?>
                    }
                }


                index++
            }
        }

        return Gson().toJson(json)
    }
    companion object {
        var threadLocalValue = ThreadLocal<JsonObject>()
    }
}