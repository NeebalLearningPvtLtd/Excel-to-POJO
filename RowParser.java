package com.kossine.ims.utility.exceltodb;

import org.apache.poi.ss.usermodel.Row;

import com.kossine.ims.utility.exceltodb.exceptions.RowParsingException;
import com.kossine.ims.utility.exceltodb.format.ColumnFormat;
import com.kossine.ims.utility.exceltodb.format.SheetFormat;

public class RowParser {

	public void parseRow(Row row, SheetFormat sf, Object obj) {
		
			
		for(ColumnFormat field : sf.getColumns()) {
			/*
			 * 
			 * left here
			 */
				
			
		}
	}
	private void set(Object obj,String fieldName ,Object o) {
		/*
		 * 
		 * left here
		 */
		
	}
	private Object getInstance(String fieldType) throws RowParsingException{
		Class<?> clazz;
		try {
			clazz=Class.forName(fieldType);
		}
		catch(ClassNotFoundException e) {
			throw new RowParsingException(e.getMessage());
		}
		Object value=null;
		try {
			value=clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RowParsingException(e.getMessage()+ " "+ fieldType +" cannot be instantionated");
		}
		return value;
	}
}
