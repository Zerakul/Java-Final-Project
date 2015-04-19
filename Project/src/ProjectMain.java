import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ProjectMain extends Application {

	private MaterialDesignButton serverButton = new MaterialDesignButton("");
	private MaterialDesignButton clientButton = new MaterialDesignButton("");

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		serverButton.setRippleColor(Color.LIGHTGREEN);
		GridPane p = new GridPane();
		p.add(serverButton,0,2);
		p.add(clientButton,1,2);
		clientButton.setDisable(true);
		serverButton.setMinHeight(161);
		serverButton.setMinWidth(200);
		clientButton.setMinHeight(161);
		clientButton.setMinWidth(200);
		Image imageclient = new Image("img/client.png", 161, 200, true, true);
		Image imageserver= new Image("img/server.png", 161, 200, true, true);

		ImageView ivclient = new ImageView(imageclient);
		ImageView ivserver = new ImageView(imageserver);
		clientButton.setGraphic(ivclient);
		serverButton.setGraphic(ivserver);
		serverButton.setStyle("-fx-background-color: linear-gradient(#FF0000, #FF4D4D);");
		clientButton.setStyle("-fx-background-color: linear-gradient(#FF0000, #FF4D4D);-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );");
		Scene s = new Scene(p);
		stage.setScene(s);
		stage.setTitle("ProjectMain");
		stage.setHeight(200);
		stage.setWidth(400);
		stage.setX(20);
		stage.setY(20);
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.show();
		stage.setOnCloseRequest(e-> {
			System.exit(0);
		});

		serverButton.setOnMouseClicked(e-> {
			ProjectServer server = new ProjectServer();
			try {
				server.start(new Stage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			serverButton.setDisable(true);
			serverButton.setStyle("-fx-background-color: linear-gradient(#00FF00, #339533);");
			clientButton.setDisable(false);
			clientButton.setStyle("-fx-background-color:linear-gradient(#f0ff35, #a9ff00),radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );");
		});

		clientButton.setOnMouseClicked(e-> {
			Client client = new Client();
			client.start(new Stage());
		});

	}

}
