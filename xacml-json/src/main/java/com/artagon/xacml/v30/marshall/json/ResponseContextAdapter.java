package com.artagon.xacml.v30.marshall.json;

import java.lang.reflect.Type;
import java.util.Collection;

import com.artagon.xacml.v30.pdp.ResponseContext;
import com.artagon.xacml.v30.pdp.Result;
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
		Collection<Result> results = context.deserialize(array, new TypeToken<Collection<Result>>(){}.getType());
		return new ResponseContext(results);
	}
}