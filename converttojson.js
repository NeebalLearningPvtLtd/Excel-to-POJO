const fs=require('fs')
let format=[
{
name:"Laptop",
columns:
[{name:"laptopTag",type:"String"},
{name:"brand",type:"String"},
{name:"modelNum",type:"String"},
{name:"serialNum",type:"String"},
{name:"batterySerialNum",type:"String"},
{name:"dop",type:"LocalDate"},
{name:"warranty",type:"boolean"},
{name:"vt",type:"boolean"},
{name:"wifi",type:"boolean"},
{name:"ram",type:"String"},
{name:"processor",type:"String"},
{name:"hdd",type:"String"},
{name:"supplier",type:"String"},
]
},
{
name:"Adapter",
columns:
[{name:"adapterTag",type:"String"},
{name:"brand",type:"String"},
{name:"modelNum",type:"String"},
{name:"serialNum",type:"String"},
{name:"dop",type:"LocalDate"},
{name:"warranty",type:"boolean"},
{name:"supplier",type:"String"},
]
}
]


fs.writeFile('./format.json', JSON.stringify(format, null, 1),()=>{});