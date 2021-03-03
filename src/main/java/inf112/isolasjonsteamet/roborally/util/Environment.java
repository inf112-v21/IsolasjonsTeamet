package inf112.isolasjonsteamet.roborally.util;

/**
 * Provide information about the environment we are currently in.
 */
public class Environment {

	public static final boolean IS_PROD = "true".equals(System.getProperty("roborally.is-prod"));
	public static final boolean IS_DEV = !IS_PROD;
}
