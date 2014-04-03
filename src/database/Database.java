package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import location.Delivered;
import location.Location;
import location.NullLocation;
import location.Storage;

public final class Database {

	private final Connection conn;
	private static Database instance;

	private Database() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost/db");
	}

	public final static Database instance() {
		if (instance == null) {
			try {
				instance = new Database();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				System.err.println("No database connection found.");
				System.err.println("Irreversable error, shutting down.");
				System.exit(-1);
			}
		}

		return instance;
	}

	/**
	 * Blocks pallets within two dates and with the specific product.
	 * 
	 * @param productName
	 * @param start
	 * @param end
	 * @return the number of pallets blocked. -1 if and error occur.
	 */
	public int blockPallet(final String productName, final Date start, final Date end) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("update Pallets" + " set blocked = 1" + " where ? <= productionDate" + " and ? >= productionDate"
					+ " and ? = productName");
			stmt.setDate(1, start);
			stmt.setDate(2, end);
			stmt.setString(3, productName);
			System.out.println(stmt.toString());
			return stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * gives the location of a pallet with a specific palletID.
	 * 
	 * @param PalletID
	 * @return a location, never null.
	 */
	public Location searchPalletLocation(final int PalletID) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("select s.PalletID, d.deliveryDate, o.Customer" + " from Pallets p"
					+ " left join Storage s on s.PalletID = p.PalletID" + " left join PalletDeliveries d on d.PalletID = p.PalletID"
					+ " left join Orders o on o.OrderID = d.OrderID" + " where p.PalletID=?");

			stmt.setInt(1, PalletID);
			final ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				if (rs.getInt(1) != 0) {
					return new Storage();
				} else {
					return new Delivered(rs.getString(3), rs.getDate(2));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new NullLocation();
	}

	/**
	 * Introduces a newly produced pallet to the database. It is added to
	 * storage and the ingridient inventory is updated.
	 * 
	 * @param id
	 * @param productName
	 * @return
	 */
	public boolean palletProduced(final int id, final String productName) {
		try {
			conn.setAutoCommit(false);
			if (!(addPallet(id, productName) && toStorage(id) && updateInventory(productName))) {
				conn.rollback();
				conn.setAutoCommit(true);
				return false;
			}

			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean addPallet(final int id, final String productName) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("insert into Pallets (PalletID, ProductName) values (?,?);");

			stmt.setInt(1, id);
			stmt.setString(2, productName);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updateInventory(final String productName) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("update Ingredients i" + " inner join RecipeIngredients r" + " on i.Ingredient = r.Ingredient"
					+ " set i.Quantity = i.Quantity - r.Quantity * 54" + " where r.ProductName = ?");

			stmt.setString(1, productName);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean toStorage(final int id) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("insert into Storage (PalletID) values (?);");

			stmt.setInt(1, id);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Fetches all the recipes from the database.
	 * 
	 * @return
	 */
	public String[] getRecipes() {
		String selectTableSQL = "select ProductName from Recipes;";
		Statement statement = null;

		List<String> products = new ArrayList<String>();

		try {
			statement = conn.createStatement();
			final ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				products.add(rs.getString("ProductName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return products.toArray(new String[0]);
	}

	public String searchPallet(int id, boolean blocked, Date from, Date to, String selectedItem, boolean selected) {
		String query = "select p.Blocked, p.ProductName, p.ProductionDate, s.PalletID, d.deliveryDate, o.Customer" + " from Pallets p"
				+ " left join Storage s on s.PalletID = p.PalletID" + " left join PalletDeliveries d on d.PalletID = p.PalletID"
				+ " left join Orders o on o.OrderID = d.OrderID";
		
		String where = "";
		if (id > 0) {
			where += "s.PalletID = " + id + " and ";
		}
		if (blocked) {
			where += "p.Blocked = 1 and ";
		}
		if (from != null && to != null) {
			where += "p.ProductionDate >= '" + from + "' and p.Productiondate <= '" + to + "' and ";
		}
		if (!selectedItem.equals("All products")) {
			where += "p.ProductName = '" + selectedItem + "' and ";
		}
		if (selected) {
			where += "d.deliveryDate IS NOT NULL and ";
		}

		if (!where.equals("")) {
			where = where.substring(0, where.length() - 4);
			query += " WHERE " + where;
		}

		System.out.println(query);

		final StringBuilder sb = new StringBuilder();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				sb.append("Pallet ID: " + rs.getInt(4) + "\n");
				sb.append("  Blocked: " + rs.getBoolean(1) + "\n");
				sb.append("  Product name: " + rs.getString(2) + "\n");
				sb.append("  Production date: " + rs.getDate(3) + "\n");

				if (rs.getDate(5) == null) {
					sb.append("  In storage.\n");
				} else {
					sb.append("  Delivery date: " + rs.getDate(5) + "\n");
					sb.append("  To customer: " + rs.getString(6) + "\n");
				}

				System.out.println("Pallet ID: " + rs.getInt(4));
				System.out.println("Blocked: " + rs.getBoolean(1));
				System.out.println("Product name: " + rs.getString(2));
				System.out.println("Production date: " + rs.getDate(3));
				System.out.println("Delivery date: " + rs.getDate(5));
				System.out.println("Customer: " + rs.getString(6));
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR!";
		}
	}

}
