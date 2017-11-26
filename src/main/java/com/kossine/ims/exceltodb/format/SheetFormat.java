package com.kossine.ims.exceltodb.format;

import java.util.List;

public class SheetFormat {
private String name;
private List<String> columns;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public List<String> getColumns() {
	return columns;
}

public void setColumns(List<String> columns) {
	this.columns = columns;
}
}
