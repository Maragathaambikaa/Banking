package Bank;
import java.sql.Connection;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

interface TransactionInterface
{
    public String deposit(double amount);             
    //Argument changing continuously - overloading 
    //only body changes, but all other argurments remains same - overriding
    //upcasting - also polymorphism
    
    double getBalance();

	public String withdraw(double amount);           // function overwrite - annotation
}

class Account implements TransactionInterface {
    private String userName, accountType; // camelCase
    private int pin;
    private double balance;//private member accessed using another class 
	private int id;

    public Account(String userName, int pin, String accountType,double balance) { // PascalCase  //contructor
       // this.id=id;
    	this.userName = userName;
        this.pin = pin;
        this.accountType = accountType;
        this.balance = balance;
    }

    public boolean authentication(int inputPin) {
        return this.pin == inputPin;
    }
@Override
    public double getBalance() {
        return this.balance;      //encapsulaton 
}
    public String getUserName() {
        return this.userName;
    }

    public String getAccountType() {
        return this.accountType;
    }
    
    public int getid() {
        return this.id;
    }

    public double getInterest() {
        if (this.accountType.equalsIgnoreCase("savings")) {
            return this.balance * 0.1;
        }
        return this.balance * 0.15;
    }

    public String getDetails() {
        return "User Name: " + this.getUserName() + "\tBalance: " + this.getBalance() + "\tAccount Type: " + this.getAccountType();
    }

    public String deposit(double amount) {
        if (amount < 0) {
            return "Invalid amount";
        }
        this.balance += amount;
        return "Deposited successfully";
    }
   //@override
    public String withdraw(double amount) {
        if (amount < 0) {
            return "Invalid Amount";
        }
        if (amount > this.balance) {
            return "Insufficient funds";
        }
        this.balance -= amount;
        return "Withdrawn successfully";
    }
}

class Banking {
   // private LinkedHashMap<String, Account> accounts = new LinkedHashMap<>();
    private Account user = null;
	public Connection con;
    
    public Banking(Connection con)
    {
    	this.con=con;
    }

    public String createAccount(int id, String userName, int pin, String accountType, double balance) {
    	
    	try
    	{
    		String query="Select count(*) from account where username=?";
    		PreparedStatement pst= con.prepareStatement(query);
    		pst.setInt(1,id);
    		/*pst.setString(2,userName);
    		pst.setInt(3,pin);
    		pst.setString(4,accountType);
    		pst.setDouble(5,balance);*/
    		ResultSet r=pst.executeQuery();
    		r.next();
    		int count=r.getInt(1);
    		if(count!=0)
    		{
    			return("Account not exist");
    		}
    		
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Error occured");
    	}
      //  if (accounts.containsKey(userName)) {
      //      return "Account already exists!";
      //  }

       // Account a = new Account(userName, pin, accountType,balance);
        
        try {
        	//String query ="";
        	String query1="insert into account(id,username,pin,accounttype,balance)values(?,?,?,?,?)";
        	PreparedStatement pst= con.prepareStatement(query1);
        	pst.setInt(1, id);
        	pst.setString(2, userName);
        	pst.setInt(3, pin);
        	pst.setString(4, accountType);
        	pst.setDouble(5, balance);
			pst.executeUpdate();
			
        }catch(SQLException e)
        {
           System.out.println("Error occured");
        }
        //accounts.put(userName, a);
        
        return "Account is successfully created";
    }

    public String login(String username, int pin) {
    	if (user != null)
	    {
	    	return "Multiple account login";
	    }
    	
    	String query="Select pin,accounttype,balance from account where username=?";
    	
    	try {
    		PreparedStatement pst= con.prepareStatement(query);
    		pst.setString(1,username);
    	    ResultSet rs = pst.executeQuery();
    	    
	       if(!rs.next())
	       {
	    	   return"Account not exist";
	       }
	       
	       int ogpin=rs.getInt(1);
	       String atype=rs.getString(2);
	       double bal=rs.getDouble(3);
	       if(ogpin==pin)
	       {
	    	   user= new Account(username,ogpin,atype, bal);
	    	   return "logged in";
	       }
	       }catch(SQLException e)
    	{
    		
    	}
    	return"Wrong pin";
	       
      /*  if (!accounts.containsKey(username)) {
            return "Account doesn't exist";
        }*/
       /* if (accounts.get(username).authentication(pin)) {
            user = accounts.get(username);//checks from the hash map
            return "Logged in....";
        }
        return "Wrong pin";*/
    	
		//return username;
    }

