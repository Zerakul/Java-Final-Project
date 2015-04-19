import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.application.Platform;

public class ProjectServer extends Application {

	private TextArea ta = new TextArea();
	private int clientNo = 0;
	private ServerSocket server;
	private Database db;
	private MissionQueue mq = new MissionQueue();
	private Thread mainThread;

	/*
	public static void main(String[] args) {
		launch(args);
	}
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void start(Stage stage) throws Exception {
		ta.setEditable(false);
		Scene scene = new Scene(ta);
		stage.setScene(scene);
		stage.setTitle("ProjectServer");
		stage.setHeight(200);
		stage.setWidth(410);
		stage.setX(15);
		stage.setY(225);
		stage.setAlwaysOnTop(true);
		stage.setOnCloseRequest(e -> {
			try {
				mainThread.stop();
				db.closeConnection();
				server.close();
			} catch (Exception e1) {
			}
		});

		mainThread = new Thread(() -> {
			try {			
				print("ProjectServer started at " + new Date());
				server = new ServerSocket(8000);
				// To avoid trying to open two servers.
				Platform.runLater(() -> {
					stage.show();
				});			

				db = new Database();
				print("Driver Loaded\nConnection Established");
				print(db.createDatabase());

				while (true) {
					Socket socket = server.accept();

					clientNo++;
					print("Starting thread for client " + clientNo + " at "
							+ new Date());
					InetAddress clientInetAddress = socket.getInetAddress();
					print("Client " + clientNo + "'s host name is "
							+ clientInetAddress.getHostName());
					print("Client " + clientNo + "'s IP Address is "
							+ clientInetAddress.getHostAddress());
					new Thread(new HandleAClient(socket)).start();
				}
			} catch (IOException ex) { 
			}
		});
		mainThread.start();

		new Thread(()-> {
			try {
				while(true) {
					Thread.sleep(50);
					if(mq.size()!=0) {
						if(mq.size()!=0) {
							Mission m = mq.pop();
							String[] elements;
							switch(m.getType()) {
							case "SELECT":
								try {
									String result = "Failed";
									if (!m.getMission().equals("")) {
										elements = m.getMission().split(";");
										result = "SELECT * FROM Student WHERE ";
										for(int i=0;i<elements.length;i++) {
											result += elements[i];
											if(i!=elements.length-1)
												result += " AND ";
										}

										result = db.select(result);
									}
									if(result.equals("Failed")) {
										m.getOutputStream().writeUTF(db.getMetaData());
										m.getOutputStream().writeUTF(db.select("SELECT * FROM Student"));
									}
									else {
										m.getOutputStream().writeUTF(db.getMetaData());
										m.getOutputStream().writeUTF(result);
									}
								} catch (Exception e1) { System.out.println(e1.getMessage()); }
								break;
							case "UPDATE":
								boolean isError = false;
								elements = m.getMission().split(";");
								for (int i = 0; i < elements.length; i += 3) {
									String s = db.update(Integer.parseInt(elements[i]),
											elements[i + 1], elements[i + 2]);
									if(!s.equals("Update succeeded")) {
										print(s);
										isError = true;
									}
								}
								if(!isError)
									print("Update succeeded");
								break;
							case "DELETE":
								print(db.delete(Integer.parseInt(m.getMission())));
								break;
							case "DELCOLUMN":
								print(db.deleteColumn(m.getMission()));
								break;
							case "INSERT":
								elements = m.getMission().split(";");
								print(db.insert(elements[0],Integer.parseInt(elements[1]),elements[2],
										elements[3],elements[4],elements[5],Integer.parseInt(elements[6]),
										Integer.parseInt(elements[7]),elements[8],
										Integer.parseInt(elements[9]),elements[10],
										Integer.parseInt(elements[11]),Float.parseFloat(elements[12]),
										Integer.parseInt(elements[13]),elements[14]));
								break;
							case "ADDCOLUMN":
								elements = m.getMission().split(";");
								print(db.addColumn(elements[0],elements[1],elements[2],elements[3]));
								break;
							case "RESET":
								print(db.createDatabase());
								break;
							}

							if(!m.getType().equals("SELECT")&&!m.getType().equals("UPDATE")) {
								Thread.sleep(50);
								m.getOutputStream().writeUTF(db.getMetaData());
								m.getOutputStream().writeUTF(db.select("SELECT * FROM Student"));
							}
						}
					}
				}
			}catch(Exception e1) { System.out.println(e1.getMessage()); }
		}).start();
	}

	public void print(String text) {
		Platform.runLater(() -> {
			ta.appendText(text + "\n");
		});
	}

	class HandleAClient implements Runnable {
		private Socket connectToClient;

		public HandleAClient(Socket socket) {
			connectToClient = socket;
		}

		public void run() {
			try {
				DataInputStream isFromClient = new DataInputStream(
						connectToClient.getInputStream());
				DataOutputStream osToClient = new DataOutputStream(
						connectToClient.getOutputStream());

				// returns a string consist of all database rows with ';'
				// between columns and '\n' between rows
				osToClient.writeUTF(db.getMetaData());
				osToClient.writeUTF(db.select("SELECT * FROM Student"));
				while (true) {
					try {
						String type = isFromClient.readUTF();
						String str = isFromClient.readUTF();
						switch (type) {
						case "S":
							mq.push(new Mission(osToClient,"SELECT",str));
							break;
						case "U":						
							mq.push(new Mission(osToClient,"UPDATE",str));
							break;
						case "D":
							mq.push(new Mission(osToClient,"DELETE",str));
							break;
						case "I":
							mq.push(new Mission(osToClient,"INSERT",str));
							break;
						case "DC":
							mq.push(new Mission(osToClient,"DELCOLUMN",str));
							break;
						case "IC":
							mq.push(new Mission(osToClient,"ADDCOLUMN",str));
							break;
						case "R":
							mq.push(new Mission(osToClient,"RESET",str));
							break;
						}

					} catch (NumberFormatException e) {
						print("parsing problem");
					}
				}
			} catch (IOException e) {
			}
		}

	}
}

class Mission {
	DataOutputStream output;
	String type;
	String mission;

	public Mission(DataOutputStream out, String type, String mission) {
		output = out;
		this.type = type;
		this.mission = mission;
	}

	public String getType() {
		return type;
	}

	public DataOutputStream getOutputStream() {
		return output;
	}

	public String getMission() {
		return mission;
	}

	public String toString() {
		return "mission = " + mission;
	}
}

class MissionQueue {
	ArrayList<Mission> list = new ArrayList<Mission>();
	int last = 0;
	int first = 0;
	boolean b = false;

	public void push(Mission m) {
		list.add(m);
	}

	public Mission pop() {
		Mission m = list.get(first);
		if(list.size()>0) {
			list.remove(0);
			return m;
		}
		return null;
	}

	public int size() {
		return list.size();
	}

	public void printList() {
		for (int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
	}
}