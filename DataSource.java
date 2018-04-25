import java.sql.*; 

class DataSource 
{
	public  String TID, resort, DBstatus;
	private Connection conn;
	private Statement stmt;
	private int connected;
	private String user = "jvaidya";
	private String pass = "radfordpass123";
	private String query; 
	
	//I got this code from Employee.java from Ms. Ughetta's JDBC stuff
	public void test()
	{
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@Picard2:1521:itec2", user, pass);
			stmt = conn.createStatement();
			
			conn.setAutoCommit(false);
			query = "Select * "
					+ "from Trip ";
					
			System.out.println(query);
	
			ResultSet rset = stmt.executeQuery(query);
			while(rset.next())
			{
				TID = rset.getString("TID");
				resort = rset.getString("Resort");
				DBstatus = "Found";
			}
			conn.close();
		}
		catch(SQLException e)
		{
			DBstatus = "Error";
			System.out.println(e);
		}
	}
}