    public String logout() {
        if (user != null) {
        	
            user = null;
            return "Logged out successfully";
        }
        return "Account is not yet logged in";
    }

    public String deposit(double amount) {
    	if(amount<0)
    	{
    		return "invalid amount";
    	}
        if (user != null) {
        	try
        	{
        		String query="Update account set balance=balance+? where username=?";
        		PreparedStatement pst=con.prepareStatement(query);
        		pst.setDouble(1, amount);
        		pst.setString(2, user.getUserName());
        		pst.executeUpdate();
        	}
        	catch(SQLException e)
        	{
        		System.out.println("Error Ocurred");
        	}
        	
        	logTransaction(user.getUserName(),"deposit",amount);
            return user.deposit(amount);
        }
        return "Login First then Try";
    }

 
	public String withdraw(double amount) {
		if(amount<0)
    	{
    		return "invalid amount";
    	}
       
        if (user != null) {
        	if(user.getBalance()<amount)
        	{
        		return "invalid amount";
        	}
        	try
        	{
        		String query="Update account set balance=balance-? where username=?";
        		PreparedStatement pst=con.prepareStatement(query);
        		pst.setDouble(1, amount);
        		pst.setString(2, user.getUserName());
        		pst.executeUpdate();
        	}
        	catch(SQLException e)
        	{
        		System.out.println("Error Ocurred");
        	}
        	logTransaction(user.getUserName(),"withdraw",amount);
            return user.withdraw(amount);
        }
        return"Login First then Try";
    }

    public String checkBalance() {
        if (user != null) {
        	String query="Select balance from account where username=?";
        	try
        	{
        		PreparedStatement pst=con.prepareStatement(query);
        		pst.setString(1, user.getUserName());
        	    ResultSet rs = pst.executeQuery();
        	    rs.next();
        	    return String.valueOf(rs.getDouble(1));
        		
        	}catch(SQLException e)
        	{
        		System.out.println("Error Ocurred");
        	}
        }
        return "Login First then try";
    }
    
    public String Display()
    {
    	try {
    	String query="Select *from account join transaction on account .username=transaction.username";
    	PreparedStatement pst=con.prepareStatement(query);//conert the query into prepared statement
    	ResultSet rs = pst.executeQuery();
    	/*if(rs.next()) {
    		return "No data found";
    	}*/
    	while(rs.next())
    	{
    		System.out.println(rs.getInt("id")+"\t");
			System.out.println(rs.getString("username")+"\t");
			System.out.println(rs.getInt("pin")+"\t");
			System.out.println(rs.getString("accounttype")+"\t");
			System.out.println(rs.getDouble("Balance")+"\t");
    	}
    	}catch(SQLException e)
    	{
    		System.out.println("Error Ocurred");
    	}
    	
    	return "";
    }
    
    public void logTransaction(String username,String trans_type,double amount)
    {
    	double balance=Double.parseDouble(checkBalance());
    	String query="insert into transactions (username,trans_type,amount,balance)values(?,?,?,?)";
    	
    	try {
    		
    	PreparedStatement pst=con.prepareStatement(query);
    	pst.setString(1, username);
    	pst.setString(2, trans_type);
    	pst.setDouble(3, amount);
    	pst.setDouble(4, balance);
        pst.executeUpdate();
    
    	}catch(SQLException e)
    	{
    		
    	}
    }
    
    public void DisplayAllTransaction()
    {
    	String query="Select *from transactions";
    	try
    	{
    		PreparedStatement pst=con.prepareStatement(query);
    		ResultSet rs = pst.executeQuery();
    		while(rs.next())
    		{
    			String result=rs.getInt("trans_id")+"\t"+rs.getString("username")+"\t"+rs.getString("trans_type")+"\t"+rs.getDouble("amount")+"\t"+rs.getDouble("balance")+"\t"+rs.getDate("date")+"\t";
    			System.out.println(result);
    		try(FileWriter f= new FileWriter("TransactionHistory.txt",true);//true to avoid overwrite
    			BufferedWriter bf=new BufferedWriter(f);)	{
    			//manipulate the text as per convienient
    			bf.write(result);
    		}
    		catch(Exception e)
    		{
    			
    		}
    		}
    	}catch(SQLException e)
    	{
    		
    	}
    }
    
