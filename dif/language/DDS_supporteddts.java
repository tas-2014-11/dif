package com.lumenare.dif.language;

public class DDS_supporteddts {
	protected Log log = new Log(this);

	protected String _manufacturer;
        protected String _model;

	public DDS_supporteddts(String manufacturer, String model) {
		_manufacturer = manufacturer;
                _model = model;
	}

	public String toString() {
		String s = "DDS_supporteddts(" + _manufacturer + ", " + _model + ")";
		return(s);
	}

	public String manufacturer() {
		return(_manufacturer);
	}

	public String model() {
		return(_model);
	}

	public void describe() {
		log.describe();
	}
}


