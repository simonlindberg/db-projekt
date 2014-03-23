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

public class Database {

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

	public int blockPallet(final String productName, final Date start, final Date end) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("update Pallets"
					+ " set blocked = 1"
					+ " where ? <= productionDate"
					+ " and ? >= productionDate"
					+ " and ? = productName");
			stmt.setDate(1, start);
			stmt.setDate(2, end);
			stmt.setString(3, productName);
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

	public Location searchPalletLocation(final int PalletID) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement("select s.PalletID, d.deliveryDate, o.Customer"
					+ " from Pallets p"
					+ " left join Storage s on s.PalletID = p.PalletID"
					+ " left join PalletDelivery d on d.PalletID = p.PalletID"
					+ " left join Orders o on o.OrderID = d.OrderID"
					+ " where p.PalletID=?");

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
			stmt = conn.prepareStatement("update Ingredients i"
					+ " inner join RecipeIngredients r"
					+ " on i.Ingredient = r.Ingredient"
					+ " set i.Quantity = i.Quantity - r.Quantity * 54"
					+ " where r.ProductName = ?");

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

		return products.toArray(new String[products.size()]);
	}

}
