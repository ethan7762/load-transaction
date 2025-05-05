package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	private final static ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
			.registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	public static ObjectMapper getMapper() {
		return mapper;
	}
	
	public static String toJson(Object obj) {
		String json = "{}";
		try {
			json = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("Parse json error", e);
		}
		return json;
	}
	
	public static <T> T fromJson(String jsonInString, Class<T> classOfT) {
		T obj = null;
		try {
			obj = mapper.readValue(jsonInString, classOfT);
		} catch (Exception e) {
			logger.error("Parse json error", e);
		}
		return obj;
	}
}
