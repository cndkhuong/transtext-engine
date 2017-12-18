package com.sess.tx.msg.field

/**
 * Created by kevin on 7/10/17.
 */
abstract class BaseField(override var id:String, override val min:Int, override val max:Int, override var tranformId:String, override val default:String?) : FieldSet {

    abstract override fun isNumeric():Boolean

    abstract override fun isset(): Boolean
    abstract override fun data(): Array<Byte?>
    abstract override fun length(): Int

    abstract override fun lengthOfByte(): Int

    abstract override fun str(): String
    abstract override fun str(fmt: Char): String

    var _data: Array<Byte?> = emptyArray()

    fun transform(from:BaseField){
        set(String(from.data().filterNotNull().toByteArray()))
    }

}