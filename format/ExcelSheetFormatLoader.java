package com.kossine.ims.utility.excel_to_pojo.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelSheetFormatLoader {
	private File file;

	public ExcelSheetFormatLoader(File file) throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException(file + " was not found");
		else if (!file.getName().endsWith(".json"))
			throw new FileNotFoundException("provided format file is not json");
		this.file = file;
	}

	/*
	 * @param file which is json file which contains format of Excel file Eg :
	 * //contains Array of Sheets in format given below :- "sheets": [ { //sheet 1
	 * "name" : "Laptop", "columns" : ["tag","brand"] } , { //sheet 2 "name" :
	 * "Adapter", "columns " : ["tag","brand"] } //and so on ]
	 * 
	 * @return List of SheetFormat objects
	 */
	public List<SheetFormat> loadFromJson() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(file, new TypeReference<List<SheetFormat>>() {
		});
	}
}
