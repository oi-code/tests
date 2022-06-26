package ImageConvertor.core;

@SuppressWarnings("serial")
public class PathNotFound extends RuntimeException {

	public PathNotFound() {
		super();
	}

	public PathNotFound(String string) {
		super(string);
	}

}
