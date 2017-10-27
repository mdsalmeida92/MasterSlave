package utils;



import java.util.Map;

public class MyEntry {


	private Map<String, String> attributes;

	public MyEntry() {
	}

	public MyEntry(Map<String, String> attributes) {

		this.attributes = attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}


	public Map<String, String> getAttributes() {
		return attributes;
	}


}