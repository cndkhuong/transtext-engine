package com.sess.tx.msg

/**
 * Created by kevin on 7/11/17.
 */
interface MsgRef {
    fun unpack(from: Array<Byte?>, fromPos: Int = 0): Int
    fun pack(to: Array<Byte?>, toPos: Int = 0): Int
}