package ru.anatol.sjema.producer;

import org.json.JSONObject;
import ru.anatol.sjema.model.view.ViewModel;

public interface ViewStructureProducer {

    JSONObject produce(ViewModel viewModel, String elementId, String elementName) throws ProducerException;

}
