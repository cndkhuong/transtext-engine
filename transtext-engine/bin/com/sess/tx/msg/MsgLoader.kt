package com.sess.tx.msg

import com.sess.tx.msg.field.packager.Packager
import com.sess.tx.msg.format.FieldFormat
import com.sess.tx.msg.format.MsgFormat
import com.sess.tx.msg.format.charset.CHARSET
import com.sess.tx.msg.types.Type
import com.sess.tx.msg.types.TypeNum
import com.sess.tx.sys.TxClassLoader
import com.sess.tx.sys.TxLogging
import java.io.File
import java.lang.reflect.Constructor
import java.net.URL
import java.io.FileReader
import com.google.gson.stream.JsonReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sess.tx.msg.types.TypeStr
import java.util.*


/**
 * Created by kevin on 7/11/17.
 */
object MsgLoader : TxLogging() {
    fun initClassLoader(appPath: String, libs: String?): TxClassLoader {
        val parentLoader: ClassLoader = this.javaClass.classLoader
        val txClassLoader: TxClassLoader = TxClassLoader(parentLoader)

        val urls: Array<URL?> = if (libs != null) {
            val jars: List<String> = libs.split(",")
            val urls: Array<URL?> = arrayOfNulls<URL>(jars.size)

            var i: Int = 0
            for (jar in jars) {
                if (jar.takeLast(4) != ".jar") throw MsgException("<$this.classLoader> invalid lib jar name ($jar)")
                val f: File = File("$appPath/lib/$jar")
                if (!f.exists()) throw MsgException("<$this.classLoader> library ($jar) not found in $appPath/lib")
                urls[i] = f.toURI().toURL()
                i = i + 1
            }

            urls
        } else {
            emptyArray()
        }

        txClassLoader.addURLs(urls)
        return txClassLoader
    }

    private fun parseValue(v: String, classLoader: TxClassLoader): Any?  {
        val a: List<String> = v.split('=')
        if (a.size != 2) throw MsgException("<${this.javaClass}.parseValue> invalid value format")

        if (a[0] == "Integer") {
            return a[1].toInt()
        }
        else if (a[0] == "String") {
            if (a[1] == "[NIL]")
                return null
            else
                return a[1]
        }
        else if (a[0].toUpperCase() == "CHARSET") {
            return CHARSET.valueOf(a[1])
        }
        else if (a[0] == "Type") {
            val classType: Class<TypeNum> = classLoader.loadClass(a[1]) as Class<TypeNum>
//            return  classType.getField("MODULE$").get(classType) as Type
            return classType.constructors[0].newInstance() as Type
        }

        else {
            throw MsgException("<${this.javaClass}.parseValue> ${a[0]} not supported")
        }
    }

    fun getPackager(name: String, classLoader: TxClassLoader, packager: Map<String, Any>): Packager {
        val clazz: String = if (packager.isEmpty() || packager["type"] == null)
            "com.sess.tx.msg.field.packager.LengthFixed"
        else
            packager["type"] as String

            val maskString: String? = if (packager.isEmpty()) null else packager["mask"]?.toString() ?: null
            val mask: Byte = if (packager.isEmpty()) 0x00.toByte() else if (maskString == null) 0x00.toByte() else maskString[0].toByte()

            val args: Array<Any?> = if (packager.isEmpty() || packager["type-args"] == null) {
                arrayOf(mask)
            }
            else {

                var a: MutableList<Any?> = mutableListOf<Any?>()
                val b: List<String> = packager.get("type-args") as List<String>
                a.add(mask)
                for (arg in b) {
                    a.add(parseValue(arg, classLoader))
                }

                a.toTypedArray()
            }


        val classLength: Class<Packager> = classLoader.loadClass(clazz) as Class<Packager>
        val ctorLength: Constructor<Packager> = classLength.getConstructors()[0] as Constructor<Packager>

        return ctorLength.newInstance(args[0])
    }

    fun getFieldFormat(fmtId: String, classLoader: TxClassLoader, field: Map<String, Any>) : FieldFormat  {
        if (field.isEmpty()) throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> fields format is not defined")

        val name: String = if (field["name"] == null)
            throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> fields name is not defined")
        else
            field.get("name") as String

        val fieldId: String = if (field["field"] == null)
            name
        else
            field.get("field") as String



        val format: Map<String, Any> = if (field["format"] == null)
            throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> format for fields $name is not defined")
        else
            field.get("format") as Map<String, Any>

        val default: String? = format["default"]?.toString() ?: ""

        val ftype: String = if (format.get("type") == null)
            throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> type for fields $name is not defined")
        else
            format["type"] as String

        val aType: List<String> = ftype.split('?')
        val charset: CHARSET = if (format.get("charset") != null ) CHARSET.valueOf(format.get("charset").toString()) else CHARSET.CHARSET_NONE


        val classType:Class<Type> = classLoader.loadClass(aType[0]) as Class<Type>

        val obj: Type = classType.getConstructor().newInstance() as Type

        val min: Int = if (format.get("min") == null)
            if (format.get("length") == null)
                throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> min length for fields $name is not defined")
        else
            format["length"].toString().toDouble().toInt()
        else
            format["min"].toString().toDouble().toInt()

        val max: Int = if (format.get("max") == null)
            if (format.get("length") == null)
                throw MsgException("<${this.javaClass.name}[$fmtId].getFieldFormat> max length for fields $name is not defined")
        else
            format["length"].toString().toDouble().toInt()
        else
            format["max"].toString().toDouble().toInt()

        val mandatory: Boolean? = format["mandatory"]?.toString()?.toBoolean() ?: false


        val pack: Map<String, Any> = try {
            val get = field.get("packager")
            if (get != null) get as Map<String, Any> else emptyMap()
        }
        catch(e:NoSuchElementException) {
            emptyMap<String, Any>()
        }catch(e:Exception) {
           throw e
        }

        val packager: Packager = getPackager(name, classLoader, pack)
        return FieldFormat(name,
                fieldId,
                field["desc"]?.toString() ?: "" as String,
                obj,
                charset,
                min,
                max,
                mandatory,
                default!!,
                packager)
    }

    fun loadFromJson(appPath: String, jsonFile: String): MsgFormat {
        val gson = Gson()
        val reader = JsonReader(FileReader("$appPath/cfg/$jsonFile"))
        val jsMap:Map<String, Any> = gson.fromJson(reader, object : TypeToken<Map<String, Any>>() {

        }.type)

        val classLoader: TxClassLoader = initClassLoader(appPath, jsMap["libs"]?.toString() ?: null)

        val name: String = if (jsMap.get("name") == null)
            throw MsgException("<${this.javaClass.name}.loadFromJson[$jsonFile]> msg name is not defined")
        else
            jsMap["name"] as String

        val formatter: String = if (jsMap.get("formatter") == null)
            throw MsgException("<${this.javaClass.name}.loadFromJson[$jsonFile]> formatter for msg $name is not defined")
        else
            jsMap["formatter"] as String

        val msgType: Class<Msg> = classLoader.loadClass(formatter) as Class<Msg>
        val msgFormat: MsgFormat = MsgFormat(name, jsMap["desc"]?.toString() ?: "" as String, msgType)

        val fields: List<Map<String, Any>> = jsMap["fields"] as List<Map<String, Any>>
        for (field in fields) {
            val fieldFormat: FieldFormat = getFieldFormat(name, classLoader, field)
            msgFormat.registerField(fieldFormat)
        }

        return msgFormat
    }
}