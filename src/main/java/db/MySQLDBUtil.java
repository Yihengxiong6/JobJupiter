package db;

public class MySQLDBUtil {
	/*
	 * This class is used for the info which is needed by the database creation
	 */
	private static final String INSTANCE = "jobproject-db.cncmzpfp8x1p.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "jobrecommend";
	private static final String USERNAME = "yihengx";
	private static final String PASSWORD = "";
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
}
