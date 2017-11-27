package com.kossine.ims.utility.exceltodb;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import javax.annotation.processing.FilerException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kossine.ims.models.Inventory;
import com.kossine.ims.utility.exceltodb.exceptions.FileParsingException;
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

	public Map<Class<? extends Inventory>, List<? extends Inventory> > parseFile() throws Exception {
		
		sheetFormats = formatLoader.loadFromJson();
		workbook = new XSSFWorkbook(new FileInputStream(excelFile));
		//greater workbook sheets doesnot matter rather if we only want to parse first few sheets ?
		if(workbook.getNumberOfSheets() < sheetFormats.size() )
			throw new FileParsingException("format size is greater than number of sheets");
		
		
		List<SheetParser> sheetParsingTasks = new ArrayList<>();
		for (final SheetFormat sf : sheetFormats)
			sheetParsingTasks.add(new SheetParser(workbook.getSheetAt(sf.getIndex()),sf));
		
//		System.out.println(sheetParsingTasks);
		
		ForkJoinPool pool = new ForkJoinPool();

		List<Future<List<? extends Inventory>>> results = pool.invokeAll(sheetParsingTasks);
		Map<Class<? extends Inventory>, List<? extends Inventory>> mp = new HashMap<>();
		int idx = 0;
		String prefix = "com.kossine.ims.models.";
		for (Future<List<? extends Inventory>> result : results) {
			
			@SuppressWarnings("unchecked")
			Class<? extends Inventory> clazz = (Class<? extends Inventory>) Class.forName(prefix+sheetFormats.get(idx).getName());
//			System.out.println(result.isDone());
			mp.put(clazz,result.get());
			idx++;
		}

		return mp;
	}

}
