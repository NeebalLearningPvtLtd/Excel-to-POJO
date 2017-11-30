package com.kossine.ims.utility.exceltodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	public FileParser(File excelFile, File formatFile) throws FileNotFoundException {

		formatLoader = new ExcelSheetFormatLoader(formatFile);
		if (!excelFile.exists() || !excelFile.getName().endsWith(".xlsx"))
			throw new FileNotFoundException(excelFile + " does not exist");
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
		Class<? extends Inventory> clazz;
		for (final SheetFormat sf : sheetFormats) {
			clazz = InventoryFactory.getClazz(sf.getName());

			if (clazz == null)
				throw new SheetParsingException("Name of the Sheet doesnot match POJO Class Name");

			sheetParsingTasks.add(new SheetParser(workbook.getSheetAt(sf.getIndex()), sf, clazz));

		}
		ForkJoinPool pool = new ForkJoinPool();

		List<Future<List<Inventory>>> results = pool.invokeAll(sheetParsingTasks);

		Map<Class<? extends Inventory>, List<Inventory>> map = new HashMap<>();

		for (int i = 0; i < results.size(); i++)
			try {
				clazz = results.get(i).get().get(0).getClass();

				map.put(clazz, results.get(i).get());
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		return map;
	}

}
