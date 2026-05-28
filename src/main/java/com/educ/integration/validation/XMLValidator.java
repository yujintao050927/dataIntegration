package com.educ.integration.validation;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;

public class XMLValidator {

    public static boolean validate(String xmlContent, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            Source source = new StreamSource(new StringReader(xmlContent));
            validator.validate(source);
            return true;
        } catch (SAXException e) {
            System.err.println("XML验证失败: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("验证过程出错: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateCourses(String xmlContent, String department) {
        String xsdPath = "xsd/unified_course.xsd";
        return validate(xmlContent, xsdPath);
    }

    public static boolean validateRequest(String xmlContent) {
        String xsdPath = "xsd/request.xsd";
        return validate(xmlContent, xsdPath);
    }
}
