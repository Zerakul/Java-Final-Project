import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.*;

public class Menu extends Pane {
	private final String[] Fnames = {"Alex","Judy","Joe","Francis","Ray","Jacob","John","George","Frank",
			"Jean","Josh","Joy","Toni","Patrick","Rick"};
	private final String[] Lnames = {"Franklin","Smith","Yao","Goldman","Templeton","Bedat","Woo","Chang",
			"Chin","Stevenson","Heintz","Jones","Kennedy","Peterson","Stoneman"};
	private final String[] City = {"Atlanta","Austin","Indianapolis","Springfield","Sacramento"};
	private final String[] Street = {"Washington Street","Bay Street","West Ford Street","Main Street",
	"Franklin Street"};
	private final String[] Dept = {"Computer Science","Mathematics","Accounting"};

	private String[] student;
	private ObservableList<Student> data;
	private File file;
	private boolean isOpen = true;
	private String newStudentImage;

	private ArrayList<ComboBox<String>> compCombox=new ArrayList<ComboBox<String>>();
	private ArrayList<ComboBox<String>> data1Combox=new ArrayList<ComboBox<String>>();
	private ArrayList<ComboBox<String>> data2Combox=new ArrayList<ComboBox<String>>();
	private DatePicker date1;
	private DatePicker date2;

	private ObservableList<Integer> SID = FXCollections.observableArrayList();
	private ObservableList<String> FirstNames = FXCollections.observableArrayList();
	private ObservableList<String> LastNames = FXCollections.observableArrayList();
	private ObservableList<String> Cities = FXCollections.observableArrayList();
	private ObservableList<String> StreetValues = FXCollections.observableArrayList();
	private ObservableList<Integer> HouseNums = FXCollections.observableArrayList();
	private ObservableList<Integer> ZipCodes = FXCollections.observableArrayList();
	private ObservableList<Integer> StartTimes = FXCollections.observableArrayList();
	private ObservableList<String> DeptValues = FXCollections.observableArrayList();
	private ObservableList<Integer> NumCredits = FXCollections.observableArrayList();
	private ObservableList<Float> AVG = FXCollections.observableArrayList();
	private ObservableList<Integer> Fails = FXCollections.observableArrayList();
	private ObservableList<String> Places = FXCollections.observableArrayList();

	private ObservableList<String> compList = FXCollections.observableArrayList();
	private ObservableList<String> addcList = FXCollections.observableArrayList();;
	private ObservableList<String> delcList = FXCollections.observableArrayList();;

	// Adding new Student
	private ImageView imageChooser;
	private NumberPicker sidPicker = new NumberPicker(Integer.class,1);
	private ComboBox<String> fNameCombo = new ComboBox<String>();
	private ComboBox<String> lNameCombo = new ComboBox<String>();
	private ComboBox<String> cityCombo = new ComboBox<String>();
	private ComboBox<String> streetCombo = new ComboBox<String>();
	private NumberPicker houseNumPicker = new NumberPicker(Integer.class,1,1000);
	private NumberPicker zipPicker = new NumberPicker(Integer.class,10000,99999);
	private DatePicker birthDatePicker;
	private NumberPicker startTimePicker = new NumberPicker(Integer.class,1000,
			Calendar.getInstance().get(Calendar.YEAR));
	private ComboBox<String> deptCombo = new ComboBox<String>();	
	private NumberPicker numCreditsPicker = new NumberPicker(Integer.class,0,160);
	private NumberPicker avgPicker = new NumberPicker(Float.class,0,100);
	private NumberPicker failPicker = new NumberPicker(Integer.class,0,8);
	private ComboBox<String> genderCombo = new ComboBox<String>();
	private Label errorLabel = new Label();
	private String idExistsError = "Student ID already exists";
	private String missingOrWrongInfoError = "Missing information/Value incorrect";
	private MaterialDesignButton addStudent;

	private ArrayList<String> update = new ArrayList<String>();
	private BorderPane pane;
	private BorderPane addStudentPane;
	private VBox bottomfull = new VBox();
	private TableView<Student> tableView;
	private String[] metaArray;

	private ComboBox<String> addcol;
	private ComboBox<String> delcol;
	private int compareValue = -1;

