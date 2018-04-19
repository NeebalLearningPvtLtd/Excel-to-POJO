package com.kossine.ims.utility.excel_to_pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kossine.ims.models.InventoryFactory;
import com.kossine.ims.utility.excel_to_pojo.exceptions.FileParsingException;
import com.kossine.ims.utility.excel_to_pojo.format.ExcelSheetFormatLoader;
import com.kossine.ims.utility.excel_to_pojo.format.SheetFormat;

public class FileParser<U> {
	private File excelFile;
	private ExcelSheetFormatLoader formatLoader;
	private Workbook workbook;
	private List<SheetFormat> sheetFormats;
	private static final Logger log=LoggerFactory.getLogger(FileParser.class);
	public FileParser(File excelFile, File formatFile) throws FileNotFoundException {
		if (excelFile == null)
			throw new FileNotFoundException("Excel file  doesnot exist , Exception at FileParser constructor");
		if (formatFile == null)
			throw new FileNotFoundException("Json format file  doesnot exist , Exception at FileParser constructor");

		formatLoader = new ExcelSheetFormatLoader(formatFile);
		if (!excelFile.exists() || !excelFile.getName().endsWith(".xlsx"))
			throw new FileNotFoundException(excelFile + " does not exist");
		this.excelFile = excelFile;
	}

	@SuppressWarnings("unchecked")
	public <T extends U> Map<Class<T>, List<T>> parseFile() throws Exception {

		sheetFormats = formatLoader.loadFromJson();
		workbook = new XSSFWorkbook(new FileInputStream(excelFile));
		// greater workbook sheets doesnot matter rather if we only want to parse first
		// few sheets ?
		if (workbook.getNumberOfSheets() < sheetFormats.size())
			throw new FileParsingException("format number of sheets is greater than excel number of sheets");

		List<SheetParser<T,U>> sheetParsingTasks = new ArrayList<>();
		
		
		Class<T> clazz;
		for (final SheetFormat sf : sheetFormats) {
			clazz = (Class<T>) InventoryFactory.getClazz(sf.getName());

			if (clazz == null) {
				log.warn("Name of the Sheet :"+sf.getName()+" doesnot match POJO Class");
				continue;
			}
			SheetParser<T,U> sheetParser = new SheetParser<>(workbook.getSheetAt(sf.getIndex()), sf, clazz);
			sheetParsingTasks.add(sheetParser);
		
		}
		ExecutorService pool = Executors.newFixedThreadPool(sheetParsingTasks.size() + 2);

		
		List<Future<List<T>>> results = pool.invokeAll(sheetParsingTasks);

		Map<Class<T>, List<T>> map = new HashMap<>();

		int n = results.size();
		for (int count = 0, i = 0; count < n;) {

			if (results.get(i) != null && results.get(i).isDone()) {
				count++;
				try {
					List<T> list = results.get(i).get();
					try {
						clazz = (Class<T>) list.get(0).getClass();
						// allow duplicates
						if (map.containsKey(clazz))
							map.get(clazz).addAll(list);
						else
							map.put(clazz, list);
						
					} catch (IndexOutOfBoundsException e) {
						log.warn("Sheet " + sheetFormats.get(i).getIndex() + 1 + "/"
								+ sheetFormats.get(i).getName() + " is empty");
					}finally{
						results.set(i, null);
					}
				} catch (ExecutionException | InterruptedException | NullPointerException ex ) {
					List<String> errors = new ArrayList<String>();
					Arrays.stream(ex.getStackTrace()).forEach(e->errors.add(e.toString()));
					log.error("error in parsing excel file  at : {}" , errors);
				}

			} else
				i = (i + 1) % n;

		}
		pool.shutdown();
		return map;
	}

}
