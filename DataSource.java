import java.sql.*; 

public class DataSource 
{
	public  String name, unit, bldg, resort, RID, TID, DBstatus;
	private Connection conn;
	private Statement stmt;
	private int connected;
	private String user = "jvaidya";
	private String pass = "radfordpass123";
	private String query; 
	private Reports r = new Reports(); 

	//I got this code from Employee.java from Ms. Ughetta's JDBC stuff
	public void test()
	{
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@Picard2:1521:itec2", user, pass);
			// stmt = conn.createStatement();
			
			conn.setAutoCommit(false);
<<<<<<< HEAD
	

			//r.condoDetail(conn);
			// ResultSet rset = stmt.executeQuery(query);
			// while(rset.next())
			// {
			// 	TID = rset.getString("TID");
			// 	resort = rset.getString("Resort");
			// 	DBstatus = "Found";
			// }
=======
			query= "SELECT cr.name, cr.Unit_NO, cr.Bldg, cr.RID,  cr.TID "+
								"FROM Condo_Reservation cr"; 
			/*query = "SELECT cr.name, cr.Unit_NO, cr.Bldg, cr.RID, t.Resort, cr.TID "+
                 "FROM Condo_Reservation cr "+
                 "INNER JOIN Trip t "+
                 "ON t.TID = cr.TID "+  
                 "INNER JOIN Condo_Assign ca"+
                 "ON cr.RID = ca.RID";*/
			System.out.println(query);
			System.out.println("  1");
			ResultSet rset = stmt.executeQuery(query);
			System.out.println("  2");
			while(rset.next())
			{
				System.out.println("  loop");
				name = rset.getString("name");
				unit = rset.getString("Unit_NO");
				bldg = rset.getString("Bldg");
				resort = rset.getString("Resort");
				RID = rset.getString("RID");
				TID = rset.getString("TID");
				DBstatus = "Found";
				System.out.println(name +" "+ unit +" "+ bldg +" "+ resort +" "+ RID+" " +TID);
			}
			System.out.println("  3");
>>>>>>> 9114a3b559e792f2e20fa35cfd17181c08cc61b5
			conn.close();
		}
		catch(SQLException e)
		{
			DBstatus = "Error";
			System.out.println(e);
		}
	}
}
