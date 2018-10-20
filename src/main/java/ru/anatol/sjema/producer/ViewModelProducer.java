package ru.anatol.sjema.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.printer.ViewModelPrinter;
import ru.anatol.sjema.validator.ValidatorException;
import ru.anatol.sjema.validator.ViewModelValidator;

public class ViewModelProducer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewModelProducer.class);

    private ViewStructureProducer viewStructureProducer;
    private ViewMapperProducer viewMapperProducer;
    private ViewWidgetProducer viewWidgetProducer;

    public ViewModelProducer() {
    }

    public ViewStructureProducer getViewStructureProducer() {
        return viewStructureProducer;
    }

    public void setViewStructureProducer(ViewStructureProducer viewStructureProducer) {
        this.viewStructureProducer = viewStructureProducer;
    }

    public ViewMapperProducer getViewMapperProducer() {
        return viewMapperProducer;
    }

    public void setViewMapperProducer(ViewMapperProducer viewMapperProducer) {
        this.viewMapperProducer = viewMapperProducer;
    }

    public ViewWidgetProducer getViewWidgetProducer() {
        return viewWidgetProducer;
    }

    public void setViewWidgetProducer(ViewWidgetProducer viewWidgetProducer) {
        this.viewWidgetProducer = viewWidgetProducer;
    }

    public ViewModel produce(XsdSchemaResolver xsdSchemaResolver, String schemaLocation) throws ProducerException {
        final ViewModel viewModel = produceViewModel(xsdSchemaResolver, schemaLocation);
        validateViewModel(viewModel);
        return viewModel;
    }

    public ViewModel produceViewModel(XsdSchemaResolver xsdSchemaResolver, String schemaLocation) throws ProducerException {
        try {
            final TempModel tempModel = new XsdSchemaToTempModelConverter().convert(xsdSchemaResolver, schemaLocation);

            final TempModelToViewModelConverter tempModelToViewModelConverter = new TempModelToViewModelConverter();
            tempModelToViewModelConverter.setViewStructureProducer(viewStructureProducer);
            tempModelToViewModelConverter.setViewMapperProducer(viewMapperProducer);
            tempModelToViewModelConverter.setViewWidgetProducer(viewWidgetProducer);
            return tempModelToViewModelConverter.convert(tempModel);
        } catch (ConverterException ex) {
            throw new ProducerException(ex);
        }
    }

    public void validateViewModel(ViewModel viewModel) throws ProducerException {
        try {
            ViewModelValidator.validate(viewModel);
        } catch (ValidatorException ex) {
            try {
                LOGGER.debug(new ViewModelPrinter(viewModel).print());
            } catch (Exception ignore) {
            }
            throw new ProducerException(ex);
        }
    }


}
