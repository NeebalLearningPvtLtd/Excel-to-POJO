package com.kossine.ims.utility.exceltodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import javax.annotation.processing.FilerException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kossine.ims.models.Inventory;
import com.kossine.ims.models.InventoryFactory;
import com.kossine.ims.utility.exceltodb.exceptions.FileParsingException;
import com.kossine.ims.utility.exceltodb.exceptions.SheetParsingException;
import com.kossine.ims.utility.exceltodb.format.ExcelSheetFormatLoader;
import com.kossine.ims.utility.exceltodb.format.SheetFormat;

@SuppressWarnings("unused")
public class FileParser {
	private File excelFile;
	private File formatFile;
	private ExcelSheetFormatLoader formatLoader;
	private Workbook workbook;
	private List<SheetFormat> sheetFormats;

	public FileParser(File excelFile, File formatFile) throws FilerException {

		formatLoader = new ExcelSheetFormatLoader(formatFile);
		if (!excelFile.exists() || !excelFile.getName().endsWith(".xlsx"))
			throw new FilerException(excelFile + " does not exist");
		this.excelFile = excelFile;
		this.formatFile = formatFile;
	}

	public Map<Class<? extends Inventory>, List<Inventory>> parseFile() throws Exception {

		sheetFormats = formatLoader.loadFromJson();
		workbook = new XSSFWorkbook(new FileInputStream(excelFile));
		// greater workbook sheets doesnot matter rather if we only want to parse first
		// few sheets ?
		if (workbook.getNumberOfSheets() < sheetFormats.size())
			throw new FileParsingException("format number of sheets is greater than excel number of sheets");

		List<SheetParser> sheetParsingTasks = new ArrayList<>();
		List<Class<? extends Inventory>> pojoClasses = new ArrayList<>();
		// package in which POJO resides
		String prefix = "com.kossine.ims.models.";

		for (final SheetFormat sf : sheetFormats) {
			Class<? extends Inventory> clazz = InventoryFactory.getClazz(prefix + sf.getName());
			if (clazz == null)
				throw new SheetParsingException("Name of the Sheet doesnot match POJO Class Name");

			sheetParsingTasks.add(new SheetParser(workbook.getSheetAt(sf.getIndex()), sf, clazz));
			pojoClasses.add(clazz);
		}
		ForkJoinPool pool = new ForkJoinPool();

		List<Future<List<Inventory>>> results = pool.invokeAll(sheetParsingTasks);

		Map<Class<? extends Inventory>, List<Inventory>> map = new HashMap<>();

		for (int i = 0; i < results.size(); i++)
			map.put(pojoClasses.get(i), results.get(i).get());

		return map;
	}

}
