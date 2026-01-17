package com.ecw.deidtool.helper;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.ecw.deidtool.repository.DeIDConfigDAO;
import com.ecw.deidtool.repository.DeIDDBConfig;
import com.ecw.deidtool.storage.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;


/**
 * Few simple utils to read DOM. This is originally from the Jakarta Commons
 * Modeler.
 *
 * @author Costin Manolache
 */

@Slf4j
@Component
public final class DOMXmlHelper {


//    private static AppProperties appProperties;

    private final DeIDConfigDAO deIDConfigDAO;
    private static Map<String, String> xPathMap = new HashMap<>();
    private static List<DeIDDBConfig> deIDDBConfigList;
    private Map<String, String> namespaceMap;


    private DOMXmlHelper(DeIDConfigDAO deIDConfigDAO) {
        this.deIDConfigDAO = deIDConfigDAO;
        deIDDBConfigList = deIDConfigDAO.findAllByIsDeleted(0);
        log.info("Config from DB cached");
        log.debug("Cached DB Config: {}", deIDDBConfigList);
    }

    public static NamespaceContext getNameSpaceContext(Map<String, String> nsMap) {
        NamespaceContext namespaceContext = new CCDANamespaceContextResolver(nsMap);
        return namespaceContext;
    }

    @Value("#{appProperties.namespaces}")
    public void setNamespaceMap(Map<String, String> ccdaNamespaceMap) {
        this.namespaceMap = ccdaNamespaceMap;
        String defaultNamespaceValue = this.namespaceMap.get("default");
        this.namespaceMap.remove("default");
        this.namespaceMap.put("", defaultNamespaceValue);
        log.debug("C-CDA resolved namespaces: " + this.namespaceMap);
    }

    public static boolean isXMLFile(MultipartFile file) {

        boolean isXML = false;
        String mimeType = null;

        if(Objects.isNull(file))
            return false;

        AutoDetectParser parser = new AutoDetectParser();
        Detector detector = parser.getDetector();
        try {
            Metadata metadata = new Metadata();
            TikaInputStream stream = TikaInputStream.get(file.getInputStream());
            MediaType mediaType = detector.detect(stream, metadata);
            mimeType = mediaType.toString();
            log.info("Uploaded File Type: " + mimeType);
            if (mimeType.equalsIgnoreCase("application/xml")) {

                isXML = true;
            }
        } catch (IOException e) {
            log.error(MimeTypes.OCTET_STREAM);
        }

        return isXML;
    }

    public String removePII(String xmlText, List<String> categories) {
        Document document = convertStringToDocument(xmlText);
        document = removePII(document,categories);
        return convertDocumentToString(document);
    }

    public Document removePII(Document document, List<String> categories) {
        log.debug("categories: {}", categories);
        xPathMap = getXPathMapForCategories(categories);
        if(Objects.isNull(xPathMap) || xPathMap.size()==0)
            return null;
        document = replaceDataInXpaths(document, xPathMap);
        return document;
    }

    public MultipartFile removePII(MultipartFile file, List<String> categories) {
        Document document = convertFileToDocument(file);
        document = removePII(document,categories);
        return convertDocumentToMultipartFile(document);
    }

    private Document replaceDataInXpaths(Document document, Map<String, String> xPathMap) {

        Set<String> updatedXPaths = new HashSet<>();
        List<String> xPathsNotUpdated;

        for (String expression : xPathMap.keySet()) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(getNameSpaceContext(this.namespaceMap));
            log.debug("XPATH: {}", expression);
            XPathExpression xPathExpr = null;
            NodeList nodeList = null;
            try {
                xPathExpr = xpath.compile(expression);
                nodeList = (NodeList) xPathExpr.evaluate(document, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                log.error("Error parsing XPath '" + expression + "'");
                throw new RuntimeException(e);
            }

            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                log.debug("XPATHS Node(" + j + "): " + node.getNodeName());
                if (!node.getTextContent().isEmpty()) {
                    node.setTextContent(xPathMap.get(expression));
                    updatedXPaths.add(expression);
                }
            }
        }

        log.info("XPaths updated: {}", updatedXPaths.toString());
        xPathsNotUpdated = new ArrayList<>(xPathMap.keySet());
        xPathsNotUpdated.removeAll(updatedXPaths);

        cleanupXPath(xPathsNotUpdated);
        log.debug("XPaths Not Updated: {}", xPathsNotUpdated);

        if (!updatedXPaths.isEmpty() && updatedXPaths.size() <= xPathMap.size()) {
            return document;
        }

        return null;
    }




    private MultipartFile convertDocumentToMultipartFile(Document document) {
        if (Objects.isNull(document))
                return null;
        byte[] byteArr = convertDocumentToByteArray(document);
        return new CustomMultipartFile(byteArr);
    }

    public byte[] convertDocumentToByteArray(Document document){
        try{

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(document);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            return outputStream.toByteArray();
        }catch (TransformerException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public String convertDocumentToString(Document document) {
        try {
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            if(Objects.isNull(document))
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }



    public Document convertFileToDocument(MultipartFile file) {
        InputStream inputStream = null;
        Document document = null;

        if(!isXMLFile(file))
            return null;

        try {
            inputStream = file.getInputStream();
            document = convertInputStreamToDocument(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("Error reading XML file. ", e);
        }

        return document;
    }

    public Document convertStringToDocument(String xmlText) {

        if(Objects.isNull(xmlText) || xmlText.isEmpty())
            return null;

        InputStream inputStream = new ByteArrayInputStream(xmlText.trim().getBytes());

        return convertInputStreamToDocument(inputStream);
    }

    public static Document convertInputStreamToDocument(InputStream is) {

        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            db.setEntityResolver(new NullResolver());
            doc = db.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Unable to parse input text: {}", e);
        }

        return doc;
    }

    public static Document convertInputStreamToDocument_DOMParser (InputStream is) {
        try {
            // Create a DOM parser
            DOMParser parser = new DOMParser();

            // Set the parser to be lenient
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);

            // Parse the invalid XML
            //parser.parse(new InputSource(new StringReader(is)));

            // Retrieve the document
            Document document = parser.getDocument();

            // Process the document as needed
            // ...

            return document;

        } catch (Exception e) {
            e.printStackTrace();
        }



        return null;


    }



    private Map<String, String> getXPathMapForCategories(List<String> categories) {
        Map<String, String> xPathsForCategories = new HashMap<>();

        if(Objects.isNull(categories))
            return null;

        for (String category : categories) {
            for (DeIDDBConfig deIDDBConfig : deIDDBConfigList) {
                if (deIDDBConfig.getCategory().equalsIgnoreCase(category)) {
                    xPathsForCategories.put(deIDDBConfig.getXPath(), deIDDBConfig.getValue());
                }
            }
        }

        log.debug("xPathValuesForCategories: {}", xPathsForCategories);
        return xPathsForCategories;
    }


    private static void cleanupXPath(List<String> xPathsNotUpdated) {
        // clean up namespaces in xPaths
        xPathsNotUpdated.replaceAll(path -> path.replace("/:", "/"));
    }



}


class NullResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId)  {
        return new InputSource(new StringReader(""));
    }
}









