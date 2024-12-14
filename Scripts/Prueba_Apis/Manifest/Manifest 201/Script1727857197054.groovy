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

// Definir API keys y URLs en un solo lugar
final String API_KEY = "ZEArkj7WfXt3qGsFZyiP4XLffuMInlcaDOJKytZ1OHI="
final String PARCEL_DECLARE_URL = "https://dev-icrossborder.olvacourier.com/util/convertidorParcelDeclare"
final String MANIFEST_DECLARE_URL = "https://dev-icrossborder.olvacourier.com/util/convertidorManifestDeclare"
final String GLOBAL_CUSTOMS_DECLARE_NOTIFY_URL = "https://dev-icrossborder.olvacourier.com/API/GLOBAL_CUSTOMS_DECLARE_NOTIFY"

// Definir la cantidad de iteraciones
int numIterations = 1  // Cambia este número según tus necesidades

// Método para generar un número en el formato CB000123457790
String generateCopNo() {
    def prefix = "Ca"
    def timestamp = System.currentTimeMillis()
    def randomNumber = (int)(Math.random() * 99999)
    return prefix + String.format("%013d", timestamp + randomNumber)
}

// Método para generar un número en el formato OLVA00012010 (con timestamp para evitar repetición)
String generateWayBillNo() {
    def prefix = "OLVa"
    def timestamp = System.currentTimeMillis()
    def randomNumber = (int)(Math.random() * 99999)
    return prefix + String.format("%08d", timestamp + randomNumber)
}

// Método para generar un masterWayBill
String generateMasterWayBill() {
    def prefix = "MB9"
    def randomNumber = (int)(Math.random() * 9999999999) + 1000000000
    return prefix + String.format("%09d", randomNumber)
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

// Lista para almacenar los copNo y wayBillNo generados en cada iteración
def parcelListData = []

// Bucle para ejecutar el Test Case `numIterations` veces
for (int i = 1; i <= numIterations; i++) {
    println("Ejecución # " + i)

    // Crear un nuevo objeto de solicitud para la API Codificador/ParceldeclareCod
    RequestObject requestObject = new RequestObject()
    requestObject.setRestUrl(PARCEL_DECLARE_URL)  // Usar la variable centralizada
    requestObject.setRestRequestMethod('POST')

    String copNo = generateCopNo()
    String wayBillNo = generateWayBillNo()
    String[] randomData = getRandomDataFromExcel('Data Files/datos_passport', 'fullName', 'tinNo')
    String fullName = randomData[0]
    String tinNo = randomData[1]

    String requestBody = """
    {
        "guid": "a1cf1c241846434ca241803a2ae10f7a",
        "appType": "1",
        "appTime": "20241009194805+0800",
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
        new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, API_KEY)  // Usar la API key centralizada
    ])
    requestObject.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))

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
        targetRequest.setRestUrl(GLOBAL_CUSTOMS_DECLARE_NOTIFY_URL)
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
            new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, API_KEY)
        ])

        def formBody = parameters.collect { k, v -> "$k=${URLEncoder.encode(v.toString(), 'UTF-8')}" }.join('&')
        targetRequest.setBodyContent(new HttpTextBodyContent(formBody, 'UTF-8', 'application/x-www-form-urlencoded'))

        try {
            def targetResponse = WS.sendRequest(targetRequest)

            // Verificar el código de estado 201
            if (WS.verifyResponseStatusCode(targetResponse, 201)) {
                println("Código 201 recibido. Continuando con el proceso...")

                // Procesar la respuesta si el código de estado es 201
                def encryptedResponseBody = targetResponse.getResponseText()
                def responseJson1 = new JsonSlurper().parseText(encryptedResponseBody)

                if (responseJson1.content) {
                    byte[] decodedBytes1 = Base64.decoder.decode(responseJson1.content)
                    String decryptedResponseBody1 = new String(decodedBytes1, 'UTF-8')
                    println("Cuerpo de la respuesta (desencriptada): " + decryptedResponseBody1)
                }

                // Almacenar los valores de copNo y wayBillNo generados en esta iteración
                parcelListData.add([copNo: copNo, wayBillNo: wayBillNo])

                // Continuar con el proceso de `ManifestDeclare`
                println("Construyendo el parcelList para el ManifestDeclare...")

                // Crear la lista de paquetes para el `ManifestDeclare`
                def parcelList = []
                int gnum = 1

                // Estructura del `parcelList` con los datos almacenados en `parcelListData`
                parcelListData.each { parcel ->
                    def parcelEntry = """
                    {
                        "gnum": $gnum,
                        "logisticsCode": "sdfgsfgsdgsdf",
                        "wayBillNo": "${parcel.wayBillNo}",
                        "copNo": "${parcel.copNo}",
                        "bigBagId": "BIG0123456789"
                    }
                    """
                    parcelList.add(parcelEntry.trim()) // Añadir el JSON formateado
                    gnum++
                }

                // Convertir `parcelList` a formato JSON correctamente con múltiples entradas
                String parcelListJson = parcelList.join(",\n")
                String masterWaybill = generateMasterWayBill()

                println("Parcel List para el ManifestDeclare: " + parcelListJson)

                // Ahora, crear la solicitud para `ManifestDeclare`
                String manifestBody = """
                {
                  "guid": "16d257a6a3554546b1d4621acd435dc1",
                  "appType": "1",
                  "appTime": "20241009112740+0800",
                  "platform": "AE",
                  "clearanceMode": "9610",
                  "testMode": "N",
                  "ieFlag": "E",
                  "copNo": "CB9876543210", 
                  "masterWayBill": "$masterWaybill",
                  "declareCountry": "CN",
                  "declarePortCode": "5314",
                  "fromPortCode": "5314",
                  "toPortCode": "110",
                  "transportType": "4",
                  "transportNo": "A123456",
                  "netWeight": "100.0",
                  "grossWeight": "100.0",
                  "virTotalCount": "50",
                  "parcelCount": "50",
                  "declarePassCount": "50",
                  "bigBagCount": "10",
                  "etd": "2024-10-09",
                  "eta": "2024-10-09",
                  "pmc": [
                      "123",
                      "456"
                  ],
                  "declareFailList": [
                      "CB0123456789"
                  ],
                  "feature": {
                      "customsStatus": "399"
                  },
                  "parcelList": [
                    ${parcelListJson}
                  ]
                }
                """

                println("Manifest Body: " + manifestBody)

                // Enviar la solicitud de `ManifestDeclare`
                RequestObject manifestRequest = new RequestObject()
                manifestRequest.setRestUrl(MANIFEST_DECLARE_URL)  // Usar la URL centralizada
                manifestRequest.setRestRequestMethod('POST')
                manifestRequest.setHttpHeaderProperties([
                    new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json"),
                    new TestObjectProperty("X-API-KEY", ConditionType.EQUALS, API_KEY)  // Usar la API key centralizada
                ])
                manifestRequest.setBodyContent(new HttpTextBodyContent(manifestBody, 'UTF-8', 'application/json'))

                def manifestResponse = WS.sendRequest(manifestRequest)
                WS.verifyResponseStatusCode(manifestResponse, 200)
                println("Response de ManifestDeclare: " + manifestResponse.getResponseText())

            } else {
                // Si el código de estado no es 201, terminar el proceso
                println("Error: Código de estado no es 201. Terminando el proceso.")
                return  // Detener la ejecución si no se recibe el código 201
            }

        } catch (Exception e) {
            println("Error en la ejecución # " + i + ": " + e.message)
        }
    }
}
// Fin del proceso
