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

    // Establecer el campo obligatorio a vacío respetando su tipo de dato
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
            def originalValue = currentMap[fieldName]
            if (originalValue instanceof Map) {
                currentMap[fieldName] = [:] // Mapa vacío
            } else if (originalValue instanceof List) {
                currentMap[fieldName] = [] // Lista vacía
            } else {
                currentMap[fieldName] = "" // Cadena vacía
            }
        }
    } else {
        if (requestBodyMap.containsKey(campoObligatorioJson)) {
            def originalValue = requestBodyMap[campoObligatorioJson]
            if (originalValue instanceof Map) {
                requestBodyMap[campoObligatorioJson] = [:] // Mapa vacío
            } else if (originalValue instanceof List) {
                requestBodyMap[campoObligatorioJson] = [] // Lista vacía
            } else {
                requestBodyMap[campoObligatorioJson] = "" // Cadena vacía
            }
        }
    }

    // Convertir el Map de vuelta a JSON
    String modifiedRequestBody = JsonOutput.toJson(requestBodyMap)

    // Imprimir el requestBody modificado
    println("\nCuerpo de la solicitud (requestBody) con el campo '${campoObligatorio}' vacío:")
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
        int initialStatusCode = response.getStatusCode()

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

        // Verificar que los campos existen
        if (content && formatType && bizKey && bizType && dataDigest) {

            // Preparar la solicitud a GLOBAL_CUSTOMS_DECLARE_NOTIFY
            def logistics_interface = "{\"content\":\"${content.trim()}\",\"formatType\":\"${formatType.trim()}\",\"bizKey\":\"${bizKey.trim()}\",\"bizType\":\"${bizType.trim()}\"}"

            // Imprimir el logistics_interface
            println("\nContenido de 'logistics_interface':")
            println(JsonOutput.prettyPrint(logistics_interface))

            // Crear los parámetros para x-www-form-urlencoded
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

            // Variable para almacenar la respuesta decodificada
            decryptedResponseBody = ""

            if (statusCode == 400) {
                // Verificar que el código de respuesta es 400
                WS.verifyResponseStatusCode(targetResponse, 400, FailureHandling.CONTINUE_ON_FAILURE)

                // Parsear el cuerpo de la respuesta como JSON
                def encryptedResponseBody = targetResponse.getResponseText()
                def responseJson = jsonSlurper.parseText(encryptedResponseBody)

                if (responseJson.content) {
                    // Decodificar el campo 'content' en Base64
                    byte[] decodedBytes = Base64.decoder.decode(responseJson.content)
                    decryptedResponseBody = new String(decodedBytes, 'UTF-8')
                } else {
                    decryptedResponseBody = "La respuesta no contiene el campo 'content'."
                }

            } else {
                // Manejar cualquier código de respuesta diferente de 400
                // Imprimir el código de respuesta y el cuerpo
                println("\nCódigo de respuesta diferente de 400: ${statusCode}")
                println("Cuerpo de la respuesta: ${targetResponse.getResponseText()}")

                // Intentar decodificar y descomprimir si es necesario
                def encryptedResponseBody = targetResponse.getResponseText()
                try {
                    def responseJson = jsonSlurper.parseText(encryptedResponseBody)

                    if (responseJson.content) {
                        // Decodificar el campo 'content' en Base64
                        byte[] decodedBytes = Base64.decoder.decode(responseJson.content)

                        // Intentar descomprimir usando GZIP
                        ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes)
                        GZIPInputStream gzipInputStream = new GZIPInputStream(bais)
                        byte[] buffer = new byte[1024]
                        int len
                        ByteArrayOutputStream baos = new ByteArrayOutputStream()
                        while ((len = gzipInputStream.read(buffer)) > 0) {
                            baos.write(buffer, 0, len)
                        }
                        gzipInputStream.close()
                        baos.close()

                        String decompressedContent = new String(baos.toByteArray(), StandardCharsets.UTF_8)

                        // Imprimir el contenido descomprimido
                        decryptedResponseBody = decompressedContent

                        println("\nContenido descomprimido de 'content':")
                        println(JsonOutput.prettyPrint(decryptedResponseBody))
                    } else {
                        decryptedResponseBody = "La respuesta no contiene el campo 'content'."
                    }
                } catch (Exception ex) {
                    decryptedResponseBody = "No se pudo decodificar o descomprimir la respuesta: ${ex.message}"
                    println(decryptedResponseBody)
                }
            }

        } else {
            println("Error: Faltan valores necesarios en la respuesta")
            decryptedResponseBody = "Error: Faltan valores necesarios en la respuesta del primer servicio."
            statusCode = initialStatusCode
        }

    } catch (AssertionError e) {
        KeywordUtil.markFailedAndContinue("Prueba con campo '${campoObligatorio}' falló: " + e.message)
        statusCode = -1
        decryptedResponseBody = "Prueba fallida: " + e.message
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
        println("\nPrueba con campo '${campoObligatorio}' vacío:")
        println("Código de respuesta: ${statusCode}")
        println("Respuesta decodificada: ${decryptedResponseBody}")
    }
}

// Después de finalizar el bucle, imprimir el resumen de resultados
println("Número total de filas en el Excel: " + rowCount)

println("\nResumen de resultados de las pruebas:")
resultadosPruebas.each { resultado ->
    println("----------------------------------------------------")
    println("Campo vacío: ${resultado.campo}")
    println("Código de respuesta: ${resultado.codigoRespuesta}")
    println("Respuesta decodificada:")
    println(resultado.respuestaDecodificada)
}
println("----------------------------------------------------")
println("Número total de campos procesados: ${resultadosPruebas.size()}")

// Exportar los resultados a un archivo CSV
def csvFile = new File("resumen_resultados.csv")
def csvHeader = "Campo vacío,Código de respuesta,Respuesta decodificada\n"

// Crear o sobrescribir el archivo CSV y escribir el encabezado
csvFile.write(csvHeader)

// Escribir los resultados en el archivo CSV
resultadosPruebas.each { resultado ->
	def campo = resultado.campo.replaceAll(",", " ") // Reemplazar comas en los datos para evitar problemas en CSV
	def codigoRespuesta = resultado.codigoRespuesta.toString()
	def respuestaDecodificada = resultado.respuestaDecodificada.replaceAll("\\r?\\n", " ").replaceAll(",", " ")
	def csvLine = "${campo},${codigoRespuesta},${respuestaDecodificada}\n"
	csvFile << csvLine
}

println("Los resultados han sido exportados al archivo 'resumen_resultados.csv'.")