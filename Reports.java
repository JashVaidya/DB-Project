import java.sql.*; 

/*
 * Reports: generates rerports
 *
*/

public class Reports
{
	//Trip Detail Jash?
	/**
	 * List each trip planned for the Ski Club (including the name of the resort,  the date and city/state), 
	 * and for each trip list the names of all students attending, their condo assignments (including the 
	 * name of the condo BUT not the unit or the building number), and the total they have each paid for the trip.
	 */
	public void tripDetail(Connection conn) {
		String DBstatus;
		String result = "";
		try {
			
			String query1 = "SELECT t.resort, t.sun_date, t.city, t.state, ca.MID, cr.name, SUM(p.payment)"+
			                "FROM Trip t " +
							"INNER JOIN Condo_Reservation cr ON t.TID = cr.TID " +
							"INNER JOIN Condo_Assign ca ON cr.RID = ca.RID " +
							"INNER JOIN Payment p ON ca.MID = p.MID " +
							"GROUP BY  t.resort, t.sun_date, t.city, t.state, ca.MID, cr.name";
			
			Statement stmt1 = conn.createStatement (); 
			ResultSet rset1 = stmt1.executeQuery(query1);
			while (rset1.next ()) { 
				result += "Resort: " + rset1.getString("resort") + "  Date: " + rset1.getString("sun_date") +
				"  City: " +rset1.getString("city") + "  State: " + rset1.getString("state") + "  Member: " +
						rset1.getString("MID") + "  Payment: $" + rset1.getString("sum(p.payment)");
				result += "\n";
			} 
			System.out.println("-- Trip Detail --\n" + result);
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
	//Condo. detail Jackie
	/**
	 *List each condominum (the name, unit number, building and room), the resort name and trip number, the
	 *count of students in each condo, and the total amount currently paid for each reservation. 
	 */
	public void condoDetail(Connection conn) {
		String DBstatus;
		String result = "";
		try {
			DBstatus = ""; 
			String query1 = "SELECT cr.name, cr.Unit_NO, cr.Bldg, cr.RID, t.Resort, " +
				"cr.TID, COUNT(*), SUM(p.Payment) " +
                "FROM Condo_Reservation cr " + 
				"INNER JOIN Trip t ON t.TID = cr.TID " + 
				"INNER JOIN Condo_Assign ca ON cr.RID = ca.RID " +
				"INNER JOIN Payment p ON ca.MID = p.MID AND ca.RID = p.RID " +
                "GROUP BY cr.name, cr.Unit_NO, cr.Bldg, cr.RID, t.Resort, cr.TID ";
					
			
			Statement stmt1 = conn.createStatement (); 
			ResultSet rset1 = stmt1.executeQuery(query1);
			while (rset1.next ()) 
			{
				result += "Name: " + rset1.getString("name") + "  Unit: " + rset1.getString("Unit_No") +
				"  Bldg: " + rset1.getString("Bldg") + "  RID: " + rset1.getString("RID") + "  Resort: " +
				rset1.getString("Resort") + "  TID: " + rset1.getString("TID") + "  NumStudPerCondo: " +
				rset1.getString("COUNT(*)") + "  TotPaidPerReserv: $" + rset1.getString("SUM(p.Payment)");
				result += "\n";
			} 
			System.out.println("-- Condo Detail --\n" + result);
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

	//Student Detail Ricky
	/**
	 * Build a report for students that lists each trip they are booked for (by resort name, date, and
	 * city/state), the condominium they are staying in, including unit number and building,
	 * the amount they have paid for each trip and 
	 * the amount they owe for each trip
	 */
	 public String studentDetail(Connection conn)
	{
		String result = "";
		try
		{
			String query = "SELECT distinct p.MID, t.resort, t.sun_date, t.city, t.state, cr.name, cr.unit_no, cr.bldg, p.payment " +
			"FROM condo_reservation cr " +
			"INNER JOIN trip t on t.TID = cr.TID " +
			"INNER JOIN payment p on p.RID = cr.RID " +
			"WHERE p.mid IN (SELECT MID from SkiClub) "+
			"ORDER BY p.mid";
			
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next())
			{			
				result += "Student: " + rset.getString("MID")+ " Resort: " + rset.getString("resort") + "  Date: " + rset.getString("sun_date") + "  City: " +
				rset.getString("city") + "  State: " + rset.getString("state") + "  Name: " + rset.getString("name") +
				"  Unit#: " + rset.getString("unit_no") + "  Payment: $" + rset.getString("payment") + "  AmountOwed: $"+
				  (100 - Integer.parseInt(rset.getString("payment")))+ "\n";
			}
			System.out.println("-- Student Detail Report --\n" + result);
			// Release the statement and result set
			stmt.close();
			rset.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return result;
		
	}
 
	//Financial Detail Ricky
	/**
	 * Build a report for the administator that lists each trip by number and resort name, the total 
	 * owed from all students for that trip,  the total paid by all students, and the total 
	 * remaining balance to be collected. 
	 */
	 public String financialDetail(Connection conn)
	 {
		String result = "";
		int totalOwed = 0;
		int totalPaid = 0;
		int totalRemaining = 0;
		int numStudents = 0;
		try
		{
			String query = "SELECT t.TID, t.resort, SUM(p.payment), COUNT(MID) " +
			"FROM condo_reservation cr " +
			"INNER JOIN trip t ON t.TID = cr.TID " +
			"INNER JOIN payment p ON p.RID = cr.RID " +
			"GROUP BY t.TID, t.resort";
			
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);
			
			while(rset.next())
			{			
				result += "TripID: " + rset.getString("TID") + " Resort: " + rset.getString("resort");
				
				totalPaid = Integer.parseInt(rset.getString("SUM(p.payment)"));
				numStudents = Integer.parseInt(rset.getString("COUNT(MID)"));
				totalOwed =  numStudents * 100;
				totalRemaining = totalOwed - totalPaid;
				
				result += "  numStudents: " + numStudents + "  TotalPaid: $" + totalPaid  + "  TotalOwed: $" + totalOwed +
							"  TotalRemaining: $" + totalRemaining + "\n";
			}
			System.out.println("-- Financial Detail Report --\n" + result);
			// Release the statement and result set
			stmt.close();
			rset.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return result;
	 }
}