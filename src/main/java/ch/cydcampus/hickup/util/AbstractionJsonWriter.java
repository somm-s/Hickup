package ch.cydcampus.hickup.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;
import ch.cydcampus.hickup.pipeline.feature.Feature.Protocol;

/**
 * Write the abstractions that are on the tokenization level to a JSON Lines file.
 * Each abstraction is written to a new line and represented as a JSON object.
 * The fields of the JSON object are the features of the abstraction as well as the timestamp and the level of the abstraction.
 * The children of the abstraction are represented as an array of JSON objects.
 */
public class AbstractionJsonWriter {

    private BufferedWriter bufferedWriter;
    private ObjectMapper objectMapper;

    /**
     * Constructs a new abstraction writer that writes to the file at the given path.
     *
     * @param path The path to the file to write the abstractions to.
     * @throws IOException
     */
    public AbstractionJsonWriter(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        bufferedWriter = new BufferedWriter(fileWriter);
        objectMapper = new ObjectMapper();
        configureObjectMapper();
    }

    private void configureObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Abstraction.class, new AbstractionSerializer());
        objectMapper.registerModule(module);
    }

    /**
     * Writes the given abstraction to the file.
     *
     * @param abstraction The abstraction to write.
     * @throws IOException
     */
    public void writeAbstraction(Abstraction abstraction) throws IOException {
        String jsonString = objectMapper.writeValueAsString(abstraction);
        bufferedWriter.write(jsonString);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    /**
     * Closes the file writer.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        bufferedWriter.close();
    }

    /**
     * Custom serializer for the Abstraction class.
     */
    @JsonSerialize(using = AbstractionSerializer.class)
    private static class AbstractionSerializer extends StdSerializer<Abstraction> {

        public AbstractionSerializer() {
            this(null);
        }

        public AbstractionSerializer(Class<Abstraction> t) {
            super(t);
        }

        @Override
        public void serialize(Abstraction abstraction, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("start", abstraction.getFirstUpdateTime());
            jsonGenerator.writeNumberField("end", abstraction.getLastUpdateTime());
            jsonGenerator.writeNumberField("level", abstraction.getLevel());

            for(Feature feature : abstraction.getFeatures()) {
                serializeFeature(jsonGenerator, feature);
            }

            if(abstraction.getLevel() == 1) {
                jsonGenerator.writeEndObject();
                return;
            }

            jsonGenerator.writeArrayFieldStart("children");
            for (Abstraction childAbstraction : abstraction.getChildren()) {
                serialize(childAbstraction, jsonGenerator, provider);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }

        private void serializeFeature(JsonGenerator jsonGenerator, Feature feature) throws IOException {
            switch(feature.getType()) {
                case STRING:
                    jsonGenerator.writeStringField(feature.getName(), feature.asString());
                    break;
                case INT:
                    jsonGenerator.writeNumberField(feature.getName(), feature.asInt());
                    break;
                case DOUBLE:
                    jsonGenerator.writeNumberField(feature.getName(), feature.asDouble());
                    break;
                case BOOLEAN:
                    jsonGenerator.writeBooleanField(feature.getName(), feature.asBoolean());
                    break;
                case LONG:
                    jsonGenerator.writeNumberField(feature.getName(), feature.asLong());
                    break;
                case IP:
                    jsonGenerator.writeStringField(feature.getName(), feature.asIP().toString());
                    break;
                case PROTOCOL:
                    jsonGenerator.writeNumberField(feature.getName(), Protocol.toInt(feature.asProtocol()));
                    break;
                default:
                    jsonGenerator.writeStringField(feature.getName(), feature.toString());
            }
        }
    }
}
