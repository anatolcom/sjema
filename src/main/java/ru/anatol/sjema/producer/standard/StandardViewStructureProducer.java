package ru.anatol.sjema.producer.standard;

import org.json.JSONObject;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.producer.ViewStructureProducer;

public class StandardViewStructureProducer implements ViewStructureProducer {

    @Override
    public JSONObject produce(ViewModel viewModel, String elementId, String elementName) {
        return new JSONObject();
    }

}
