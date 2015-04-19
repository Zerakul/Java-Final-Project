import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private String file = "database.txt";
	private Connection con = null;
	private Statement st = null;
	private String url = "jdbc:mysql://localhost/";
	private String db = "";
	private String driver = "com.mysql.jdbc.Driver";
	private ResultSet  rs;

	String[] Fnames = {"Alex","Judy","Joe","Francis","Ray","Jacob","John","George","Frank","Jean","Josh",
			"Joy","Toni","Patrick","Rick"};
	String[] Lnames = {"Franklin","Smith","Yao","Goldman","Templeton","Bedat","Woo","Chang","Chin",
			"Stevenson","Heintz","Jones","Kennedy","Peterson","Stoneman"};
	String[] city = {"Atlanta","Austin","Indianapolis","Springfield","Sacramento"};
	String[] street = {"Washington Street","Bay Street","West Ford Street","Main Street","Franklin Street"};
	String[] dept = {"Computer Science","Mathematics","Accounting"};

	String pictureDefault = "img/default-profile.gif";

	public Database() {
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url+db,"scott","tiger");
				st = con.createStatement();
			}
			catch(Exception e) {  }
	}

	public String createDatabase() {
		executeFromFile(file);

		for(int i=0;i<dept.length;i++)
			updatePlace(dept[i]);

		return "Database created";
	}

	@SuppressWarnings("resource")
	private void executeFromFile(String f) {
		try {
			FileInputStream fstream = new FileInputStream(f);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)
			{ 
				if (strLine != null && !strLine.equals("")) {
					st.execute(strLine);  
				}
			}

		} 
		catch(Exception e) { System.out.println(e); }
	}

	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {  }
	}

	public String insert(String pic,int sid , String firstName, String lastName,
			String city, String street, int houseNum, int zipCode, String birthDate,
			int startTime, String dept, int numOfCredits, float avg, int fail, String gen) {
		/** birthDate form: year-month-day */

		String str = "";
		try {
			// check if student exists
			rs = st.executeQuery("SELECT count(sid) FROM student WHERE sid = "+sid);
			if(rs.next())
				if(rs.getString(1).equals("1")) {
					return "Student already exists.";
				}

			str = "INSERT INTO Student VALUES (";
			if(!pic.equals("null")) 
				str+="'"+pic+"'";
			if(str.charAt(str.length()-1)!='(')
				str+=", ";
			str+=sid+", '"+firstName+"', '"+lastName+"', '"+city+"', '"+street+"', "+houseNum;
			if(zipCode>0)
				str+=", "+zipCode;
			str+=", '"+birthDate+"', "+startTime+", '"+dept+"', "+numOfCredits;
			if(avg>=0)
				str+=", "+avg;
			str+=", "+fail+", 'no rank'";
			if(!gen.equals("null"))
				str+=", '"+gen+"'";
			str += ")";
			st.execute(str);

			if(avg>=0)
				updatePlace(dept);

		}			
		catch (SQLException e) {
			e.printStackTrace();
			return "wrong info. can't add student.\n"+str;			
		}
		return "Insert succeeded";
	}

	private void updatePlace(String dept) {
		String s="";
		try {
			// find median in department
			String str = "SELECT numOfCredits FROM "
					+"(SELECT s1.firstName,s1.numOfCredits,COUNT(s1.numOfCredits) rank FROM Student s1, Student s2 "
					+"WHERE (s1.numOfCredits < s2.numOfCredits OR (s1.numOfCredits = s2.numOfCredits "
					+"AND s1.firstName <= s2.firstName)) AND s1.dept = '"+dept+"' AND s2.dept = '"+dept+"' "
					+"GROUP BY s1.firstName, s1.numOfCredits ORDER BY s1.numOfCredits DESC) s3 "
					+"WHERE rank = (SELECT (COUNT(*)+1) DIV 2 "
					+"FROM Student WHERE dept = '"+dept+"') OR CASE WHEN (SELECT COUNT(*) FROM Student "
					+"WHERE dept = '"+dept+"') % 2 = 0 "
					+"THEN rank = (SELECT ((COUNT(*)+1) DIV 2)+1 FROM Student WHERE dept = '"+dept+"') END;";
			float median = 0;
			int c = 0;
			rs = st.executeQuery(str);
			while(rs.next()) {
				median += Integer.parseInt(rs.getString(1));
				c++;
			}
			if(c == 2) median = median / 2;

			// update department's places	
			rs = st.executeQuery("SELECT sid, avg FROM Student WHERE dept = '"+dept+"' AND numOfCredits >= "+median
					+" ORDER BY avg DESC");
			float avg = -1;
			int place = 1;
			while(rs.next()) {
				if(avg==-1) {
					s += "UPDATE Student SET place = '"+place+"' WHERE sid = "+rs.getString(1)+"; ";
					avg = rs.getFloat(2);
				}
				else {
					float temp = Float.parseFloat(rs.getString(2));
					if(temp<avg)
						place++;
					s += "UPDATE Student SET place = '"+place+"' WHERE sid = "+rs.getString(1)+"; ";
				}
			}
			if(s.length()!=0) {
				String[] split = s.split(";");
				for(int i=0;i<split.length-1;i++) {
					st.execute(split[i]);
				}	
			}
		}
		catch(Exception e) { System.out.println(e.getMessage()); }
	}

	public String delete(int sid) {
		try{
			rs = st.executeQuery("SELECT dept FROM Student WHERE sid = "+sid);

			String dept="";
			if(rs.next())
				dept = rs.getString(1);
			st.execute("DELETE FROM Student WHERE sid = "+sid);
			updatePlace(dept);
			return "Delete student succeeded";
		}
		catch(Exception e) { 
			try {
				st.execute("DELETE FROM Student WHERE sid = "+sid);
				return "Delete student succeeded";
			} catch (SQLException e1) {
				return e.getMessage();
			}		
		}
	}

	public String update(int sid, String column, String newData) {
		try {
			rs = st.executeQuery("SELECT count(sid) FROM student WHERE sid = "+sid);
			if(rs.next())
				if(rs.getString(1).equals("0")) {
					return "Student no longer exists.";
				}

			//startTime,numOfCredits,fail - int ; avg - float ; rest has ''
			if(column.equals("startTime") || column.equals("fail")
					|| column.equals("numOfCredits")||column.equals("sid"))
				Integer.parseInt(newData);
			else if(column.equals("avg"))
				Float.parseFloat(newData);
			else
				newData = "'"+newData+"'";

			rs = st.executeQuery("SELECT dept FROM Student WHERE sid = "+sid);

			String dept1="",dept2="";
			if(rs.next())
				dept1 = rs.getString(1);

			st.execute("UPDATE Student SET "+column+" = "+newData+" WHERE sid = "+sid);

			rs = st.executeQuery("SELECT dept FROM Student WHERE sid = "+sid);
			if(rs.next())
				dept2 = rs.getString(1);

			updatePlace(dept1);
			if(dept1!=dept2)
				updatePlace(dept2);

			return "Update succeeded";
		} 
		catch(SQLException e) { return e.getMessage(); }
		catch(NumberFormatException e) { return "data doesn't fit column"; }		
	}

	public String deleteColumn(String column) {
		try {
			if(column.equals("pic")||column.equals("avg")||column.equals("zipCode")||column.equals("gender")) {
				st.execute("ALTER TABLE Student DROP COLUMN "+column);

				if(column.equals("avg"))
					st.execute("UPDATE Student SET place = 'no rank'");
				return "Delete column succeeded";
			}
			else
				return "Can't delete "+column;
		} catch (SQLException e) { return e.getMessage(); }
	}

	public String addColumn(String column, String type, String def, String addition) {
		try {
			st.execute("ALTER TABLE student ADD "+column+" "+type+" DEFAULT "+def+" "+addition);
			for(String d : dept)
				updatePlace(d);
			return "Add column succeeded";
		} catch (SQLException e) {
			return e.getMessage();
		}
	}

	public String getMetaData() {
		String str = "";
		String sql="SELECT * FROM Student";
		try {
			rs = st.executeQuery(sql);
			int n = rs.getMetaData().getColumnCount();
			for(int i=1;i<=n;i++)
				str += rs.getMetaData().getColumnLabel(i)+";";
		}
		catch(SQLException e) { System.out.println(e.getMessage()); }
		return str;
	}

	public String select(String sql) {
		String str = "";
		try {		
			rs = st.executeQuery(sql);
			int n = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				for(int i=1;i<=n;i++)
					str += rs.getString(i)+";";
				str += "\n";
			}
			
			return str;
		} catch (SQLException e) { return "Failed"; }	
	}

	public String getValues() {
		String str = "";
		for(int i=0;i<Fnames.length;i++)
			str += Fnames[i]+";";
		str += "\n";

		for(int i=0;i<Lnames.length;i++)
			str += Lnames[i]+";";
		str += "\n";

		for(int i=0;i<city.length;i++)
			str += city[i]+";";
		str += "\n";

		for(int i=0;i<street.length;i++)
			str += street[i]+";";
		str += "\n";

		for(int i=0;i<dept.length;i++)
			str += dept[i]+";";
		str += "\n";

		return str;
	}
}
