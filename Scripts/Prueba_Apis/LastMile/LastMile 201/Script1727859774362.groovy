import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent as HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper as JsonSlurper
import java.util.Random
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.net.URLEncoder
import java.util.Base64
import groovy.json.JsonOutput


// Definir la cantidad de iteraciones
int numIterations = 1  // Cambia este número según tus necesidades

// Definir las URLs de las APIs y el API Key en un solo lugar
String apiKey = "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI="

String convertidorParcelDeclareUrl = "https://dev-icrossborder.olvacourier.com/util/convertidorParcelDeclare"
String globalCustomsDeclareNotifyUrl = "https://dev-icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY"
String lastmileAsnFirmaUrl = "https://dev-icrossborder.olvacourier.com/util/lastmileAsnFirma"
String productionGlobalCustomsDeclareNotifyUrl = "https://dev-icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY"

// Método para generar un número en el formato CB000123457790
String generateCopNo() {
    def prefix = "Cr"
	def timestamp = System.currentTimeMillis()
    def randomNumber = (int)(Math.random() * 9999999999) + 1004567890
    return prefix + String.format("%013d", timestamp + randomNumber)
}

// Método para generar un número en el formato OLVA00012010
String generateWayBillNo() {
    def prefix = "OLV8"
	def timestamp = System.currentTimeMillis()
    def randomNumber = (int)(Math.random() * 9999999999) + 1004567890
    return prefix + String.format("%08d", timestamp + randomNumber)
}

// Método para seleccionar datos aleatorios del Excel
String[] getRandomDataFromExcel(String testDataId, String columnNameFullName, String columnNameTinNo) {
    def testData = findTestData('Data Files/datos_passport')
    int rowCount = testData.getRowNumbers()
    Random random = new Random()
    int randomRow = random.nextInt(rowCount) + 1
    String fullName = testData.getValue(columnNameFullName, randomRow)
    String tinNo = testData.getValue(columnNameTinNo, randomRow)
    return [fullName, tinNo]
}

// Inicializar variables fuera del bucle para mantener su valor después
String lastGeneratedWayBillNo = ""
String lastGeneratedFullName = ""
String lastGeneratedTinNo = ""

