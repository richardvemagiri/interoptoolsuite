package com.ecw.deidtool.interfaces;

import java.util.List;

public interface DeIDTextService {

    String deidentifyCCDAXMLText(String xmlText, List<String> categories);
}
