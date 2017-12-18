package com.sess.tx.msg.format

import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgException
import com.sess.tx.msg.format.Format

/**
 * Created by kevin on 7/11/17.
 */
data class MsgFormat(val name: String,
                val desc: String,
                val formatterType: Class<Msg>) : Format(name, desc) {

    val fieldFormats = mutableListOf<FieldFormat>()
    val fieldsByName = mutableMapOf<String, Int>()

    private var _minLength: Int = 0
    private var _maxLength: Int = 0

    fun registerField(fieldFormat: FieldFormat): Unit {
        if (fieldsByName.contains(fieldFormat.name))
            throw MsgException("<$this.registerFields> duplicate fields name ${fieldFormat.name}")

        fieldFormats += fieldFormat
        fieldsByName[fieldFormat.name] = fieldFormats.size - 1

        _minLength += (if (fieldFormat.mandatory != null) fieldFormat.minLength() else 0)
        _maxLength += fieldFormat.maxLength()

    }

    fun minLength(): Int = _minLength
    fun maxLength(): Int = _maxLength

    fun msg(): Msg {
        val ctorArgs: Msg = formatterType.constructors[0].newInstance(this) as Msg
        return ctorArgs
    }

    override fun toString(): String  {
        return "${this.javaClass.name}[$name]"
    }
}