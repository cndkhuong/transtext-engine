package com.sess.tx.msg.impl

import com.sess.tx.msg.FieldNotFound
import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.FieldSet
import com.sess.tx.msg.format.MsgFormat

/**
 * Created by kevin on 7/11/17.
 */
class MsgStd : Msg {

    constructor(format: MsgFormat) : super(format) {
        print("Standard")
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
        var npos: Int = fromPos
        for(i in fields.indices) {
            npos = format.fieldFormats[i].packager.unpack(fields[i] as FieldSet, from, npos)
        }
        _length = npos
        return npos
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int {
        var npos: Int = toPos
        for(i in fields.indices) {
            npos = format.fieldFormats[i].packager.pack(fields[i] as FieldSet, to, npos)
        }
        return npos
    }

    override fun toString(): String = "${this.javaClass.name}[Format=${format.name}]"
}