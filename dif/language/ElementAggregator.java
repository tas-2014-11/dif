package com.lumenare.dif.language;

public interface ElementAggregator {
	public void append(DDSElement op);
	public DDSElement get(int i);
	public int size();
}

