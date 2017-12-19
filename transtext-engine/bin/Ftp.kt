import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgLoader
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.MsgFormat
import org.junit.Test

class Ftp {

    @Test
    fun testJson(){
        val jsonMsg: String = """
{
  "BatchStart": {
		"RecordType" : "0",
		"FileName": "biooil.txt",
		"InterfaceID" : "1",
        "ProcessInd": "b.txt",
        "ProcessTimeStamp" : "P",
        "ReturnDescription" : "NORMAL",
        "Filler" : ""
  },
  "BatchBody": [
    {
    "BatchHeader": {
        "RecordType" : "1",
        "FileName": "biooil30.txt",
        "PaymentType" : "P",
        "ServiceType" : "NORMAL",
        "Immediate" : "",
        "CompanyID" : "2017112400000000220",
        "OriginatingBICCode" : "UOVBSGSGXXX",
        "OriginatingAccountNoCurrency" : "SGD",
        "OriginatingAccountNo" : "1231119113",
        "OriginatingAcName" : "Bio Oil",
        "CreationDate" : "",
        "ValueDate" : "20171231",
        "UltimateOriginatingCustomer" : "",
        "BulkCustomerReference" : "CustomerRef123 for batch30",
        "SoftwareLabel" : "",
        "BatchID" : "30",
        "OriginalSource" : "CCC",
        "InternetRef" : "",
        "Filler" : ""
    },
    "BatchDetail": [{
        "RecordType" : "2",
        "ReceivingBICCode" : "DBSSSGS0XXX",
        "ReceivingAccountNo" : "1231112001",
        "ReceivingAcName" : "Alexander Green",
        "Currency" : "SGD",
        "Amount" : "000000000003011000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3001",
        "PurposeCode" : "BEXP",
        "RemittanceInformation" : "Info1",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment111 for batch30",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "BOFASGS0XXX",
        "ReceivingAccountNo" : "1231112002",
        "ReceivingAcName" : "James Cook",
        "Currency" : "SGD",
        "Amount" : "000000000003022000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3002",
        "PurposeCode" : "BONU",
        "RemittanceInformation" : "Info2",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment222 for batch30",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
	},{
		"RecordType" : "2",
        "ReceivingBICCode" : "BOFASGS0XXX",
        "ReceivingAccountNo" : "1231112002",
        "ReceivingAcName" : "James Cook",
        "Currency" : "SGD",
        "Amount" : "000000000003022000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3003",
        "PurposeCode" : "BONU",
        "RemittanceInformation" : "Info2",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment333 for batch30",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
	},{
		"RecordType" : "2",
        "ReceivingBICCode" : "CITISGS0XXX",
        "ReceivingAccountNo" : "1231112003",
        "ReceivingAcName" : "Dorothy Pears",
        "Currency" : "SGD",
        "Amount" : "000000000003033000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3004",
        "PurposeCode" : "CBTV",
        "RemittanceInformation" : "Info3",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment444 for batch30",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
    }],
    "BatchTrailer": {
        "RecordType":"9",
        "TotalAmount" : "000000000000060000",
        "TotalCount" : "4",
        "HashTotal" : "",
        "Filler" : ""
    }
    },
	{
    "BatchHeader": {
        "RecordType" : "1",
        "FileName": "biooil31.txt",
        "PaymentType" : "P",
        "ServiceType" : "NORMAL",
        "Immediate" : "",
        "CompanyID" : "2017112400000000220",
        "OriginatingBICCode" : "UOVBSGSGXXX",
        "OriginatingAccountNoCurrency" : "SGD",
        "OriginatingAccountNo" : "1231119113",
        "OriginatingAcName" : "Bio Oil",
        "CreationDate" : "",
        "ValueDate" : "20171231",
        "UltimateOriginatingCustomer" : "",
        "BulkCustomerReference" : "CustomerRef123 for batch31",
        "SoftwareLabel" : "",
        "BatchID" : "31",
        "OriginalSource" : "CCC",
        "InternetRef" : "",
        "Filler" : ""
    },
    "BatchDetail": [{
        "RecordType" : "2",
        "ReceivingBICCode" : "DBSSSGS0XXX",
        "ReceivingAccountNo" : "1231112001",
        "ReceivingAcName" : "Amy Tan Ee Jhia",
        "Currency" : "SGD",
        "Amount" : "000000000003111000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3101",
        "PurposeCode" : "BEXP",
        "RemittanceInformation" : "Info1",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment111 for batch31",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "BOFASGS0XXX",
        "ReceivingAccountNo" : "1231112002",
        "ReceivingAcName" : "Juliana Robertson",
        "Currency" : "SGD",
        "Amount" : "000000000003122000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3102",
        "PurposeCode" : "CDCD",
        "RemittanceInformation" : "Info2",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment222 for batch31",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "CITISGS0XXX",
        "ReceivingAccountNo" : "1231112003",
        "ReceivingAcName" : "Andrew Park",
        "Currency" : "SGD",
        "Amount" : "000000000003133000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3103",
        "PurposeCode" : "COMM",
        "RemittanceInformation" : "Info3",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment333 for batch31",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "CITISGS0XXX",
        "ReceivingAccountNo" : "1231112003",
        "ReceivingAcName" : "Andrew Park",
        "Currency" : "SGD",
        "Amount" : "000000000003133000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3104",
        "PurposeCode" : "COMM",
        "RemittanceInformation" : "Info3",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment444 for batch31",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "BOFASGS0XXX",
        "ReceivingAccountNo" : "1231112002",
        "ReceivingAcName" : "Albert Steve",
        "Currency" : "SGD",
        "Amount" : "000000000003122000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3105",
        "PurposeCode" : "CDCD",
        "RemittanceInformation" : "Info2",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment555 for batch31",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
	}],
    "BatchTrailer": {
        "RecordType":"9",
        "TotalAmount" : "000000000000060000",
        "TotalCount" : "5",
        "HashTotal" : "",
        "Filler" : ""
    }
    },
    {
    "BatchHeader": {
        "RecordType" : "1",
        "FileName": "biooil32.txt",
        "PaymentType" : "P",
        "ServiceType" : "NORMAL",
        "Immediate" : "",
        "CompanyID" : "2017112400000000220",
        "OriginatingBICCode" : "UOVBSGSGXXX",
        "OriginatingAccountNoCurrency" : "SGD",
        "OriginatingAccountNo" : "1231119113",
        "OriginatingAcName" : "Bio Oil",
        "CreationDate" : "",
        "ValueDate" : "20171231",
        "UltimateOriginatingCustomer" : "",
        "BulkCustomerReference" : "CustomerRef123 for batch32",
        "SoftwareLabel" : "",
        "BatchID" : "32",
        "OriginalSource" : "CCC",
        "InternetRef" : "",
        "Filler" : ""
    },
    "BatchDetail": [{
        "RecordType" : "2",
        "ReceivingBICCode" : "DBSSSGS0XXX",
        "ReceivingAccountNo" : "1231112001",
        "ReceivingAcName" : "Orlando Blooms",
        "Currency" : "SGD",
        "Amount" : "000000000003211000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3201",
        "PurposeCode" : "BEXP",
        "RemittanceInformation" : "Info1",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment111 for batch32",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "BOFASGS0XXX",
        "ReceivingAccountNo" : "1231112002",
        "ReceivingAcName" : "Herzberg Cerz",
        "Currency" : "SGD",
        "Amount" : "0000000000003222000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3202",
        "PurposeCode" : "BONU",
        "RemittanceInformation" : "Info2",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment222 for batch32",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
		},{
		"RecordType" : "2",
        "ReceivingBICCode" : "CITISGS0XXX",
        "ReceivingAccountNo" : "1231112003",
        "ReceivingAcName" : "Fiona Czeniawaska",
        "Currency" : "SGD",
        "Amount" : "000000000003233000",
        "EndtoEndID" : "",
        "MandateID" : "BIOOIL-3203",
        "PurposeCode" : "DIVD",
        "RemittanceInformation" : "Info3",
        "UltimatePayer" : "",
        "CustomerReference" : "Payment333 for batch32",
        "ActualOriginatingAccount" : "",
        "ActualOriginatingAccountName" : "",
        "SourceBatchIdentifier" : "",
        "SourceTransactionIdentifier" : "",
        "Filler" : ""
    }],
    "BatchTrailer": {
        "RecordType":"9",
        "TotalAmount" : "000000000000060000",
        "TotalCount" : "3",
        "HashTotal" : "",
        "Filler" : ""
    }
  }
],

  "BatchEnd": {
		"RecordType" : "X",
		"BatchCnt":"9",
        "RecordCnt" : "000000000000060000",
        "Filler" : "3"
   }
}
        """

        val bJsonMsg: Array<Byte?> = jsonMsg.toByteArray().toTypedArray().copyOf(jsonMsg.length)
        val fmtJson: MsgFormat = MsgLoader.loadFromJson("/home/kevin/Workspace/Sources/sess-tx/sess-tx-core/src/main/resources/", "FTPJSON.json")
        val msg: Msg = fmtJson.msg()
        msg.unpack(bJsonMsg)
        for (f in msg.fields) {
            val bf: BaseField = f as BaseField
            if (f.isset())
                println(  "${bf.id} : ${bf.str()}"   )
        }
        println(msg.toString())
//        val fmtJsonOut: MsgFormat = MsgLoader.loadFromJson("/home/kevin/Workspace/Sources/sess-tx/sess-tx-core/src/main/resources", "EPSJSON.json")
//        val msgOut: Msg = fmtJsonOut.msg()
//        msgOut.transform(msg)
//
//        println("TRANSFORMED = " + msgOut )
//        for (f in msgOut.fields) {
//            val bf:BaseField = f as BaseField
//            if (f.isset())
//                println(  (bf.str())   )
//        }
//
//        //Pack to bytes
//        val bytes:Array<Byte?> = arrayOfNulls<Byte>(msgOut.length())
//        msgOut.pack(bytes)
//        println( "packed : " + (msgOut.toString())   )
    }
}