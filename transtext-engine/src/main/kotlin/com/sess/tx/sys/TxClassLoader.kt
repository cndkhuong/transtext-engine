package com.sess.tx.sys

import java.net.URL
import java.net.URLClassLoader

/**
 * Created by kevin on 7/11/17.
 */
class TxClassLoader (parent: ClassLoader) : URLClassLoader( arrayOf(), parent) {
    override fun addURL(uRL: URL): Unit {
        super.addURL(uRL)
    }

    fun addURLs (uRLs: Array<URL?>): Unit  {
        for (uRL in uRLs) {
            super.addURL(uRL)
        }
    }
}