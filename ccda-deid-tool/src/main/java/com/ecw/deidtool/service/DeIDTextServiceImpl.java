package com.ecw.deidtool.service;

import com.ecw.deidtool.helper.DOMXmlHelper;
import com.ecw.deidtool.interfaces.DeIDTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DeIDTextServiceImpl implements DeIDTextService {

    private final DOMXmlHelper domXmlHelper;

    public DeIDTextServiceImpl(DOMXmlHelper domXmlHelper) {
        this.domXmlHelper = domXmlHelper;
    }

    @Override
    public String deidentifyCCDAXMLText(String xmlText, List<String> categories) {

        String deIDCCDAXML = null;

        if(Objects.isNull(xmlText) || xmlText.length() <=0)
            return null;

        deIDCCDAXML = domXmlHelper.removePII(xmlText, categories);
        log.info("C-CDA XML De-identified");

        return deIDCCDAXML;

    }
}
