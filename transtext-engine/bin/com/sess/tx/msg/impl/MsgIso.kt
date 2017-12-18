package com.sess.tx.msg.impl

import com.sess.tx.msg.FieldNotFound
import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgException
import com.sess.tx.msg.field.Field
import com.sess.tx.msg.field.FieldSet
import com.sess.tx.msg.format.FieldFormat
import com.sess.tx.msg.format.MsgFormat
import java.math.BigInteger
import java.util.*

class MsgIso : Msg {

    constructor(format: MsgFormat) : super(format) {
        print("iso")
    }

    private val mandatoryList: Array<String> = arrayOf("MTI", "BMAP") //, "BMAP2", "BMAP3"
    override val fields: ArrayList<Field> = let {
        val f: ArrayList<Field> = arrayListOf<Field>()

        for (fmt in format.fieldFormats) {
            if (fmt.name != mandatoryList[0] &&
                    fmt.name != mandatoryList[1]
//                    && fmt.name != mandatoryList[2] &&  fmt.name != mandatoryList[3]
                    ){
//                if (fmt.name.length != 4) throw MsgException("<$this> fields ${fmt.name} with invalid length")
//                if (fmt.name[0] != 'F') throw MsgException("<$this> fields ${fmt.name} should start with 'F'")

                val n: Int = fmt.name.substring(1).toInt()
                if (n <= 0 || n >= 192) throw MsgException("<$this> fields ${fmt.name} out of range")
            }

            val field: Field = fmt.newField()
            f += field
            fieldsByName.put(fmt.name, field)
        }

        mandatoryList.forEach { m ->
            if (!fieldsByName.contains(m))
                throw MsgException("<$this> missing $m fields")
        }

        f
    }
    val bitmap: Array<Char> =  Array<Char>(64, { '0' }) // Arrays.fill(192,'0')

    fun setBit(id: String, on: Boolean): Unit {
        if (id.length == 4 && id[0] == 'F') {
            val num: String = id.drop(1)
            if (num.matches(Regex("[0-9]+"))) {
                val index: Int = num.toInt() - 1
                if (index < 0 || index > 63) throw MsgException("<$this.setBit> out of range index $index")

                if (on) bitmap[index] = '1' else bitmap[index] = '0'
            }
        }
    }

    override fun length(): Int {
        val _length_mandatory: Int = let {
            var xpos: Int = 0

            for (m: Int in mandatoryList.indices) {
                try {
                    val index: Int = format.fieldsByName.get(mandatoryList[m])!!
                    val f: Field = fields[index]

                    when (m) {
                        0 ->
                            xpos = xpos + f.lengthOfByte()

                        1 -> {
                            if (bitmap.slice(64..128).all { b -> b == '0' })
                                bitmap[0] = '0'
                            else
                                bitmap[0] = '1'

                            (f as FieldSet).set("b|" + bitmap.slice(0..64).toString())
                            xpos = xpos + f.lengthOfByte()
                        }
//                        2 -> {
//                            if (bitmap[0] == '1') {
//                                if (bitmap.slice(128..192).all { b -> b == '0' })
//                                    bitmap[64] = '0'
//                                else
//                                    bitmap[64] = '1'
//
//                                (f as FieldSet).set("b|" + bitmap.slice(64..128).toString())
//                                xpos = xpos + f.lengthOfByte()
//                            }
//
//                        }
//
//                        3 ->
//                            if (bitmap[0] == '1') {
//                                (f as FieldSet).set("b|" + bitmap.slice(128..192).toString())
//                                xpos = xpos + f.lengthOfByte()
//                            }
                    }
                } catch (e: NoSuchElementException) {
                    throw FieldNotFound("<$this.apply>", mandatoryList[m])
                } catch (e: Exception) {
                    throw e
                }

            }

            xpos
        }

        fun _length_bitmap(bno: Int, l: Int): Int {
            val skip: Boolean = bno == 0 || bno == 64
            val stop: Boolean = if (bno >= 192)
                true
            else if (bno >= 128 && bitmap[64] != '1')
                true
            else if (bno >= 64 && bitmap[0] != '1')
                true
            else
                false

            if (stop)
                return l
            else if (skip)
                return _length_bitmap(bno + 1, l)
            else
                if (bitmap[bno] == '1') {
                    val n: String = "F%03d".format(bno + 1)

                    val index: Int = format.fieldsByName.get(n)!!
                    val fmt: FieldFormat = format.fieldFormats[index]
                    val f: FieldSet = fields[index] as FieldSet

                    if (!f.isset()) throw MsgException("<$this.pack> field $n is not set but bit is ON")

                    return _length_bitmap(bno + 1, l + fmt.packager.lengthUsed + f.lengthOfByte())
                } else {
                    return _length_bitmap(bno + 1, l)
                }

        }

        return _length_mandatory + _length_bitmap(0, 0)

    }
    fun apply(fname: String): Field {
        try
        {
            val index: Int = format.fieldsByName.get(fname)!!
            val f: Field = fields[index]

            if (fname == mandatoryList[0]) {
                return f
            }
            else if (fname == mandatoryList[1]) {
                if (bitmap.slice(64.. 128).all{b -> b == '0'})
                bitmap[0] = '0'
                else
                bitmap[0] = '1'

                (f as FieldSet).set("b|" + bitmap.slice(0.. 64).toString())
                return f
            }
            else if (fname == mandatoryList[2]) {
                if (bitmap.slice(128.. 192).all{b -> b == '0'})
                bitmap[64] = '0'
                else
                bitmap[64] = '1'

                (f as FieldSet).set("b|" + bitmap.slice(64.. 128).toString())
                return f
            }
            else if (fname == mandatoryList[3]) {
                (f as FieldSet).set("b|" + bitmap.slice(128.. 192).toString())
                return f
            }
            else {
                val no: Int = fname.drop(1).toInt() - 1
                if (bitmap[no] == '0') {
                    (f as FieldSet).unset()
                }
                return f
            }
        }
        catch(e:NoSuchElementException) {
            throw FieldNotFound("<$this.apply>", fname)
        }catch (e:Exception) {
            throw e
        }
    }

