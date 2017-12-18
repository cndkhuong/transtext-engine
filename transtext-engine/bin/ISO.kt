import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgLoader
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.MsgFormat
import org.junit.Test

class ISO {

    @Test
    fun testISO(){
        val sMsgIso: String = "0200FA3A00010801C016000000001700000110120000136591202000000002000000000002000010121529140000011529141012101206970437714912014999024IBT SMARTLINK HANOI VNM|7047040020106IF_DEP016gPBWnfeDi7k2aJSz06970458101200001365101200001367026Chuyen tien lien ngan hangFB5D06EA25B04052"
        val bMsgIso: Array<Byte?> = sMsgIso.toByteArray().toTypedArray().copyOf(sMsgIso.length)
        val fmt48rs: MsgFormat = MsgLoader.loadFromJson("/home/kevin/Workspace/Sources/sess-tx/sess-tx-core/src/main/resources", "sim_isomsg.json")
        val msg: Msg = fmt48rs.msg()
        msg.unpack(bMsgIso)
        for (f in msg.fields) {
            val bf:BaseField = f as BaseField
            if (f.isset())
                println(  (bf.str())   )
        }
    }
}