	public Menu(String[] metaArray, String[] dataArray,
			MaterialDesignButton run,MaterialDesignButton reset,MaterialDesignButton del,
			MaterialDesignButton delc,MaterialDesignButton addStudent,MaterialDesignButton addc,
			boolean isServerRunning) {

		this.metaArray = metaArray;
		this.addStudent = addStudent;

		if(isServerRunning) {
			if(getIndex(metaArray,"gender")!=-1)
				delcList.add("Gender");
			else
				addcList.add("Gender");
			if(getIndex(metaArray,"pic")!=-1)
				delcList.add("Picture");
			else
				addcList.add("Picture");
			if(getIndex(metaArray,"zipCode")!=-1)
				delcList.add("ZipCode");
			else
				addcList.add("ZipCode");
			if(getIndex(metaArray,"avg")!=-1)
				delcList.add("Average");
			else
				addcList.add("Average");
		}

		FirstNames.addAll(Fnames);
		LastNames.addAll(Lnames);
		Cities.addAll(City);
		StreetValues.addAll(Street);
		DeptValues.addAll(Dept);
		for(int i=0;i<101;i++)
			AVG.add((float) i);
		for(int i=0;i<161;i++)
			NumCredits.add(i);
		for(int i=0;i<9;i++)
			Fails.add(i);		

		addcol = new ComboBox<String>(addcList);
		delcol = new ComboBox<String>(delcList);

		compList.addAll("", ">", "<", ">=", "<=", "=", "BTW");
		if(isServerRunning)
			for(int i=0;i<metaArray.length;i++){
				ComboBox<String> temp1 = null;
				ComboBox<String> temp2 = null;
				ComboBox<String> comp = null;
				if(!metaArray[i].equals("pic")&&!metaArray[i].equals("birthDate")) {
					temp1 = new ComboBox<String>();
					temp2 = new ComboBox<String>();
					comp = new ComboBox<String>(compList);
				}
				if(metaArray[i].equals("birthDate"))
					comp = new ComboBox<String>(compList);
				compCombox.add(comp);
				data1Combox.add(temp1);
				data2Combox.add(temp2);
			}
		pane = new BorderPane();

		tableView = new TableView<Student>();
		if(isServerRunning){
			try {
				tableView.getColumns().removeAll(tableView.getColumns());
			} catch (Exception e) {
			}

			data = FXCollections.observableArrayList();

			for (int i = 0; i < dataArray.length; i++) {
				student = dataArray[i].split(";");
				if(dataArray[i].equals(""))
					break;

				data.add(createStudent());
			}
			tableView.setItems(data);

			for (int i = 0; i < metaArray.length; i++) {
				tableView = createTable(tableView, metaArray[i]);
				tableView.setEditable(true);
			}	
			sortComboLists();
		}
		else {
			Label close = new Label("Please close window and open the server again");
			close.setStyle("-fx-font-size: 18; -fx-text-fill: red");
			tableView.setPlaceholder(close);
		}

		pane.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		pane.setId("pane");

		pane.setCenter(tableView);

		MaterialDesignButton add = new MaterialDesignButton("Add ro.");
		add.setOnAction(e-> {
			if(addStudentPane.isVisible()) {
				bottomfull.getChildren().removeAll(addStudentPane);
				addStudentPane.setVisible(false);
			}
			else {
				addStudentPane = createBottomPane();
				bottomfull.getChildren().add(addStudentPane);
				addStudentPane.setVisible(true);
			}
		});

		if(!isServerRunning) {
			reset.setDisable(true);
			run.setDisable(true);
			del.setDisable(true);
			delc.setDisable(true);
			add.setDisable(true);
			addc.setDisable(true);
			delcol.setDisable(true);
			addcol.setDisable(true);
		}				
		else {
			addStudentPane = createBottomPane();
			addStudentPane.setVisible(false);
		}
		bottomfull.getChildren().add(createButtonPane(isServerRunning,reset,run,del,delc,add,addc));
		pane.setBottom(bottomfull);
		pane.setStyle("-fx-background-color:#c0c0c0");
	}

