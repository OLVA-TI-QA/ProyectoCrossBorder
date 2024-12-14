import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.kms.katalon.core.util.KeywordUtil
import java.util.Base64
import java.net.URLEncoder
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import com.kms.katalon.core.model.FailureHandling

// Mapa de tipos esperados según la documentación
def tiposEsperados = [
    "guid": String,
    "appType": String,
    "appTime": String,
    "testMode": String,
    "logisticsCode": String,
    "copNo": String,
    "masterWayBill": String,
    "wayBillNo": String,
    "transportType": String,
    "transportNo": String,
    "etd": String,
    "eta": String,
    "ieFlag": String,
    "declareCountry": String,
    "fromCountry": String,
    "goodsPrice": BigDecimal,
    "postPrice": BigDecimal,
    "totalPrice": BigDecimal,
    "currency": String,
    "grossWeight": BigDecimal,
    "receiverInfo": Map,  
    "senderInfo": Map,     
    "returnInfo": Map,     
    "taxPt": String,       
    "itemList": List       
]

// Función para validar los tipos de datos
def validarTiposDeDatos(requestBodyMap, tiposEsperados) {
    def errores = []

    tiposEsperados.each { campo, tipoEsperado ->
        if (requestBodyMap.containsKey(campo)) {
            def valor = requestBodyMap[campo]
            if (!(valor.getClass() == tipoEsperado)) {
                errores << "El campo '${campo}' debe ser de tipo ${tipoEsperado.simpleName}, pero se encontró ${valor.getClass().simpleName}."
            }
        }
    }

    return errores
}

// Cargar los datos desde el archivo Excel "campos_negativos"
def testDataNegativos = findTestData('Data Files/campos_negativos')
int rowCount = testDataNegativos.getRowNumbers()

// Lista para almacenar los resultados de cada prueba
def resultadosPruebas = []

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
        "goodsPrice": 100.50,
        "taxPrice": 55.50,
        "postPrice": 10.00,
        "totalPrice": 170.00,
        "currency": "CNY",
        "grossWeight": 50.00,
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

    // Eliminar el campo obligatorio si es necesario
    if (campoObligatorioJson.contains('.')) {
        def parts = campoObligatorioJson.split('\\.')
        def currentMap = requestBodyMap
        for (int j = 0; j < parts.length - 1; j++) {
            if (currentMap.containsKey(parts[j]) && currentMap[parts[j]] instanceof Map) {
                currentMap = currentMap[parts[j]]
            } else {
                currentMap = null
                break
            }
        }
        if (currentMap != null) {
            def fieldName = parts[parts.length - 1]
            if (currentMap.containsKey(fieldName)) {
                currentMap.remove(fieldName) // Eliminar el campo del JSON
            }
        }
    } else {
        if (requestBodyMap.containsKey(campoObligatorioJson)) {
            requestBodyMap.remove(campoObligatorioJson) // Eliminar el campo del JSON
        }
    }

    // Validar tipos de datos antes de enviar la solicitud
    def erroresDeTipos = validarTiposDeDatos(requestBodyMap, tiposEsperados)

    // Si hay errores de tipos, fallar la prueba
    if (!erroresDeTipos.isEmpty()) {
        erroresDeTipos.each { error ->
            println(error)
        }
        KeywordUtil.markFailedAndStop("Error de tipos de datos en la solicitud.")
    }

    // Convertir el Map de vuelta a JSON
    String modifiedRequestBody = JsonOutput.toJson(requestBodyMap)

    // Imprimir el requestBody modificado
    println("\nCuerpo de la solicitud (requestBody) con el campo '${campoObligatorio}' eliminado:")
    println(JsonOutput.prettyPrint(modifiedRequestBody))

    // Crear un nuevo objeto de solicitud
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

    // Variables para almacenar el código de respuesta y la respuesta decodificada
    int statusCode = 0
    String decryptedResponseBody = ""

    try {
        // Enviar la solicitud a la API
        def response = WS.sendRequest(requestObject)

        // Obtener el código de respuesta
        statusCode = response.getStatusCode()

        // Verificar que el código de respuesta es 200
        WS.verifyResponseStatusCode(response, 200, FailureHandling.CONTINUE_ON_FAILURE)

        // Parsear la respuesta
        def jsonResponse = jsonSlurper.parseText(response.getResponseText())

        // Obtener campos necesarios para la siguiente solicitud
        def content = jsonResponse.body?.content
        def formatType = jsonResponse.body?.formatType
        def bizKey = jsonResponse.body?.bizKey
        def bizType = jsonResponse.body?.bizType
        def dataDigest = jsonResponse.dataDigest

        if (content && formatType && bizKey && bizType && dataDigest) {
            // Preparar la solicitud a GLOBAL_CUSTOMS_DECLARE_NOTIFY
            def logistics_interface = "{\"content\":\"${content.trim()}\",\"formatType\":\"${formatType.trim()}\",\"bizKey\":\"${bizKey.trim()}\",\"bizType\":\"${bizType.trim()}\"}"

            // Codificar los parámetros para x-www-form-urlencoded
            def parameters = [
                ('logistics_interface') : logistics_interface,
                ('partner_code') : 'GATE_31450540',
                ('from_code') : 'gccs-overseas',
                ('msg_type') : 'GLOBAL_CUSTOMS_DECLARE_NOTIFY',
                ('data_digest') : dataDigest,
                ('msg_id') : '1725538172016'
            ]

            // Configurar la solicitud a GLOBAL_CUSTOMS_DECLARE_NOTIFY
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

            // Establecer el cuerpo de la solicitud
            targetRequest.setBodyContent(new HttpTextBodyContent(formBody, 'UTF-8', 'application/x-www-form-urlencoded'))

            // Enviar la solicitud a GLOBAL_CUSTOMS_DECLARE_NOTIFY
            def targetResponse = WS.sendRequest(targetRequest)

            // Obtener el código de respuesta
            statusCode = targetResponse.getStatusCode()

            // Imprimir la respuesta
            println("\nRespuesta de la API 'GLOBAL_CUSTOMS_DECLARE_NOTIFY':")
            println(targetResponse.getResponseText())

        } else {
            println("Error: Faltan valores necesarios en la respuesta del primer servicio")
        }

    } catch (Exception e) {
        KeywordUtil.markFailedAndContinue("Error en la prueba con campo '${campoObligatorio}': " + e.message)
        statusCode = -1
        decryptedResponseBody = "Excepción capturada: " + e.message
    } finally {
        // Agregar los resultados a la lista
        resultadosPruebas.add([
            'campo': campoObligatorio,
            'codigoRespuesta': statusCode,
            'respuestaDecodificada': decryptedResponseBody
        ])

        // Imprimir los detalles de la prueba actual
        println("\nPrueba con campo '${campoObligatorio}' eliminado:")
        println("Código de respuesta: ${statusCode}")
        println("Respuesta decodificada: ${decryptedResponseBody}")
    }
}

// Después de finalizar el bucle, imprimir el resumen de resultados
println("Número total de filas en el Excel: " + rowCount)

println("\nResumen de resultados de las pruebas:")
resultadosPruebas.each { resultado ->
    println("----------------------------------------------------")
    println("Campo eliminado: ${resultado.campo}")
    println("Código de respuesta: ${resultado.codigoRespuesta}")
    println("Respuesta decodificada:")
    println(resultado.respuestaDecodificada)
}
println("----------------------------------------------------")
println("Número total de campos procesados: ${resultadosPruebas.size()}")
