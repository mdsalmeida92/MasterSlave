package utils;

import java.io.Serializable;

public class MyBoolean implements Serializable{
	
	private boolean Myboolean;

	public boolean isMyboolean() {
		return Myboolean;
	}

	public void setMyboolean(boolean myboolean) {
		Myboolean = myboolean;
	}

	public MyBoolean(boolean myboolean) {
		super();
		Myboolean = myboolean;
	};
	
	public MyBoolean() {

	};


}
