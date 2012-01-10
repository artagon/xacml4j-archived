package com.artagon.xacml.v30;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class XPathExpression 
{
	private String path;
	private AttributeCategory categoryId;
	
	public XPathExpression(String path, 
			AttributeCategory category){
		Preconditions.checkNotNull(path);
		Preconditions.checkNotNull(category);
		this.path = path;
		this.categoryId = category;
	}
	
	public AttributeCategory getCategory(){
		return categoryId;
	}
	
	public String getPath(){
		return path;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(o == this){
			return true;
		}
		if(!(o instanceof XPathExpression)){
			return false;
		}
		XPathExpression xpath = (XPathExpression)o;
		return categoryId.equals(xpath.categoryId) 
				&& path.equals(xpath.path);
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(this)
				.add("category", categoryId.toString())
				.add("path", path).toString();
	}
}