    public void DisplayMyTransaction()
    {
    	if(user==null)System.out.println("");
    
    	try
    	{
    		String query ="Select *from transactions where user=?";
    		PreparedStatement pst=con.prepareStatement(query);
    		ResultSet rs = pst.executeQuery();
    		
    		while(rs.next())
    		{
    			String result=rs.getInt("trans_id")+"\t"+rs.getString("username")+"\t"+rs.getString("trans_type")+"\t"+rs.getDouble("amount")+"\t"+rs.getDouble("balance")+"\t"+rs.getDate("date")+"\t";
        		try(FileWriter f= new FileWriter("TransactionHistory.txt",true);//true to avoid overwrite
            			BufferedWriter bf=new BufferedWriter(f);)	{
            			//manipulate the text as per convienient
            			bf.write(result);
            		}
            		catch(Exception e)
            		{
            			
            		}
    			/*System.out.println(rs.getInt("trans_id")+"\t");
    			System.out.println(rs.getString("username")+"\t");
    			System.out.println(rs.getString("trans_type")+"\t");
    			System.out.println(rs.getDouble("amount")+"\t");
    			System.out.println(rs.getDouble("balance")+"\t");
    			System.out.println(rs.getDate("date")+"\t");*/
    		}
    	}
    	catch(SQLException e)
    	{
    		
    	}
    }

    
}


public class Bank {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
        Connection con=DBConnection.getConnection();
       
        Banking bank = new Banking(con);
        boolean run = true;

        while (run) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Logout");
            System.out.println("4. Deposit");
            System.out.println("5. Withdraw");
            System.out.println("6. Check Balance");
            System.out.println("7. Display Accounts");
          //  System.out.println("8. logTransaction");
            System.out.println("8. DisplayAllTransaction()");
            System.out.println("9.DisplayMyTransaction()");
            System.out.println("10. View Account types");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter the id,Name, Pin, Account Type,balance:");
                    int id = sc.nextInt();
                    String username = sc.next();
                    int pin = sc.nextInt();
                    String accountType = sc.next();
                    double balance = sc.nextDouble(); 
                    System.out.println(bank.createAccount(id,username, pin, accountType,balance));
                    break;

                case 2:
                    System.out.println("Enter login credentials:");
                    System.out.println("Username:");
                    username = sc.next();
                    System.out.println("Pin:");
                    int loginPin = sc.nextInt();
                    System.out.println(bank.login(username, loginPin));
                    break;

                case 3:
                    System.out.println(bank.logout());
                    break;

                case 4:
                    System.out.println("Enter amount to be deposited:");
                    double amount = sc.nextDouble();
                    System.out.println(bank.deposit(amount));
                    break;

                case 5:
                    System.out.println("Enter amount to withdraw:");
                    double withdrawAmount = sc.nextDouble();
                    System.out.println(bank.withdraw(withdrawAmount));
                    break;

                case 6:
                    System.out.println(bank.checkBalance());
                    break;
                    
                case 7:
                	System.out.println("Displaying the details:");
                	System.out.println(bank.Display());
                	break;
                	
                
                case 8:
                	System.out.println("Displaying all transactions:");
                	bank.DisplayAllTransaction();
                	break;
                	
                case 9:
                	System.out.println("Displaying My transactions:");
                	bank.DisplayMyTransaction();
                	break;
                	
                case 10:
                    System.out.println("Showing the details");
                    System.out.println("1.Savings 2.FDP 3.Current 4.Student ");
                    break;
                    

                case 11:
                    run = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice.. Choose Again");
            }
          
        }
        }
        catch(SQLException e) {
		
			System.out.println("not connected");
    }
        sc.close();
    }
}
// logical error handlesd in exception 
// arrayindexout of bound - java gives this as exception .. in c it gives as garbage value

// exception handling keywords - try , catch , finally, throw,throws