package com.kossine.ims.utility.exceltodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.kossine.ims.models.Inventory;
import com.kossine.ims.utility.exceltodb.exceptions.RowParsingException;
import com.kossine.ims.utility.exceltodb.exceptions.SheetParsingException;
import com.kossine.ims.utility.exceltodb.format.SheetFormat;

public class SheetParser implements Callable<List<Inventory>> {
	private Sheet sheet;
	private SheetFormat sheetFormat;
	private List<Inventory> list;
	private RowParser rowParser;
	private Class<? extends Inventory> clazz;

	SheetParser(Sheet sheet, SheetFormat sheetFormat, Class<? extends Inventory> clazz) {
		this.sheet = sheet;
		this.sheetFormat = sheetFormat;
		this.clazz = clazz;
		rowParser = new RowParser();
		list = new ArrayList<>();
	}

	@Override
	public List<Inventory> call() throws SheetParsingException {
		int rowStart = getStartRow(sheet);
		int rowEnd = getLastRow(sheet);

		for (int i = rowStart; i <= rowEnd; i++) {
			Object obj = null;
			try {
				obj = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SheetParsingException(e.getMessage());
			}
			try {
			rowParser.parseRow(sheet.getRow(i), sheetFormat, obj);
			// ? List<? extends Inventory > doesnot work for casting
			list.add(clazz.cast(obj));
			}catch(RowParsingException e) {
				System.err.println(e.getMessage()+" ,"+sheet.getSheetName());
			}
		}

		return list;
	}

	// gets index of first row where data is there
	private int getStartRow(Sheet sheet) throws SheetParsingException {

		Iterator<Row> rit = sheet.iterator();
		while (rit.hasNext()) {
			Row row = rit.next();
			Cell cell = row.getCell(0);

			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				/*
				 * In my case first column is Sr.no. and Data starts where sr. no is 1 ,so this
				 * is start row index
				 */
				if (cell.getNumericCellValue() == 1.0)
					return row.getRowNum();
			}
		}
		throw new SheetParsingException("no start index found for data");
	}

	/*
	 * Since there may be redundant whitespaces at bottom of the sheet so to get the
	 * end of the row where data exist
	 */
	private int getLastRow(Sheet sheet) throws SheetParsingException {
		int lastRowIndex = -1;
		if (sheet.getPhysicalNumberOfRows() > 0) { // getLastRowNum() actually returns an index, not a row number
			lastRowIndex = sheet.getLastRowNum();

			// now, start at end of spreadsheet and work our way backwards until we find a
			// row having data
			for (; lastRowIndex >= 0; lastRowIndex--) {
				Row row = sheet.getRow(lastRowIndex);
				if (row != null) {
					Cell cell = row.getCell(0);
					if (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
						break;
				}
			}
			return lastRowIndex;
		} else
			throw new SheetParsingException(sheet.getSheetName() + " is empty");
	}
}
