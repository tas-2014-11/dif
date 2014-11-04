package com.lumenare.dif.language;

public class Operation_bind extends DDSOperation {
	protected String _name;
	protected String _source;
	protected String _path;

	public static final String INVENTORY = "inventory";
	public static final String TOPOLOGY = "topology";

	protected Accessor _accessor;

	public Operation_bind(String name,String source,String path) throws DDSParseException {
		//String method = "Operation_bind()";

		_name = checknull(this,name,"name");
		_source = checklength(this,source,"source");
		_path = checklength(this,path,"path");

		if(INVENTORY.equals(source)) {
			_accessor = InventoryAccessorFactory._locateAccessor(_path);
		}
		else if(TOPOLOGY.equals(source)) {
			AccessorFactory af = new TopologyAccessorFactory(_path);
			_accessor = af.locateAccessor(_path);
		}
		else {
			abort(this,"UNKNOWNBINDSOURCE(" + _source + ")");
			return; // not reachable
		}
		if(null == _accessor) {
			abort(this,"ACCESSORNOTFOUND(" + _source + "," + _path + ")");
		}

		//log.invoke(method);
		//log.ret(method);
	}

	public String toString() {
		String s = "Operation_bind(";
		s += _name + ",";
		s += _source + ",";
		s += _path + ")";
		return(s);
	}

	public void describe() {
		log.describe();
		_accessor.describe();
	}

	protected void execute(ExecutionContext ec) throws DDSExecutionException {
		String method = "execute(" + ec + ")";
		log.invoke(method);

		SymbolTable symtab = ec.getSymbolTable();
		//symtab.describe();

		String value = _accessor.fetchValue(ec);
		symtab.assign(_name,value);

		log.ret(method);
	}
}
