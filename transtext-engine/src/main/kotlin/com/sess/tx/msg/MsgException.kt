package com.sess.tx.msg

import java.lang.RuntimeException

/**
 * Created by kevin on 7/10/17.
 */

class MsgException(m: String, cause: Throwable) : TxException(m, cause) {
    constructor(m:String) : this(m, null!!)

}

class FieldNotFound(f: String, n: String) : TxException("$f Field $n not found") {
    constructor(m:String) : this(m, null!!)
}

open class TxException(message:String, cause:Throwable) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null!!)
}