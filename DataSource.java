import java.sql.*; 

public class DataSource 
{
	public  String name, unit, bldg, resort, RID, TID, DBstatus;
	private Connection conn;
	private Statement stmt;
	private int connected;
	private String user = "jvaidya";
	private String pass = "radfordpass123";
	//I got this code from Employee.java from Ms. Ughetta's JDBC stuff
	public void test()
	{
		try
		{

			Reports r = new Reports(); 

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@Picard2:1521:itec2", user, pass);

			conn.setAutoCommit(false);

			r.tripDetail(conn);
			r.condoDetail(conn);
			r.studentDetailQuery(conn);
			r.financialDetailQuery(conn);
		
			conn.close();
		}
		catch(SQLException e)
		{
			DBstatus = "Error";
			System.out.println(e);
		}
	}
}
