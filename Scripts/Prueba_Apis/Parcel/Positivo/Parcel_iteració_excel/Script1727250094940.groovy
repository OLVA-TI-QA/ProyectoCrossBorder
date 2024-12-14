import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent as HttpTextBodyContent
import groovy.json.JsonSlurper as JsonSlurper
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import java.net.URLEncoder
import com.kms.katalon.core.testobject.TestObjectProperty  
import com.kms.katalon.core.testobject.ConditionType  
import java.util.Base64  // Importar la clase para decodificar Base64

// Crear una lista para almacenar las respuestas
List<String> allResponses = new ArrayList<String>()

// Obtener el número total de filas en el archivo de Excel
int rowCount = findTestData('Data Files/Datajson').getRowNumbers()

// Contador de filas ejecutadas exitosamente
int successCount = 0

// Iterar sobre cada fila del archivo Excel y obtener las respuestas
for (int index = 1; index <= rowCount; index++) {
    try {
        // Obtener el body dinámico desde el archivo de Excel
        def requestBody = findTestData('Data Files/Datajson').getValue('json_tramas', index)

        // Cargar el objeto de la API desde el Object Repository
        RequestObject requestObject = findTestObject('Object Repository/Codificador/ParceldeclareCod')

        // Reemplazar el body de la solicitud con el valor del archivo Excel
        requestObject.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))
		
		requestObject.setHttpHeaderProperties([
			new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
			new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI=")
		])

        // Enviar la solicitud a la API tramaparceldeclare
        def response = WS.sendRequest(requestObject)

        // Verificar la respuesta de la API
        WS.verifyResponseStatusCode(response, 200)

        // Parsear el response para extraer campos específicos
        def jsonResponse = new JsonSlurper().parseText(response.getResponseText())

        // Obtener campos del response
        def content = jsonResponse.body?.content
        def formatType = jsonResponse.body?.formatType
        def bizKey = jsonResponse.body?.bizKey
        def bizType = jsonResponse.body?.bizType
        def dataDigest = jsonResponse.dataDigest

        // Verificación de valores antes de proceder
        if ((((content && formatType) && bizKey) && bizType) && dataDigest) {
			// Imprimir los valores obtenidos para depuración
			/*println('Content: ' + content)
	
			println('Format Type: ' + formatType)
	
			println('BizKey: ' + bizKey)
	
			println('BizType: ' + bizType)
	
			println('DataDigest: ' + dataDigest)*/
			
            // Formatear el campo logistics_interface
            def logistics_interface = "{\"content\":\"${content.trim()}\",\"formatType\":\"${formatType.trim()}\",\"bizKey\":\" ${bizKey.trim()}\",\"bizType\":\"${bizType.trim()}\"}"
            
            // Otros campos fijos que siempre son los mismos
            def partner_code = 'GATE_31450540'
            def from_code = 'gccs-overseas'
            def msg_type = 'GLOBAL_CUSTOMS_DECLARE_NOTIFY'
            def msg_id = '1725538172016'

            // Crear el objeto de la API de destino para el envío de datos en x-www-form-urlencoded
            RequestObject targetRequest = findTestObject('Object Repository/cainiao/ParcelDeclare')

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
            WS.verifyResponseStatusCode(targetResponse, 201, FailureHandling.CONTINUE_ON_FAILURE)
			
			// Mostrar el mensaje response de la API cainiao/ParcelDeclare
			println("Response de cainiao/ParcelDeclare para la fila $index: " + targetResponse.getResponseText())

            // Almacenar el response original en la lista
            allResponses.add(response.getResponseText())

            // Aumentar el contador de ejecuciones exitosas
            successCount++
        } else {
            println("Error: Faltan valores para la fila $index")
        }

        // Imprimir el response en la consola para cada iteración
        println("Response para la fila $index: " + response.getResponseText())

        // Liberar memoria innecesaria
        jsonResponse = null
        requestObject = null
        response = null
        targetRequest = null
        System.gc() // Invocar recolección de basura

        // **Aquí agregamos la pausa cada 50 filas procesadas**
        if (index % 50 == 0) {
            println("Pausa de 2 segundos después de procesar 50 filas.")
            Thread.sleep(2000) // Pausa de 2 segundos
        }

    } catch (Exception e) {
        println("Error en la fila $index: ${e.message}")
    }
}

// Imprimir el total de registros procesados exitosamente
println("Total de registros ejecutados con éxito: $successCount")
