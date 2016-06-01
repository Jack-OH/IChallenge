package com.lge.simple.WMSserver.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sqlite.SQLiteConfig;

import com.lge.simple.WMSserver.Model.InventoryInfo;
import com.lge.simple.WMSserver.Model.Item;
import com.lge.simple.WMSserver.Model.OrderInfo;
import com.lge.simple.WMSserver.Model.OverrideOrderQueue;
import com.lge.simple.WMSserver.Model.RobotMoveInfo;
import com.lge.simple.WMSserver.Model.RobotStatus;
import com.lge.simple.WMSserver.Model.Stock;
import com.lge.simple.WMSserver.Model.WarehouseInfo;

public class Database {

	private Connection connection;
	private String dbFileName;
	private boolean isOpened = false;

	public void init() {
		open();
		createTable();
		initTable();
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Database(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	public boolean open() {
		try {
			SQLiteConfig config = new SQLiteConfig();
			// config.setReadOnly(true);
			this.connection = DriverManager.getConnection("jdbc:sqlite:"
					+ this.dbFileName, config.toProperties());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		isOpened = true;
		System.out.println("Opened database successfully");
		return true;
	}

	public boolean close() {
		if (this.isOpened == false) {
			return true;
		}

		try {
			this.connection.close();
			isOpened = false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void createTable() {
		try {
			executeQuery(CreateQuery.MAKE_ORDERINFO_TBL);
			/*
			 * executeQuery(CreateQuery.MAKE_BACKORDERINFO_TBL);
			 * executeQuery(CreateQuery.MAKE_COMPLETEORDERINFO_TBL);
			 */
			executeQuery(CreateQuery.MAKE_ITEM_TBL);
			executeQuery(CreateQuery.MAKE_INVENTORYSTATION_TBL);
			executeQuery(CreateQuery.MAKE_WAREHOUSE_TBL);
			// executeQuery(CreateQuery.MAKE_OVERRIDE_TBL);
			executeQuery(CreateQuery.MAKE_ROBOT_TBL);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Created table successfully");
	}

	public void initTable() {
		Statement stmt;

		String sql = "SELECT * FROM ITEM";
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (!rs.next()) {
				executeQuery(CreateQuery.INSERT_ITEM_VALUE);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "SELECT * FROM INVENTORYSTATION";
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (!rs.next()) {
				executeQuery(CreateQuery.INSERT_INVENTORYSTATION_VALUE);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "SELECT * FROM WAREHOUSE";
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (!rs.next()) {
				executeQuery(CreateQuery.INSERT_WAREHOUSE_VALUE);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "SELECT * FROM ROBOT";
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (!rs.next()) {
				executeQuery(CreateQuery.INSERT_ROBOT_VALUE);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int executeQuery(String query) {
		int result = 0;
		try {
			Statement st = this.connection.createStatement();
			result = st.executeUpdate(query);
			st.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void insertTable() {
		Statement st = null;
		String sql;

		try {
			st = this.connection.createStatement();
			sql = "INSERT INTO INVENTORY (ID,TYPE,QUANTITY)"
					+ "VALUES (1,'BASEBALL',20);";
			st.executeUpdate(sql);

			sql = "INSERT INTO INVENTORY (ID,TYPE,QUANTITY)"
					+ "VALUES (2,'BASKETBALL',30);";
			st.executeUpdate(sql);

			sql = "INSERT INTO INVENTORY (ID,TYPE,QUANTITY)"
					+ "VALUES (3,'SOCCERBALL',40);";
			st.executeUpdate(sql);

			st.close();
			this.connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Records created successfully");
	}

	public void selectDB() {
		Statement st = null;

		try {
			st = this.connection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM INVENTORY");

			while (rs.next()) {
				int id = rs.getInt("id");
				String type = rs.getString("type");
				int quantity = rs.getInt("quantity");

				System.out.println("ID = " + id);
				System.out.println("TYPE = " + type);
				System.out.println("QUANTITY = " + quantity);
				System.out.println();
			}

			rs.close();
			st.close();
			System.out.println("Selected successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> getItemType() {
		ArrayList<String> itemTypes;
		itemTypes = new ArrayList<String>();
		String first, second;

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM INVENTORYSTATION");

			while (rs.next()) {
				first = rs.getString("firstitemname");
				if (first != null && first != ""
						&& itemTypes.contains(first) == false)
					itemTypes.add(first);
				second = rs.getString("seconditemname");
				if (second != null && second != ""
						&& itemTypes.contains(second) == false)
					itemTypes.add(second);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return itemTypes;
	}

	public void insertNewOrder(String userId, String desc, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String sql = "INSERT INTO ORDERINFO (username, desc, inputdatetime,status) VALUES ("
				+ "'"
				+ userId
				+ "',"
				+ "'"
				+ desc
				+ "',"
				+ "'"
				+ sdf.format(date)
				+ "','"
				+ ConstantMessages.STATUS_PENDING
				+ "');";

		executeQuery(sql);

	}

	public ArrayList<String> getInventoryList() {
		ArrayList<String> inventories = new ArrayList<String>();
		String result;

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT itemname FROM ITEM");

			while (rs.next()) {
				result = rs.getString("itemname");
				inventories.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return inventories;
	}

	public HashMap<Integer, Integer> getInventoryInfo(int wmsId, String itemName) {
		InventoryInfo info = new InventoryInfo(wmsId, itemName);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		String sql1 = "SELECT stationid,firstitemcount FROM INVENTORYSTATION "
				+ "WHERE warehouseid = '" + wmsId + "' and "
				+ "firstitemname = '" + itemName + "'";

		String sql2 = "SELECT stationid,seconditemcount FROM INVENTORYSTATION "
				+ "WHERE warehouseid = '" + wmsId + "' and "
				+ "seconditemname = '" + itemName + "'";

		try {
			Statement stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);

			while (rs.next()) {
				info.add(rs.getInt("stationid"), rs.getInt("firstitemcount"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		try {
			Statement stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql2);

			while (rs.next()) {
				info.add(rs.getInt("stationid"), rs.getInt("seconditemcount"));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		result = info.getResult();
		return result;
	}

	public Item getItemInfo(String name) {
		Item i = null;
		String sql = "SELECT desc, price FROM ITEM where itemname = '" + name
				+ "'";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				i = new Item(name);
				i.desc = rs.getString("desc");
				i.price = rs.getFloat("price");
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return i;

	}

	public int getItemCount(String name) {
		int count = 0;
		int totalCount = 0;
		String itemName = "";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM INVENTORYSTATION");

			while (rs.next()) {
				itemName = rs.getString("firstitemname");
				if (itemName != null && itemName.equals(name)) {
					count = rs.getInt("firstitemcount");
					if (count != 0) // count가 0이 아닌 null일 수도 있을까?
						totalCount += count;
				}
				itemName = rs.getString("seconditemname");
				if (itemName != null && itemName.equals(name)) {
					count = rs.getInt("seconditemcount");
					if (count != 0) // count가 0이 아닌 null일 수도 있을까?
						totalCount += count;
				}
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalCount;
	}

	public ArrayList<WarehouseInfo> getWarehouselist() {
		ArrayList<WarehouseInfo> result = new ArrayList<WarehouseInfo>();

		String sql = "SELECT * FROM WAREHOUSE";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				WarehouseInfo i = new WarehouseInfo(String.valueOf(rs
						.getInt("warehouseid")), rs.getString("location"),
						rs.getInt("stationCount"));
				result.add(i);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return result;

	}

	public String updateInventory(int wmsId, String itemName,
			String description, double price, HashMap<String, Long> items) {
		// 현재 추가하는 item이 item table에 없다면 item table에 추가
		Item i = getItemInfo(itemName);
		if (i == null) {
			insertNewItem(itemName, description, price);
		}

		HashMap<String, String> updatelocation = new HashMap<String, String>();

		for (Entry<String, Long> ety : items.entrySet()) {
			int key = (Integer.valueOf(ety.getKey()));
			int val = ety.getValue().intValue();
			if (val == 0)
				continue;
			String loc = canUpdateInventoryStationInfo(wmsId, itemName, key);
			// inventory station에 추가할 수 있는지? (inventory station 정보가 없으면 생성하고,
			// inventory station에서 해당 item이 있는지 확인하고,
			// 없다면 itemcount가 0이라서 추가할 수 있는 곳이 있는지 확인해서
			// 어디다가 넣을건지 first or second 인지 확인해줌
			if (loc != null
					&& loc.equals(ConstantMessages.NOSPACETOUPDATEORDER)) {
				return ConstantMessages.NOSPACETOUPDATEORDER;
			} else {
				updatelocation.put(String.valueOf(key), loc);
			}
		}

		for (Entry<String, String> ety : updatelocation.entrySet()) {
			String key = ety.getKey();
			updateInventoryStationToDB(wmsId, key, itemName,
					updatelocation.get(key), items.get(key).intValue());
		}
		updateItemInfo(itemName, description, price);

		return "";
	}

	private void updateInventoryStationToDB(int wmsId, String key,
			String itemName, String updatelocationID, int count) {
		String updatename, updatecount;
		if (updatelocationID != null && updatelocationID.equals("first")) {
			updatename = "firstitemname";
			updatecount = "firstitemcount";
		} else {
			updatename = "seconditemname";
			updatecount = "seconditemcount";
		}
		String sql = "UPDATE INVENTORYSTATION SET " + updatename + "='"
				+ itemName + "', " + updatecount + "=" + updatecount + "+"
				+ count + " WHERE warehouseid = '" + wmsId
				+ "' and stationid='" + key + "'";
		executeQuery(sql);
	}

	private void updateItemInfo(String itemName, String description,
			double price) {
		String sql = "UPDATE ITEM SET price ='" + price + "', desc = '"
				+ description + "' WHERE itemname = '" + itemName + "'";

		executeQuery(sql);
	}

	private void insertNewItem(String itemname, String desc, double price) {
		String sql = "INSERT INTO ITEM (itemname, desc, price) VALUES (" + "'"
				+ itemname + "'," + "'" + desc + "'," + "'" + price + "');";
		executeQuery(sql);
	}

	private String canUpdateInventoryStationInfo(int wmsId, String name,
			int stationId) {
		String result = ConstantMessages.NOSPACETOUPDATEORDER;

		if (CheckInventoryStationExist(wmsId, stationId)) {
			return "first";
		}

		String sql = "SELECT * FROM INVENTORYSTATION where (warehouseid = '"
				+ wmsId + "' and stationid = '" + stationId
				+ "' ) and (firstitemname = '" + name
				+ "' or seconditemname = '" + name + "')";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if ((rs.getString("firstitemname")) != null
						&& (rs.getString("firstitemname")).equals(name))
					return "first";
				else
					return "second";
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "SELECT * FROM INVENTORYSTATION where (warehouseid = '"
				+ wmsId
				+ "' and stationid = '"
				+ stationId
				+ "' ) and (firstitemcount = '0' or seconditemcount = '0' or firstitemcount is null or seconditemcount is null)";

		Statement stmt2;
		try {
			stmt2 = this.connection.createStatement();
			ResultSet rs = stmt2.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("firstitemcount") == 0)
					return "first";
				else
					return "second";
			}
			rs.close();
			stmt2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	private boolean CheckInventoryStationExist(int wmsId, int stationId) {
		String sql = "SELECT * FROM INVENTORYSTATION where (warehouseid = '"
				+ wmsId + "' and stationid = '" + stationId + "' )";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				rs.close();
				stmt.close();
				return false;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "INSERT INTO INVENTORYSTATION (warehouseid,stationid) VALUES ("
				+ "'" + wmsId + "'," + "'" + stationId + "')";

		executeQuery(sql);
		return true;
	}

	public ArrayList<RobotStatus> getRobotStatus(int wmsId) {
		ArrayList<RobotStatus> result = new ArrayList<RobotStatus>();
		String sql = "SELECT * FROM ROBOT WHERE warehouseid = '" + wmsId + "'";
		String temp;
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RobotStatus robotstatus = new RobotStatus();
				robotstatus.robotId = rs.getInt("botid");
				robotstatus.status = rs.getString("status");
				robotstatus.manualControl = rs.getString("manualcontrol");
				robotstatus.error = rs.getString("errordetail");
				robotstatus.orderId = rs.getInt("orderno");
				temp = rs.getString("location");
				temp = temp.substring(temp.indexOf(':') + 1);
				robotstatus.location.put("src",
						temp.substring(0, temp.indexOf('-')));
				robotstatus.location.put("dst",
						temp.substring(temp.indexOf('-') + 1));

				temp = rs.getString("loadeditem");
				if (temp != null && !temp.isEmpty()) {
					String[] temparray = temp.split(",");
					for (String s : temparray) {
						// System.out.println(s);
						robotstatus.loadedItem.put(s.substring(0,
								s.indexOf(':')), Integer.parseInt(s.substring(s
								.indexOf(':') + 1)));
					}
				}
				result.add(robotstatus);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String insertRobotMoveOrder(int wmsId, int robotId, String direction) {

		try {
			OverrideOrderQueue ooq = OverrideOrderQueue.getInstance();
			ooq.command.add(wmsId + "," + robotId + "," + direction);
			return "";
		} catch (Exception e) {
			return "Insert command queue error";
		}
	}

	public ArrayList<Integer> getOrderList() {
		ArrayList<Integer> orderList = new ArrayList<Integer>();

		String sql = "SELECT orderno FROM ORDERINFO";
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				orderList.add(rs.getInt("orderno"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*
		 * sql = "SELECT orderno FROM ORDERINFO"; try { stmt =
		 * this.connection.createStatement(); ResultSet rs =
		 * stmt.executeQuery(sql); while (rs.next()) {
		 * orderList.add(rs.getInt("orderno")); } } catch (SQLException e) {
		 * e.printStackTrace(); } sql = "SELECT orderno FROM BACKORDERINFO"; try
		 * { stmt = this.connection.createStatement(); ResultSet rs =
		 * stmt.executeQuery(sql); while (rs.next()) {
		 * orderList.add(rs.getInt("orderno")); } } catch (SQLException e) {
		 * e.printStackTrace(); }
		 */
		return orderList;
	}

	public OrderInfo getOrderInfo(int orderid) {
		ArrayList<Stock> stocks = calculateStock();
		OrderInfo oi = null;
		String sql = "SELECT * FROM ORDERINFO where orderno = " + orderid;
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				oi = new OrderInfo();
				oi.userId = rs.getString("username");
				oi.orderId = orderid;
				oi.status = rs.getString("status");
				oi.route = rs.getString("path");

				String temp = rs.getString("desc");
				for (String s : temp.split(",")) {
					String itemName = s.substring(0, s.indexOf(':'));
					int count = Integer
							.valueOf(s.substring(s.indexOf(':') + 1));
					oi.items.put(itemName, count);
					if (stocks == null) {
						oi.backorderdItems.add(itemName);
					} else {
						for (Stock stock : stocks) {
							if (stock != null && stock.name != null
									&& stock.name.equals(itemName)
									&& stock.count < count)
								oi.backorderdItems.add(itemName);
						}
					}
				}

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return oi;
	}

	/*
	 * public OrderInfo getOneOrderInfoInBackOrder() { ArrayList<Stock> stocks =
	 * calculateStock(); OrderInfo oi = null; String sql =
	 * "SELECT * FROM BACKORDERINFO order by orderno asc limit 1"; Statement
	 * stmt; try { stmt = this.connection.createStatement(); ResultSet rs =
	 * stmt.executeQuery(sql); while (rs.next()) { oi = new OrderInfo();
	 * oi.userId = rs.getString("username"); oi.orderId = rs.getInt("orderno");
	 * oi.status = ConstantMessages.STATUS_BACKORDER;
	 * 
	 * String temp = rs.getString("desc"); for (String s : temp.split(",")) {
	 * String itemName = s.substring(0, s.indexOf(':') - 1); int count = Integer
	 * .valueOf(s.substring(s.indexOf(':') + 1)); oi.items.put(itemName, count);
	 * if (stocks == null) { oi.backorderdItems.add(itemName); } else { for
	 * (Stock stock : stocks) { if (stock.name == itemName && stock.count <
	 * count) oi.backorderdItems.add(itemName); } } }
	 * 
	 * } } catch (SQLException e) { e.printStackTrace(); } return oi; }
	 */

	public ArrayList<Stock> calculateStock() {
		ArrayList<Stock> stock = new ArrayList<Stock>();

		stock = AdjustStock(stock);

		String sql = "SELECT * FROM INVENTORYSTATION";
		Statement stmt;
		String name = "";
		int count = 0;

		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				boolean find = false;
				name = rs.getString("firstitemname");
				count = rs.getInt("firstitemcount");

				for (Stock s : stock) {
					if (s != null && s.name != null && s.name.equals(name)) {
						s.count = s.count + count;
						find = true;
					}
				}

				if (find == false) {
					stock.add(new Stock(name, count));
				}

				name = rs.getString("seconditemname");
				count = rs.getInt("seconditemcount");

				for (Stock s : stock) {
					if (s != null && s.name != null && s.name.equals(name)) {
						s.count = s.count + count;
						find = true;
					}
				}
				if (find == false) {
					stock.add(new Stock(name, count));
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stock;

	}

	private ArrayList<Stock> AdjustStock(ArrayList<Stock> stocks) {
		String sql = "select * FROM ROBOT where status = '"
				+ ConstantMessages.STATUS_INPROGRESS + "'";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				String loadeditem = rs.getString("loadeditem");
				if (loadeditem != null && !loadeditem.equals("")) {
					for (String s : loadeditem.split(",")) {
						boolean boolfind = false;
						String itemName = s.substring(0, s.indexOf(':'));
						int count = Integer
								.valueOf(s.substring(s.indexOf(':') + 1));
						if (stocks != null)
							stocks = new ArrayList<Stock>();
						for (Stock stock : stocks) {
							if (stock.name != null
									&& stock.name.equals(itemName)) {
								stock.count = stock.count + count;
								boolfind = true;
							}
						}
						if (boolfind == false)
							stocks.add(new Stock(itemName, count));
					}
				}

				String path = rs.getString("path"); // 원래 있는 것에서 뺄꺼라서 stocks의
													// null처리 안함.
				for (String s : path.split(",")) {
					s = s.substring(s.indexOf(":") + 1);
					s = s.substring(s.indexOf(":") + 1);
					String itemName = s.substring(0, s.indexOf(':'));
					int count = Integer
							.valueOf(s.substring(s.indexOf(':') + 1));
					if (stocks != null)
						stocks = new ArrayList<Stock>();
					for (Stock stock : stocks) {
						if (stock.name != null && stock.name.equals(itemName)) {
							stock.count = stock.count - count;
						}
					}
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stocks;
	}

	/*
	 * private OrderInfo getOrderInfoInOrder(int orderid) { OrderInfo oi = null;
	 * String sql = "SELECT * FROM ORDERINFO where orderno = " + orderid;
	 * Statement stmt; try { stmt = this.connection.createStatement(); ResultSet
	 * rs = stmt.executeQuery(sql); while (rs.next()) { oi = new OrderInfo();
	 * oi.userId = rs.getString("username"); oi.orderId = orderid; if
	 * (rs.getString("path") == null || rs.getString("path") == "") oi.status =
	 * ConstantMessages.STATUS_PENDING; else oi.status =
	 * ConstantMessages.STATUS_INPROGRESS;
	 * 
	 * String temp = rs.getString("desc"); for (String s : temp.split(",")) {
	 * oi.items.put(s.substring(0, s.indexOf(':') - 1),
	 * Integer.valueOf(s.substring(s.indexOf(':') + 1))); }
	 * 
	 * } } catch (SQLException e) { e.printStackTrace(); } return oi; }
	 */

	public OrderInfo getOneCandidateOrderInfo() {
		ArrayList<Stock> stocks = calculateStock();
		String backorderedId = "";
		OrderInfo oi = null;
		String sql = "select * FROM ("
				+ "SELECT * FROM ORDERINFO where status = 'backordered' order by orderno asc"
				+ ")"
				+ "union all "
				+ "select * FROM ("
				+ "SELECT * FROM ORDERINFO where status = 'pending' order by orderno asc limit 1"
				+ ")";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				boolean backordered = false;
				String temp = rs.getString("desc");
				for (String s : temp.split(",")) {
					String itemName = s.substring(0, s.indexOf(':'));
					int count = Integer
							.valueOf(s.substring(s.indexOf(':') + 1));
					if (stocks == null) {
						backorderedId = backorderedId
								+ String.valueOf(rs.getInt("orderno")) + ",";
						backordered = true;
					} else {
						for (Stock stock : stocks) {
							if (stock != null && stock.name != null
									&& stock.name.equals(itemName)
									&& stock.count < count) {
								backorderedId = backorderedId
										+ String.valueOf(rs.getInt("orderno"))
										+ ",";
								backordered = true;
							}
						}
					}
				}

				if (backordered == false && oi == null) {

					oi = new OrderInfo();
					for (String s : temp.split(",")) {
						// System.out.println("Make order info : " + s );
						String itemName = s.substring(0, s.indexOf(':'));

						// System.out.println("Make order info Name: " +
						// itemName );
						int count = Integer
								.valueOf(s.substring(s.indexOf(':') + 1));

						// System.out.println("Make order info Cnt: " + count );
						oi.items.put(itemName, count);
					}
					oi.userId = rs.getString("username");
					oi.orderId = rs.getInt("orderno");
					oi.status = rs.getString("status");
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (backorderedId.length() != 0) {
			orderStatusChange(
					backorderedId.substring(0, backorderedId.length() - 1),
					ConstantMessages.STATUS_BACKORDER);
		}
		return oi;
	}

	/*
	 * private OrderInfo getOrderInfoInCompleteOrder(int orderid) { OrderInfo oi
	 * = null; String sql = "SELECT * FROM ORDERINFO where status = '" +
	 * ConstantMessages.STATUS_COMPLETE + "' orderno = '" + orderid +"'";
	 * Statement stmt; try { stmt = this.connection.createStatement(); ResultSet
	 * rs = stmt.executeQuery(sql); while (rs.next()) { oi = new OrderInfo();
	 * oi.userId = rs.getString("username"); oi.orderId = orderid; oi.status =
	 * rs.getString("status"); String temp = rs.getString("desc"); for (String s
	 * : temp.split(",")) { oi.items.put(s.substring(0, s.indexOf(':') - 1),
	 * Integer.valueOf(s.substring(s.indexOf(':') + 1))); }
	 * 
	 * } } catch (SQLException e) { e.printStackTrace(); } return oi; }
	 */

	public void orderStatusChange(String target, String status) {
		String sql = "UPDATE ORDERINFO SET status = '" + status
				+ "' WHERE orderno in (" + target + ")";
		executeQuery(sql);

	}

	public String checkBackorderd() {
		String sql = "SELECT * FROM ORDERINFO where status ='"
				+ ConstantMessages.STATUS_BACKORDER + "'";
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return "true";
			}
			rs.close();
			stmt.close();
			return "false";
		} catch (SQLException e) {
			e.printStackTrace();
			return "-1";
		}
	}

	public String setManaulControlMode(int wmsId, int robotId, String manualMode) {
		String result = "";
		result = insertRobotMoveOrder(wmsId, robotId, manualMode);
		if (result != "")
			return result;

		String sql = "UPDATE ROBOT SET manualcontrol = '" + manualMode + "'"
				+ "  WHERE warehouseid = '" + wmsId + "' and botid ='"
				+ robotId + "'";
		if (executeQuery(sql) == 1)
			return "";
		else
			return "No Robot data in Database";
	}

	public String findWMSId(String macid) {
		String result = "1";
		String sql = "SELECT warehouseid FROM WAREHOUSE where macaddr ='"
				+ macid + "'";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				result = String.valueOf(rs.getInt("warehouseid"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public void updateWMSControllerStatus(int warehouseId,
			String errorCauseString) {
		String sql = "UPDATE WAREHOUSE SET status='" + errorCauseString
				+ "' WHERE warehouseid = '" + warehouseId + "'";
		executeQuery(sql);
	}

	public void updateRobotErrorStatus(int warehouseId, int robotId,
			String errorCauseString) {

		String status = ConstantMessages.ROBOT_ERROR;
		if (errorCauseString != null && errorCauseString.equals("")){
			String sql = "SELECT * FROM ROBOT where ( warehouseid = '" + warehouseId
					+ "' and botid ='"
					+ robotId + "')"; 
			String path = "";
			Statement stmt;
			try {
				stmt = this.connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					path = rs.getString("path");
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (path != null && !path.equals(""))
			{
				status = ConstantMessages.STATUS_INPROGRESS;
				//path가 ""가 아니면 inprogress
			}
			else //path가 ""이면 idle
				status = ConstantMessages.ROBOT_IDLE;
		}
			
		
		String sql = "UPDATE ROBOT SET status = '" + status
				+ "',  errordetail='" + errorCauseString
				+ "' WHERE warehouseid = '" + warehouseId + "' and botid = '"
				+ robotId + "'";
		executeQuery(sql);

	}

	// dead function
	public void updateRobotLocation(int warehouseId, int robotId, int src,
			int dst) {
		String location = warehouseId + ":" + src + "-" + dst;

		String sql = "UPDATE ROBOT SET location = '" + location
				+ "' WHERE warehouseid = '" + warehouseId + "' and botid = '"
				+ robotId + "'";

		executeQuery(sql);
	}

	public String findRobotId(String macid) {
		String result = "1,1";
		String sql = "SELECT warehouseid,botid FROM ROBOT where macaddr ='"
				+ macid + "'";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				result = String.valueOf(rs.getInt("warehouseid")) + ','
						+ String.valueOf(rs.getInt("botid"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public void updateRobotStatus(int warehouseId, int robotId, String status) {
		String sql = "UPDATE ROBOT SET status = '" + status
				+ "' WHERE warehouseid = '" + warehouseId + "' and botid = '"
				+ robotId + "'";
		executeQuery(sql);
	}

	public int findIdleRobot(int wmsId) {
		String sql = "SELECT * FROM ROBOT where (status = '"
				+ ConstantMessages.ROBOT_IDLE + "' and warehouseid = '" + wmsId
				+ "')";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getInt("botId");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void updateOrderPath(int orderId, String path) {
		String sql = "UPDATE ORDERINFO SET path = '" + path
				+ "' WHERE orderno = '" + orderId + "'";
		// System.out.println(sql);
		executeQuery(sql);
	}

	public void updateRobotPathAndOrderNo(int wmsId, int robotId, String path,
			int orderno) {
		String sql = "UPDATE ROBOT SET path = '" + path + "', orderno ='"
				+ orderno + "' WHERE warehouseid = '" + wmsId
				+ "' and botid ='" + robotId + "'";
		executeQuery(sql);
	}

	public void setShippingCompleteJob(int warehouseId) {
		int orderno = findArrivedOrderNo((int) warehouseId, 0);
		completeAfterRobotInfo(orderno);
		completeAfterOrderInfo(orderno);
	}

	private void completeAfterOrderInfo(int orderno) {
		// orderinfo 에는 completetime넣고 status를 complete로
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String sql = "UPDATE ORDERINFO  SET status = '"
				+ ConstantMessages.STATUS_COMPLETE + "', completedatetime ='"
				+ sdf.format(new Date()) + "' WHERE orderno ='" + orderno + "'";
		executeQuery(sql);
	}

	private void completeAfterRobotInfo(int orderno) {
		// robot db : orderno 없애고, path 없애고, loaded item 없애고, state를 idle로
		String sql = "UPDATE ROBOT SET path = null, orderno = null, loadeditem = null, status ='"
				+ ConstantMessages.ROBOT_IDLE
				+ "' WHERE orderno ='"
				+ orderno
				+ "'";
		executeQuery(sql);
	}

	private int findArrivedOrderNo(int warehouseId, int stationId) {
		return findArrivedOrderNo(warehouseId, stationId, stationId);
	}

	private int findArrivedOrderNo(int warehouseId, int srcstationId,
			int dststationId) {
		String loc = warehouseId + ":" + srcstationId + "-" + dststationId;
		String sql = "SELECT orderno FROM ROBOT where (location = '" + loc
				+ "' and orderno is not null )";
		// System.out.println(sql);

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getInt("orderno");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;
		return -1;
	}

	public boolean setRobotStopLocation(int warehouseId, int i) {
		// i로 오는 로봇을 찾아서 i,j라고 바꾸는 함수
		String loc = warehouseId + ":%-" + i;
		// String loc = "1:1-1";
		String sql = "SELECT * FROM ROBOT where (location like '" + loc + "')";
		int botid = -1;

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				botid = rs.getInt("botid");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (botid == -1){
			System.out.println("ERROR!! : Maybe wrong station was detected at" + loc);
			return false;
		}

		loc = warehouseId + ":" + i + '-' + i;
		sql = "update ROBOT set location = '" + loc + "' where warehouseid ='"
				+ warehouseId + "' and botid = '" + botid + "'";

		System.out.println("Find Robot and chage location to " + loc);
		executeQuery(sql);
		return true;
	}

	public void setLoadingCompleteJob(int warehouseId, int stationId) {
		int orderno = findArrivedOrderNo(warehouseId, stationId);
		String sql = "SELECT path FROM ROBOT where (orderno ='" + orderno
				+ "')";
		String path = "";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				path = rs.getString("path");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;
		String[] detailorder = path.split(",");

		for (String d : detailorder) {
			// int wmsid = Integer.valueOf(d.substring(0, d.indexOf(":") ));
			d = d.substring(d.indexOf(":") + 1);
			int staId = Integer.valueOf(d.substring(0, d.indexOf(":")));
			if (staId == stationId) {
				// order 내용 중에 요 station에서 하는 일이 있을 때
				d = d.substring(d.indexOf(":") + 1);
				// System.out.println(d);
				;
				String itemname = d.substring(0, d.indexOf(":"));
				int count = Integer.valueOf(d.substring(d.indexOf(":") + 1));
				// loaded item에는 d를 추가하고,
				AddLoadedItem(warehouseId, orderno, d);

				// 인벤토리 스테이션에선 item 카운트를 깍아줘야함
				ReduceInventoryCount(warehouseId, stationId, itemname, count);
			}
		}
	}

	private void ReduceInventoryCount(int warehouseId, int stationId,
			String itemname, int count) {

		String targetCol = "";
		int targetCnt = 0;

		String sql = "SELECT * FROM INVENTORYSTATION where (stationid ='"
				+ stationId + "' and warehouseid ='" + warehouseId + "')";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (itemname != null
						&& rs.getString("firstitemname") != null
						&& itemname.equals((String) rs
								.getString("firstitemname"))) {
					targetCol = "firstitemcount";
				} else {
					targetCol = "seconditemcount";
				}
				targetCnt = rs.getInt(targetCol);
				targetCnt = targetCnt - count;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sql = "update INVENTORYSTATION set " + targetCol + " ='" + targetCnt
				+ "' where (stationid ='" + stationId + "' and warehouseid ='"
				+ warehouseId + "')";
		executeQuery(sql);

	}

	private void AddLoadedItem(int warehouseId, int orderno, String d) {
		String sql = "SELECT * FROM ROBOT where (orderno ='" + orderno
				+ "' and warehouseid ='" + warehouseId + "')";
		String loadeditem = "";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				loadeditem = rs.getString("loadeditem");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;

		if (loadeditem == null || loadeditem.equals(""))
			loadeditem = d;
		else
			loadeditem = loadeditem + "," + d;

		sql = "update ROBOT set loadeditem = '" + loadeditem
				+ "' where warehouseid ='" + warehouseId + "' and orderno = '"
				+ orderno + "'";

		executeQuery(sql);
	}

	public boolean findRobotLoadItemRequired(int warehouseId, int stationId) {

		int orderno = findArrivedOrderNo(warehouseId, stationId);
		String sql = "SELECT path FROM ROBOT where (orderno ='" + orderno
				+ "')";
		String path = "";
		
		System.out.println("Find path From bot has orderno :" + orderno);

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				path = rs.getString("path");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("PATH = " + path);
		String[] detailorder = path.split(",");

		for (String d : detailorder) {
			d = d.substring(d.indexOf(":") + 1);
			// System.out.println(d);
			int staId = Integer.valueOf(d.substring(0, d.indexOf(":")));
			if (staId == stationId) {
				System.out.println("Need to Wait");
				return true;
			}
		}
		System.out.println("Just go!");
		return false;
	}

	public RobotMoveInfo getRobotMoveInfo(int warehouseId, String target) {
		String loc = warehouseId + ":" + target + '-' + target;
		String sql = "SELECT * FROM ROBOT where (location = '" + loc + "')";

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				RobotMoveInfo rmi = new RobotMoveInfo();
				rmi.warehouseId = warehouseId;
				rmi.srcStationId = Integer.valueOf(target);
				rmi.dstStationId = (Integer.valueOf(target) + 1) % 4;
				rmi.robotId = rs.getInt("botid");
				rs.close();
				stmt.close();
				return rmi;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void SetRobotMoveInfo(RobotMoveInfo rmi) {
		String loc = rmi.warehouseId + ":" + rmi.srcStationId + '-'
				+ rmi.dstStationId;
		String sql = "update ROBOT set location = '" + loc
				+ "' where warehouseid ='" + rmi.warehouseId
				+ "' and botid = '" + rmi.robotId + "'";
		executeQuery(sql);

	}

}
