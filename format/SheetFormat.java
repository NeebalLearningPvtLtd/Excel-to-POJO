package org.kossine.ims.utility.exceltodb.format;

import java.util.List;

public class SheetFormat {
private String name;
private List<ColumnFormat> columns;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public List<ColumnFormat> getColumns() {
	return columns;
}

public void setColumns(List<ColumnFormat> columns) {
	this.columns = columns;
}

@Override
public String toString() {
	return "SheetFormat [name=" + name + ", columns=" + columns + "]";
}

}

