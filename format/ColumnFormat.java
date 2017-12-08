package com.kossine.ims.utility.excel_to_pojo.format;

public class ColumnFormat {
private String name;
private String type;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
@Override
public String toString() {
	return "ColumnFormat [name=" + name + ", type=" + type + "]";
}

}