    override fun update(fname: String, value: String) : Unit {
        try {
            val index: Int = format.fieldsByName.get(fname)!!
            val f: FieldSet = fields[index] as FieldSet
            if (fname == mandatoryList[0]) {
                f.set(value)
            }
            else {
                mandatoryList.drop(1).forEach{ e ->
                    if (e == fname) {
                        throw MsgException("<$this.update> field $fname not allowed")
                    }
                }

                f.set(value)
                setBit(fname, on = true)
            }
        }
        catch(e:NoSuchElementException) {
            throw FieldNotFound("<$this.update>", fname)
        }catch (e:Exception){
            throw e
        }
    }

    override fun unset(fname: String) {
        try {
            val f: FieldSet = fieldsByName.get(fname) as FieldSet
            mandatoryList.forEach{e ->
                if (e == fname) {
                    throw MsgException("<$this.update> field $fname not allowed")
                }
            }

            f.unset()
            setBit(fname, on = false)
        }
        catch(e:NoSuchElementException ) {
            throw FieldNotFound("<$this.update>", fname)
        }catch (t:Exception) {
            throw t
        }
    }

    override fun unpack(from: Array<Byte?>, fromPos: Int): Int {
        fun _unpack_mandatory(fdata: Array<Byte?>, fpos: Int): Int {
            var xpos: Int = fpos

            for (i: Int in bitmap.indices) {
                bitmap[i] = '0'
            }

            for (m: Int in mandatoryList.indices) {
                try {
                    val index: Int = format.fieldsByName.get(mandatoryList[m])!!
                    val fmt: FieldFormat = format.fieldFormats[index]
                    val f: FieldSet = fields[index] as FieldSet

                    when (m) {
                        0 ->
                            xpos = fmt.packager.unpack(f, fdata, xpos)

                        1 -> {
                            xpos = fmt.packager.unpack(f, fdata, xpos)
                            val smap: String = if (f.str().take(2) == "x|") f.str().drop(2) else f.str()
                            val bmap: Array<Char> = hexToBinary(f.str()).toCharArray().toTypedArray()
                            //smap.replace(Regex("[^0-9A-Fa-f]"), "").toCharArray().toTypedArray()
//                                    .toCharArray().asList().slide(2, 2).toList().stream().map { Integer.parseInt(it.toString(), 16).toByte() }
//                                    .map { e -> "%8s".format(Integer.toBinaryString(e.toInt() and 0xff)) }.toArray().toString()
//                                    .map { it.toString().replace(' ', '0') }.toCharArray().toTypedArray()
                            System.arraycopy(bmap, 0, bitmap, 0, 64)
                        }
//                        2 -> {
//                            if (bitmap[0] == '1') {
//                                xpos = fmt.packager.unpack(f, fdata, xpos)
//                                val smap: String = if (f.str('x').take(2) == "x|") f.str('x').drop(2) else f.str()
//                                val bmap: Array<Char> = smap.replace(Regex("[^0-9A-Fa-f]"), "")
//                                        .toCharArray().asList().slide(2, 2)
//                                        .map { Integer.parseInt(it.toString(), 16).toByte() }
//                                        .map { e -> "%8s".format(Integer.toBinaryString(e.toInt() and 0xff)).replace(' ', '0') }
//                                        .toString().toCharArray().toTypedArray()
//                                System.arraycopy(bmap, 0, bitmap, 64, 64)
//                            }
//                        }
//
//
//                        3 -> {
//                            if (bitmap[64] == '1') {
//                                xpos = fmt.packager.unpack(f, fdata, xpos)
//                                val smap: String = if (f.str('x').take(2) == "x|") f.str('x').drop(2) else f.str()
//                                val bmap: Array<Char> = smap.replace(Regex("[^0-9A-Fa-f]"), "").toCharArray().asList().slide(2, 2)
//                                        .map { Integer.parseInt(it.toString(), 16).toByte() }
//                                        .map { e -> "%8s".format(Integer.toBinaryString(e.toInt() and 0xff)).replace(' ', '0') }
//                                        .toString().toCharArray().toTypedArray()
//                                System.arraycopy(bmap, 0, bitmap, 128, 64)
//                            }
//                        }


                    }
                } catch (e: NoSuchElementException) {
                    throw FieldNotFound("<$this.apply>", mandatoryList[m])
                } catch (t: Throwable) {
                    throw t
                }
            }
            return xpos
        }

        fun _unpack_bitmap(bno: Int, fdata: Array<Byte?>, fpos: Int): Int {
            val stop: Boolean = if (bno >= 192)
                true
            else if (bno >= 128 && bitmap[64] != '1')
                true
            else if (bno >= 64 && bitmap[0] != '1')
                true
            else
                false

            if (stop)
                return fpos
            else {
                val skip: Boolean = bno == 0 || bno == 64 || bitmap[bno] == '0'

                if (skip) {
                    return _unpack_bitmap(bno + 1, fdata, fpos)
                } else {
                    val n: String = "F%03d".format(bno + 1)

                    val index: Int = format.fieldsByName.get(n)!!
                    val fmt: FieldFormat = format.fieldFormats.get(index)
                    val f: FieldSet = fields[index] as FieldSet

                    val xpos = fmt.packager.unpack(f, fdata, fpos)

                    return _unpack_bitmap(bno + 1, fdata, xpos)
                }
            }



        }
        return _unpack_bitmap(0, from, _unpack_mandatory(from, fromPos))
    }

