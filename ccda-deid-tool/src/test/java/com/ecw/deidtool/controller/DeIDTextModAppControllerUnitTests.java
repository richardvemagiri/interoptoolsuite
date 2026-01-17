package com.ecw.deidtool.controller;

import com.ecw.deidtool.DeidToolApplication;
import com.ecw.deidtool.config.AppProperties;
import com.ecw.deidtool.controller.DeIDTextModAppController;
import com.ecw.deidtool.interfaces.DeIDFileService;
import com.ecw.deidtool.interfaces.DeIDTextService;
import com.ecw.deidtool.storage.StorageProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = {DeIDTextModAppController.class},
            excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                                        DeidToolApplication.class,
                                        AppProperties.class})
@MockBean(classes = {AppProperties.class, StorageProperties.class, DeIDFileService.class})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DeIDTextModAppControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

//    @InjectMocks
//    private DeIDTextModAppController deIDTextModAppController;

    @MockBean
    private DeIDTextService deIDTextServiceMock;

//    @MockBean
//    private AppProperties appProperties;

//    @MockBean
//    private DeIDAppConfig deIDAppConfig;

//
//    @Test
//    @BeforeAll
//    public static void setupTest(){
//        System.out.println("Test method");
//
//    }


    // test cases are more valuable for operations involving service layer

    @Test
    @DisplayName("[POST] TextMod: Return DeID CCDA")
    public void givenCCDAString_whenDeidentifyCCDAReturnsResponse_thenReturnThymeleafFragment() throws Exception {

        // given - precondition or setup
        String expectedResult = "response :: deid-ccda-text";
        String ccdaXml = "DummyCCDAInput";
        String dummyCCDAOutput = "DummyCCDAOutput";
        String[] categoryArray = {"dummy", "data"};
        List<String> categories = List.of(categoryArray);

        // when - deIDTextService is called
        Mockito.when(deIDTextServiceMock.deidentifyCCDAXMLText(ccdaXml, categories))
                .thenReturn(dummyCCDAOutput);

        // then return view name and model attributes
        mockMvc.perform(MockMvcRequestBuilders.post("/textmod")
                        .param("ccdaXML", ccdaXml)
                        .param("categories", categoryArray))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(expectedResult))
                .andExpect(MockMvcResultMatchers.model().attribute("ccdaOutput", dummyCCDAOutput))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("userFeedbackForText"));
    }

    @Test
    @DisplayName("[POST] TextMod: Null Response from Service")
    public void givenCCDAString_whenDeidentifyCCDAReturnsNull_thenReturnErrorMsg() throws Exception {

        // given - precondition or setup
        String expectedResult = "response :: deid-ccda-text";
        String ccdaXML = "DummyCCDAInput";
        String[] categoryArray = {"dummy", "data"};
        List<String> categories = List.of(categoryArray);

        // when - deIDTextService is called
        when(deIDTextServiceMock.deidentifyCCDAXMLText(ccdaXML, categories))
                .thenReturn(null);

        // then return view name
        mockMvc.perform(MockMvcRequestBuilders.post("/textmod")
                        .param("ccdaXML", ccdaXML)
                        .param("categories", categoryArray))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(expectedResult))
                .andExpect(MockMvcResultMatchers.model().attribute("userFeedbackForText", "Error occurred! Try again."))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("ccdaOutput"));
    }

    @Test
    @DisplayName("[POST] TextMod: Blank Response from Service")
    public void givenCCDAString_whenDeidentifyCCDAReturnsBlank_thenReturnErrorMsg() throws Exception {

        // given - precondition or setup
        String expectedResult = "response :: deid-ccda-text";
        String ccdaXML = "DummyCCDAInput";
        String[] categoryArray = {"dummy", "data"};
        List<String> categories = List.of(categoryArray);

        // when - deIDTextService is called
        when(deIDTextServiceMock.deidentifyCCDAXMLText(ccdaXML, categories))
                .thenReturn("");

        // then return view name
        mockMvc.perform(MockMvcRequestBuilders.post("/textmod")
                        .param("ccdaXML", ccdaXML)
                        .param("categories", categoryArray))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(expectedResult))
                .andExpect(MockMvcResultMatchers.model().attribute("userFeedbackForText", "No patient identifiers found!"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("ccdaOutput"));

    }


}


