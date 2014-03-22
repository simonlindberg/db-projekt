package location;

public class Storage implements Location {

	@Override
	public String getLocationName() {
		return "in storage";
	}

	@Override
	public String toString() {
		return getLocationName();
	}

}
