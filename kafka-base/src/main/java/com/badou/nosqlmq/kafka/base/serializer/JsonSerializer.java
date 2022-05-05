package com.badou.nosqlmq.kafka.base.serializer;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer<T> implements Serializer<T> {

	protected final ObjectMapper objectMapper;

	public JsonSerializer() {
		this(new ObjectMapper());
		this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public JsonSerializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void configure(Map<String, ?> configs, boolean isKey) {
		// No-op
	}

	public byte[] serialize(String topic, T data) {
		if (data == null) {
			return null;
		}
		try {
			byte[] result = null;
			if (data != null) {
				result = this.objectMapper.writeValueAsBytes(data);
			}
			return result;
		}
		catch (IOException ex) {
			throw new SerializationException("Can't serialize data [" + data + "] for topic [" + topic + "]", ex);
		}
	}

	public void close() {
		// No-op
	}

}

