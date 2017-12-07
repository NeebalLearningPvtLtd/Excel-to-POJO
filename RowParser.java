package com.kossine.ims.utility.exceltodb;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import com.kossine.ims.models.Inventory;
import com.kossine.ims.utility.exceltodb.exceptions.CellParsingException;
import com.kossine.ims.utility.exceltodb.exceptions.RowParsingException;
import com.kossine.ims.utility.exceltodb.format.ColumnFormat;
import com.kossine.ims.utility.exceltodb.format.SheetFormat;

public class RowParser {

	public void parseRow(Row row, SheetFormat sf, Object obj) throws RowParsingException {

		Iterator<Cell> cellItr = row.iterator();
		cellItr.next(); // for sr. no. column skip
		for (ColumnFormat field : sf.getColumns())
			if (cellItr.hasNext()) {
				Cell cell = cellItr.next();
				try {
					set(obj, field.getName(), getValue(cell, field.getType()));
				} catch (CellParsingException e) {
					throw new RowParsingException(e.getMessage() + " , at row " + row.getRowNum());
				}
			}
	}

	@SuppressWarnings("deprecation")
	private Object getValue(Cell raw, String fieldType) throws CellParsingException {
		switch (raw.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return raw.getBooleanCellValue();

		case Cell.CELL_TYPE_STRING: {
			String s = raw.getStringCellValue();
			String boolString = s.toLowerCase();
			if (boolString.equals("yes"))
				return true;
			if (boolString.equals("no"))
				return false;
			return s;
		}
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(raw)) {
				Date date = raw.getDateCellValue();
				return LocalDate.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
			}
			return (int) raw.getNumericCellValue();

		// formula parsing doesnot work
		// can be done using FormulaEvaluater
		case Cell.CELL_TYPE_FORMULA:
			return true;

		case Cell.CELL_TYPE_BLANK: {

			return getInstance(fieldType);
		}
		}
		return null;
	}

	private void set(Object obj, String fieldName, Object o) throws CellParsingException {
		@SuppressWarnings("unchecked")
		Class<? extends Inventory> clazz = (Class<? extends Inventory>) obj.getClass();
		Field field = null;

		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, o);
		} catch (Exception e) {

			throw new CellParsingException(e.getMessage() +" , value of the cell is malformed");
		}

	}

	private Object getInstance(String fieldType) throws CellParsingException {
		if (fieldType.equals("String"))
			return "";
		if (fieldType.equals("Boolean"))
			return true;
		if (fieldType.equals("Integer"))
			return 0;
		Class<?> clazz;
		try {
			clazz = Class.forName(fieldType);
		} catch (ClassNotFoundException e) {
			throw new CellParsingException(e.getMessage() + " , class not found");
		}
		Object value = null;
		try {
			value = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new CellParsingException(e.getMessage() + " , " + fieldType + " cannot be instantionated");
		}
		return value;
	}

}
