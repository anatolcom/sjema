package ru.anatol.sjema.converter.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.mapper.MapperException;
import ru.anatol.sjema.model.data.DataItem;
import ru.anatol.sjema.model.data.DataModel;
import ru.anatol.sjema.model.data.DataNode;
import ru.anatol.sjema.model.data.DataRepeatableNode;
import ru.anatol.sjema.model.data.DataRepeatableValue;
import ru.anatol.sjema.model.data.DataSingleNode;
import ru.anatol.sjema.model.data.DataSingleValue;
import ru.anatol.sjema.model.data.DataValue;
import ru.anatol.sjema.model.view.ViewContent;
import ru.anatol.sjema.model.view.ViewElement;
import ru.anatol.sjema.model.view.ViewType;
import ru.anatol.sjema.xml.DomProcessor;
import ru.anatol.sjema.processor.ViewModelProcessor;
import ru.anatol.sjema.xml.PathException;
import ru.anatol.sjema.xml.path.PathConst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataModelToXmlDataConverter {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataModelToXmlDataConverter.class);

    public class Context {

        /**
         * Родительский контекст.
         */
        public final Context parent;
        /**
         * Идентификатор текущего элемента.
         */
        public final String elementId;
        /**
         * Текущий элемент View.
         */
        public final ViewElement viewElement;
        /**
         * Тип текущего элемента.
         */
        public final ViewType viewType;
        /**
         * Текущий узел XML.
         */
        public final Node node;
        /**
         * Текущий узел Data.
         */
        public final DataItem item;

        public Context(Context parent, String elementId, Node node, DataItem item) throws ConverterException {
            this.parent = parent;
            this.node = node;
            this.elementId = elementId;
            this.viewElement = viewModelProcessor.getViewElement(elementId, true);
            if (this.viewElement.getTypeId() == null) {
                this.viewType = viewModelProcessor.getDefaultType();
            } else {
                this.viewType = viewModelProcessor.getViewType(this.viewElement.getTypeId(), true);
            }
            this.item = item;
        }

        public String getFullPath() {
            Context context = this;
            List<String> pathItems = new LinkedList<>();
            while (context != null) {
                pathItems.add(0, context.viewElement.getPath());
                context = context.parent;
            }
            return String.join("/", pathItems);
        }
    }

    private ViewModelProcessor viewModelProcessor;
    private DomProcessor domProcessor;

