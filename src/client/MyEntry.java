package client;



import java.util.Map;

/**
 * Informação sobre servidor.
 */
public class MyEntry {


	private Map<String, Object> attributes;

	public MyEntry() {
	}

	public MyEntry(Map<String, Object> attributes) {

		this.attributes = attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}


	public Map<String, Object> getAttributes() {
		return attributes;
	}


}