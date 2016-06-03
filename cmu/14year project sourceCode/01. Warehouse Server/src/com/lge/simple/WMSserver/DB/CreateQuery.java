package com.lge.simple.WMSserver.DB;

public final class CreateQuery {
	public static final String MAKE_ORDERINFO_TBL = "create table IF NOT EXISTS ORDERINFO ("
			+ "orderno integer primary key autoincrement,"
			+ "username nvarchar(255),"
			+ "desc nvarchar(255),"
			+ "path nvarchar(255),"
			+ "needtocompletewarehouse nvarchar(255),"
			+ "completewarehouse nvarchar(255),"
			+ "inputdatetime nvarchar(255),"
			+ "completedatetime datetime,"
			+ "status nvarchar(50)" + ");";

/*	public static final String MAKE_BACKORDERINFO_TBL = "create table IF NOT EXISTS BACKORDERINFO ("
			+ "orderno integer primary key,"
			+ "username nvarchar(255),"
			+ "desc nvarchar(255)," + "inputdatetime datetime" + ")";

	public static final String MAKE_COMPLETEORDERINFO_TBL = "create table IF NOT EXISTS COMPLETEORDERINFO ("
			+ "orderno integer primary key,"
			+ "username nvarchar(255),"
			+ "desc nvarchar(255),"
			+ "inputdatetime  datetime,"
			+ "completedatetime datetime" + ")";*/

	public static final String MAKE_ITEM_TBL = "create table IF NOT EXISTS ITEM ("
			+ "itemname	nvarchar(255) primary key,"
			+ "desc	nvarchar(255),"
			+ "price double" + ");";
	
	public static final String INSERT_ITEM_VALUE = 
			"INSERT INTO ITEM VALUES('baseball','This is baseball',5.0);"
			+ "INSERT INTO ITEM VALUES('basketball','This is basketball',12.0);"
			+ "INSERT INTO ITEM VALUES('tennisball','This is tennisball',15.0);"
			+ "INSERT INTO ITEM VALUES('golfball','This is golfball',7.0);"
			+ "INSERT INTO ITEM VALUES('soccerball','This is soccerball',10.0);";

	public static final String MAKE_INVENTORYSTATION_TBL = "create table IF NOT EXISTS INVENTORYSTATION ("
			+ "warehouseid	integer,"
			+ "stationid	integer,"
			+ "firstitemname	nvachar(255),"
			+ "firstitemcount	integer,"
			+ "seconditemname	nvachar(255)," + "seconditemcount	integer" + ");";
	
	public static final String INSERT_INVENTORYSTATION_VALUE = 
			"INSERT INTO INVENTORYSTATION VALUES(1,1,'baseball',20,'basketball',20);"
			+ "INSERT INTO INVENTORYSTATION VALUES(1,2,'tennisball',20,'golfball',20);"
			+ "INSERT INTO INVENTORYSTATION VALUES(1,3,'soccerball',20,'',NULL);";

	public static final String MAKE_WAREHOUSE_TBL = "create table IF NOT EXISTS WAREHOUSE ("
			+ "warehouseid	integer,"
			+ "location nvarchar(255),"
			+ "stationCount integer,"
			+ "status	nvarchar(50),"
			+ "macaddr nvarchar(50)" + ");";
	
	public static final String INSERT_WAREHOUSE_VALUE =
			"INSERT INTO WAREHOUSE VALUES(1,'WeanHall',4,'','9D82010EC478');";

/*	public static final String MAKE_OVERRIDE_TBL = "create table IF NOT EXISTS OVERRIDE ("
			+ "no integer primary key autoincrement,"
			+ "warehouseid	integer,"
			+ "botid	integer," + "desc	nvarchar(10)" + ")";
*/
	public static final String MAKE_ROBOT_TBL = "create table IF NOT EXISTS ROBOT ("
			+ "warehouseid	integer,"
			+ "botid	integer,"
			+ "orderno	integer,"
			+ "path	nvarchar(255),"
			+ "location	nvarchar(255),"
			+ "loadeditem nvarchar(255),"
			+ "status	nvarchar(50),"
			+ "manualcontrol nvarchar(10),"
			+ "errordetail nvarchar(255),"
			+ "macaddr nvarchar(50)" + ");";
	
	public static final String INSERT_ROBOT_VALUE =
			"INSERT INTO ROBOT VALUES(1,1,NULL,'','1:0-0','','idle','off','','3885010EC478');";
}
