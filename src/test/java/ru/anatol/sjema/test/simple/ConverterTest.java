/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anatol.sjema.test.simple;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Schema;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.anatol.sjema.converter.data.DataModelToJsonDataConverter;
import ru.anatol.sjema.converter.data.JsonDataToDataModelConverter;
import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperParams;
import ru.anatol.sjema.mapper.standard.xmlDateTimeMapper.XmlDateFormatConst;
import ru.anatol.sjema.mapper.standard.xmlDateTimeMapper.XmlDateFormatProducer;
import ru.anatol.sjema.model.data.DataModel;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.producer.TempModelToTreeModelConverter;
import ru.anatol.sjema.producer.TempModelToViewModelConverter;
import ru.anatol.sjema.producer.XsdSchemaResolver;
import ru.anatol.sjema.producer.XsdSchemaToTempModelConverter;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.producer.model.tree.TreeModel;
import ru.anatol.sjema.simple.processor.MessageProcessor;
import ru.anatol.sjema.validator.ViewModelValidator;
import ru.anatol.sjema.xml.DomUtil;
import ru.anatol.sjema.xml.Namespaces;
import ru.anatol.sjema.xml.path.parser.PathParser;
import ru.anatol.sjema.xml.path.parser.PathPrinter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterTest {

    /**
     * Logger.
     */
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ConverterTest.class);

    @Test
    public void path() throws Exception {
        /*
        <?xml version="1.0" encoding="UTF-8"?>
        <parent>
           <child>
             <id mode="Key">Test</id>
             <item>
                <value>Target</value>
             </item>
           </child>
         </parent>
         */
        Namespaces namespaces = new Namespaces("ns", "namespace");
        List<String> paths = new ArrayList<>();
        paths.add("/ns:parent/ns:child[1]/ns:item[../ns:id[./@mode='Key']='Test']/ns:value");
        paths.add("/ns:parent/self::node()[@Code='1'][@Name='Действует']");
        paths.add("/ns:parent_1/ns:test_2");
        paths.add("@Code");


        try {
            for (String path : paths) {
                System.out.println("--------------------");
                System.out.println("source path:");
                System.out.println(path);
                System.out.println("result path:");
                PathPrinter.printPath(System.out, new PathParser(path, namespaces).read());
                System.out.println();
                System.out.println("--------------------");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
//            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void xmlDateTime() throws Exception {
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE, null, "2000-01-01", TestUtil.printDateWithDefaultZone("2000-01-01"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE, null, "2000-01-01Z", TestUtil.printDate("2000-01-01+00:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE, null, "2000-01-02+10:00", TestUtil.printDate("2000-01-02+10:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE, "0", "2000-01-01Z", "2000-01-01Z");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE, "10800000", "2000-01-01Z", "2000-01-01+03:00");

        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, null, "18:09:34", TestUtil.printTimeWithDefaultZone("18:09:34.000"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, null, "14:09:34Z", TestUtil.printTime("14:09:34.000+00:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, null, "00:09:34.000+10:00", TestUtil.printTime("00:09:34.000+10:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, "0", "14:09:34.000Z", "14:09:34.000Z");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, "3600000", "14:09:34.000Z", "15:09:34.000+01:00");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, "10800000", "14:09:34.000Z", "17:09:34.000+03:00");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, "14400000", "14:09:34.000Z", "18:09:34.000+04:00");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_TIME, "14400000", "18:09:34.000+04:00", "18:09:34.000+04:00");

        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE_TIME, null, "2000-01-01T18:09:34", TestUtil.printDateTimeWithDefaultZone("2000-01-01T18:09:34.000"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE_TIME, null, "2000-01-01T14:09:34.000Z", TestUtil.printDateTime("2000-01-01T14:09:34.000+00:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE_TIME, null, "2000-01-02T00:09:34.000+10:00", TestUtil.printDateTime("2000-01-02T00:09:34.000+10:00"));
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE_TIME, "0", "2000-01-01T14:09:34.000Z", "2000-01-01T14:09:34.000Z");
        xmlDateTime(XmlDateFormatConst.PARAM_MODE_DATE_TIME, "14400000", "2000-01-01T17:09:34.000+03:00", "2000-01-01T18:09:34.000+04:00");
    }


    public void xmlDateTime(String paramMode, String rawOffset, String text, String result) throws Exception {
        final Map<String, String> map = new HashMap<>();
        map.put(XmlDateFormatConst.PARAM_MODE, paramMode);
        if (rawOffset != null) {
            map.put(XmlDateFormatConst.PARAM_TIME_ZONE_RAW_OFFSET, rawOffset);
        }
        XmlDateFormatProducer producer = new XmlDateFormatProducer();
        Mapper mapper = producer.produce(new MapperParams(map));
        Document document = DomUtil.createDocument();
        Element element = DomUtil.appendNode(document, "time", text);
        LOGGER.debug(element.getTextContent());
        String value = mapper.getValue(element);
        LOGGER.debug("ms: {}", value);
        mapper.setValue(document.getDocumentElement(), value);
        LOGGER.debug(element.getTextContent());
        LOGGER.debug("----------------------");
        Assert.assertEquals(result, element.getTextContent());
    }

    @Test
    public void xsd_viewModel_xsd() throws Exception {
        String path = "xsd";
        String xsdFilename = "XMLSchema.xsd";

        xsdToViewModel(path, xsdFilename, true);
    }

    @Test
    public void xsd_treeModel_xsd() throws Exception {
        String path = "xsd";
        String xsdFilename = "XMLSchema.xsd";

        xsdToTreeModel(path, xsdFilename, true);
    }

    private void xsdToViewModel(String path, String schemaLocation, boolean debug) throws Exception {

        final XsdSchemaResolver xsdSchemaResolver = new ResourceXsdResolver(path);

        final TempModel tempModel = new XsdSchemaToTempModelConverter().convert(xsdSchemaResolver, schemaLocation);
        if (debug) {
            TestUtil.printTempModel(tempModel);
        }

        final ViewModel viewModel = new TempModelToViewModelConverter().convert(tempModel);
        if (debug) {
            TestUtil.printViewModel(viewModel);
        } else {
            TestUtil.printJsonView(viewModel);
        }

        ViewModelValidator.validate(viewModel);
    }

    private void xsdToTreeModel(String path, String schemaLocation, boolean debug) throws Exception {

        final XsdSchemaResolver xsdSchemaResolver = new ResourceXsdResolver(path);

        final TempModel tempModel = new XsdSchemaToTempModelConverter().convert(xsdSchemaResolver, schemaLocation);
        if (debug) {
            TestUtil.printTempModel(tempModel);
        }

        final TreeModel treeModel = new TempModelToTreeModelConverter().convert(tempModel);
        TestUtil.printTreeModel(treeModel);
    }

    private static List<String> getRootElements(Schema schema) {
        final List<String> elements = new ArrayList<>();
        for (OpenAttrs item : schema.getSimpleTypeOrComplexTypeOrGroup()) {
            if (item instanceof TopLevelElement) {
                final TopLevelElement element = (TopLevelElement) item;
                elements.add(element.getName());
            }
        }
        return elements;
    }

    @Test
    public void xsd() throws Exception {
        String path = "xsd";
        String xsdFilename = "XMLSchema.xsd";
        Boolean debug = true;
//        Boolean debug = false;

        TempModel tempModel = new XsdSchemaToTempModelConverter().convert(new ResourceXsdResolver(path), xsdFilename);
        if (debug) {
            TestUtil.printTempModel(tempModel);
        }

        try {
            ViewModel viewModel = new TempModelToViewModelConverter().convert(tempModel);
            if (debug) {
                TestUtil.printViewModel(viewModel);
            } else {
                TestUtil.printJsonView(viewModel);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
    }

    private void xml_schema(String path, String xsdFilename, String rootElement, String xmlFilename) throws Exception {

        final ViewModel viewModel = TestUtil.getViewModelFromXsd(new ResourceXsdResolver(path), xsdFilename);
        TestUtil.printJsonView(viewModel);

        final String elementId = TestUtil.getElementId(viewModel, rootElement);

        final Document xmlDataRead = TestUtil.getDocumentFromXmlData(path + File.separator + xmlFilename);
        MessageProcessor messageReader = new MessageProcessor(viewModel, elementId, xmlDataRead);
        DataModel dataModelRead = messageReader.getData();

        JSONObject jsonData = new DataModelToJsonDataConverter().convert(dataModelRead);

        TestUtil.printJsonData(jsonData);

        DataModel dataModelBuild = new JsonDataToDataModelConverter().convert(jsonData);

        Document xmlDataBuild = DomUtil.createDocument();
        MessageProcessor messageBuilder = new MessageProcessor(viewModel, elementId, xmlDataBuild);
        messageBuilder.setData(dataModelBuild);

        TestUtil.printXmlData(xmlDataBuild);
    }

    @Test
    public void xml() throws Exception {

        String path = "sjema";
        String xsdFilename = "sjema.xsd";
        String rootElement = "sjema";

        String jsonFilename = "view.json";
        String xmlFilename = "data.xml";

//        Boolean debug = true;
        Boolean debug = false;

        final ViewModel viewModel;

        boolean fromSchema = true;
//        boolean fromSchema = false;

        String elementId;

        if (fromSchema) {
            viewModel = TestUtil.getViewModelFromXsd(new ResourceXsdResolver(path), xsdFilename);
            elementId = TestUtil.getElementId(viewModel, rootElement);
        } else {
            viewModel = TestUtil.getViewModelFromJsonData(path + File.separator + jsonFilename);
            elementId = "documentationElement1";
        }

        if (debug) {
            TestUtil.printViewModel(viewModel);
        } else {
            TestUtil.printJsonView(viewModel);
        }

        final Document xmlData = TestUtil.getDocumentFromXmlData(path + File.separator + xmlFilename);
        MessageProcessor messageReader = new MessageProcessor(viewModel, elementId, xmlData);

        final Document resultXml = DomUtil.createDocument();
        MessageProcessor messageBuilder = new MessageProcessor(viewModel, elementId, resultXml);


        if (messageReader.hasData() && messageReader.containData()) {
            DataModel dataModel = messageReader.getData();

            if (debug) {
                TestUtil.printDataModel(dataModel);
            } else {
                TestUtil.printJsonData(dataModel);
            }

            messageBuilder.setData(dataModel);
        }

        TestUtil.printXmlData(resultXml);

        TestUtil.printContain(messageBuilder);
    }
}
