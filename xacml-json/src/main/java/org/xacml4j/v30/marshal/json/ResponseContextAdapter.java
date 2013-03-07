package org.xacml4j.v30.marshal.json;

import java.lang.reflect.Type;
import java.util.Collection;

import org.xacml4j.v30.ResponseContext;
import org.xacml4j.v30.Result;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class ResponseContextAdapter implements JsonDeserializer<ResponseContext>
{
	@Override
	public ResponseContext deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonArray array = json.getAsJsonArray();
		Collection<Result> r = context.deserialize(array, new TypeToken<Collection<Result>>(){}.getType());
		return ResponseContext
				.builder()
				.results(r)
				.build();
	}
}