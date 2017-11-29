# Excel to POJO


#### Description
This is utility used for parsing excel sheets data into java object used for Inventory Management System [here](./#) 

#### Dependencies
* Jackson for json format to java format maven [link](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) .

* Apache Poi for .xlsx ( 2007 and above excel formats ) maven [link](https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml) and for .xls support you need to add maven [link](https://mvnrepository.com/artifact/org.apache.poi/poi) .

**Note** It can be made work for both .xls and .xslx file format using together using [this](https://stackoverflow.com/a/11972571/8413082) .

#### Usage
*
  *  Call [parseFile](./#) method of FileParser which takes excel File and [json](./#) format file of how the data will be mapped in POJO 

    ```java
      File excelFile =new File('PATH of excel file');

      File JSONFormatFile =new File('PATH of JSON format file');

      FileParser fileParser = new FileParser(excelFile , JSONFormatFile ) ;

      Map<Class < ? extends Inventory > ,List<Inventory >> map = fileParser.parseFile();

    ```

  * It returns Map of Inventory and list of POJO's

     * Key for the Map is the Class of the POJO . 
     * Value is List of all the POJO's that are read from row of corresponding sheets

     **Note** Inventory class is just common abstract class for all my POJO's .


  *  **Make** sure that name of [SheetFormat]() name and POJO Class Name , the [ColumnFormat]() name's and POJO's Fields name matches **exactly**  , else SheetParsingException and RowParsingException is raised respectively .
  
  
  *  Excel Sheets can be read from any index  ( 0 based ) just provide index of the  Sheet in JSONFormatFile like [this]()

