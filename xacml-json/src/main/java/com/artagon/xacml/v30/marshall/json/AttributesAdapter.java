package com.artagon.xacml.v30.marshall.json;

import java.lang.reflect.Type;
import java.util.Collection;

import com.artagon.xacml.v30.AttributeCategories;
import com.artagon.xacml.v30.AttributeCategory;
import com.artagon.xacml.v30.pdp.Attribute;
import com.artagon.xacml.v30.pdp.Attributes;
import com.artagon.xacml.v30.pdp.XacmlSyntaxException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

class AttributesAdapter implements JsonDeserializer<Attributes>, JsonSerializer<Attributes>
{
	@Override
	public Attributes deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try{
			JsonObject o = json.getAsJsonObject();
			String id = GsonUtil.getAsString(o, "id", null);
			AttributeCategory category = AttributeCategories.parse(GsonUtil.getAsString(o, "category", null));
			Collection<Attribute> attributes = context.deserialize(o.getAsJsonArray("attributes"), new TypeToken<Collection<Attribute>>(){}.getType());
			return new Attributes(id, category, attributes);
		}catch(XacmlSyntaxException e){
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Attributes src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject o = new JsonObject();
		if(src.getId() != null){
			o.addProperty("id", src.getId());
		}
		o.addProperty("category", src.getCategory().getId());
		o.add("attributes", context.serialize(src.getAttributes()));
		return o;
	}
}