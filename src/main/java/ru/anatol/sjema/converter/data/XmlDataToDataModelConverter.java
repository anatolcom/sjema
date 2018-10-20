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
import ru.anatol.sjema.model.view.ViewTypeRestriction;
import ru.anatol.sjema.processor.ViewModelProcessor;
import ru.anatol.sjema.xml.DomProcessor;
import ru.anatol.sjema.xml.PathException;
import ru.anatol.sjema.xml.path.PathConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XmlDataToDataModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlDataToDataModelConverter.class);

    private class Context {

        /**
         * Родительский контекст.
         */
        public final Context parent;
        /**
         * Идентификатор текущего элемента.
         */
        public final String elementId;
        /**
         * Текущий элемент.
         */
        public final ViewElement viewElement;
        /**
         * Тип текущего элемента.
         */
        public final ViewType viewType;
        /**
         * Текущий узел.
         */
        public final Node node;

        public Context(Context parent, String elementId, Node node) throws ConverterException {
            this.parent = parent;
            this.elementId = elementId;
            this.viewElement = viewModelProcessor.getViewElement(elementId, true);
            if (this.viewElement.getTypeId() == null) {
                this.viewType = viewModelProcessor.getDefaultType();
            } else {
                this.viewType = viewModelProcessor.getViewType(this.viewElement.getTypeId(), true);
            }
            this.node = node;
        }

        public String getFullPath() {
            Context context = this;
            List<String> pathItems = new LinkedList<>();
            while (context != null) {
                final String path = getPath(context.viewElement);
                pathItems.add(0, path);
                context = context.parent;
            }
            return String.join("/", pathItems);
        }
    }

    private ViewModelProcessor viewModelProcessor;
    private DomProcessor domProcessor;

    private DataModel dataModel;

    public XmlDataToDataModelConverter() {
    }

    /**
     * Преобрадование в DataModel.
     *
     * @param target             XML данные
     * @param viewModelProcessor viewModelProcessor
     * @param rootElementId      id корневого элемента
     * @return DataModel
     * @throws ConverterException
     */
    public DataModel convert(Node target, ViewModelProcessor viewModelProcessor, String rootElementId) throws ConverterException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(viewModelProcessor, "viewModelProcessor is null");
        Objects.requireNonNull(rootElementId, "rootElementId is null");
        try {
            this.viewModelProcessor = viewModelProcessor;
            this.domProcessor = new DomProcessor(viewModelProcessor.getNamespaces());
            return toDataModel(target, rootElementId);
        } catch (ConverterException ex) {
            throw ex;
        } catch (PathException ex) {
            throw new ConverterException(ex);
        }
    }

    /**
     * Преобразование в DataModel.
     *
     * @param target        XML данные
     * @param rootElementId id корневого элемента
     * @return DataModel
     * @throws ConverterException
     * @throws PathException
     */
    private DataModel toDataModel(Node target, String rootElementId) throws ConverterException, PathException {
        dataModel = new DataModel();

        final Context context = new Context(null, rootElementId, target);
        final String path = getPath(context.viewElement);
        List<Node> processNodes = domProcessor.getNodes(context.node, path, false);
        dataModel.setItem(toDataItem(context, processNodes));
        return dataModel;
    }

    /**
     * Преобразование в DataItem.
     *
     * @param context контекст.
     * @return DataItem
     * @throws ConverterException
     */
    private DataItem toDataItem(Context context, List<Node> nodes) throws ConverterException {
        Objects.requireNonNull(context, "context is null");
        try {
            final boolean required = context.viewElement.isRequired();
            final boolean repeatable = context.viewElement.getRepeatable() != null;
            final boolean self;
            if (repeatable && context.viewElement.getRepeatable().getPath() != null) {
                self = String.valueOf(PathConst.SELF_SHORT).equals(context.viewElement.getRepeatable().getPath());
            } else {
                self = String.valueOf(PathConst.SELF_SHORT).equals(context.viewElement.getPath());
            }

            if (repeatable && self) {
                return toDataItemRepeatableGroup(context);
            }

            //если ненайдено ничего
            if (nodes.isEmpty()) {
                //если искомое поле не обязательно
                if (!required) {
                    return null;
                } else {
                    return null;
                    //TODO исправить проверку при сохранении
                    //throw new PathException("required element \"" + context.elementId + "\" not found by path \"" + context.getFullPath() + "\"");
                }
            }

            if (!repeatable) {
                if (context.viewType.isSimple()) {
                    DataSingleValue singleValue = new DataSingleValue(context.elementId);
                    singleValue.setValue(getValue(context.viewType, nodes.get(0)));
                    return singleValue;
                } else {
                    DataSingleNode singleNode = new DataSingleNode(context.elementId);
                    if (context.viewType.getContent() != null) {
                        singleNode.setNode(toDataNode(context, nodes.get(0), context.viewType.getContent()));
                    }
                    return singleNode;
                }
            } else {
                if (context.viewType.isSimple()) {
                    DataRepeatableValue multipleValue = new DataRepeatableValue(context.elementId, nodes.size());
                    for (Node node : nodes) {
                        multipleValue.getValues().add(getValue(context.viewType, node));
                    }
                    return multipleValue;
                } else {
                    DataRepeatableNode multipleNode = new DataRepeatableNode(context.elementId, nodes.size());
                    if (context.viewType.getContent() != null) {
                        for (Node node : nodes) {
                            multipleNode.getNodes().add(toDataNode(context, node, context.viewType.getContent()));
                        }
                    }
                    return multipleNode;
                }
            }
        } catch (PathException ex) {
            throw new ConverterException("convert element \"" + context.elementId + "\" with path \"" + getPath(context.viewElement) + "\" failure, because: " + ex.getMessage(), ex);
        } catch (MapperException ex) {
            throw new ConverterException("mapping element \"" + context.elementId + "\" with mapperId \"" + context.viewType.getMapperId() + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

    /**
     * Получение значения узла дерева XML.
     *
     * @param viewType тип
     * @param node     узел дерева XML
     * @return значение
     * @throws MapperException
     */
    private DataValue getValue(ViewType viewType, Node node) throws MapperException, ConverterException {
        if (viewType.getMapperId() != null) {
            String value = viewModelProcessor.getMapper(viewType.getMapperId(), true).getValue(node);
            return new DataValue(viewType.getBase(), value);
        }
        return new DataValue(viewType.getBase(), node.getTextContent());
    }

    /**
     * Преобразование в список DataNode.
     *
     * @param parentContext родительский контекст
     * @param node          узел дерева XML
     * @param viewContent   ViewContent
     * @return DataNode
     * @throws ConverterException
     * @throws PathException
     */
    private DataNode toDataNode(Context parentContext, Node node, ViewContent viewContent) throws ConverterException, PathException {
        switch (viewContent.getMode()) {
            case SEQUENCE:
                return getSequenceDataNode(parentContext, node, viewContent);
            case CHOICE:
                if (isUnion(viewContent)) {
                    return getUnionDataNode(parentContext, node, viewContent);
                } else {
                    return getChoiceDataNode(parentContext, node, viewContent);
                }
            default:
                throw new ConverterException("unknown mode " + viewContent.getMode());
        }
    }

    //TODO скорее всего решение верное. альтернатива добавить режим UNION
    boolean isUnion(ViewContent viewContent) throws ConverterException {
        if (!ViewContent.Mode.CHOICE.equals(viewContent.getMode())) {
            return false;
        }
        if (viewContent.getElementIds() == null || viewContent.getElementIds().isEmpty()) {
            return false;
        }
        for (String elementId : viewContent.getElementIds()) {
            ViewElement viewElement = viewModelProcessor.getViewElement(elementId, true);
            final String path = getPath(viewElement);
            if (!String.valueOf(PathConst.SELF_SHORT).equals(path)) {
                return false;
            }
        }
        return true;
    }

    private DataNode getSequenceDataNode(Context parentContext, Node node, ViewContent viewContent) throws ConverterException, PathException {
        DataNode dataNode = new DataNode();
        if (viewContent.getElementIds() != null) {
            for (String elementId : viewContent.getElementIds()) {
                final Context context = new Context(parentContext, elementId, node);
                final String path = getPath(context.viewElement);
                List<Node> processNodes = domProcessor.getNodes(context.node, path, false);
                DataItem item = toDataItem(context, processNodes);
                if (item != null) {
                    dataNode.add(item);
                }
            }
        }
        return dataNode;
    }

    private DataNode getChoiceDataNode(Context parentContext, Node node, ViewContent viewContent) throws ConverterException, PathException {
        DataNode dataNode = new DataNode();
        if (viewContent.getElementIds() != null) {
            for (String elementId : viewContent.getElementIds()) {
                final Context context = new Context(parentContext, elementId, node);
                final String path = getPath(context.viewElement);
                List<Node> processNodes = domProcessor.getNodes(context.node, path, false);
                if (processNodes.isEmpty()) {
                    continue;
                }
                DataItem item = toDataItem(context, processNodes);
                if (item != null && validation(item, context.viewType)) {
                    if (dataNode.getItems().size() > 0) {
                        throw new ConverterException("content with mode CHOICE can not contain more one element");
//                        //TODO убрано исключение для UNION типов
//                        LOGGER.error("content with mode CHOICE can not contain more one element {}", elementId);
//                        break;
                    }
                    dataNode.add(item);
                }
            }
        }
        return dataNode;
    }

    private DataNode getUnionDataNode(Context parentContext, Node node, ViewContent viewContent) throws ConverterException, PathException {
        DataNode dataNode = new DataNode();
        if (viewContent.getElementIds() != null) {
            for (String elementId : viewContent.getElementIds()) {
                final Context context = new Context(parentContext, elementId, node);
                final String path = getPath(context.viewElement);
                List<Node> processNodes = domProcessor.getNodes(context.node, path, false);
                if (processNodes.isEmpty()) {
                    continue;
                }
                DataItem item = toDataItem(context, processNodes);
                if (item == null || !validation(item, context.viewType)) {
                    continue;
                }
                dataNode.add(item);
                break;
            }
        }
        return dataNode;
    }

    /**
     * Вычисление пути для поиска.
     *
     * @param viewElement элемент
     * @return путь
     */
    private String getPath(ViewElement viewElement) {

        final String basePath = viewElement.getPath();

        final String repeatablePath;
        if (viewElement.getRepeatable() != null && viewElement.getRepeatable().getPath() != null) {
            repeatablePath = viewElement.getRepeatable().getPath();
        } else {
            repeatablePath = null;
        }

        if (basePath != null) {
            if (repeatablePath != null) {
                return basePath + "/" + repeatablePath;
            }
            return basePath;
        } else {
            if (repeatablePath != null) {
                return repeatablePath;
            }
            return null;
        }
    }

    /**
     * Валидация данных по типу.
     *
     * @param dataItem DataItem
     * @param viewType тип
     * @return true если валидно, иначе false
     */
    private boolean validation(DataItem dataItem, ViewType viewType) {
        if (viewType.getRestriction() == null) {
            return true;
        }
        if (dataItem instanceof DataSingleValue) {
            return validation(((DataSingleValue) dataItem).getValue(), viewType.getRestriction());
        }
        return false;
    }

    /**
     * Валидация данных по ограничениям типа.
     *
     * @param dataValue   DataValue
     * @param restriction ограничения
     * @return если валидно, иначе false
     */
    private boolean validation(DataValue dataValue, ViewTypeRestriction restriction) {

        if (restriction.getEnumeration() != null && !restriction.getEnumeration().isEmpty()) {
            if (!restriction.getEnumeration().containsKey(dataValue.getValue())) {
                return false;
            }
        }

        //TODO добавить остальные проверки ограничений

        return true;
    }

    /**
     * Преобразование в DataItem для повторяющихся групп.
     *
     * @param context контекст
     * @return DataItem
     * @throws ConverterException
     * @throws PathException
     */
    private DataItem toDataItemRepeatableGroup(Context context) throws ConverterException, PathException {

//        final Node node = context.node;
        final ViewType viewType = context.viewType;
        final ViewContent viewContent = viewType.getContent();

        final Node baseNode;
        if (context.viewElement.getRepeatable().getPath() != null) {
            baseNode = domProcessor.getNode(context.node, context.viewElement.getPath(), false);
        } else {
            baseNode = context.node;
        }

        //создание карты контекстов и соответсвующих ей результатов поиска.
        final Map<Context, List<Node>> contextListMap = new HashMap<>();
        final List<String> paths = new ArrayList<>(viewContent.getElementIds().size());
        for (String elementId : viewContent.getElementIds()) {
            final Context subContext = new Context(context, elementId, baseNode);
            final String path = getPath(subContext.viewElement);
            contextListMap.put(subContext, domProcessor.getNodes(baseNode, path, false));
            paths.add(path);
        }
        //для вычисления порядка получаем общий список
        final List<Node> nodes = domProcessor.getNodes(baseNode, String.join("|", paths), false);

        //создание карты соответсвия узлов и контекстов;
        final Map<Node, Context> nodeCtxMap = new HashMap<>();
        for (Node node : nodes) {
            Context ctx = null;
            for (Map.Entry<Context, List<Node>> entry : contextListMap.entrySet()) {
                if (entry.getValue().contains(node)) {
                    ctx = entry.getKey();
                    break;
                }
            }
            nodeCtxMap.put(node, ctx);
        }

        if (viewType.isSimple()) {
            DataRepeatableValue multipleValue = new DataRepeatableValue(context.elementId, nodes.size());
            for (Node node : nodes) {
                //TODO доделать
                LOGGER.debug("ALARM!!!");
                LOGGER.debug("node: " + node.getLocalName());
            }
            return multipleValue;
        } else {
            DataRepeatableNode multipleNode = new DataRepeatableNode(context.elementId, nodes.size());
            if (viewContent != null) {
                switch (viewContent.getMode()) {
                    case SEQUENCE:
                        //вычитывание повторяющихся групп sequence
//                        Queue<Node> queue = new LinkedList<>();
                        for (Node node : nodes) {
                            //TODO доделать
                            LOGGER.debug("ALARM!!!");
                            LOGGER.debug("node: " + node.getLocalName());
//                            queue.add(node);
                        }
//                        while (!queue.isEmpty()) {
//                            Node node = queue.poll();
//                        }
                        break;
                    case CHOICE:
                        //вычитывание повторяющихся групп choice
                        for (Node node : nodes) {
                            List<Node> processNodes = new ArrayList<>(1);
                            processNodes.add(node);
                            DataNode choiceNode = new DataNode();
                            DataItem item = toDataItem(nodeCtxMap.get(node), processNodes);
                            if (item != null) {
                                choiceNode.add(item);
                            }
                            multipleNode.getNodes().add(choiceNode);
                        }
                        break;
                    default:
                        throw new ConverterException("unknown mode " + viewContent.getMode());
                }
            }
            return multipleNode;
        }
    }
}
