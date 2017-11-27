# Excel to POJO

<hr>

#### Description
This is utility used for parsing excel sheets data into java object used for Inventory Management System [here](./#) 
<hr>

#### Dependencies
* Jackson for json format to java format maven [link](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) .
* Apache Poi for .xlsx ( 2007 and above excel formats ) maven [link](https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml/3.9) 
, it can be made work for both .xls and .xslx file format using [this](https://stackoverflow.com/a/11972571/8413082) .

#### Usage

*  Call [this](./#) function which takes excel File and [json](./#) format file of how the data will be mapped in POJO 
 
  ```java
  
	FileParser fileParser = new FileParser(ExcelFile , JSONFormatFile ) ;

  ```

It returns 
 
 ```java
	HashMap<Class < ? extends Inventory > ,List<? extends Inventory > > 
	
  ```
 * Key for the HashMap is the Class of the POJO . 
 * where Inventory class is just common abstract class for all my POJO's
 * Value is List of all the POJO's that are read from row of corresponding sheets
