package fr.labri.progress.comet.conf;

/**
 * a singleton that contains input from CLI
 * 
 * @author nherbaut
 *
 */
public final class CliConfSingleton {
	
	public static String TranscodageFile = System.getenv().get("TRANSCOD_PARAM_FILE");
	public static String rabbitHost;
	public static String rabbitUser;
	public static String rabbitPassword;
	public static Integer rabbitPort;
	public static String streamerBaseURL;
	public static String externalAddr;
	public static Integer frontendPort;
	public static String storageHostname = System.getenv().get("STORAGE_HOSTNAME");
	public static String frontendHostname= System.getenv().get("FRONTEND_HOSTNAME");
	
	public static String swiftUrl = "http://10.10.1.107:8080";
	public static String swiftPathAuth = "/auth/v1.0";
	public static String swiftSharedKey = "azerty";
	public static String swiftLogin= "admin:admin";
	public static String swiftPassword="admin";

}
