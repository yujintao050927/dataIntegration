package edu.nju.di.integration.xslt;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XsltTransformer {
    private final Path xsltDir;
    private final TransformerFactory transformerFactory;

    public XsltTransformer(Path xsltDir) {
        this.xsltDir = xsltDir;
        this.transformerFactory = createSecureTransformerFactory();
    }

    public String transformXml(String xml, String xsltFileName) throws IOException, TransformerException {
        Path xsltPath = xsltDir.resolve(xsltFileName).normalize();
        if (!Files.exists(xsltPath)) {
            throw new IOException("XSLT文件不存在: " + xsltPath);
        }

        try (Reader xsltReader = Files.newBufferedReader(xsltPath, StandardCharsets.UTF_8);
             StringReader xmlReader = new StringReader(xml)) {

            Source xsltSource = new StreamSource(xsltReader);
            Source xmlSource = new StreamSource(xmlReader);

            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            StringWriter out = new StringWriter();
            transformer.transform(xmlSource, new StreamResult(out));
            return out.toString();
        }
    }

    private static TransformerFactory createSecureTransformerFactory() {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException ignored) {
            // 某些实现不支持该feature，忽略即可
        }

        try {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException ignored) {
        }
        return factory;
    }
}
