import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Client extends Application {
	private DataOutputStream toServer = null;
	private DataInputStream fromServer = null;
	private Socket socket;
	private ArrayList<String> update = new ArrayList<String>();
	private MaterialDesignButton run = new MaterialDesignButton("Run Query");
	private MaterialDesignButton reset = new MaterialDesignButton("Reset DB");
	private MaterialDesignButton del = new MaterialDesignButton("Delete ro.");
	private MaterialDesignButton delc = new MaterialDesignButton("Delete co.");
	private MaterialDesignButton add = new MaterialDesignButton("Add Student");
	private MaterialDesignButton addc = new MaterialDesignButton("Add co.");
	private String[] metaArray = null;
	private String[] dataArray = null;
	private Menu mnm;
	private Tab tab1;
	private boolean isServerRunning = false;
	private String pictureDefault = "img/default-profile.gif";

	@Override
	public void start(Stage primaryStage) {

		try {
			socket = new Socket("localhost", 8000);
			fromServer = new DataInputStream(socket.getInputStream());
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
			load(fromServer);
			isServerRunning = true;

		} catch (UnknownHostException e1) {
		} catch (IOException e1) {
		}

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.setId("tab-pane");

		tab1 = new Tab("Main");

		HBox menu = new HBox();
		menu.setAlignment(Pos.CENTER);
		menu.getChildren().add(tabPane);

		setButtons();
		
		mnm = new Menu(metaArray, dataArray, run, reset, del, delc, add, addc,
				isServerRunning);

		// Set Icons For Tabs
		setImagesForTabs(tab1);
		tab1.setContent(mnm.getPane());

		tabPane.getTabs().addAll(tab1);

		Scene scene = new Scene(menu, 1350, 500);

		tabPane.tabMinWidthProperty()
		.bind((scene.widthProperty().divide(1.03)));
		scene.getStylesheets().add(
				getClass().getResource("styles.css").toExternalForm());
		primaryStage.setTitle("DisplayFigure"); // Set the window title
		primaryStage.setScene(scene); // Place the scene in the window
		primaryStage.setWidth(1350);
		primaryStage.setHeight(700);
		primaryStage.show(); // Display the window
	}

	public boolean isIn(String s, String[] array) {
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(s))
				return true;
		return false;
	}

	public void setButtons() {
		run = new MaterialDesignButton("Run Query");
		reset = new MaterialDesignButton("Reset DB");
		del = new MaterialDesignButton("Delete ro.");
		delc = new MaterialDesignButton("Delete co.");
		add = new MaterialDesignButton("Add Student");
		addc = new MaterialDesignButton("Add co.");
		
		run.setOnAction(e -> {
			update = mnm.getUpdateList();

			String str = "";
			for (int i = 0; i < update.size(); i++) {
				str += update.get(i);
				if (i < update.size() - 1)
					str += ";";
			}
			try {
				if(!str.equals("")) {
					toServer.writeUTF("U");
					toServer.writeUTF(str);
					mnm.resetUpdateList();
				}

				String query = mnm.getQuery();
				toServer.writeUTF("S");
				toServer.writeUTF(query);	
				updateTable();

			} catch (IOException ex) {
				updateTable();
			}
		});

		reset.setOnAction(e -> {
			try {
				toServer.writeUTF("R");
				toServer.writeUTF("");

				updateTable();
			} catch (Exception e1) {
				updateTable();
			}
		});

		del.setOnAction(e -> {
			try {
				if (mnm.getRowID() != -1) {
					toServer.writeUTF("D");
					toServer.writeUTF("" + mnm.getRowID());

					updateTable();
				}
			} catch (Exception e1) {
				updateTable();
			}
		});

		delc.setOnAction(e -> {
			try {
				String s = mnm.getColumn(0);
				if (!s.equals("")) {
					toServer.writeUTF("DC");
					switch (s) {
					case "Picture":
						toServer.writeUTF("pic");
						break;
					case "ZipCode":
						toServer.writeUTF("zipCode");
						break;
					case "Average":
						toServer.writeUTF("avg");
						break;
					case "Gender":
						toServer.writeUTF("gender");
						break;
					}

					updateTable();
				}
			} catch (Exception e1) {
				updateTable();
			}
		});

		add.setOnAction(e -> {
			try {
				String s = mnm.getStudent();
				if (s != "") {
					toServer.writeUTF("I");
					toServer.writeUTF(s);

					updateTable();
				}
			} catch (Exception e1) {
				updateTable();
			}
		});

		addc.setOnAction(e -> {
			try {
				String s = mnm.getColumn(1);
				if (!s.equals("")) {
					toServer.writeUTF("IC");
					switch (s) {
					case "Picture":
						toServer.writeUTF("pic;varchar(100);'"+pictureDefault
								+ "';FIRST");
						break;
					case "ZipCode":
						toServer.writeUTF("zipCode;char(5);'10000';AFTER houseNum");
						break;
					case "Average":
						toServer.writeUTF("avg;float;0.0;AFTER numOfCredits");
						break;
					case "Gender":
						toServer.writeUTF("gender;varchar(6);'Male'; ");
						break;
					}

					updateTable();
				}
			} catch (Exception e1) {
				updateTable();
			}
		});
	}
	
	public void updateTable() {
		load(fromServer);
		setButtons();
		mnm = new Menu(metaArray, dataArray, run, reset, del, delc, add, addc,
				isServerRunning);
		tab1.setContent(mnm.getPane());
	}

	private void load(DataInputStream fromServer) {
		String mettaData = "";
		String data = "";
		try {
			mettaData = fromServer.readUTF();
			data = fromServer.readUTF();

			if (mettaData.equals(""))
				isServerRunning = false;
		} catch (IOException e) {
			isServerRunning = false;
		}
		metaArray = mettaData.split(";");
		dataArray = data.split("\n");
	}

	/** Setting Icons For Tabs */
	private void setImagesForTabs(Tab tab1) {
		Image imagemenu = new Image("img/Start-Menu-Button.png", 25, 25, true,
				true);
		ImageView ivmenu = new ImageView(imagemenu);
		tab1.setGraphic(ivmenu);
	}

	public void serverClosed() {
		isServerRunning = false;
	}
	/*
	public static void main(String[] args) {
		launch(args);
	}
	 */
}
