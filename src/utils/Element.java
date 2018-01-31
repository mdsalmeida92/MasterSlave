package utils;

import java.io.Serializable;

public class Element implements Serializable{
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	private String field;
	private String element;

	private String key;
	
	public Element(String field, String element) {
		super();
		this.field = field;
		this.element = element;
	}

	public Element() {
	}
	
}
