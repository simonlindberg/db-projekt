package location;

public class NullLocation implements Location {

	@Override
	public String getLocationName() {
		return "no where to be found.";
	}

	@Override
	public String toString() {
		return getLocationName();
	}

}