	private int getIndex(String[] array, String item) {
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(item))
				return i;
		return -1;
	}

	private Student createStudent() {
		SimpleStringProperty v1 = new SimpleStringProperty();
		SimpleIntegerProperty v2 = new SimpleIntegerProperty();
		SimpleStringProperty v3 = new SimpleStringProperty();
		SimpleStringProperty v4 = new SimpleStringProperty();
		SimpleStringProperty v5 = new SimpleStringProperty();
		SimpleStringProperty v6 = new SimpleStringProperty();
		SimpleIntegerProperty v7 = new SimpleIntegerProperty();
		SimpleIntegerProperty v8 = new SimpleIntegerProperty();
		SimpleStringProperty v9 = new SimpleStringProperty();
		SimpleIntegerProperty v10 = new SimpleIntegerProperty();
		SimpleStringProperty v11 = new SimpleStringProperty();
		SimpleIntegerProperty v12 = new SimpleIntegerProperty();
		SimpleFloatProperty v13 = new SimpleFloatProperty();
		SimpleIntegerProperty v14 = new SimpleIntegerProperty();
		SimpleStringProperty v15 = new SimpleStringProperty();
		SimpleStringProperty v16 = new SimpleStringProperty();

		int count = 0;
		if (getIndex(metaArray, "pic")!=-1) {
			v1.set(student[count]);
			count++;
		}
		v2.set(Integer.parseInt(student[count]));
		SID.add(Integer.parseInt(student[count]));
		count++;
		v3.set(student[count]);
		count++;
		v4.set(student[count]);
		count++;
		v5.set(student[count]);
		count++;
		v6.set(student[count]);
		count++;
		v7.set(Integer.parseInt(student[count]));
		if(!HouseNums.contains(Integer.parseInt(student[count])))
			HouseNums.add(Integer.parseInt(student[count]));
		count++;
		if (getIndex(metaArray, "zipCode")!=-1) {
			v8.set(Integer.parseInt(student[count]));
			if(!ZipCodes.contains(Integer.parseInt(student[count])))
				ZipCodes.add(Integer.parseInt(student[count]));
			count++;
		}
		v9.set(student[count]);
		count++;
		v10.set(Integer.parseInt(student[count]));
		if(!StartTimes.contains(Integer.parseInt(student[count])))
			StartTimes.add(Integer.parseInt(student[count]));
		count++;
		v11.set(student[count]);
		count++;
		v12.set(Integer.parseInt(student[count]));
		count++;
		if (getIndex(metaArray, "avg")!=-1) {
			v13.set(Float.parseFloat(student[count]));
			count++;
		}
		v14.set(Integer.parseInt(student[count]));
		count++;
		v15.set(student[count]);
		if(!Places.contains(student[count]))
			Places.add(student[count]);
		count++;
		if (getIndex(metaArray, "gender")!=-1) {
			v16.set(student[count]);
			count++;
		}

		return new Student(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11,
				v12, v13, v14, v15, v16);
	}

	@SuppressWarnings("unchecked")
	private TableView<Student> createTable(TableView<Student> tableView,String string) {
		switch (string) {
		case "pic": 
			TableColumn<Student, Image> pic = 
			createTableColumn("image", string, getIndex(metaArray, string), 0, true, false, false,null);
			//pic.setStyle("-fx-background-color: skyblue");
			pic.setCellFactory(new Callback<TableColumn<Student,Image>, TableCell<Student,Image>>() {

				@Override
				public TableCell<Student,Image> call(
						TableColumn<Student,Image> param) {

					return new TableCell<Student,Image>(){
						VBox vb;
						ImageView imgv;
						{
							vb=new VBox();
							imgv=new ImageView();
							vb.setAlignment(Pos.CENTER);
							imgv.setFitHeight(50);
							imgv.setFitWidth(50);
							vb.getChildren().add(imgv);
							this.setGraphic(vb);
						}
						@Override
						public void updateItem(Image item, boolean empty) {
							if(item!=null) {
								this.setStyle("-fx-background-color:#DDDDDD");
								imgv.setImage(item);
							}							
						}					
					};
				}			
			});

			// File Chooser
			FileChooser fch = new FileChooser();
			fch.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("JPG", "*.jpg"),
					new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
					new FileChooser.ExtensionFilter("GIF", "*.gif"));

			pic.setOnEditStart((CellEditEvent<Student, Image> t) -> {
				int sid = ((Student) t.getTableView().getItems()
						.get(t.getTablePosition().getRow())).getSid();

				file = (File) fch.showOpenDialog(null);	
				String path = "";
				try {
					path = file.getAbsolutePath();
				} catch(Exception e) {
					isOpen = false;
				}

				if(isOpen) {
					path=path.replace('\\', '$');
					path=path.replaceAll(" ", "%20");

					if(t.getOldValue()!=t.getNewValue()) {
						((Student) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setPic(new Image("file:/"+path, 50, 50, true, true));

						update.add(sid + ";pic;" + "file:/"+path);
					}
				}
			});

			tableView.getColumns().add(pic);
			break;
		case "sid": 
			tableView.getColumns().add(
					createTableColumn("int", string, getIndex(metaArray, string), 200, false, true, false,SID));
			break;
		case "firstName":
			TableColumn<Student, String> firstName = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,FirstNames);

			firstName.setCellFactory(ComboBoxTableCell.forTableColumn(FirstNames));

			firstName.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setFirstName(new SimpleStringProperty(t
									.getNewValue()));
					update.add(sid + ";firstName;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(firstName);
			break;
		case "lastName": 
			TableColumn<Student, String> lastName = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,LastNames);

			lastName.setCellFactory(ComboBoxTableCell.forTableColumn(LastNames));

			lastName.setOnEditCommit((CellEditEvent<Student, String> t) -> {

				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setLastName(new SimpleStringProperty(t.getNewValue()));
					update.add(sid + ";lastName;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(lastName);
			break;
		case "city": 
			TableColumn<Student, String> city = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,Cities);

			city.setCellFactory(ComboBoxTableCell.forTableColumn(Cities));

			city.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setCity(new SimpleStringProperty(t.getNewValue()));
					update.add(sid + ";city;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(city);
			break;
		case "street": 
			TableColumn<Student, String> street = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,StreetValues);

			street.setCellFactory(ComboBoxTableCell.forTableColumn(StreetValues));

			street.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setStreet(new SimpleStringProperty(t
									.getNewValue()));
					update.add(sid + ";street;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(street);
			break;
		case "houseNum": 
			TableColumn<Student, Integer> houseNum = 
			createTableColumn("int", string, getIndex(metaArray, string), 200, true, true, false,HouseNums);

			houseNum.setCellFactory(new Callback<TableColumn<Student,Integer>,
					TableCell<Student,Integer>>() {

				public TableCell<Student, Integer> call(TableColumn<Student, Integer> param) {
					NumberPickerCell<Student, Integer> np = new NumberPickerCell<Student, Integer>(Integer.class,1,1000);
					return np;
				}
			});

			houseNum.setOnEditCommit((CellEditEvent<Student, Integer> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setHouseNum(new SimpleIntegerProperty(t.getNewValue()));
					update.add(sid + ";houseNum;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(houseNum);
			break;
		case "zipCode": 
			TableColumn<Student, Integer> zipCode = 
			createTableColumn("int", string, getIndex(metaArray, string), 200, true, true, false,ZipCodes);

			zipCode.setCellFactory(new Callback<TableColumn<Student,Integer>,
					TableCell<Student,Integer>>() {

				public TableCell<Student, Integer> call(TableColumn<Student, Integer> param) {
					NumberPickerCell<Student, Integer> np = new NumberPickerCell<Student, Integer>(
							Integer.class,10000,99999);
					return np;
				}
			});

			zipCode.setOnEditCommit((CellEditEvent<Student, Integer> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setZipCode(new SimpleIntegerProperty(t.getNewValue()));
					update.add(sid + ";zipCode;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(zipCode);
			break;
		case "birthDate": 
			TableColumn<Student, String> birthDate = 
			createTableColumn("date", string, getIndex(metaArray, string), 200, true, false, true,null);

			birthDate.setCellFactory(new Callback<TableColumn<Student,String>,
					TableCell<Student,String>>() {

				public TableCell<Student, String> call(TableColumn<Student, String> param) {
					@SuppressWarnings({ "rawtypes" })
					DatePickerCell datePick = new DatePickerCell();
					return datePick;
				}
			});

			birthDate.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setBirthDate(new SimpleStringProperty(new SimpleDateFormat("dd/MM/yyyy").
									format(t.getNewValue())));
					update.add(sid + ";birthDate;" +
							new SimpleDateFormat("yyyy-MM-dd").format(t.getNewValue()));
				}
			});

			tableView.getColumns().add(birthDate);
			break;
		case "startTime":
			TableColumn<Student, Integer> start = 
			createTableColumn("int", string, getIndex(metaArray, string), 200, true, true, false,StartTimes);

			start.setCellFactory(new Callback<TableColumn<Student,Integer>,
					TableCell<Student,Integer>>() {

				public TableCell<Student, Integer> call(TableColumn<Student, Integer> param) {
					NumberPickerCell<Student, Integer> np = new NumberPickerCell<Student, Integer>(
							Integer.class,1000,Calendar.getInstance().get(Calendar.YEAR));
					return np;
				}
			});

			start.setOnEditCommit((CellEditEvent<Student, Integer> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setStartTime(new SimpleIntegerProperty(t.getNewValue()));
					update.add(sid + ";startTime;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(start);	
			break;
		case "dept": 
			TableColumn<Student, String> dept = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,DeptValues);

			dept.setCellFactory(ComboBoxTableCell.forTableColumn(DeptValues));

			dept.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setDept(new SimpleStringProperty(t
									.getNewValue()));
					update.add(sid + ";dept;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(dept);
			break;
		case "numOfCredits": 
			TableColumn<Student, Integer> credits = 
			createTableColumn("int", string, getIndex(metaArray, string), 200, true, true, false,NumCredits);

			credits.setCellFactory(new Callback<TableColumn<Student,Integer>,
					TableCell<Student,Integer>>() {

				public TableCell<Student, Integer> call(TableColumn<Student, Integer> param) {
					NumberPickerCell<Student, Integer> np = new NumberPickerCell<Student, Integer>(
							Integer.class,0,160);
					return np;
				}
			});

			credits.setOnEditCommit((CellEditEvent<Student, Integer> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setNumOfCredits(new SimpleIntegerProperty(t.getNewValue()));
					update.add(sid + ";numOfCredits;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(credits);		
			break;
		case "avg": 
			TableColumn<Student, Float> avg = 
			createTableColumn("float", string, getIndex(metaArray, string), 200, true, true, false,AVG);

			avg.setCellFactory(new Callback<TableColumn<Student,Float>,
					TableCell<Student,Float>>() {

				public TableCell<Student, Float> call(TableColumn<Student, Float> param) {
					NumberPickerCell<Student, Float> np = new NumberPickerCell<Student, Float>(
							Float.class,0,100);
					return np;
				}
			});

			avg.setOnEditCommit((CellEditEvent<Student, Float> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setAvg(new SimpleFloatProperty(t.getNewValue()));
					update.add(sid + ";avg;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(avg);	
			break;
		case "fail": 
			TableColumn<Student, Integer> fail = 
			createTableColumn("int", string, getIndex(metaArray, string), 200, true, true, false,Fails);

			fail.setCellFactory(new Callback<TableColumn<Student,Integer>,
					TableCell<Student,Integer>>() {

				public TableCell<Student, Integer> call(TableColumn<Student, Integer> param) {
					NumberPickerCell<Student, Integer> np = new NumberPickerCell<Student, Integer>(
							Integer.class,0,8);
					return np;
				}
			});

			fail.setOnEditCommit((CellEditEvent<Student, Integer> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setFail(new SimpleIntegerProperty(t.getNewValue()));
					update.add(sid + ";fail;" + t.getNewValue());
				}
			});

			tableView.getColumns().add(fail);	
			break;
		case "place": 
			TableColumn<Student, String> rank = 
			createTableColumn("String", string, getIndex(metaArray, string), 200, false, true, false,Places);

			rank.setComparator((s1,s2)-> {
				int n1 = 0,n2 = 0;			
				try {
					n1 = Integer.parseInt(s1);
					n2 = Integer.parseInt(s2);
					if(n1>n2) return 1;
					else if(n1<n2) return -1;
					else return 0;
				} catch(Exception e) {  
					if(rank.getSortType() == SortType.ASCENDING)	{
						if(s1.equals("no rank"))
							return 1;
						else if(s2.equals("no rank"))
							return -1;
						else
							return 0;
					}
					else {
						if(s1.equals("no rank"))
							return -1;
						else if(s2.equals("no rank"))
							return 1;
						else
							return 0;
					}
				}
			});

			tableView.getColumns().add(rank);	
			break;
		case "gender": 
			TableColumn<Student, String> gender = createTableColumn("String", string, getIndex(metaArray, string), 200, true, true, false,
					FXCollections.observableArrayList("Male","Female"));
			gender.setCellFactory(ComboBoxTableCell.forTableColumn(
					FXCollections.observableArrayList("Male","Female")));

			gender.setOnEditCommit((CellEditEvent<Student, String> t) -> {
				if(t.getOldValue()!=t.getNewValue()) {
					int sid = ((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow())).getSid();
					((Student) t.getTableView().getItems()
							.get(t.getTablePosition().getRow()))
							.setGender(new SimpleStringProperty(t
									.getNewValue()));
					update.add(sid + ";gender;" + t.getNewValue());
				}
			});
			tableView.getColumns().add(gender);		
			break;
		default: {}
		break;
		}

		return tableView;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableColumn createTableColumn(String type, String column,int index,double width,
			boolean isEditable,boolean drawCombo,boolean drawPicker,ObservableList comboList) {
		TableColumn t;

		switch(type) {
		case "int":
			t = new TableColumn<Student,Integer>();
			t.setCellValueFactory(new PropertyValueFactory<Student, Integer>(column));
			break;
		case "image":
			t = new TableColumn<Student,Image>(checkMetaData(column));
			t.setCellValueFactory(new PropertyValueFactory<Student, Image>(column));
			break;
		case "float":
			t = new TableColumn<Student,Float>();
			t.setCellValueFactory(new PropertyValueFactory<Student, Float>(column));
			break;
		case "date":
			t = new TableColumn<Student,Float>();
			t.setCellValueFactory(new PropertyValueFactory<Student, Date>(column));
			break;
		default:
			t = new TableColumn<Student,String>();
			t.setCellValueFactory(new PropertyValueFactory<Student, String>(column));
		}
		t.setStyle("-fx-alignment: CENTER;");

		if(drawCombo||drawPicker) {
			VBox container = new VBox();
			FlowPane fpane = new FlowPane();
			fpane.setHgap(2);
			fpane.setStyle("-fx-alignment: CENTER;");
			container.setPadding(new Insets(4,0,4,0));
			container.setSpacing(3);
			container.setStyle("-fx-alignment: CENTER;");
			container.setMinWidth(width-34);

			compCombox.get(index).setMinWidth(width-34);

			if(drawPicker) {
				date1 = createDatePicker();
				date2 = createDatePicker();

				fpane.getChildren().addAll(date1);

				date1.setMaxWidth(width-34);
				date1.setMinWidth(width-34);
				date2.setMaxWidth(0.5*width-17);				
				date2.setMinWidth(0.5*width-17);

				compCombox.get(index).setOnAction(e->{
					if(compCombox.get(index).getValue().equals("BTW")) {
						date1.setMaxWidth(0.5*width-17);
						date1.setMinWidth(0.5*width-17);
						fpane.getChildren().addAll(date2);
					}
					else {
						date1.setMaxWidth(width-34);
						date1.setMinWidth(width-34);
						fpane.getChildren().removeAll(date2);
					}
				});
			}

			else {				
				fpane.getChildren().addAll(data1Combox.get(index));

				data1Combox.get(index).setMaxWidth(width-34);
				data1Combox.get(index).setMinWidth(width-34);
				data2Combox.get(index).setMaxWidth(0.5*width-17);
				data2Combox.get(index).setMinWidth(0.5*width-17);

				data1Combox.get(index).setItems(comboList);
				data2Combox.get(index).setItems(comboList);

				compCombox.get(index).setOnAction(e->{
					if(compCombox.get(index).getValue().equals("BTW")) {
						data1Combox.get(index).setMaxWidth(0.5*width-17);
						data1Combox.get(index).setMinWidth(0.5*width-17);
						fpane.getChildren().addAll(data2Combox.get(index));
					}
					else {
						data1Combox.get(index).setMaxWidth(width-34);
						data1Combox.get(index).setMinWidth(width-34);
						fpane.getChildren().removeAll(data2Combox.get(index));
					}
				});
			}
			container.getChildren().addAll(new Label(checkMetaData(column)),
					compCombox.get(index),fpane);
			compCombox.get(index).setMaxWidth(75);

			t.setGraphic(container);
			t.setMinWidth(width);
			t.setMaxWidth(width);
		}

		t.setEditable(isEditable);

		return t;
	}

	private void sortComboLists() {
		Collections.sort(SID);
		Collections.sort(FirstNames);
		Collections.sort(LastNames);
		Collections.sort(Cities);
		Collections.sort(StreetValues);
		Collections.sort(HouseNums);
		Collections.sort(ZipCodes);
		Collections.sort(StartTimes);
		Collections.sort(DeptValues);
		Collections.sort(NumCredits);
		Collections.sort(AVG);
		Collections.sort(Fails);
		Collections.sort(Places);
	}

	private String checkMetaData(String string) {

		String retu;
		switch (string) {
		case "sid":
			retu = "ID";
			break;
		case "firstName":
			retu = "First Name";
			break;
		case "lastName":
			retu = "Last Name";
			break;
		case "city":
			retu = "City";
			break;
		case "street":
			retu = "Street";
			break;
		case "houseNum":
			retu = "House Num";
			break;
		case "zipCode":
			retu = "ZipCode";
			break;
		case "birthDate":
			retu = "BirthDate";
			break;
		case "startTime":
			retu = "Start Year";
			break;
		case "dept":
			retu = "Department";
			break;
		case "numOfCredits":
			retu = "Points";
			break;
		case "avg":
			retu = "Average";
			break;
		case "fail":
			retu = "Fails";
			break;
		case "place":
			retu = "Rank";
			break;
		case "pic":
			retu = "Picture";
			break;
		case "gender":
			retu = "Gender";
			break;
		default:
			retu = "None";
			break;
		}
		return retu;
	}

	private FlowPane createButtonPane(boolean isServerRunning,MaterialDesignButton reset,
			MaterialDesignButton run,MaterialDesignButton del,MaterialDesignButton delc,
			MaterialDesignButton add,MaterialDesignButton addc) {
		Label srvStatus=new Label();

		String textStyle = "-fx-text-fill: #FFFFFF;-fx-font-size: 16";
		double w = 130;
		double h = 45;

		Label serverStatus=new Label("Server Status: ");
		serverStatus.setStyle("-fx-text-fill: #020824;-fx-font-size: 16");
		serverStatus.setEffect(new Reflection());
		srvStatus.setStyle("-fx-font-weight: bold");
		srvStatus.setEffect(new Reflection());
		if(isServerRunning){
			Image servs= new Image("img/database-up-icon-md.png", 40, 40, true, true);
			ImageView servStat = new ImageView(servs);
			servStat.setEffect(new Reflection());
			srvStatus.setText("UP!");
			srvStatus.setGraphic(servStat);
			srvStatus.setTextFill(Paint.valueOf("GREEN"));
		}
		else
		{
			Image servs= new Image("img/database-down-icon-md.png", 40, 40, true, true);
			ImageView servStat = new ImageView(servs);
			servStat.setEffect(new Reflection());
			srvStatus.setGraphic(servStat);
			srvStatus.setText("DOWN!");
			srvStatus.setTextFill(Paint.valueOf("RED"));
		}

		FlowPane flow = new FlowPane();
		flow.maxWidthProperty().bind(pane.widthProperty());
		flow.setMinHeight(85);

		Image res= new Image("img/mssql_refresh_128.png", 30, 30, true, true);
		ImageView resetDB = new ImageView(res);
		reset.setGraphic(resetDB);
		reset.setMinWidth(w);
		reset.setMaxWidth(w);
		reset.setMinHeight(h);
		reset.setMaxHeight(h);
		reset.setStyle(textStyle);
		reset.setEffect(new Reflection());

		Image runQ= new Image("img/ExecuteSQL.png", 25, 25, true, true);
		ImageView runQuery = new ImageView(runQ);
		run.setGraphic(runQuery);
		run.setMinWidth(w);
		run.setMaxWidth(w);
		run.setMinHeight(h);
		run.setMaxHeight(h);
		run.setStyle(textStyle);
		run.setEffect(new Reflection());

		Image delr= new Image("img/row_delete.png", 30, 30, true, true);
		ImageView delrow = new ImageView(delr);

		del.setGraphic(delrow);
		del.setMinWidth(w);
		del.setMaxWidth(w);
		del.setMinHeight(h);
		del.setMaxHeight(h);
		del.setStyle(textStyle);
		del.setEffect(new Reflection());

		Image addR= new Image("img/row_add.png", 30, 30, true, true);
		ImageView addRow = new ImageView(addR);
		add.setGraphic(addRow);
		add.setMinWidth(w);
		add.setMaxWidth(w);
		add.setMinHeight(h);
		add.setMaxHeight(h);
		add.setStyle(textStyle);
		add.setEffect(new Reflection());

		Image delC= new Image("img/column_delete.png", 30, 30, true, true);
		ImageView delCol = new ImageView(delC);
		delc.setGraphic(delCol);
		delc.setMinWidth(w);
		delc.setMaxWidth(w);
		delc.setMinHeight(h);
		delc.setMaxHeight(h);
		delc.setStyle(textStyle);
		delc.setEffect(new Reflection());

		Image addC= new Image("img/column_add.png", 30, 30, true, true);
		ImageView addCol = new ImageView(addC);
		addc.setGraphic(addCol);
		addc.setMinWidth(w);
		addc.setMaxWidth(w);
		addc.setMinHeight(h);
		addc.setMaxHeight(h);
		addc.setStyle(textStyle);
		addc.setEffect(new Reflection());

		delcol.setMaxWidth(w);
		delcol.setMinWidth(w);
		delcol.setPromptText("Choose co.");
		delcol.setMinHeight(h);
		delcol.setTooltip(new Tooltip("Column to delete"));
		delcol.setEffect(new Reflection());

		addcol.setMaxWidth(w);
		addcol.setMinWidth(w);
		addcol.setPromptText("Choose co.");
		addcol.setMinHeight(h);
		addcol.setTooltip(new Tooltip("Column to add"));
		addcol.setEffect(new Reflection());

		FlowPane combos = new FlowPane();
		combos.setHgap(5);
		combos.getChildren().addAll(delcol, delc, addcol, addc);
		combos.setPadding(new Insets(0,0,0,15));
		combos.setMinWidth(550);

		flow.setHgap(5);
		flow.setPadding(new Insets(5));

		flow.getChildren().addAll(serverStatus, srvStatus, reset, run, del, add, 
				combos);
		flow.setStyle("-fx-alignment: CENTER;");

		return flow;
	}

	private BorderPane createBottomPane() {
		FlowPane studentFlow = new FlowPane();
		studentFlow.setHgap(5);
		newStudentImage = "img/default-profile.gif";
		double w = 95;

		if(getIndex(metaArray, "pic")!=-1) {
			imageChooser = new ImageView(new Image(newStudentImage,35,35,true,true));

			FileChooser fch = new FileChooser();
			fch.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("JPG", "*.jpg"),
					new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
					new FileChooser.ExtensionFilter("GIF", "*.gif"));

			imageChooser.setOnMouseClicked(e-> {
				file = (File) fch.showOpenDialog(null);	
				String path = "";
				try {
					path = file.getAbsolutePath();
				} catch(Exception ex) {
					isOpen = false;
				}

				if(isOpen) {
					path=path.replace('\\', '$');
					path=path.replaceAll(" ", "%20");

					newStudentImage = path;

					if(path.charAt(0)=='C')
						path = "file:/"+path;
					path = path.replace('$', '/');
					imageChooser.setImage(new Image(path,35,35,true,true));
				}
			});

			Pane con = new Pane();
			con.setMinHeight(40);
			con.setMaxHeight(40);
			con.getChildren().add(imageChooser);
			imageChooser.fitHeightProperty().bind(con.heightProperty().multiply(0.9));
			studentFlow.getChildren().add(con);
		}
		sidPicker.setFieldWidth(w);
		sidPicker.getTextField().setPromptText("ID");

		fNameCombo = new ComboBox<String>(FirstNames);
		fNameCombo.setMaxWidth(w);
		fNameCombo.setPromptText("FirstName");
		lNameCombo = new ComboBox<String>(LastNames);
		lNameCombo.setMaxWidth(w);
		lNameCombo.setPromptText("LastName");
		cityCombo = new ComboBox<String>(Cities);
		cityCombo.setMaxWidth(w);
		cityCombo.setPromptText("City");
		streetCombo = new ComboBox<String>(StreetValues);
		streetCombo.setMaxWidth(w);
		streetCombo.setPromptText("Street");

		houseNumPicker.setFieldWidth(w);
		houseNumPicker.getTextField().setPromptText("House Num");
		studentFlow.getChildren().addAll(sidPicker,fNameCombo,lNameCombo,cityCombo,streetCombo,houseNumPicker);

		if(getIndex(metaArray, "zipCode")!=-1) {
			zipPicker.setFieldWidth(w);
			zipPicker.getTextField().setPromptText("ZipCode");
			studentFlow.getChildren().add(zipPicker);
		}
		birthDatePicker = createDatePicker();
		birthDatePicker.setMaxWidth(w);
		birthDatePicker.setPromptText("BirthDate");
		studentFlow.getChildren().add(birthDatePicker);

		startTimePicker.setFieldWidth(w);
		startTimePicker.getTextField().setPromptText("Start Year");

		deptCombo = new ComboBox<String>(DeptValues);
		deptCombo.setMaxWidth(w);
		deptCombo.setPromptText("Dept");

		numCreditsPicker.setFieldWidth(w);
		numCreditsPicker.getTextField().setPromptText("Points");

		studentFlow.getChildren().addAll(startTimePicker,deptCombo,numCreditsPicker);

		if(getIndex(metaArray, "avg")!=-1) {
			avgPicker.setFieldWidth(w);
			avgPicker.getTextField().setPromptText("Average");
			studentFlow.getChildren().add(avgPicker);
		}

		failPicker.setFieldWidth(w);
		failPicker.getTextField().setPromptText("Fails");
		studentFlow.getChildren().add(failPicker);

		if(getIndex(metaArray, "gender")!=-1) {
			genderCombo = new ComboBox<String>(FXCollections.observableArrayList("Male","Female"));
			genderCombo.setMaxWidth(w);
			genderCombo.setPromptText("Gender");
			studentFlow.getChildren().add(genderCombo);
		}

		errorLabel.setTextFill(Paint.valueOf("RED"));
		errorLabel.setText(missingOrWrongInfoError);
		errorLabel.setVisible(false);
		studentFlow.getChildren().add(errorLabel);

		BorderPane bottomPane = new BorderPane();
		bottomPane.setCenter(studentFlow);

		FlowPane fp = new FlowPane();
		fp.setAlignment(Pos.CENTER);
		fp.getChildren().add(addStudent);
		addStudent.setStyle("-fx-text-fill: #FFFFFF;-fx-font-size: 16;");
		fp.setMaxWidth(220);
		fp.setMinWidth(220);
		fp.setStyle("-fx-background-color:#a5a5a5;-fx-background-radius: 30;");
		bottomPane.setRight(fp);
		bottomPane.setPadding(new Insets(5));
		bottomPane.setVisible(false);
		bottomPane.setMinWidth(40);
		return bottomPane;
	}

	private DatePicker createDatePicker() {
		DatePicker picker = new DatePicker();
		picker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = 
					DateTimeFormatter.ofPattern("yyyy-MM-dd");
			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}
			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		return picker;
	}

	public BorderPane getPane() {
		return pane;
	}

	public ArrayList<String> getUpdateList() {
		return update;
	}

	public void resetUpdateList() {
		update = new ArrayList<String>();
	}

	private String updateBottom(String errorType,Node missing) {
		errorLabel.setText(errorType);
		errorLabel.setVisible(true);

		sidPicker.setStyle("-fx-border-color: transparent;");
		fNameCombo.setStyle("-fx-border-color: transparent;");
		lNameCombo.setStyle("-fx-border-color: transparent;");
		cityCombo.setStyle("-fx-border-color: transparent;");
		streetCombo.setStyle("-fx-border-color: transparent;");
		houseNumPicker.setStyle("-fx-border-color: transparent;");
		zipPicker.setStyle("-fx-border-color: transparent;");
		birthDatePicker.setStyle("-fx-border-color: transparent;");
		startTimePicker.setStyle("-fx-border-color: transparent;");
		deptCombo.setStyle("-fx-border-color: transparent;");
		numCreditsPicker.setStyle("-fx-border-color: transparent;");
		avgPicker.setStyle("-fx-border-color: transparent;");
		failPicker.setStyle("-fx-border-color: transparent;");

		if(missing!=null)
			missing.setStyle("-fx-border-color: #FF0000;-fx-border-width: 2px;");
		return "";
	}

	public int getRowID() {
		Student s = tableView.getSelectionModel().getSelectedItem();
		if(s!=null)
			return s.getSid();
		else return -1;
	}

	public String getColumn(int state) {
		String s;
		switch(state) {
		case 0:
			s = delcol.getSelectionModel().getSelectedItem();
			if(s==null) 
				return "";
			return s;
		case 1:
			s = addcol.getSelectionModel().getSelectedItem();
			if(s==null) 
				return "";	
			return s;
		}

		return "";
	}

	public String getStudent() {
		if(sidPicker.getNumber()!=null) {
			int sid = (int) sidPicker.getNumber();
			if(SID.contains(sid)) {
				return(updateBottom(idExistsError, sidPicker));
			}
		}
		else
			return(updateBottom(missingOrWrongInfoError, sidPicker));

		String temp,s = "";
		if(getIndex(metaArray, "pic")!=-1)
			s += newStudentImage + ";";
		else s += "null;";
		s += ""+sidPicker.getNumber() + ";";

		temp = fNameCombo.getSelectionModel().getSelectedItem();
		if(temp!=null) 
			s += temp + ";";
		else
			return(updateBottom(missingOrWrongInfoError, fNameCombo));

		temp = lNameCombo.getSelectionModel().getSelectedItem();
		if(temp!=null)
			s += temp+";";
		else
			return(updateBottom(missingOrWrongInfoError, lNameCombo));

		temp = cityCombo.getSelectionModel().getSelectedItem();
		if(temp!=null)
			s += temp+";";
		else
			return(updateBottom(missingOrWrongInfoError, cityCombo));

		temp = streetCombo.getSelectionModel().getSelectedItem();
		if(temp!=null)
			s += temp+";";
		else
			return(updateBottom(missingOrWrongInfoError, streetCombo));

		if(houseNumPicker.getNumber()!=null)
			s += houseNumPicker.getNumber()+";";
		else
			return(updateBottom(missingOrWrongInfoError, houseNumPicker));

		if(getIndex(metaArray, "zipCode")!=-1) {
			if(zipPicker.getNumber()!=null)
				s += zipPicker.getNumber()+";";
			else
				return(updateBottom(missingOrWrongInfoError, zipPicker));
		}
		else s += "-1;";

		if(birthDatePicker.getValue()!=null)
			s += birthDatePicker.getValue()+";";
		else
			return(updateBottom(missingOrWrongInfoError, birthDatePicker));

		if(startTimePicker.getNumber()!=null)
			s += startTimePicker.getNumber()+";";
		else
			return(updateBottom(missingOrWrongInfoError, startTimePicker));

		temp = deptCombo.getSelectionModel().getSelectedItem();
		if(temp!=null)
			s += temp+";";
		else
			return(updateBottom(missingOrWrongInfoError, deptCombo));

		if(numCreditsPicker.getNumber()!=null)
			s += numCreditsPicker.getNumber()+";";
		else
			return(updateBottom(missingOrWrongInfoError, numCreditsPicker));

		if(getIndex(metaArray, "avg")!=-1) {
			if(avgPicker.getNumber()!=null)
				s += avgPicker.getNumber()+";";
			else
				return(updateBottom(missingOrWrongInfoError, avgPicker));
		}
		else s += "-1;";

		if(failPicker.getNumber()!=null)
			s += failPicker.getNumber()+";";
		else
			return(updateBottom(missingOrWrongInfoError, failPicker));

		if(getIndex(metaArray, "gender")!=-1) {
			temp = genderCombo.getSelectionModel().getSelectedItem();
			if(temp!=null)
				s += temp+";";
			else
				return(updateBottom(missingOrWrongInfoError, genderCombo));
		}
		else s += "null;";

		return s;
	}

	public String getQuery() {
		String s = "";
		for(int i=0;i<compCombox.size();i++) {
			if(compCombox.get(i)!=null) {
				String selectedOp = compCombox.get(i).getValue();
				String data1 = "null",data2 = "null";
				if(selectedOp!=null&&!selectedOp.equals("")) {					
					if(i == getIndex(metaArray, "birthDate")) {
						data1 = date1.getValue().toString();
						if(selectedOp.equals("BTW")) 
							data2 = date2.getValue().toString();
					}

					else {						
						data1 = String.valueOf(data1Combox.get(i)
								.getSelectionModel().getSelectedItem());
						if(selectedOp.equals("BTW")) 			
							data2 = String.valueOf(data2Combox.get(i)
									.getSelectionModel().getSelectedItem());
					}

					if(selectedOp.equals("BTW")) {					
						if(data1!="null" && data2!="null")
							s += metaArray[i]+" BETWEEN "+getDataFormat(metaArray[i], data1)
							+" AND "+getDataFormat(metaArray[i], data2);
						else continue;
					}

					else {
						if(data1!="null")
							s += metaArray[i]+" "+selectedOp+" "+getDataFormat(metaArray[i], data1);
						else continue;
					}

					s += ";";
				}
			}
		}

		return s;
	}

	private String getDataFormat(String c,String data) {
		if(c.equals("pic")||c.equals("firstName")||c.equals("lastName")||c.equals("city")||
				c.equals("street")||c.equals("birthDate")||c.equals("dept")||
				c.equals("place")||c.equals("gender"))
			data = "'"+data+"'";
		return data;
	}
}
