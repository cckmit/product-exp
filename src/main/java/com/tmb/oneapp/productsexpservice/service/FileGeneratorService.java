package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.model.request.notification.FlexiLoanSubmissionWrapper;
import lombok.AllArgsConstructor;
import org.apache.fop.apps.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;

@Service
@AllArgsConstructor
public class FileGeneratorService {
    private static final TMBLogger<FileGeneratorService> logger = new TMBLogger<>(FileGeneratorService.class);
    private final ObjectMapper mapper;
    private final FopFactory fopFactory;
    private final TransformerFactory transformerFactory;

    private void generatePDFFile(String jsonData, String fileName, String template) throws IOException, FOPException, TransformerException {
        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        String baseDir = System.getProperty("user.dir");
        userAgent.getRendererOptions().put(
                "target-bitmap-size", new Dimension(1200, 1650));
        File outputDir = new File(baseDir + File.separator + "pdf");
        outputDir.mkdir();
        File pdfFile = new File(outputDir, fileName + ".pdf");

        String xmlString = getXMLString(jsonData);
        StreamSource data = new StreamSource(new StringReader(xmlString));
        try (OutputStream out = new FileOutputStream(pdfFile);
             BufferedOutputStream buffOut = new BufferedOutputStream(out)) {

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, buffOut);
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File(template)));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(data, res);
            logger.info("generated pdf success:{}", pdfFile.getAbsolutePath());
        }
    }

    private String getXMLString(String jsonDataString) {
        String xmlString = "";
        try {
            xmlString = XML.toString(new JSONObject(jsonDataString), "customroot");
        } catch (JSONException e) {
            logger.info("JSONException in getXMLString {}", e.toString());
        }
        return xmlString;
    }

    public void generateFlexiLoanSubmissionPdf(FlexiLoanSubmissionWrapper request, String fileName, String template) {
        try {
            String jsonData = mapper.writeValueAsString(request);
            generatePDFFile(jsonData, fileName, template);
        } catch (IOException | FOPException | TransformerException e) {
            logger.error("generate flexi loan submission pdf got error:{}", e);
        }
    }
}
