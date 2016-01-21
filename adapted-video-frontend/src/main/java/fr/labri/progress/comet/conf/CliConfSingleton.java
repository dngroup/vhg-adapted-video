package fr.labri.progress.comet.conf;

/**
 * a singleton that contains input from CLI
 * 
 * @author nherbaut
 *
 */
public final class CliConfSingleton {
	public static String rabbitHost;
	public static String rabbitUser;
	public static String rabbitPassword;
	public static Integer rabbitPort;
	public static String streamerBaseURL;
	public static String externalAddr;
	public static Integer frontendPort;
	public static String storageHostname = System.getenv().get("STORAGE_HOSTNAME");
	public static String frontendHostname= System.getenv().get("FRONTEND_HOSTNAME");

}
