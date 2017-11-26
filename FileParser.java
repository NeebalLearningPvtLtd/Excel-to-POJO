package org.kossine.ims.utility.exceltodb;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.FilerException;

import org.kossine.ims.models.Inventory;
import org.kossine.ims.models.Laptop;
import org.kossine.ims.utility.exceltodb.format.ExcelSheetFormatLoader;

@SuppressWarnings("unused")
public class FileParser {	
	private File excelFile;
	private File formatFile;
	private ExcelSheetFormatLoader formatLoader;

	public FileParser(File excelFile, File formatFile) throws FilerException {

		formatLoader = new ExcelSheetFormatLoader(formatFile);
		if (!excelFile.exists() || !excelFile.getName().endsWith(".xlsx"))
			throw new FilerException(excelFile + " does not exist");
		this.excelFile = excelFile;
		this.formatFile = formatFile;
	}

	public Map<?, ?> parseFile() throws Exception {
		Map<Inventory,List<Inventory>> mp = new HashMap<>();
		mp.put((Inventory) new Laptop(),new ArrayList<>());
		//#############################//
		// threading for reading from sheets 
		// common Inventory interface
		
		System.out.println(mp);
		
		/*
		new Laptop().getClass().getDeclaredAnnotation(Inventory.class);
		for (SheetFormat sheetFormat : formatLoader.loadFromJson()) {
			
			System.out.println(sheetFormat.getName());
			
		}
*/
		return null;
	}

}
