import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class Student {
	// sid;firstName;lastName;city;street;houseNum;zipCode;birthDate;startTime;dept;numOfCredits;avg;fail;place;

	private SimpleIntegerProperty sid;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private SimpleStringProperty city;
	private SimpleStringProperty street;
	private SimpleIntegerProperty houseNum;
	private SimpleIntegerProperty zipCode;
	private SimpleObjectProperty<Date> birthDate;
	private SimpleIntegerProperty startTime;
	private SimpleStringProperty dept;
	private SimpleStringProperty gender;
	private SimpleIntegerProperty numOfCredits;
	private SimpleFloatProperty avg;
	private SimpleIntegerProperty fail;
	private SimpleStringProperty place;
	private Image pic;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Student(SimpleStringProperty image, SimpleIntegerProperty sid, SimpleStringProperty firstName,
			SimpleStringProperty lastName, SimpleStringProperty city,
			SimpleStringProperty street, SimpleIntegerProperty houseNum,
			SimpleIntegerProperty zipCode, SimpleStringProperty birthDate,
			SimpleIntegerProperty startTime, SimpleStringProperty dept,
			SimpleIntegerProperty numOfCredits, SimpleFloatProperty avg,
			SimpleIntegerProperty fail, SimpleStringProperty place,SimpleStringProperty gender) {
		super();
		
		if(image.get()!=null) {
			String str = image.get();
			if(str.charAt(0)=='C')
				str = "file:/"+str;
			str = str.replace('$', '/');
			this.pic = new Image(str,50,50,true,true);
		}
		
		this.sid = sid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.street = street;
		this.houseNum = houseNum;
		this.zipCode = zipCode;
		try {
			this.birthDate = new SimpleObjectProperty(
					new SimpleDateFormat("yyyy-MM-dd").parse(birthDate.get()));
		} catch (ParseException e) {
			System.out.println("problem with given date\n"+e.getMessage());
		}
		this.startTime = startTime;
		this.dept = dept;
		this.numOfCredits = numOfCredits;
		this.avg = avg;
		this.fail = fail;
		this.place = place;
		this.gender = gender;
	}
	public int getSid() {
		return sid.get();
	}
	public void setSid(SimpleIntegerProperty sid) {
		this.sid = sid;
	}
	public String getFirstName() {
		return firstName.get();
	}
	public void setFirstName(SimpleStringProperty firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName.get();
	}
	public void setLastName(SimpleStringProperty lastName) {
		this.lastName = lastName;
	}
	public String getCity() {
		return city.get();
	}
	public void setCity(SimpleStringProperty city) {
		this.city = city;
	}
	public String getStreet() {
		return street.get();
	}
	public void setStreet(SimpleStringProperty street) {
		this.street = street;
	}
	public Integer getHouseNum() {
		return houseNum.get();
	}
	public void setHouseNum(SimpleIntegerProperty houseNum) {
		this.houseNum = houseNum;
	}
	public Integer getZipCode() {
		return zipCode.get();
	}
	public void setZipCode(SimpleIntegerProperty zipCode) {
		this.zipCode = zipCode;
	}
	public Date getBirthDate() {
		return birthDate.get();
	}
	public void setBirthDate(SimpleStringProperty birthDate) {
		try {
			this.birthDate.set(new SimpleDateFormat("dd/MM/yyyy").parse(birthDate.get()));
		} catch (ParseException e) {
			System.out.println("problem with given date");
		}
	}
	public Integer getStartTime() {
		return startTime.get();
	}
	public void setStartTime(SimpleIntegerProperty startTime) {
		this.startTime = startTime;
	}
	public String getDept() {
		return dept.get();
	}
	public void setDept(SimpleStringProperty dept) {
		this.dept = dept;
	}
	public String getGender() {
		return gender.get();
	}
	public void setGender(SimpleStringProperty gender) {
		this.gender = gender;
	}
	public Integer getNumOfCredits() {
		return numOfCredits.get();
	}
	public void setNumOfCredits(SimpleIntegerProperty numOfCredits) {
		this.numOfCredits = numOfCredits;
	}
	public Float getAvg() {
		return avg.get();
	}
	public void setAvg(SimpleFloatProperty avg) {
		this.avg = avg;
	}
	public Integer getFail() {
		return fail.get();
	}
	public void setFail(SimpleIntegerProperty fail) {
		this.fail = fail;
	}
	public String getPlace() {
		return place.get();
	}
	public void setPlace(SimpleStringProperty place) {
		this.place = place;
	}
	
	public void setPic(Image img) {	
		this.pic = img;
	}
	
	public Image getPic(){
	    return pic; 
	}
	
}
