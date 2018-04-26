import java.sql.*; 

/*
 * Reports: generates rerports
 *
*/

public class Reports
{
	//Trip Detail
	/**
	 * List each trip planned for the Ski Club (including the name of the resort,  the date and city/state), 
	 * and for each trip list the names of all students attending, their condo assignments (including the 
	 * name of the condo BUT not the unit or the building number), and the total they have each paid for the trip.
	 */

	public void tripDetail(Connection conn) {
		String DBstatus;
		try {
			
			String query1 = "";
			System.out.println(query1);
			
			Statement stmt1 = conn.createStatement (); 
			ResultSet rset1 = stmt1.executeQuery(query1);
			while (rset1.next ()) { 
				System.out.println(rset1.getString("name") + "  " + rset1.getString("Unit_No"));
			} 
	
			// Release the statement and result set
			stmt1.close();
			rset1.close(); 
		} 
		catch(SQLException e)
		{
			DBstatus = "Error";
			System.out.println(e);
		}
			
	}
	/**
	 *List each condominum (the name, unit number, building and room), the resort name and trip number, the
	 *count of students in each condo, and the total amount currently paid for each reservation. 
	 */
	public void condoDetail(Connection conn) {
		try {
			DBstatus = ""; 
			//String query1 = "SELECT cr.name, cr.Unit_NO, cr.Bldg, cr.RID FROM Condo_Reservation cr"; 
			String query1 = "SELECT cr.name, cr.Unit_NO, cr.Bldg, cr.RID, t.Resort, cr.TID, COUNT(*), SUM(p.Payment) " +
                "FROM Condo_Reservation cr "+
                "INNER JOIN Trip t "+
                "ON t.tid = cr.tid "+
                "INNER JOIN Condo_Assign ca "+
                "ON cr.rid = ca.rid " +
				"INNER JOIN Payment p on ca.MID = p.mid and ca.rid = p.rid " +
                "GROUP BY cr.name, cr.Unit_NO, cr.Bldg, cr.RID, t.Resort, cr.TID ";
					
			System.out.println(query1);
			
			Statement stmt1 = conn.createStatement (); 
			ResultSet rset1 = stmt1.executeQuery(query1);
			while (rset1.next ()) 
			{
				System.out.println(rset1.getString("name") + "  " + rset1.getString("Unit_No"));
			} 
	
			// Release the statement and result set
			stmt1.close();
			rset1.close(); 
		} 
		catch(SQLException e)
		{
			DBstatus = "Error";
			System.out.println(e);
		}
			
	}

	//Student Detail
	/**
	 * Build a report for students that lists each trip they are booked for (by resort name, date, and
	 * city/state), the condominium they are staying in, including unit number and building, the 
	 * amount they have paid for each trip and the amount they owe for each trip.
	 */
	 public String studentDetailQuery(Connection conn)
	{
		String result = "";
		try
		{
			String query = "select t.resort, t.sun_date, t.city," + " t.state, cr.name, cr.unit_no, cr.bldg,p.payment " +
			"from condo_reservation cr " +
			"inner join trip t on t.tid = cr.tid " +
			"inner join payment p on p.rid = cr.rid " +
			"where p.mid in (select mid from skiclub)";
			System.out.println(query);
			
			Statement stmt = conn.createStatement();
			System.out.println(query);
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next())
			{			
				result += "Resort: " + rset.getString("resort") + " Date: " + rset.getString("sun_date") + " City: " + rset.getString("city") + " State: " + rset.getString("state") + " Name: " + rset.getString("name") + " Unit#: " + rset.getString("unit_no") + " Payment: " + rset.getString("payment") + "\n";
			}
		}
		catch(Exception e)
		{
			System.out.println("Broken");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
		
	}
 
	//Financial Detail
	/**
	 * Build a report for the administator that lists each trip by number and resort name, the total 
	 * owed from all students for that trip,  the total paid by all students, and the total 
	 * remaining balance to be collected. 
	 */
}