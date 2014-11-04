package com.lumenare.dif.remote;

import com.avulet.db.Key;

import com.lumenare.common.domain.attribute.AttributeCollection;
import com.lumenare.common.domain.attribute.adapter.AttributeUtil;

public class TargetedAttributeCollection extends TargetedObject {
	public TargetedAttributeCollection(Key k,AttributeCollection ac) {
		super(k,ac);
	}

	public AttributeCollection attributeCollection() {
		return((AttributeCollection)_object);
	}


	// TODO: AttributeCollectionImpl does not override Object.toString :(

	public String toString() {
		String s = "TargetedAttributeCollection(";
		s += _key + ",";
		s += AttributeUtil.describe(attributeCollection()) + ")";
		return(s);
	}
}
