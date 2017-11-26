package com.kossine.ims.exceltodb.format;
import java.io.File;
import java.util.List;
public class ExcelSheetFormatLoader {
	/*
	 * @param file which is json file which contains format of Excel file
	 * Eg :
	 * //contains Array of Sheets in format given below :-
	 * "sheets":
	 * [
	 * {
	 * 	//sheet 1
	 * 	"name" : "Laptop",
	 *  "columns" : ["tag","brand"]
	 * }
	 * ,
	 * { //sheet 2
	 *  "name" : "Adapter",
	 *  "columns " : ["tag","brand"]
	 * }
	 *  //and so on
	 * ] 
	 * 
	 * @return List of SheetFormat objects
	 */
	public List<SheetFormat> loadFromJson(File file){
			
		
		return null;
	}
}
