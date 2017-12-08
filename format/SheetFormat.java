package com.kossine.ims.utility.excel_to_pojo.format;

import java.util.List;

public class SheetFormat {
	private int index;
	private String name;
	private List<ColumnFormat> columns;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

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
		return "SheetFormat [index=" + index + ", name=" + name + ", columns=" + columns + "]";
	}
	

}
