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


// Definir la cantidad de iteraciones
int numIterations = 1  // Puedes cambiar este número según tus necesidades

// Método para generar un número en el formato CB000123457790
String generateCopNo() {
    def prefix = "Cq"
    def randomNumber = (int)(Math.random() * 9999999999) + 1234567890  
    return prefix + String.format("%013d", randomNumber)
}

// Método para generar un número en el formato OLVA00012010
String generateWayBillNo() {
    def prefix = "OLVq"
    def randomNumber = (int)(Math.random() * 9999999999) + 1200000000  
    return prefix + String.format("%08d", randomNumber)
}

// Método para seleccionar datos aleatorios del Excel
String[] getRandomDataFromExcel(String testDataId, String columnNameFullName, String columnNameTinNo) {
    // Cargar los datos del archivo Excel
    def testData = findTestData('Data Files/datos_passport')
    
    // Obtener el número de filas en el archivo Excel
    int rowCount = testData.getRowNumbers()
    
    // Generar un índice aleatorio para seleccionar una fila
    Random random = new Random()
    int randomRow = random.nextInt(rowCount) + 1  

    // Obtener los datos de la fila seleccionada
    String fullName = testData.getValue(columnNameFullName, randomRow)
    String tinNo = testData.getValue(columnNameTinNo, randomRow)
    
    return [fullName, tinNo]  // Devolver ambos valores en un array
}

// Bucle para ejecutar el Test Case `numIterations` veces
for (int i = 1; i <= numIterations; i++) {
    println("Ejecución # " + i)  

    // Crear un nuevo objeto de solicitud para la API Codificador/ParceldeclareCod
    RequestObject requestObject = new RequestObject()
    requestObject.setRestUrl('https://icrossborder.olvacourier.com/util/convertidorParcelDeclare') 
    requestObject.setRestRequestMethod('POST')

    // Generar los valores dinámicos para copNo y wayBillNo
    String copNo = generateCopNo()
    String wayBillNo = generateWayBillNo()

    // Obtener datos aleatorios del Excel
    String[] randomData = getRandomDataFromExcel('Data Files/datos_passport', 'fullName', 'tinNo')  
    String fullName = randomData[0]
    String tinNo = randomData[1]

    // Definir el cuerpo de la solicitud en duro, usando los valores generados
    String requestBody = """
    {
        "guid": "a1cf1c241846434ca241803a2ae10f7a",
        "appType": "1",
        "appTime": "202102071948050800",
        "platform": "AE",
        "transportNo": "sdfsdfsdfsd",
        "clearanceMode": "9610",
        "etd": "s5fgg6sf",
        "eta": "s5fg51s6s",
        "testMode": "N",
        "orderNo": "2020020719111",
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
            "address": "China guangdong, dongguanCity",
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
            "address": "LIMA~LA MOLINA- Urb. Las Vi as Apartamento 101",
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
                "gnum": 2,
                "itemId": "111111112",
                "hsCode": "3301000000",
                "itemName": "Ropa",
                "currency": "USD",
                "qty": "10",
                "country": "CN",
                "price": "50.500"
            },
            {
                "gnum": 3,
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

    // Configurar las cabeceras para la solicitud
    requestObject.setHttpHeaderProperties([
        new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
        new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI=")
    ])

    // Asignar el cuerpo de la solicitud
    requestObject.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))

    try {
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
            targetRequest.setRestUrl('https://icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY')
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
            WS.verifyResponseStatusCode(targetResponse, 201)

            // Mostrar el mensaje response de la API cainiao/ParcelDeclare
            println("Response de cainiao/ParcelDeclare: " + targetResponse.getResponseText())
			
			// Parsear el cuerpo de la respuesta como JSON
			def encryptedResponseBody = targetResponse.getResponseText()
			def responseJson = jsonSlurper.parseText(encryptedResponseBody)
			
			// Decodificar el campo 'content' en Base64
			byte[] decodedBytes = Base64.decoder.decode(responseJson.content)
			String decryptedResponseBody = new String(decodedBytes, 'UTF-8')

			// Imprimir la respuesta desencriptada
			println("Cuerpo de la respuesta (desencriptada): " + decryptedResponseBody)

        } else {
            println("Error: Faltan valores necesarios en la respuesta")
        }
    } catch (Exception e) {
        println("Error en la ejecución # " + i + ": " + e.message)
    }
}

// Imprimir final
println("Test ejecutado " + numIterations + " veces con éxito")

