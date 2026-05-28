package com.educ.integration.xslt;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XSLTTransformer {

    public static String transform(String xmlContent, String xsltPath) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xsltSource = new StreamSource(new java.io.File(xsltPath));
            Transformer transformer = factory.newTransformer(xsltSource);

            Source xmlSource = new StreamSource(new StringReader(xmlContent));
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            transformer.transform(xmlSource, result);
            return writer.toString();
        } catch (Exception e) {
            System.err.println("XSLT转换失败: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String toUnifiedFormat(String xmlContent, String department) {
        String xsltPath = "xsl/" + department + "_to_unified.xsl";
        return transform(xmlContent, xsltPath);
    }

    public static String fromUnifiedFormat(String xmlContent, String targetDepartment) {
        String xsltPath = "xsl/unified_to_" + targetDepartment + ".xsl";
        return transform(xmlContent, xsltPath);
    }
}
