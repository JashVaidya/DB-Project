/* 
 * Report Driver : only contains main method
 * 4/25/2018
*/

public static void main(String[] args) throws SQLException 
{
	//DataSource d = new DataSource();
	//d.test();
	
	//The code below is from DataSource.java
	 String TID, resort, DBstatus;
	 Connection conn;
	 Statement stmt;
	 int connected;
	 String user = "jvaidya";
	 String pass = "radfordpass123";
	 String query; 
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
 