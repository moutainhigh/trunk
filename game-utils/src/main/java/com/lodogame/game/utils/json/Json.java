package com.lodogame.game.utils.json;

import java.io.IOException;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.lodogame.game.exception.JsonException;

public class Json {
	private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share

	static {
		// ignore every property you haven't defined in your POJO
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static final Log LOGGER = LogFactory.getLog(Json.class);

	// globally

	public Json() {

	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			String str = mapper.writeValueAsString(obj);
			return str;
		} catch (JsonGenerationException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		} catch (IOException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		}
	}

	public static <T> T toObject(String content, Class<T> valueType) {
		if (StringUtils.isEmpty(content)) {
			return null;
		}
		try {
			return mapper.readValue(content, valueType);
		} catch (JsonParseException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		} catch (JsonMappingException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		} catch (IOException e) {
			LOGGER.error(e);
			throw new JsonException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(String json, Class<T> c) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		List<T> results = new ArrayList<T>();
		List<Map<String, Object>> list = Json.toObject(json, List.class);

		try {
			for (Map<String, Object> m : list) {
				T t = c.newInstance();
				BeanUtils.populate(t, m);
				results.add(t);
			}
		} catch (InstantiationException ise) {
			LOGGER.error(ise.getMessage(), ise);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException ie) {
			LOGGER.error(ie.getMessage(), ie);
		}

		return results;
	}

	public static <T> List<T> toList(List<String> jsonList, Class<T> c) {

		if (jsonList == null) {
			return null;
		}

		List<T> results = new ArrayList<T>();

		for (String json : jsonList) {

			if (StringUtils.isEmpty(json)) {
				continue;
			}

			results.add(toObject(json, c));
		}
		return results;
	}

	public static void main(String[] args) {
		Map m = new HashMap();
		m.put("test", "1");
		String s = toJson(m);
		System.out.println(s);
		Map m2 = toObject(s, Map.class);
		System.out.println(m2.get("test"));
	}

}
