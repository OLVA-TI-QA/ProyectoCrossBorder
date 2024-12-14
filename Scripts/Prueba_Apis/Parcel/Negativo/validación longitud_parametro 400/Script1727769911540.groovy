import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.model.FailureHandling

// Mapa de longitudes máximas según la documentación
def longitudesMaximas = [
    "guid": 32,
    "appType": 1,
    "appTime": 32,
    "testMode": 1,
    "logisticsCode": 32,
    "copNo": 32,
    "masterWayBill": 64,
    "wayBillNo": 32,
    "transportType": 1,
    "transportNo": 16,
    "etd": 128,
    "eta": 128,
    "ieFlag": 1,
    "declareCountry": 2,
    "fromCountry": 2,
    "goodsPrice": 16,
    "postPrice": 16,
    "totalPrice": 16,
    "currency": 8,
    "grossWeight": 16,
    "receiverInfo": null,  
    "senderInfo": null,    
    "returnInfo": null,    
    "taxPt": null,         
    "itemList": null       
]

// Función para validar la longitud de los campos
def validarLongitudesDeCampos(requestBodyMap, longitudesMaximas) {
    def erroresLongitud = []

    longitudesMaximas.each { campo, longitudMaxima ->
        if (longitudMaxima != null && requestBodyMap.containsKey(campo)) {
            def valor = requestBodyMap[campo]
            if (valor instanceof String && valor.length() > longitudMaxima) {
                erroresLongitud << "El campo '${campo}' tiene una longitud de ${valor.length()}, pero debe tener como máximo ${longitudMaxima} caracteres."
            }
        }
    }

    return erroresLongitud
}

// Cargar los datos desde el archivo Excel "campos_negativos"
def testDataNegativos = findTestData('Data Files/campos_negativos')
int rowCount = testDataNegativos.getRowNumbers()

// Bucle para recorrer cada fila del Excel "campos_negativos"
for (int i = 1; i <= rowCount; i++) {
    String campoObligatorio = testDataNegativos.getValue('obligatorios', i)
    String respuestaEsperada = testDataNegativos.getValue('respuesta', i)

    // No convertir el nombre del campo a minúsculas
    String campoObligatorioJson = campoObligatorio

    // Definir el cuerpo de la solicitud original
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
        "taxPt": "123",
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

    // Convertir el cuerpo a un Map
    def jsonSlurper = new JsonSlurper()
    def requestBodyMap = jsonSlurper.parseText(requestBody)

    // Validar la longitud de los campos antes de enviar la solicitud
    def erroresDeLongitud = validarLongitudesDeCampos(requestBodyMap, longitudesMaximas)

    // Si hay errores de longitud, fallar la prueba
    if (!erroresDeLongitud.isEmpty()) {
        erroresDeLongitud.each { error ->
            println(error)
        }
        KeywordUtil.markFailedAndStop("Error de longitud de los campos en la solicitud.")
    }

    // Convertir el Map de vuelta a JSON
    String modifiedRequestBody = JsonOutput.toJson(requestBodyMap)

    // Imprimir el requestBody modificado
    println("\nCuerpo de la solicitud (requestBody) con las validaciones aplicadas:")
    println(JsonOutput.prettyPrint(modifiedRequestBody))

    // Enviar la primera solicitud
    RequestObject requestObject = new RequestObject()
    requestObject.setRestUrl('https://dev-icrossborder.olvacourier.com/util/convertidorParcelDeclare')
    requestObject.setRestRequestMethod('POST')

    // Configurar las cabeceras
    requestObject.setHttpHeaderProperties([
        new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
        new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI=")
    ])

    // Asignar el cuerpo de la solicitud modificado
    requestObject.setBodyContent(new HttpTextBodyContent(modifiedRequestBody, 'UTF-8', 'application/json'))

    try {
        // Enviar la solicitud a la API
        def response = WS.sendRequest(requestObject)

        // Verificar que el código de respuesta es 400
        WS.verifyResponseStatusCode(response, 400, FailureHandling.CONTINUE_ON_FAILURE)

        // Parsear la respuesta si es 400
        def jsonResponse = jsonSlurper.parseText(response.getResponseText())
        println("Respuesta de la primera solicitud con código 400:\n" + JsonOutput.prettyPrint(JsonOutput.toJson(jsonResponse)))

        // Obtener campos necesarios para la siguiente solicitud
        def content = jsonResponse.body?.content
        def formatType = jsonResponse.body?.formatType
        def bizKey = jsonResponse.body?.bizKey
        def bizType = jsonResponse.body?.bizType
        def dataDigest = jsonResponse.dataDigest

        if (content && formatType && bizKey && bizType && dataDigest) {
            def logistics_interface = "{\"content\":\"${content.trim()}\",\"formatType\":\"${formatType.trim()}\",\"bizKey\":\"${bizKey.trim()}\",\"bizType\":\"${bizType.trim()}\"}"

            // Preparar parámetros para la segunda solicitud
            def parameters = [
                ('logistics_interface') : logistics_interface,
                ('partner_code') : 'GATE_31450540',
                ('from_code') : 'gccs-overseas',
                ('msg_type') : 'GLOBAL_CUSTOMS_DECLARE_NOTIFY',
                ('data_digest') : dataDigest,
                ('msg_id') : '1725538172016'
            ]

            // Configurar la segunda solicitud
            RequestObject targetRequest = new RequestObject()
            targetRequest.setRestUrl('https://dev-icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY')
            targetRequest.setRestRequestMethod('POST')

            // Configurar las cabeceras
            targetRequest.setHttpHeaderProperties([
                new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/x-www-form-urlencoded"),
                new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI="),
                new TestObjectProperty("Cache-Control", ConditionType.EQUALS, "no-cache"),
                new TestObjectProperty("Accept", ConditionType.EQUALS, "*/*")
            ])

            // Codificar los parámetros para x-www-form-urlencoded
            def formBody = parameters.collect { k, v -> "$k=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')

            // Asignar el cuerpo de la solicitud
            targetRequest.setBodyContent(new HttpTextBodyContent(formBody, 'UTF-8', 'application/x-www-form-urlencoded'))

            // Enviar la segunda solicitud
            def targetResponse = WS.sendRequest(targetRequest)

            // Verificar si la segunda solicitud retorna un código 400
            WS.verifyResponseStatusCode(targetResponse, 400, FailureHandling.CONTINUE_ON_FAILURE)

            // Imprimir la respuesta de la segunda solicitud
            println("Respuesta de la segunda solicitud con código 400:\n" + targetResponse.getResponseText())
        } else {
            println("Error: Faltan valores necesarios para la segunda solicitud.")
        }

    } catch (Exception e) {
        // Manejo del error con markFailed para detener la ejecución
        KeywordUtil.markFailed("Error en la solicitud: " + e.message)
    }
}

// Imprimir el resumen de todas las respuestas al final
println("\nResumen de todas las respuestas:")
listaRespuestas.each { respuesta ->
	println("Solicitud: ${respuesta[0]}")
	println("Respuesta: ${respuesta[1]}")
	println("----------------------------------------------------")
}
