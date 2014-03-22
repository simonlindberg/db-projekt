package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private final Connection conn;
	private static Database instance;

	private Database() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost/db");
	}

	public final Database instance() {
		if (instance == null) {
			try {
				instance = new Database();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("No database connection found.");
				System.out.println("Irreversable error, shutting down.");
				System.exit(-1);
			}
		}

		return instance;
	}

	public boolean blockPallet(final String productName, final Date start, final Date end) {
		PreparedStatement stmt = null;
		try {
			stmt = conn
					.prepareStatement("update Pallets"
							+ " set blocked = 1"
							+ " where ? <= productionDate"
							+ " and ? >= productionDate"
							+ " and ? = productName");
			stmt.setDate(1, start);
			stmt.setDate(2, end);
			stmt.setString(3, productName);
			return stmt.executeUpdate() >= 1;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
					+ " where p.PalletID=id");

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
		} finally{
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return new NullLocation();
	}
}
