import com.sess.tx.msg.Msg
import com.sess.tx.msg.MsgLoader
import com.sess.tx.msg.field.BaseField
import com.sess.tx.msg.format.MsgFormat
import org.junit.Test

class Json {

    @Test
    fun testJson(){
        val jsonMsg: String = """
{
  "Header": {
    "Source": {
      "SourceCode": "FUND_TRANSFER_SIMULATOR",
      "ClientId": "USER01"
    },
    "Bah": {
      "MsgDefinition": "",
      "FromBIC": "",
      "ToBIC": "",
      "MsgRecipientBIC": "",
      "MsgCreationTms": "",
      "MsgSignature": "",
      "JournalSequence": 602371234,
      "BatchType": "",
      "BatchNumber": 0
    },
    "TrancheId": "",
    "Trans": {
      "SourceBranch": 0,
      "TransCode": "10110",
      "TransDescription": ""
    },
    "Menu": {
      "MenuId": "",
      "MenuLink": "",
      "ApiLink": "",
      "Action": ""
    },
    "OrigBah": {
      "OrigMsgCreationTms": "",
      "OrigBatchNumber": 0,
      "OrigJournalSequence": 0
    },
    "Override": {
      "OverrideCodes": "",
      "OverrideDescription": ""
    }
  },
  "Body": {
    "BwcHdr": {
      "RequestTermId": "",
      "RequestChannelId": "G3",
      "RequestPgmName": "",
      "RequestMsgTms": "",
      "ResponseMsgTms": "",
      "ServiceRequestId": "602371234",
      "ServiceRequestCd": "TM0010",
      "ErrRecoveryRevFlg": "N",
      "BusinessDrivenRevFlg": "N",
      "TimeoutPeriod": 30,
      "MsgExpiryPeriod": 25,
      "ReplyQueue": "",
      "InputMsgTms": "",
      "OutputMsgTms": "",
      "InputQueue": "",
      "OutputQueue": "",
      "StatusCd": "",
      "HostErrCd": "",
      "HostErrDesc": "",
      "CompletionCd": "",
      "ReasonCd": "",
      "RequestMsgId": ""
    },
    "CommApplHdr": {
      "SourceSys": "G3",
      "SourceCountry": "SG",
      "DestinationSys": "BPS",
      "DestinationCountry": "SG",
      "TransactionCd": "10110",
      "CostCenter": 0,
      "TerminalId": "SERV100",
      "JournalSeqNo": 602371234,
      "ResponseCd": "",
      "ResponseDesc": "",
      "ReturnQueue": "",
      "ClientUserId": "USER123",
      "ClientTransDtTm": "201710201139",
      "OrigUserId": "",
      "OrigUserTms": 0,
      "ResponseDtTm": 0,
      "ECInd": "N",
      "OrigJournalSeqNo": 0,
      "MoreRecInd": "N",
      "LastRecKey": "",
      "HashTotalNo": 0,
      "ApprovalId": "",
      "MsgSignature": "",
      "JournalSubSeqNo": 0
    },
    "BusinessApplHdr": {
      "MsgSenderId": "DBSSSGS0XXX",
      "MsgReceiverId": "UOVBSGS0XXX",
      "BusinessMsgId": "B20161117UOVBSGS0BAA0068057",
      "MsgDefinitionId": "pacs.008.001.02",
      "InstructionId": "20161117UOVBSGS0BRT0037099",
      "CreationDt": 0,
      "CopyDuplicate": "",
      "TransStatus": "",
      "ReasonCd": "",
      "AcceptanceDtTm": "",
      "ClearingSysCd": "MEP",
      "RepeatCounter": 0,
      "RoutingCd": 0,
      "StoredAndFwdInd": "N"
    },
    "CommReqInfo": {
      "OANo": "123123",
      "OACcy": "SGD",
      "DebtorAcctNo": "123123",
      "DebtorAcctCcy": "SGD",
      "DebtorAcctType": "",
      "CreditorAcctNo": "100001",
      "CreditorAcctCcy": "SGD",
      "CreditorAcctType": "",
      "MandateId": "",
      "MsgId": "PIB1611170002267647",
      "StartDtRange": 0,
      "EndDtRange": 0,
      "ClearingAmt": 0,
      "ChannelCd": ""
    },
    "ReqTransDetInfo": {
      "DebtorAgent": "DBSSSGS0XXX",
      "DebtorName": "1",
      "UltimateDebtorName": "",
      "CreditorAgent": "UOVBSGS0XXX",
      "CreditorName": "v",
      "UltimateCreditorName": "",
      "TotalInterbankSettlCcy": "SGD",
      "TotalInterbankSettlAmt": 222.00,
      "NoOfTrans": 1,
      "SettlMethod": "CLRG",
      "EndToEndId": "",
      "TransId": "20161117UOVBSGS0BRT0037099",
      "InterbankSettlCcy": "SGD",
      "InterbankSettlAmt": 222.00,
      "ClearingSysRef": "1",
      "InterbankSettlDt": "2017-10-20",
      "ChargeBearer": "SLEV",
      "PurposeCd": "BENE",
      "ServiceLevelCd": "SDVA",
      "RemittanceInfo": "",
      "ChargeAcctNo": "",
      "ChargeAcctCcy": "",
      "PenaltyChargeAmt": 0,
      "ServiceChargeAmt": 0,
      "ChargeCd": "",
      "Sic1": "",
      "Sic2": "",
      "Sic3": "",
      "Sic4": "",
      "CreateSource": "",
      "CreationDt": 0,
      "CreationTm": 0,
      "MaintUser": "",
      "MaintTermId": "",
      "MaintDt": 0,
      "MaintTms": 0,
      "CheckerId": "",
      "CheckerTermId": "",
      "CheckerDt": 0,
      "CheckerTms": 0,
      "YourRef": "",
      "OurRef": "",
      "OrigCreationSource": "",
      "CIFNo": 0,
      "AdditionalInfo": ""
    },
    "DDRevInfo": {
      "OrigMsgId": "",
      "OrigInstructionId": "",
      "OrigMsgDefinitionId": "",
      "OrigCreationDtTm": "",
      "OrigSettlCcy": "",
      "OrigInterbankSettlAmt": 0,
      "OrigInterbankSettlDt": "",
      "Proprietary": ""
    },
    "RevReasonInfo": {
      "ReasonCd": "",
      "RBKRespInfo": "",
      "BusinessDt": 0,
      "OAAvailableBal": 0,
      "OAAvailableBalSign": "",
      "OALedgerBal": 0,
      "OALedgerBalSign": "",
      "RAAvailableBal": 0,
      "RAAvailableBalSign": "",
      "RALedgerBal": 0,
      "RALedgerBalSign": ""
    },
    "FXInfo": {
      "FXBank1": "",
      "FXContract1": "",
      "FXCcy1": "",
      "FXContractAmt1": 0,
      "FXBank2": "",
      "FXContract2": "",
      "FXCcy2": "",
      "FXContractAmt2": 0,
      "FXBank3": "",
      "FXContract3": "",
      "FXCcy3": "",
      "FXContractAmt3": 0,
      "FXBank4": "",
      "FXContract4": "",
      "FXCcy4": "",
      "FXContractAmt4": 0,
      "FXBank5": "",
      "FXContract5": "",
      "FXCcy5": "",
      "FXContractAmt5": 0,
      "FXAggrUtilizedCcy": "",
      "FXAggrUtilizedAmt": 0,
      "FXBoardRate": 0,
      "FXToleranceRate": 0
    }
  }
}


        """

 val bJsonMsg: Array<Byte?> = jsonMsg.toByteArray().toTypedArray().copyOf(jsonMsg.length)
        val fmtJson: MsgFormat = MsgLoader.loadFromJson("/home/kevin/Workspace/Sources/sess-tx/sess-tx-core/src/main/resources/", "CHLJSON.json")
        val msg: Msg = fmtJson.msg()
        msg.unpack(bJsonMsg)
        for (f in msg.fields) {
            val bf: BaseField = f as BaseField
            if (f.isset())
                println(  (bf.str())   )
        }
        val fmtJsonOut: MsgFormat = MsgLoader.loadFromJson("/home/kevin/Workspace/Sources/sess-tx/sess-tx-core/src/main/resources", "EPSJSON.json")
        val msgOut: Msg = fmtJsonOut.msg()
        msgOut.transform(msg)

        println("TRANSFORMED = " + msgOut )
        for (f in msgOut.fields) {
            val bf:BaseField = f as BaseField
            if (f.isset())
                println(  (bf.str())   )
        }

        //Pack to bytes
        val bytes:Array<Byte?> = arrayOfNulls<Byte>(msgOut.length())
        msgOut.pack(bytes)
        println( "packed : " + (msgOut.toString())   )
    }
}