    override fun pack(to: Array<Byte?>, toPos: Int): Int {

        fun _pack_mandatory(tdata: Array<Byte?>, tpos: Int): Int {
            var xpos: Int = tpos

            for (m: Int in mandatoryList.indices) {
                try {
                    val index: Int = format.fieldsByName.get(mandatoryList[m])!!
                    val fmt: FieldFormat = format.fieldFormats[index]
                    val f: FieldSet = fields[index] as FieldSet

                    when (m) {
                        0 ->
                            xpos = fmt.packager.pack(f, tdata, xpos)

                        1 -> {
                            if (bitmap.slice(64..128).all { b -> b == '0' })
                                bitmap[0] = '0'
                            else
                                bitmap[0] = '1'

                            (f as FieldSet).set("b|" + bitmap.slice(0..64).toString())
                            xpos = fmt.packager.pack(f, tdata, xpos)
                        }
                        2 -> {
                            if (bitmap[0] == '1') {
                                if (bitmap.slice(128..192).all { b -> b == '0' })
                                    bitmap[64] = '0'
                                else
                                    bitmap[64] = '1'

                                (f as FieldSet).set("b|" + bitmap.slice(64..128).toString())
                                xpos = fmt.packager.pack(f, tdata, xpos)
                            }

                        }

                        3 -> {
                            if (bitmap[0] == '1') {
                                (f as FieldSet).set("b|" + bitmap.slice(128..192).toString())
                                xpos = fmt.packager.pack(f, tdata, xpos)
                            }
                        }

                    }
                } catch (e: NoSuchElementException) {
                    throw FieldNotFound("<$this.apply>", mandatoryList[m])
                } catch (t: Throwable) {
                    throw t
                }
            }

            return xpos
        }

        fun _pack_bitmap(bno: Int, tdata: Array<Byte?>, tpos: Int): Int {
            val skip: Boolean = bno == 0 || bno == 64
            val stop: Boolean = if (bno >= 192)
                true
            else if (bno >= 128 && bitmap[64] != '1')
                true
            else if (bno >= 64 && bitmap[0] != '1')
                true
            else
                false

            if (stop)
                return tpos
            else if (skip)
                return _pack_bitmap(bno + 1, tdata, tpos)
            else
                if (bitmap[bno] == '1') {
                    val n: String = "F%03d".format(bno + 1)

                    val index: Int = format.fieldsByName.get(n)!!
                    val fmt: FieldFormat = format.fieldFormats[index]
                    val f: FieldSet = fields[index] as FieldSet

                    if (!f.isset()) throw MsgException("<$this.pack> field $n is not set but bit is ON")
                    val xpos = fmt.packager.pack(f, tdata, tpos)

                    return _pack_bitmap(bno + 1, tdata, xpos)
                } else {
                    return _pack_bitmap(bno + 1, tdata, tpos)
                }
        }
        return _pack_bitmap(1, to, _pack_mandatory(to, toPos))
    }

    override fun toString(): String = "${this::class.java.name}[Format=${format.name}]"

    fun hexToBinary(hex: String): String {
        val binStrBuilder = StringBuilder()
        var c = 1
        var i = 0
        while (i < hex.length - 1) {

            val output = hex.substring(i, i + 2)

            val decimal = Integer.parseInt(output, 16)

            val binStr = Integer.toBinaryString(decimal)
            val len = binStr.length
            val sbf = StringBuilder()
            if (len < 8) {

                for (k in 0..8 - len - 1) {
                    sbf.append("0")
                }
                sbf.append(binStr)
            } else {
                sbf.append(binStr)
            }

            c++
            binStrBuilder.append(sbf.toString())
            i += 2
        }

        return binStrBuilder.toString()
    }
}

