package ru.anatol.sjema.simple.processor;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.converter.data.DataModelToJsonDataConverter;
import ru.anatol.sjema.converter.data.DataModelToXmlDataConverter;
import ru.anatol.sjema.converter.data.XmlDataToDataModelConverter;
import ru.anatol.sjema.model.data.DataModel;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.processor.ViewModelProcessor;
import ru.anatol.sjema.xml.DomUtil;
import ru.anatol.sjema.xml.PathException;

import java.util.Objects;

public class MessageProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    private final ViewModelProcessor viewModelProcessor;
    private final Document document;
    private final String elementId;
    private final JSONObject structure;

    /**
     * Констркутор.
     *
     * @param viewModel вью модель
     * @param elementId идентификатор элемента
     * @param document  XML документ
     */
    public MessageProcessor(ViewModel viewModel, String elementId, Document document) {
        Objects.requireNonNull(viewModel);
        Objects.requireNonNull(elementId);
        Objects.requireNonNull(document);

        this.viewModelProcessor = new ViewModelProcessor(viewModel);
        this.elementId = elementId;
        this.structure = viewModel.getStructure().get(elementId);
        this.document = document;
    }

    /**
     * Доступность данных.
     *
     * @return true если доступно, иначе false
     */

    public boolean hasData() {
        return structure != null;
    }

    /**
     * Чтение данных из XML.
     *
     * @return данные
     * @throws ConverterException
     */
    public DataModel getData() throws ConverterException {
        if (!hasData()) {
            throw new ConverterException("data unavailable");
        }
        try {
            return new XmlDataToDataModelConverter().convert(document, viewModelProcessor, elementId);
        } catch (Exception ex) {
            throw new ConverterException("data don't get, because: " + ex.getMessage(), ex);
        }
    }

    public void removeData() throws ConverterException {
        if (!hasData()) {
            throw new ConverterException("data unavailable");
        }
        DomUtil.clearNode(document);
    }


    /**
     * Наличие данных.
     *
     * @return true если одержит данные, иначе false
     */
    public boolean containData() {
        return !DomUtil.isEmpty(document);
    }

    /**
     * Запись данных в XML.
     *
     * @param dataModel данные
     * @throws ConverterException
     * @throws PathException
     */
    public void setData(DataModel dataModel) throws ConverterException {
        try {
            DomUtil.clearNode(document);
            if (dataModel != null) {
                new DataModelToXmlDataConverter().convert(dataModel, viewModelProcessor, elementId, document);
            }
        } catch (ConverterException ex) {
            LOGGER.error("dataModel failure, because: {}", ex.getMessage());
            LOGGER.debug(new DataModelToJsonDataConverter().convert(dataModel).toString());
            throw ex;
        } catch (Exception ex) {
            throw new ConverterException("data don't set, because: " + ex.getMessage(), ex);
        }
    }

    public Document getDocument() {
        return document;
    }
}
