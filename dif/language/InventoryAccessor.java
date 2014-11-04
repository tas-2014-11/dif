package com.lumenare.dif.language;

abstract class InventoryAccessor extends Accessor {
	public InventoryAccessor() {
		_fieldName = calculateFieldName();
	}

	protected String calculateFieldName() {
		String className = this.getClass().getName();
		String packageName = this.getClass().getPackage().getName();
		int i = packageName.length();
		i += 19;  // strlen("InventoryAccessor_");
		// FIXME:  If somebody mis-names their class then what???  ArrayOutOfBoundsException???
		String baseName = className.substring(i);
		return(baseName);
	}
}
