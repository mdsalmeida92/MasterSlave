package utils;


public class Element {
	
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
	private String field;
	private String element;
	
	public Element(String field, String element) {
		super();
		this.field = field;
		this.element = element;
	}
	
	public Element() {
	}
	

}
