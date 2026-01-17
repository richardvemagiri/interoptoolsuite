package com.ecw.deidtool.controller;

import com.ecw.deidtool.DeidToolApplication;
import com.ecw.deidtool.config.AppProperties;
import com.ecw.deidtool.interfaces.DeIDFileService;
import com.ecw.deidtool.interfaces.StorageService;
import com.ecw.deidtool.storage.StorageException;
import com.ecw.deidtool.storage.StorageFileNotFoundException;
import com.ecw.deidtool.storage.StorageProperties;
import com.nimbusds.oauth2.sdk.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(controllers = {DeIDAppController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                DeidToolApplication.class,
                AppProperties.class})
@MockBean(classes = {AppProperties.class, StorageProperties.class, DeIDFileService.class})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DeIDAppControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService mockStorageService;

    @MockBean
    private DeIDFileService deIDFileService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private Principal principal;

    @Mock
    private Resource mockResource;


    @Test
    @DisplayName("showHomePage: [GET] View Homepage: Azure AD [OFF]")
    public void showHomePage_given_whenAzureADOff_thenReturnHomeViewPage() throws Exception {

        //given
        String viewName = "home";

        //when url is launched

        //then return "home" view
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewName));
    }

    @Test
    @DisplayName("showHomePage: [GET] View Homepage: SessionID Attribute Exists")
    public void showHomePage_given_whenHomeUrlLaunched_thenCheckSessionIDAttr() throws Exception {

        //given
        String viewName = "home";

        //when url is launched
//        Mockito.when(session.getId()).thenReturn("TestFolder");

        //then check sessionID attribute
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewName))
                .andExpect(MockMvcResultMatchers.model().attributeExists("sessionID"));
    }

    @Test
    @DisplayName("showHomePage: [GET] View Homepage: Check Storage Service call")
    public void showHomePage_given_whenHomeUrlLaunched_thenCheckStorageServiceCall() throws Exception {

        //given

        //when url is launched

        //then check if storage service's createUserDIR method is called
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(result -> {
                    Mockito.verify(mockStorageService, Mockito.times(1)).createUserDIR(anyString());
                });
    }


    @Test
    @DisplayName("showHomePage: [GET] View Homepage: Azure AD [ON]")
    @EnabledIf(expression = "${spring.cloud.azure.active-directory.enabled}", loadContext = true)
    public void showHomePage_given_whenAzureADOn_thenCheckPrincipalName() throws Exception {

        //given

        //when url is launched

        //then check if storage service's createUserDIR method is called
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(result -> {
                    Mockito.verify(mockStorageService, Mockito.times(1)).createUserDIR(anyString());
                });

    }


    @Test
    @DisplayName("showDeIDFileForUser: [GET] With DeID Files for user")
    public void showDeIDFileForUser_givenDeIDFileExists_whenMethodCall_thenFetchDeIDFileForUser() throws Exception {

        //given
        Path filePath1 = Paths.get("TestFile1.xml");
        Path filePath2 = Paths.get("TestFile2.xml");
        String viewName = "response :: deid-ccda";

        //when file paths need to be loaded
        Mockito.when(mockStorageService.loadAllForUser()).thenReturn(Stream.of(filePath1, filePath2));

        mockMvc.perform(MockMvcRequestBuilders.get("/loadFileForUser"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewName))
                .andExpect(MockMvcResultMatchers.model().attributeExists("files"))
                .andExpect(result -> {
                    List<String> files = (List<String>) result.getModelAndView()
                            .getModel()
                            .get("files");
                    assert files != null;
                    assert files.size() == 2;

                    //verify file URLS contain file names
                    assert files.stream().anyMatch(url -> url.contains("TestFile1.xml"));
                    assert files.stream().anyMatch(url -> url.contains("TestFile2.xml"));
                });
    }

    @Test
    @DisplayName("showDeIDFileForUser: [GET] Without DeID Files for user")
    public void showDeIDFileForUser_givenNoFilesExist_whenMethodCall_thenReturnEmptyList() throws Exception {

        //given
        String viewName = "response :: deid-ccda";

        //when file paths need to be loaded
        Mockito.when(mockStorageService.loadAllForUser()).thenReturn(Stream.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loadFileForUser"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewName))
                .andExpect(MockMvcResultMatchers.model().attributeExists("files"))
                .andExpect(result -> {
                    List<String> files = (List<String>) result.getModelAndView()
                            .getModel()
                            .get("files");
                    assert files != null;
                    assert files.isEmpty();
                });
    }

    @Test
    @DisplayName("Storage Service Error")
    public void showDeIDFileForUser_givenStorageServiceError_whenMethodCall_thenReturnException() throws Exception {
         //given - there is an error in the Storage Service

        // when
        Mockito.when(mockStorageService.loadAllForUser()).thenThrow(new StorageException("Storage service error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/loadFileForUser"))
                .andExpect((MockMvcResultMatchers.status().isInternalServerError()));

    }

    @Test
    @DisplayName("serveFile: WhenFileExists_ShouldReturnFile")
    public void serveFile_WhenFileExists_ShouldReturnFile() throws Exception {
        //given
        String filename = "C-CDA_TestSample-DeID.xml";

        //when
        Mockito.when(mockResource.getFilename()).thenReturn(filename);
        Mockito.when(mockStorageService.loadAsResource(filename)).thenReturn(mockResource);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/files/{filename}", filename))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""));
    }

    @Test
    @DisplayName("serveFile: WhenFileDoesNotExists_ShouldReturnError")
    public void serveFile_WhenFileDoesNotExists_ShouldReturnError() throws Exception {

        //given
        String filename = "";

        //when
        Mockito.when(mockStorageService.loadAsResource(filename)).thenReturn(null);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/files/{filename}", filename))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("serveFile: WhenFileNameHasSpaces_ShouldReturnFile")
    public void serveFile_WhenFileNameHasSpaces_ShouldReturnFile() throws Exception{
        //given
        String filename = "Test C-CDA DeID Sample.xml";

        //when
        Mockito.when(mockResource.getFilename()).thenReturn(filename);
        Mockito.when(mockStorageService.loadAsResource(filename)).thenReturn(mockResource);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/files/{filename}", filename))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""));
    }

    @Test
    @DisplayName("processFile: WhenMultipartFileisEmpty_ShouldReturnMessage")
    public void processFile_WhenMultipartFileisEmpty_ShouldReturnMessage() throws Exception{

        //given
        String viewname = "response :: deid-ccda";
        List<String> categoryArray = Arrays.asList("cat1", "cat2");
        MultipartFile multipartFile = new MockMultipartFile("file", "test.xml", "application/xml", "".getBytes());

        //when
        Mockito.when(deIDFileService.deidentifyCCDA(ArgumentMatchers.any(MultipartFile.class), ArgumentMatchers.anyList())).thenReturn(true);
        Mockito.when(mockStorageService.loadAllForUser()).thenReturn(Stream.of(Paths.get("test.xml")));
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/")
                                .file((MockMultipartFile) multipartFile)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .param("file", multipartFile)
//                        .param("categories", categoryArray)
                        .param("categories", "category1", "category2")
                        .content(multipartFile.getBytes()))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
//                    assert  multipartFile.isEmpty();
                    Mockito.verify(mockStorageService, Mockito.times(1)).emptyUserDir();

                })
                .andExpect(MockMvcResultMatchers.model().attribute("userFeedback", "Please upload a file"))
                .andExpect(MockMvcResultMatchers.view().name(viewname));

    }

    @Test
    @DisplayName("StorageFileNotFoundExceptionHandler")
    @EnabledIf(expression = "${spring.cloud.azure.active-directory.enabled}", loadContext = true)
    public void test_handleStorageFileNotFound() throws Exception {

        //when
        Mockito.when(mockStorageService.loadAllForUser()).thenThrow(StorageFileNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/loadFileForUser"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("test_handleStorageError")
    public void test_handleStorageError() throws Exception {

        Mockito.doThrow(StorageException.class).when(mockStorageService).init();
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @DisplayName("test_handleMaxUploadSizeExceeded")
    public void test_handleMaxUploadSizeExceeded() throws Exception {

        String viewName = "response :: deid-ccda";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.xml", "application/xml", new byte[(int)6292880]);


//        Mockito.doThrow(MaxUploadSizeExceededException.class).when(mockStorageService).store(mockMultipartFilemultipartFile, mockMultipartFilemultipartFile);


        Mockito.doThrow(MaxUploadSizeExceededException.class).when(deIDFileService).deidentifyCCDA(ArgumentMatchers.any(MultipartFile.class), ArgumentMatchers.anyList());

//        Mockito.when(deIDFileService.deidentifyCCDA(ArgumentMatchers.any(MultipartFile.class), ArgumentMatchers.anyList())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/")
                        .file(mockMultipartFile)
                        .param("categories", "category1", "category2")
                        .content(mockMultipartFile.getBytes()))
                .andExpect(MockMvcResultMatchers.status().isPayloadTooLarge());
    }
}