//    Document xmlData;

    public DataModelToXmlDataConverter() {
    }

    public void convert(DataModel dataModel, ViewModelProcessor viewModelProcessor, String rootElementId, Node target) throws ConverterException {
        Objects.requireNonNull(dataModel, "dataModel is null");
        Objects.requireNonNull(viewModelProcessor, "viewModelProcessor is null");
        Objects.requireNonNull(rootElementId, "rootElementId is null");
        Objects.requireNonNull(target, "document is null");
        try {
            this.viewModelProcessor = viewModelProcessor;
            this.domProcessor = new DomProcessor(viewModelProcessor.getNamespaces());
            toXmlData(target, dataModel, rootElementId);
        } catch (ConverterException ex) {
            throw ex;
        }
    }

    private void toXmlData(Node target, DataModel dataModel, String rootElementId) throws ConverterException {
        DataItem dataItem = dataModel.getItem();
        processElement(new Context(null, rootElementId, target, dataItem), null);
    }

    private void processElement(Context context, DomProcessor.CreateMode createMode) throws ConverterException {

        final boolean required = context.viewElement.isRequired();
        final boolean repeatable = context.viewElement.getRepeatable() != null;
        final boolean self;
        if (repeatable && context.viewElement.getRepeatable().getPath() != null) {
            self = String.valueOf(PathConst.SELF_SHORT).equals(context.viewElement.getRepeatable().getPath());
        } else {
            self = String.valueOf(PathConst.SELF_SHORT).equals(context.viewElement.getPath());
        }

        if (repeatable && self) {
            processXmlDataRepeatableGroup(context);
            return;
        }

        try {
            if (!repeatable) {
                if (createMode == null) {
                    createMode = DomProcessor.CreateMode.IF_NOT_EXISTS;
                }
                if (context.viewType.isSimple()) {
                    if (!(context.item instanceof DataSingleValue)) {
                        throw new ConverterException("element \"" + context.elementId + "\" is " + DataSingleValue.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                    }
                    DataSingleValue singleValue = (DataSingleValue) context.item;
                    DataValue dataValue = singleValue.getValue();
                    if (dataValue == null || dataValue.getValue() == null || dataValue.getValue().isEmpty()) {
                        if (required) {
                            throw new PathException("data of required element \"" + context.elementId + "\" not set");
                        }
                    }
                    if (dataValue != null && dataValue.getValue() != null && !dataValue.getValue().isEmpty() || context.viewElement.isCreateEmpty()) {
                        Node node = domProcessor.forceNode(context.node, context.viewElement.getPath(), createMode);
                        setValue(context.viewType, node, dataValue);
                    }
                } else {
                    if (!(context.item instanceof DataSingleNode)) {
                        throw new ConverterException("element \"" + context.elementId + "\" is " + DataSingleNode.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                    }
                    DataSingleNode singleNode = (DataSingleNode) context.item;
                    Node node = domProcessor.forceNode(context.node, context.viewElement.getPath(), createMode);
                    if (context.viewType.getContent() != null) {
                        DataNode nodeNode = singleNode.getNode();
                        processContent(context, node, context.viewType.getContent(), nodeNode, null);
                    }
                }
            } else {
                if (createMode == null) {
                    createMode = DomProcessor.CreateMode.ALWAYS;
                }
                int repeatableMin = context.viewElement.getRepeatable().getMin();
                int repeatableMax = context.viewElement.getRepeatable().getMax();
                final Node baseNode;
                final String repeatablePath;
                if (context.viewElement.getRepeatable().getPath() != null) {
                    baseNode = domProcessor.forceNode(context.node, context.viewElement.getPath(), DomProcessor.CreateMode.IF_NOT_EXISTS);
                    repeatablePath = context.viewElement.getRepeatable().getPath();
                } else {
                    baseNode = context.node;
                    repeatablePath = context.viewElement.getPath();
                }
                if (context.viewType.isSimple()) {
                    if (!(context.item instanceof DataRepeatableValue)) {
                        throw new ConverterException("element \"" + context.elementId + "\" is " + DataRepeatableValue.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                    }
                    DataRepeatableValue repeatableValue = (DataRepeatableValue) context.item;
                    if (repeatableValue.getValues().size() < repeatableMin
                            || repeatableValue.getValues().size() > repeatableMax) {
                        throw new PathException("count " + repeatableValue.getValues().size() + " of data of required element \"" + context.elementId + "\" out of range [" + repeatableMin + ", " + repeatableMax + "]");
                    }
                    for (DataValue dataValue : repeatableValue.getValues()) {
                        Node node = domProcessor.forceNode(baseNode, repeatablePath, createMode);
                        setValue(context.viewType, node, dataValue);
                    }
                } else {
                    if (!(context.item instanceof DataRepeatableNode)) {
                        throw new ConverterException("element \"" + context.elementId + "\" is " + DataRepeatableNode.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                    }
                    DataRepeatableNode repeatableNode = (DataRepeatableNode) context.item;
                    if (repeatableNode.getNodes().size() < repeatableMin
                            || repeatableNode.getNodes().size() > repeatableMax) {
                        throw new PathException("count " + repeatableNode.getNodes().size() + " of data of required element \"" + context.elementId + "\" out of range [" + repeatableMin + ", " + repeatableMax + "]");
                    }
                    if (context.viewType.getContent() != null) {
                        for (DataNode dataNode : repeatableNode.getNodes()) {
                            Node node = domProcessor.forceNode(baseNode, repeatablePath, createMode);
                            processContent(context, node, context.viewType.getContent(), dataNode, null);
                        }
                    }
                }
            }
        } catch (PathException ex) {
            throw new ConverterException("convert element \"" + context.elementId + "\" with path \"" + context.viewElement.getPath() + "\" failure, because: " + ex.getMessage(), ex);
        } catch (MapperException ex) {
            throw new ConverterException("mapping element \"" + context.elementId + "\" with mapperId \"" + context.viewType.getMapperId() + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

    private void processXmlDataRepeatableGroup(Context context) throws ConverterException {
        try {
            final Node baseNode;
            final String repeatablePath;
            if (context.viewElement.getRepeatable().getPath() != null) {
                baseNode = domProcessor.forceNode(context.node, context.viewElement.getPath(), DomProcessor.CreateMode.IF_NOT_EXISTS);
                repeatablePath = context.viewElement.getRepeatable().getPath();
            } else {
                baseNode = context.node;
                repeatablePath = context.viewElement.getPath();
            }
            if (context.viewType.isSimple()) {
                if (!(context.item instanceof DataRepeatableValue)) {
                    throw new ConverterException("element \"" + context.elementId + "\" is " + DataRepeatableValue.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                }
                DataRepeatableValue repeatableValue = (DataRepeatableValue) context.item;
                for (DataValue dataValue : repeatableValue.getValues()) {
                    Node node = domProcessor.forceNode(baseNode, repeatablePath, DomProcessor.CreateMode.ALWAYS);
                    setValue(context.viewType, node, dataValue);
                }
            } else {
                if (!(context.item instanceof DataRepeatableNode)) {
                    throw new ConverterException("element \"" + context.elementId + "\" is " + DataRepeatableNode.class.getSimpleName() + " and cannot be cast to " + context.item.getClass().getSimpleName());
                }
                DataRepeatableNode repeatableNode = (DataRepeatableNode) context.item;
                if (context.viewType.getContent() != null) {
                    for (DataNode dataNode : repeatableNode.getNodes()) {
                        Node node = domProcessor.forceNode(baseNode, repeatablePath, DomProcessor.CreateMode.ALWAYS);
                        processContent(context, node, context.viewType.getContent(), dataNode, DomProcessor.CreateMode.ALWAYS);
                    }
                }
            }
        } catch (PathException ex) {
            throw new ConverterException("convert element \"" + context.elementId + "\" with path \"" + context.viewElement.getPath() + "\" failure, because: " + ex.getMessage(), ex);
        } catch (MapperException ex) {
            throw new ConverterException("mapping element \"" + context.elementId + "\" with mapperId \"" + context.viewType.getMapperId() + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

    private void setValue(ViewType viewType, Node node, DataValue dataValue) throws ConverterException, MapperException {
        if (viewType.getMapperId() != null) {
            viewModelProcessor.getMapper(viewType.getMapperId(), true).setValue(node, dataValue.getValue());
            return;
        }
        if (dataValue != null) {
            node.setTextContent(dataValue.getValue());
        }
    }

    private void processContent(Context parentContext, Node node, ViewContent viewContent, DataNode dataNode, DomProcessor.CreateMode createMode) throws ConverterException, PathException {
        if (viewContent.getElementIds() != null) {
            if (dataNode != null && !dataNode.getItems().isEmpty()) {
                switch (viewContent.getMode()) {
                    case SEQUENCE:
                        break;
                    case CHOICE:
                        if (dataNode.getItems().size() > 1) {
                            throw new ConverterException("content with mode CHOICE can not contain more one element");
                        }
                        break;
                    default:
                        throw new ConverterException("unknown mode " + viewContent.getMode());
                }
                Map<String, DataItem> dataItemMap = new HashMap<>();
                for (DataItem dataItem : dataNode) {
                    dataItemMap.put(dataItem.getName(), dataItem);
                }
                final List<String> elementIds;
                if (viewContent.getXmlOrder() != null) {
                    elementIds = viewContent.getXmlOrder();
                } else {
                    elementIds = viewContent.getElementIds();
                }
                for (String elementId : elementIds) {
                    if (dataItemMap.containsKey(elementId)) {
                        processElement(new Context(parentContext, elementId, node, dataItemMap.get(elementId)), createMode);
                    }
                }
            }
        }
    }
}
