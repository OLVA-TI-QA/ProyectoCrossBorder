import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent as HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper as JsonSlurper
import java.text.DecimalFormat
import java.util.Random
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData



    // Crear un nuevo objeto de solicitud para la API Codificador/ParceldeclareCod
    RequestObject requestObject = new RequestObject()
    requestObject.setRestUrl('https://dev-icrossborder.olvacourier.com/util/convertidorParcelDeclare') 
    requestObject.setRestRequestMethod('POST')

    // Definir el cuerpo de la solicitud en duro, usando los valores generados
    String requestBody = """
     {
        "guid": "a1cf1c241846434ca241803a2ae10f7a",
        "appType": "1",
        "appTime": "20210207194805+0800",
        "platform": "AE",
        "transportNo": "transport123",
        "clearanceMode": "9610",
        "etd": "etd_example",
        "eta": "eta_example",
        "testMode": "N",
        "orderNo": "order12345",
        "logisticsCode": "LOG123456789",
        "copNo": "12345678",
        "masterWayBill": "MWB123456789",
        "wayBillNo": "WB00012010",
        "secWayBillNo": "SWB0123456789",
        "bigBagId": "BBID0123456789",
        "parcelCount": "10",
        "transportType": "4",
        "ieFlag": "E",
        "declareCountry": "CN",
        "declarePortCode": "5314",
        "fromCountry": "CN",
        "toCountry": "CN",
        "goodsPrice": 100,
        "taxPrice": 0,
        "postPrice": 0,
        "totalPrice": 0,
        "currency": "CNY",
        "grossWeight": 50,
        "netWeight": 50,
        "wrapType": "7",
        "lastMileCode": "LMC_0123456",
        "taxPt": "1234",
        "senderInfo": {
            "address": "Sender Address Example",
            "city": "Sender City",
            "country": "CN",
            "district": "Sender District",
            "mobilePhone": "1234567890",
            "name": "Sender Name",
            "state": "Sender State",
            "storeName": "Sender Store",
            "street": "Sender Street",
            "telephone": "0987654321",
            "zipCode": "123456"
        },
        "receiverInfo": {
            "address": "Receiver Address Example",
            "city": "Receiver City",
            "country": "PE",
            "district": "",
            "email": "example@example.com",
            "mobilePhone": "0987654321",
            "name": "John Doe",
            "state": "Receiver State",
            "street": "Receiver Street",
            "telephone": "1234567890",
            "zipCode": "654321"
        },
        "returnInfo": {
        "name": "Pierre Falconi",
        "mobilePhone": "962201152",
        "telephone": "+51",
        "email": "pefalconi82@gmail.com",
        "address": "LIMA~LA MOLINA~~Calle La Cordillera 598 - Urb. Las Vi as Apartamento 101",
        "zipCode": "15024",
        "country": "PE",
        "state": "LIMA",
        "city": "LA MOLINA",
        "street": "Calle La Cordillera 598 ",
        "district": ""
    },
        "passportDetail": {
            "fullName": "John Doe",
            "tinNo": "TIN123456789"
        },
        "feature": {
            "invoiceUrl": "http://example.com/invoice"
        },
        "itemList": [
            {
                "gnum": 1,
                "itemId": "ITEM123456789",
                "hsCode": "3301000000",
                "itemName": "Item Name Example",
                "currency": "USD",
                "qty": "10",
                "country": "CN",
                "price": "150.50"
            }
        ]
    }
    """

    // Configurar las cabeceras para la solicitud
    requestObject.setHttpHeaderProperties([
        new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
        new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI=")
    ])

    // Asignar el cuerpo de la solicitud
    requestObject.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))

  
        // Enviar la solicitud a la API
        def response = WS.sendRequest(requestObject)

        // Verificar la respuesta de la API
        WS.verifyResponseStatusCode(response, 200)

        // Imprimir la respuesta
        println("Response: " + response.getResponseText())

        // Parsear el response para extraer campos específicos
        def jsonResponse = new JsonSlurper().parseText(response.getResponseText())

        // Obtener campos del response
        def content = jsonResponse.body?.content
        def formatType = jsonResponse.body?.formatType
        def bizKey = jsonResponse.body?.bizKey
        def bizType = jsonResponse.body?.bizType
        def dataDigest = jsonResponse.dataDigest

        // Verificación de valores antes de proceder
        if (content && formatType && bizKey && bizType && dataDigest) {
            // Formatear el campo logistics_interface
            def logistics_interface = "{\"content\":\"${content.trim()}\",\"formatType\":\"${formatType.trim()}\",\"bizKey\":\"${bizKey.trim()}\",\"bizType\":\"${bizType.trim()}\"}"
            
            // Otros campos fijos que siempre son los mismos
            def partner_code = 'GATE_31450540'
            def from_code = 'gccs-overseas'
            def msg_type = 'GLOBAL_CUSTOMS_DECLARE_NOTIFY'
            def msg_id = '1725538172016'

            // Crear el objeto de solicitud para la API de destino ParcelDeclare
            RequestObject targetRequest = new RequestObject()
            targetRequest.setRestUrl('https://dev-icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY')
            targetRequest.setRestRequestMethod('POST')

            // Crear los parámetros formateados para x-www-form-urlencoded
            def parameters = [
                ('logistics_interface') : logistics_interface,
                ('partner_code') : partner_code,
                ('from_code') : from_code,
                ('msg_type') : msg_type,
                ('data_digest') : dataDigest,
                ('msg_id') : msg_id
            ]

            // Configurar las cabeceras de la solicitud
            targetRequest.setHttpHeaderProperties([
                new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"),
                new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI="),
                new TestObjectProperty("Cache-Control", ConditionType.EQUALS, "no-cache"),
                new TestObjectProperty("Accept", ConditionType.EQUALS, "*/*")
            ])

            // Codificar los parámetros para x-www-form-urlencoded
            def formBody = parameters.collect { k, v -> "$k=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')

            // Establecer el cuerpo de la solicitud
            targetRequest.setBodyContent(new HttpTextBodyContent(formBody, 'UTF-8', 'application/x-www-form-urlencoded'))

            // Enviar la solicitud a la API ParcelDeclare
            def targetResponse = WS.sendRequest(targetRequest)

            // Verificar la respuesta de la API destino
            WS.verifyResponseStatusCode(targetResponse, 200)

            // Mostrar el mensaje response de la API cainiao/ParcelDeclare
            println("Response de cainiao/ParcelDeclare: " + targetResponse.getResponseText())			
        } else {
            println("Error: Faltan valores necesarios en la respuesta")
        }
   


