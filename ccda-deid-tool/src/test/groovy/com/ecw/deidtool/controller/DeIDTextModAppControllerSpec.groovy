package com.ecw.deidtool.controller

import com.ecw.deidtool.config.AppProperties
import com.ecw.deidtool.interfaces.DeIDFileService
import com.ecw.deidtool.interfaces.DeIDTextService
import com.ecw.deidtool.storage.StorageProperties
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title

@WebMvcTest(value  = DeIDTextModAppController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@MockBean(value = [AppProperties.class, StorageProperties.class, DeIDFileService.class])
@Title("TextMode: Controller Unit Tests")
@Ignore
class DeIDTextModAppControllerSpec extends Specification{

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    private DeIDTextService deIDTextServiceMock = Mock()

    @Shared
    private String ccdaXML = "DummyCCDAInput"
    @Shared
    private endpoint = "/textmod"




    def "[POST] TextMode: Return DeID CCDA"(){

        given: "C-CDA XML is provided"
        when: "deIDTextService is called"
            deIDTextServiceMock.deidentifyCCDAXMLText(ccdaXML, categories) >> dummyCCDAOutput

        then: "return view name and model attributes"
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .param("ccdaXML", ccdaXML))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewname))
                .andExpect(MockMvcResultMatchers.model().attribute("ccdaOutput", dummyCCDAOutput))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist(modelAttrNameForUserFeedback))

        where:
            viewname = "response :: deid-ccda-text"
            dummyCCDAOutput = "DummyCCDAOutput"
            modelAttrNameForUserFeedback = "userFeedbackForText"

    }

    def "[POST] TextMod: Null Response from Service Layer"() {

        given: "C-CDA XML is provided"
        when:  "deIDTextService is called"
            deIDTextServiceMock.deidentifyCCDAXMLText(ccdaXML, categories) >> ccdaOutput
        then: "return view name with thymeleaf fragment"
            mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .param("ccdaXML", ccdaXML))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewname))
                .andExpect(MockMvcResultMatchers.model().attribute(modelAttrNameForFeedback, modelAttrValueForFeedback))
                .andExpect(MockMvcResultMatchers.model().attribute(modelAttrNameForCCDAOutput, ccdaOutput))

        where:
            viewname = "response :: deid-ccda-text"
            modelAttrNameForCCDAOutput = "ccdaOutput"
            ccdaOutput = null
            modelAttrNameForFeedback = "userFeedbackForText"
            modelAttrValueForFeedback = "Error occurred! Try again."


    }

}
