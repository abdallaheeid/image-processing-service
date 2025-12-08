package org.abdallah.imageprocessingservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransformProducer {

    private final KafkaTemplate<String, String> kafka;

    public void sendTransformEvent(String json) {
        kafka.send("image.transform", json);
    }
}