// Bucle para ejecutar el Test Case `numIterations` veces
for (int i = 1; i <= numIterations; i++) {
    println("Ejecución # " + i)

    // Crear un nuevo objeto de solicitud para la API Codificador/ParceldeclareCod
    RequestObject requestObject = new RequestObject()
    requestObject.setRestUrl(convertidorParcelDeclareUrl)
    requestObject.setRestRequestMethod('POST')

    String copNo = generateCopNo()
    String wayBillNo = generateWayBillNo()
    // Almacenar el wayBillNo generado
    lastGeneratedWayBillNo = wayBillNo

    String[] randomData = getRandomDataFromExcel('Data Files/datos_passport', 'fullName', 'tinNo')
    String fullName = randomData[0]
    String tinNo = randomData[1]
    // Almacenar fullName y tinNo generados
    lastGeneratedFullName = fullName
    lastGeneratedTinNo = tinNo

    String requestBody = """
     {
        "guid": "a1cf1c241846434ca241803a2ae10f7a",
        "appType": "1",
        "appTime": "20210207194805+0800",
        "platform": "AE",
        "transportNo": "sdfsdfsdfsd",
        "clearanceMode": "9610",
        "etd": "s5fgs65g1s6fg51sdfsdfsdsg6sf",
        "eta": "s5fgs65g1s6fg51sdfsdfsdsg6sf",
        "testMode": "N",
        "orderNo": "202002071910111111",
        "logisticsCode": "LP000123456789",
        "copNo": "$copNo",
        "masterWayBill": "123456789",
        "wayBillNo": "$wayBillNo",
        "secWayBillNo": "SSN0123456789",
        "bigBagId": "BIG0123456789",
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
        "lastMileCode": "TRUNK_0123456",
        "senderInfo": {
            "address": "China guangdong dongguan machong xinshalu xinsha Road, machongTown, dongguanCity",
            "city": "dongguan",
            "country": "CN",
            "district": "machong",
            "mobilePhone": "13416879753",
            "name": "xuxiansheng",
            "state": "guangdong",
            "storeName": "Cutesliving Store",
            "street": "xinshalu~~~xinsha Road, ",
            "telephone": "13416879753",
            "zipCode": "523133"
        },
        "receiverInfo": {
            "address": "LIMA~~LA MOLINA~~~Calle La Cordillera 598 - Urb. Las Viñas Apartamento 101",
            "city": "LA MOLINA",
            "country": "PE",
            "district": "",
            "email": "pefalconi82@gmail.com",
            "mobilePhone": "962201152",
            "name": "$fullName",
            "state": "LIMA",
            "street": "Calle La Cordillera 598 ",
            "telephone": "+51",
            "zipCode": "15024"
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
            "fullName": "$fullName",
            "tinNo": "$tinNo"
        },
        "feature": {
            "invoiceUrl": "123"
        },
        "itemList": [
            {
                "gnum": 1,
                "itemId": "111111111",
                "hsCode": "3301000000",
                "itemName": "MONITOR",
                "currency": "USD",
                "qty": "10",
                "country": "CN",
                "price": "150.500"
            },
            {
                "gnum": 1,
                "itemId": "111111112",
                "hsCode": "3301000000",
                "itemName": "Ropa",
                "currency": "USD",
                "qty": "10",
                "country": "CN",
                "price": "50.500"
            },
            {
                "gnum": 1,
                "itemId": "111111113",
                "hsCode": "3301000000",
                "itemName": "Zapatilla",
                "currency": "USD",
                "qty": "10",
                "country": "CN",
                "price": "250.500"
            }
        ]
    }
    """

    requestObject.setHttpHeaderProperties([
        new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
        new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, apiKey)
    ])
    requestObject.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))

    try {
        def response = WS.sendRequest(requestObject)
        WS.verifyResponseStatusCode(response, 200)
        println("Response parcel: " + response.getResponseText())

        def jsonResponse1 = new JsonSlurper().parseText(response.getResponseText())
        def content1 = jsonResponse1.body?.content
        def formatType1 = jsonResponse1.body?.formatType
        def bizKey1 = jsonResponse1.body?.bizKey
        def bizType1 = jsonResponse1.body?.bizType
        def dataDigest1 = jsonResponse1.dataDigest

        if (content1 && formatType1 && bizKey1 && bizType1 && dataDigest1) {
            def logistics_interface = "{\"content\":\"${content1.trim()}\",\"formatType\":\"${formatType1.trim()}\",\"bizKey\":\"${bizKey1.trim()}\",\"bizType\":\"${bizType1.trim()}\"}"
            RequestObject targetRequest = new RequestObject()
            targetRequest.setRestUrl(globalCustomsDeclareNotifyUrl)
            targetRequest.setRestRequestMethod('POST')

            def parameters = [
                ('logistics_interface'): logistics_interface,
                ('partner_code'): 'GATE_31450540',
                ('from_code'): 'gccs-overseas',
                ('msg_type'): 'GLOBAL_CUSTOMS_DECLARE_NOTIFY',
                ('data_digest'): dataDigest1,
                ('msg_id'): '1725538172016'
            ]

            targetRequest.setHttpHeaderProperties([
                new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"),
                new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, apiKey)
            ])

            def formBody = parameters.collect { k, v -> "$k=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')
            targetRequest.setBodyContent(new HttpTextBodyContent(formBody, 'UTF-8', 'application/x-www-form-urlencoded'))

            def targetResponse = WS.sendRequest(targetRequest)

			// Validar el código de estado 201 antes de continuar
			if (WS.verifyResponseStatusCode(targetResponse, 201)) {
			    println("Código de estado 201 recibido, procediendo a la función lastmileAsnFirma")
			
			    def encryptedResponseBody = targetResponse.getResponseText()
			    def responseJson1 = new JsonSlurper().parseText(encryptedResponseBody)
			
			    if (responseJson1.content) {
			        byte[] decodedBytes1 = Base64.decoder.decode(responseJson1.content)
			        String decryptedResponseBody1 = new String(decodedBytes1, 'UTF-8')
			        println("Cuerpo de la respuesta (desencriptada): " + decryptedResponseBody1)
			
			        // Aquí procedes a la lógica de la función lastmileAsnFirma
			        println("Construyendo el LastMile body para lastmileAsnFirma...")
			
			        // Usar el wayBillNo generado anteriormente
			        String wayBillNoForLastMile = lastGeneratedWayBillNo
			
			        // Construir el cuerpo del lastmile
			        String lastMileBody = """
			        {
			            "preCPResCode": "GATE_31450540",
			            "parcel": {
			                "priceUnit": "CENT",
			                "parcelQuantity": "1",
			                "priceCurrency": "USD",
			                "price": "3",
			                "bigBagWeightUnit": "g",
			                "bigBagID": "BIGBAG000677360650768",
			                "goodsList": [
			                    {
			                        "priceUnit": "CENT",
			                        "quantity": "3",
			                        "productID": "1005005992657336",
			                        "weight": "30",
			                        "declarePrice": "3",
			                        "categoryName": "Skirts",
			                        "categoryCNName": "pruebas",
			                        "url": "http://www.aliexpress.com/item/1005005992657336.html",
			                        "categoryFeature": "00",
			                        "productCategory": "Womens Clothing|Skirts",
			                        "priceCurrency": "USD",
			                        "cnName": "pruebas",
			                        "price": "3",
			                        "name": "Skirts Shuyuntest PASScopycopycopycopy",
			                        "itemPrice": "0",
			                        "suggestedENName": "Skirts Shuyuntest PASScopycopycopycopy",
			                        "categoryID": "349",
			                        "weightUnit": "g"
			                    }
			                ],
			                "weight": "160",
			                "bigBagWeight": "1",
			                "suggestedWeight": "400",
			                "weightUnit": "g"
			            },
			            "outboundTime": "2024-09-18 09:11:56",
			            "interCPResCode": "DISTRIBUTOR_31451353",
			            "bizType": "AE_4PL_STANDARD",
			            "receiver": {
			                "zipCode": "13012",
			                "address": {
			                    "country": "PE",
			                    "province": "Lima",
			                    "city": "LINCE",
			                    "district": "",
			                    "latitude": "",
			                    "detailAddress": "prueba cambio direccion Jiron Belisario Flores 855 Lince",
			                    "longitude": ""
			                },
			                "areaId": "165",
			                "phone": "+51",
			                "name": "snjsncdj",
			                "mobile": "959591642",
			                "email": "giuliano.balletta@gmail.com"
			            },
			            "currentCPResCode": "DISTRIBUTOR_31451353",
			            "laneCode": "L_CN_PE_STANDARD_OLVA",
			            "needCOD": "false",
			            "externalInfo": {
			                "cnId": "2206480288614",
			                "preOnlineAfterConsign": "false"
			            },
			            "logisticsOrderCode": "LP00677360650768",
			            "routingTrial": "1",
			            "insuranceInfo": {},
			            "trade": {
			                "priceUnit": "CENT",
			                "priceCurrency": "USD",
			                "price": "4000",
			                "tradeID": "8183862480050642"
			            },
			            "codNum": "",
			            "expectedDelivery": {},
			            "sender": {
			                "zipCode": "523000",
			                "address": {
			                    "country": "China",
			                    "province": "guang dong sheng",
			                    "city": "dong guan sh",
			                    "district": "dong guan shi",
			                    "detailAddress": "sha tian zhenjin gang nan lu 231 hao cai niao ding zhi cang 2 hao ku 3 lou ke jie cang ku"
			                },
			                "phone": "18766666666",
			                "storeUrl": "",
			                "name": "huang ling",
			                "mobile": "18766666666",
			                "imID": "aliqatest01"
			            },
			            "customs": {
			                "declarePriceTotal": "3",
			                "buyerTaxId": "$lastGeneratedTinNo"
			            },
			            "toPortCode": "LIM",
			            "trackingNumber": "$wayBillNoForLastMile",
			            "codNumCurrency": "",
			            "returnParcel": {
			                "undeliverableOption": "1",
			                "zipCode": "523000",
			                "address": {
			                    "country": "CN",
			                    "province": "pruebas",
			                    "city": "pruebas",
			                    "district": "",
			                    "detailAddress": "pruebas"
			                },
			                "phone": "18756083905",
			                "name": "pruebas ",
			                "mobile": "18756083905",
			                "imID": "AET001"
			            },
			            "waybillUrl": "waybillUrl",
			            "carrierCode": "carrierCode",
			            "waybillNumber": "RU00212323",
			            "nextCPResCode": "Tran_Store_1710058"
			        }
			        """
			
			        // Enviar el lastmile body a la API 'lastmileAsnFirma'
			        RequestObject lastmileAsnFirmaRequest = new RequestObject()
			        lastmileAsnFirmaRequest.setRestUrl(lastmileAsnFirmaUrl)
			        lastmileAsnFirmaRequest.setRestRequestMethod('POST')
			        lastmileAsnFirmaRequest.setHttpHeaderProperties([
			            new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
			            new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, apiKey)
			        ])
			        lastmileAsnFirmaRequest.setBodyContent(new HttpTextBodyContent(lastMileBody, 'UTF-8', 'application/json'))
			
			        def lastmileAsnFirmaResponse = WS.sendRequest(lastmileAsnFirmaRequest)
			        WS.verifyResponseStatusCode(lastmileAsnFirmaResponse, 200)
			        println("Respuesta de lastmileAsnFirma: " + lastmileAsnFirmaResponse.getResponseText())
			
			   
	// Procesar la respuesta de lastmileAsnFirma
	def lastmileAsnFirmaResponseJson = new JsonSlurper().parseText(lastmileAsnFirmaResponse.getResponseText())
	def lastmileDataDigest = lastmileAsnFirmaResponseJson.dataDigest
	def lastmileBodyContent = lastmileAsnFirmaResponseJson.body
	
	println("Respuesta de lastmileAsnFirma post: " + lastmileBodyContent)
	
	if (lastmileBodyContent && lastmileDataDigest) {
	    		
	    // Enviar la solicitud a GLOBAL_CUSTOMS_DECLARE_NOTIFY
	    RequestObject globalCustomsRequest = new RequestObject()
	    globalCustomsRequest.setRestUrl(productionGlobalCustomsDeclareNotifyUrl)
	    globalCustomsRequest.setRestRequestMethod('POST')
		
	    // Convertir lastmileBodyContent a una cadena JSON y luego a texto
		String lastmileBodyContentString = JsonOutput.toJson(lastmileBodyContent)
		
		
		// Enviar el lastmileBodyContent en formato text como lo tienes en la imagen
		def customParameters = [
		    ('logistics_interface'): lastmileBodyContentString,
		    ('partner_code')       : 'DISTRIBUTOR_31451353',
		    ('from_code')          : 'CNGFC',
		    ('msg_type')           : 'CAINIAO_GLOBAL_LASTMILE_ASN',
		    ('data_digest')        : lastmileDataDigest,
		    ('msg_id')             : 'CB12345678'
		]
		
		globalCustomsRequest.setHttpHeaderProperties([
			new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"),
			new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, apiKey)
		])
	
			
		// Codificar los parámetros como `application/x-www-form-urlencoded`
		def customFormBody = customParameters.collect { k, v -> "$k=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')
		
		// Preparar la solicitud con el cuerpo codificado
		globalCustomsRequest.setBodyContent(new HttpTextBodyContent(customFormBody, 'UTF-8', 'application/x-www-form-urlencoded'))
		
		println("Cuerpo de la solicitud enviada: " + customFormBody)
		println("logistics_interface enviada: " + lastmileBodyContentString)
		println("data_digest enviada: " + lastmileDataDigest)
		
		// Enviar la solicitud y verificar la respuesta
		def globalCustomsResponse = WS.sendRequest(globalCustomsRequest)
		WS.verifyResponseStatusCode(globalCustomsResponse, 201)
		
		// Imprimir la respuesta del servidor
		println("Respuesta completa del servidor:")
		println(globalCustomsResponse.getResponseText())

			
	    // Mostrar el mensaje response de la API GLOBAL_CUSTOMS_DECLARE_NOTIFY
	    println("Respuesta de GLOBAL_CUSTOMS_DECLARE_NOTIFY: " + globalCustomsResponse.getResponseText())
	} else {
	    println("Error: Faltan valores necesarios en la respuesta de lastmileAsnFirma")
	}
        }
			} else {
				// Si el código de estado no es 201, imprime error
				println("Error: El código de estado no es 201. Se detiene el flujo.")
			}
			
		} else {
			// Si el código de estado no es 201, lanza error
			println("Error: El código de estado de targetRequest no es 201. Fin del flujo.")
		}

    } catch (Exception e) {
        println("Error en la ejecución # " + i + ": " + e.message)
        e.printStackTrace()
    }
}

// Fin del proceso