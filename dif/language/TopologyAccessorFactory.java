package com.lumenare.dif.language;

public class TopologyAccessorFactory extends AccessorFactory {
	public TopologyAccessorFactory(String path) {
		super(path);
	}

	protected Accessor locateAccessor(String path) {
		Accessor accessor = new TopologyAccessor(path);
		return(accessor);
	}
}

class TopologyAccessor extends Accessor {
	public TopologyAccessor(String fieldName) {
		_fieldName = fieldName;
	}

	public String fetchValue(ExecutionContext ec) {
		String value = ec.fetchInputArgValue(_fieldName);
		return(value);
	}
}
