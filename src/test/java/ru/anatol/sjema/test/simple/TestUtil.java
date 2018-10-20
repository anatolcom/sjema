package ru.anatol.sjema.test.simple;

import org.json.JSONObject;
import org.w3._2001.xmlschema.Schema;
import org.w3c.dom.Document;
import ru.anatol.sjema.converter.data.DataModelToJsonDataConverter;
import ru.anatol.sjema.converter.view.ViewModelToJsonViewConverter;
import ru.anatol.sjema.model.data.DataModel;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.printer.DataModelPrinter;
import ru.anatol.sjema.printer.TempModelPrinter;
import ru.anatol.sjema.printer.ViewModelPrinter;
import ru.anatol.sjema.processor.ViewModelProcessor;
import ru.anatol.sjema.producer.ProducerException;
import ru.anatol.sjema.producer.ViewModelProducer;
import ru.anatol.sjema.producer.XsdSchemaResolver;
import ru.anatol.sjema.readers.ViewModelReader;
import ru.anatol.sjema.readers.XmlDataReader;
import ru.anatol.sjema.readers.XsdSchemaReader;
import ru.anatol.sjema.simple.processor.MessageProcessor;
import ru.anatol.sjema.xml.DomUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    private TestUtil() {
    }

    public static Schema getSchemaFromXsd(String xsdFilename) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(xsdFilename)) {
            if (inputStream == null) {
                throw new IOException("resource with name: \"" + xsdFilename + "\" not found");
            }
            return new XsdSchemaReader().read(inputStream);
        }
    }

    public static ViewModel getViewModelFromXsd(XsdSchemaResolver xsdSchemaResolver, String schemaLocation) throws ProducerException {
        return new ViewModelProducer().produce(xsdSchemaResolver, schemaLocation);
    }

    public static String getElementId(ViewModel viewModel, String elementName) {
        final ViewModelProcessor viewModelProcessor = new ViewModelProcessor(viewModel);
        String prefix = viewModelProcessor.getNamespaces().getPrefix(viewModel.getTargetNamespace());
        return viewModelProcessor.findElementIdByPath(prefix + ":" + elementName);
    }

    public static ViewModel getViewModelFromJsonData(String jsonFilename) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(jsonFilename)) {
            if (inputStream == null) {
                throw new IOException("resource with name: \"" + jsonFilename + "\" not found");
            }
            return new ViewModelReader().read(inputStream, StandardCharsets.UTF_8);
        }
    }

    public static Document getDocumentFromXmlData(String xmlFilename) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlFilename)) {
            if (inputStream == null) {
                throw new IOException("file with name: \"" + xmlFilename + "\" not found");
            }
            return new XmlDataReader().read(inputStream);
        }
    }

    public static void printTempModel(TempModel tempModel) {
        System.out.println("-------  T E M P   M O D E L  -------");
        System.out.println("-------------------------------------");
        new TempModelPrinter(tempModel).print(System.out, 0);
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printViewModel(ViewModel viewModel) {
        System.out.println("-------  V I E W   M O D E L  -------");
        System.out.println("-------------------------------------");
        new ViewModelPrinter(viewModel).print(System.out, 0);
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printJsonView(ViewModel viewModel) {
        System.out.println("--------  J S O N   V I E W  --------");
        System.out.println("-------------------------------------");
        try {
            JSONObject json = new ViewModelToJsonViewConverter().convert(viewModel);
            System.out.print(json.toString(4));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printDataModel(DataModel dataModel) {
        System.out.println("-------  D A T A   M O D E L  -------");
        System.out.println("-------------------------------------");
        new DataModelPrinter(dataModel).print(System.out, 0);
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printJsonData(DataModel dataModel) {
        System.out.println("--------  J S O N   D A T A  --------");
        System.out.println("-------------------------------------");
        try {
            JSONObject json = new DataModelToJsonDataConverter().convert(dataModel);
            System.out.print(json.toString(4));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printJsonData(JSONObject json) {
        System.out.println("--------  J S O N   D A T A  --------");
        System.out.println("-------------------------------------");
        try {
            System.out.print(json.toString(4));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printXmlData(Document xmlData) {
        System.out.println("---------  X M L   D A T A  ---------");
        System.out.println("-------------------------------------");
        try {
            System.out.print(DomUtil.printFormat(xmlData, 4));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println();
        System.out.println("-------------------------------------");
    }

    public static void printContain(MessageProcessor messageProcessor) {
        System.out.println("---------  X M L   D A T A  ---------");
        System.out.println("-------------------------------------");
        try {
            if (messageProcessor.hasData()) {
                System.out.println("- contain data : " + messageProcessor.containData());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("-------------------------------------");
    }

}
