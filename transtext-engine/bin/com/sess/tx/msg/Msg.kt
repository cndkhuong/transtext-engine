package com.sess.tx.msg

import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.FieldSet
import com.sess.tx.msg.format.MsgFormat

/**
 * Created by kevin on 7/11/17.
 */
abstract class Msg(val format: MsgFormat) : MsgRef {
    val fieldsByName = mutableMapOf<String, Field>()
    open val fields = mutableListOf<Field>()

    init {
        var f: Array<Field> = arrayOf()
        for (fmt in format.fieldFormats) {
            val field: Field = fmt.newField()
            f = f + field
            fieldsByName[fmt.name] = field
        }
    }

    abstract fun length(): Int

    fun field( fname:String): Field = try {
        val index: Int = format.fieldsByName[fname] ?: 0
        fields[index]
    }
    catch(e:NoSuchElementException) {
        throw FieldNotFound("<${this.javaClass.name}.invoke>", fname)
    }catch (e:Exception){
       throw e
    }


    private operator fun  Msg.invoke(fname: String): Any {
        val index: Int = format.fieldsByName[fname] ?: 0
        return fields[index]
    }

    open fun update(fname: String, value: String): Unit {
        try {
            val index: Int = format.fieldsByName[fname] ?: 0
            val f: FieldSet = fields[index] as FieldSet
            f.set(value)
        }
        catch( t:NoSuchElementException) {
            throw FieldNotFound ("<${this.javaClass.name}.update>", fname)
        }catch ( t:Exception ) {
            throw t
        }
    }

    fun update(fname: String, value: Int): Unit {
        try {
            val f: BaseField = fieldsByName[fname] as BaseField
            if (!f.isNumeric())
                throw MsgException("<${this.javaClass.name}.update> fields $fname is not Field Numeric")

            this.update(fname, value.toString())
        }
        catch(t:NoSuchElementException) {
            throw FieldNotFound("<${this.javaClass.name}.update>", fname)
        }catch ( t:Exception ) {
            throw t
        }
    }

    open fun unset(fname: String): Unit {
        try {
            fieldsByName[fname] as FieldSet
        }
        catch(e:NoSuchElementException) {
            throw FieldNotFound("<${this.javaClass.name}.update>", fname)
        }catch ( t:Exception ) {
            throw t
        }
    }

    fun transform(msg:Msg) : Msg{
        for(i in 0 until fields.size){
            val to: BaseField = fields[i] as BaseField

            if(to._data.isEmpty()  && to.default != null)
                to.set(to.default!!)

            for( f in msg.fields ){
                if(fields[i].tranformId.equals(f.id) || fields[i].id.equals(f.id)) {
                    (fields[i] as BaseField).transform(f as BaseField)
                }
            }

        }
        return this
    }

}