package Bank;
import java.sql.*;
public class Main3 {  // to check connection we use this code

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String url="jdbc:mysql://localhost:3306/Bank";
			String user ="root";
			String password="root";
			Connection c=DriverManager.getConnection(url,user,password);
			System.out.println("Connected");
			//String query="Select *from account where balance>500";
			
			//String query="update account set username=? where account.id=1";//directly update in database
			//PreparedStatement pst = c.prepareStatement(query);//c-connection's object
			
		/*	String query="insert into account(id,username,pin,accounttype,balance)values(?,?,?,?,?)";
			PreparedStatement pst= c.prepareStatement(query);//c-connection's object
			pst.setInt(1,3);
			pst.setString(2,"xyz");
			pst.setInt(3,3333);
			pst.setString(4,"FDP");
			pst.setInt(5,3000); */
			
			
			
			//?-placeholder
		//	pst.executeUpdate();
			
			/*String query="Select *from account where balance>500";
			PreparedStatement pst= c.prepareStatement(query);
			ResultSet r=pst.executeQuery(); //- when result set comes- use get*/
         
			/*String query="create table customer(id int primary key,name varchar(20))";
			//alter table customer add cloumn age int;
			PreparedStatement pst= c.prepareStatement(query);
			pst.executeUpdate();*/
			
			String query="alter table customer add column age int";
			PreparedStatement pst= c.prepareStatement(query);
			pst.executeUpdate();
			
			
			// stores the data in the tabele- result set gets and stores it
			//r ponits the column
			//to execute preparedstatement
			/*while(r.next())
			{//r.next()-points the first row
			System.out.println(r.getInt("id"));
			System.out.println(r.getString("username"));
			System.out.println(r.getInt("pin"));
			System.out.println(r.getDouble("Balance"));
		}*/
			}
		catch(SQLException e)
		{
			System.out.println("not connected");
		}

	}

}
