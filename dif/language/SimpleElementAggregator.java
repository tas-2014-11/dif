package com.lumenare.dif.language;

import java.util.ArrayList;

// TODO: Should I just extend ArrayList ???

public class SimpleElementAggregator implements ElementAggregator {
	protected Log log = new Log(this);

	protected ArrayList _elementList = new ArrayList();

	public void append(DDSElement op) {
		// FIXME: Handle null!!!
		_elementList.add(op);
	}

	public void describe() {
		for(int i=0;i<_elementList.size();i++) {
			DDSElement el = (DDSElement)_elementList.get(i);
			el.describe();
		}
	}

	public DDSElement get(int i) {
		return((DDSElement)_elementList.get(i));
	}

	public int size() {
		return(_elementList.size());
	}
}

