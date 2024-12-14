import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.callTestCase(findTestCase('Reporte_Regularizacion/0-Login/Inicio'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/span_Est. Consign_select2-selection select2_1ba879'))

WebUI.delay(1)

WebUI.scrollToElement(findTestObject('New Folder/VALID_ OK'), 0)

WebUI.click(findTestObject('New Folder/VALID_ OK'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/button_Buscar'))

WebUI.delay(2)

WebUI.verifyElementText(findTestObject('Object Repository/td_VALIDADO OK'), 'VALIDADO OK')

WebUI.verifyElementText(findTestObject('Object Repository/td_VALIDADO OK'), 'VALIDADO OK')

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/ul_VALIDADO OK'))

WebUI.delay(1)

WebUI.click(findTestObject('Object Repository/li_CORREGIDO'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/button_Buscar'))

WebUI.delay(2)

WebUI.verifyElementPresent(findTestObject('Object Repository/td_CORREGIDO'), 0)

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/span_Est. Consign_select2-selection select2_1ba879'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/li_VALIDADO ERROR_1'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/span_'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2'))

WebUI.delay(2)

WebUI.verifyElementText(findTestObject('Object Repository/td_VALIDADO ERROR'), 'VALIDADO ERROR')

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2_3'))

WebUI.delay(5)

WebUI.rightClick(findTestObject('Object Repository/input_Est. Consign_select2-search__field'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/li_rsolsol'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2_3_4'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/button_Buscar'))

WebUI.delay(2)

WebUI.verifyElementText(findTestObject('Object Repository/td_rsolsol'), 'rsolsol')

WebUI.verifyElementText(findTestObject('Object Repository/td_rsolsol'), 'rsolsol')

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/ul_rsolsol'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/li_santoro'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2_3_4_5'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/button_Buscar'))

WebUI.delay(2)

WebUI.scrollToElement(findTestObject('Object Repository/td_santoro'), 0)

WebUI.verifyElementText(findTestObject('Object Repository/td_santoro'), 'santoro')

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2_3_4_5_6'))

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/label_Motivo'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/li_DNI NO MATCH'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/li_RUC NO EXISTE'))

WebUI.click(findTestObject('Object Repository/section_Inicio                             _7f11ec_1_2_3_4_5_6_7'))

WebUI.delay(2)

WebUI.click(findTestObject('Object Repository/button_Buscar'))

WebUI.delay(2)

WebUI.verifyElementText(findTestObject('Object Repository/td_DNI NO MATCH'), 'DNI NO MATCH')

WebUI.verifyElementText(findTestObject('Object Repository/td_RUC NO EXISTE'), 'RUC NO EXISTE')

