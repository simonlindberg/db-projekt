package location;

import java.sql.Date;

public class Delivered implements Location {

	private String customer;
	private Date date;

	public Delivered(final String customer, final Date date) {
		this.customer = customer;
		this.date = date;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getLocationName() {
		return "delivered to "
	+ customer + ", "
				+ date.toString() +
				", at " + date.getHours() + ":" + date.getMinutes() + ".";
	}

	@Override
	public String toString() {
		return getLocationName();
	}

}
