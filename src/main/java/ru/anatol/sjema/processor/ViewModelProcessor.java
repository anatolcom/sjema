package ru.anatol.sjema.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperException;
import ru.anatol.sjema.mapper.MapperManager;
import ru.anatol.sjema.mapper.standard.dateFormat.DateFormatProducer;
import ru.anatol.sjema.mapper.standard.xmlDateTimeMapper.XmlDateFormatProducer;
import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewElement;
import ru.anatol.sjema.model.view.ViewMapper;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.model.view.ViewType;
import ru.anatol.sjema.producer.XsdConst;
import ru.anatol.sjema.xml.Namespaces;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ViewModelProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewModelProcessor.class);

    private final ViewModel viewModel;

    private final Namespaces namespaces;

    private final MapperManager mapperManager;

    public ViewModelProcessor(ViewModel viewModel) {
        Objects.requireNonNull(viewModel);
        this.viewModel = viewModel;
        this.namespaces = createNamespaces(viewModel.getNamespaces());
        this.mapperManager = createMapperManager();
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public MapperManager getMapperManager() {
        return mapperManager;
    }

    private Namespaces createNamespaces(Map<String, String> map) {
        final Namespaces namespaces = new Namespaces();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            namespaces.addNamespace(entry.getKey(), entry.getValue());
        }
        return namespaces;
    }

    private MapperManager createMapperManager() {
        final MapperManager mapperManager = new MapperManager();
        mapperManager.registration(new DateFormatProducer());
        mapperManager.registration(new XmlDateFormatProducer());
        return mapperManager;
    }

    public String getTargetNamespace() {
        return viewModel.getTargetNamespace();
    }

    public ViewElement getViewElement(String elementId, boolean required) throws ConverterException {
        if (viewModel.getElements().containsKey(elementId)) {
            return viewModel.getElements().get(elementId);
        }
        if (required) {
            throw new ConverterException("element with id \"" + elementId + "\" not found");
        }
        return null;
    }

    public ViewType getDefaultType() {
        final ViewType viewType = new ViewType();
        viewType.setBase(BaseType.STRING);
        return viewType;
    }

    public ViewType getViewType(String typeId, boolean required) throws ConverterException {
        if (viewModel.getTypes().containsKey(typeId)) {
            return viewModel.getTypes().get(typeId);
        }
        if (XsdConst.BASE_TYPE_ID.equals(typeId) || XsdConst.typeIdSet.contains(typeId)) {
            return getDefaultType();
        }
        if (required) {
            throw new ConverterException("type with id \"" + typeId + "\" not found");
        }
        return null;
    }

    public Mapper getMapper(String mapperId, boolean required) throws ConverterException, MapperException {
        if (viewModel.getMappers() != null && viewModel.getMappers().containsKey(mapperId)) {
            final ViewMapper viewMapper = viewModel.getMappers().get(mapperId);
            return mapperManager.getMapper(viewMapper);
        }
        if (required) {
            throw new ConverterException("mapper with id \"" + mapperId + "\" not found");
        }
        return null;
    }

    public String findElementIdByPath(String path) {
        for (Map.Entry<String, ViewElement> entry : viewModel.getElements().entrySet()) {
            if (entry.getValue().getPath().equals(path)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String findElementIdByName(String name) {
        final String prefix = namespaces.getPrefix(getTargetNamespace());
        return findElementIdByPath(prefix + ":" + name);
    }

    public void removeIdAttribute(String elementId) {
        final ViewElement viewElement = viewModel.getElements().get(elementId);
        if (viewElement.getTypeId() == null) {
            return;
        }
        final ViewType viewType = viewModel.getTypes().get(viewElement.getTypeId());
        if (viewType == null || viewType.getContent() == null || viewType.getContent().getElementIds() == null) {
            return;
        }
        final List<String> elementIds = viewType.getContent().getElementIds();
        for (int index = 0; index < elementIds.size(); index++) {
            final ViewElement subElement = viewModel.getElements().get(elementIds.get(index));
            if ("@Id".equals(subElement.getPath())) {
                elementIds.remove(index);
                break;
            }
        }
    }

}
