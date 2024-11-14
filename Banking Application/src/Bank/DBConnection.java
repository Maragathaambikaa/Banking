package Bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String url="jdbc:mysql://localhost:3306/Bank";
	private static final String user ="root";
	private static final String password="root";
	private static Connection connection;
	
	public static Connection getConnection()throws SQLException
	{
		if(connection==null|| connection.isClosed())
		{
		try
		{
			//Class.forName("com.mysql.cj.jdbc.Driver");
			
			connection=DriverManager.getConnection(url,user,password);
			System.out.println("connected");

		}
		catch(SQLException e)
		{
			System.out.println(" not connected");

		}
		}
		return connection;
	}

}
