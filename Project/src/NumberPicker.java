import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class NumberPicker extends FlowPane implements ChangeListener<String> {

	private Class<?> type;
	private final double ARROW_SIZE = 4;
	private int MIN = Integer.MIN_VALUE;
	private int MAX = Integer.MAX_VALUE;
	private TextField numberField = new TextField();
	private final Button incrementButton = new Button();
	private final Button decrementButton = new Button();
	private NumberBinding buttonHeight;
	private NumberBinding spacing;
	private HBox container = new HBox();

	public NumberPicker(Class<?> t,int min,int max) {
		MIN = min;
		MAX = max;
		type = t;
		initGraphics();
	}

	public NumberPicker(Class<?> t,int min) {
		MIN = min;
		type = t;
		initGraphics();
	}

	public NumberPicker(Class<?> t) {	
		type = t;
		initGraphics();
	}

	private void initGraphics() {
		// Painting the up and down arrows
		Path arrowUp = new Path();
		arrowUp.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0),
				new LineTo(0, -ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
		// mouse clicks should be forwarded to the underlying button
		arrowUp.setMouseTransparent(true);

		Path arrowDown = new Path();
		arrowDown.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0),
				new LineTo(0, ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
		arrowDown.setMouseTransparent(true);

		buttonHeight = numberField.heightProperty().subtract(3).divide(2);
		spacing = numberField.heightProperty().subtract(2).subtract(buttonHeight.multiply(2));

		// inc button
		incrementButton.prefWidthProperty().bind(numberField.heightProperty());
		incrementButton.minWidthProperty().bind(numberField.heightProperty());
		incrementButton.maxHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.prefHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.minHeightProperty().bind(buttonHeight.add(spacing));
		incrementButton.setFocusTraversable(false);
		incrementButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				increment();
				//ae.consume();
			}
		});

		// Paint arrow path on button using a StackPane
		StackPane incPane = new StackPane();
		incPane.getChildren().addAll(incrementButton, arrowUp);
		incPane.setAlignment(Pos.CENTER);

		// dec button
		decrementButton.prefWidthProperty().bind(numberField.heightProperty());
		decrementButton.minWidthProperty().bind(numberField.heightProperty());
		decrementButton.maxHeightProperty().bind(buttonHeight);
		decrementButton.prefHeightProperty().bind(buttonHeight);
		decrementButton.minHeightProperty().bind(buttonHeight);
		decrementButton.setFocusTraversable(false);
		decrementButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				decrement();
				//ae.consume();
			}
		});

		StackPane decPane = new StackPane();
		decPane.getChildren().addAll(decrementButton, arrowDown);
		decPane.setAlignment(Pos.CENTER);

		setEditable(true);
		GridPane buttons = new GridPane();
		buttons.add(incPane,0,0);
		buttons.add(decPane,0,1);

		container.getChildren().addAll(numberField, buttons);
		numberField.setPrefWidth(120);
		buttons.setPrefWidth(20);
		container.setPrefWidth(151);

		getChildren().add(container);
		minWidthProperty().bind(container.widthProperty().add(4));
		maxWidthProperty().bind(container.widthProperty().add(4));
		setAlignment(Pos.CENTER);
		setHeight(USE_COMPUTED_SIZE);
	}

	public TextField getTextField() {
		return numberField;
	}

	public void setFieldWidth(double width) {
		numberField.setPrefWidth(width);
		container.setPrefWidth(width+31);
	}
	
	public Number getNumber() {
		if(!numberField.getText().equals("")&&checkNumber()) {
			if(type==Float.class) {
				String s = numberField.getText();
				if(s.charAt(s.length()-1)=='.')
					s.concat("0");
				return Float.parseFloat(s);
			}
			return Integer.parseInt(numberField.getText());
		}
		
		return null;
	}

	public void setNumber(Number n) {
		numberField.setText(n+"");
	}

	public void setNumberFromString(String s) {
		try {
			if(type==Float.class) 
				Float.parseFloat(s);
			else
				Integer.parseInt(s);
			numberField.setText(s+"");
		}
		catch(Exception e) {
			
		}		
	}
	
	private void increment() {
		numberField.requestFocus();	
		try {
			if(!numberField.getText().equals("")) {
				if(type==Float.class) {
					float value = Float.parseFloat(numberField.getText());
					value = value + 0.01f;
					if(value<=MAX)
						numberField.setText(""+value);
				}
				else if(type==Integer.class) {
					int value = Integer.parseInt(numberField.getText());
					value = value + 1;
					if(value<=MAX)
						numberField.setText(""+value);
				}
			}
			else setNumber(MIN);
		}
		catch(NumberFormatException e) {
			System.out.println("can't format string "+numberField.getText());
		}
	}

	private void decrement() {
		numberField.requestFocus();
		try {
			if(!numberField.getText().equals("")) {
				if(type==Float.class) {
					float value = Float.parseFloat(numberField.getText());
					value = value - 0.01f;
					if(value>=MIN)
						numberField.setText(""+value);
				}
				else if(type==Integer.class) {
					int value = Integer.parseInt(numberField.getText());
					value = value - 1;
					if(value>=MIN)
						numberField.setText(""+value);
				}
			}
			else setNumber(MAX);
		}
		catch(NumberFormatException e) {
			System.out.println("can't format string "+numberField.getText());
		}
	}

	public boolean checkNumber() {
		if(type==Float.class) {
			float num = Float.parseFloat(numberField.getText());
			return num<=MAX && num>=MIN;
		}
		else {
			int num = Integer.parseInt(numberField.getText());
			return num<=MAX && num>=MIN;
		}
	}
	
	public void setEditable(boolean b) {
		numberField.setEditable(b);
		if(b) {
			numberField.textProperty().addListener(this);
		}
		else
			numberField.textProperty().removeListener(this);
	}

	@Override
	public void changed(ObservableValue<? extends String> observable,
			String oldValue, String newValue) {
		String s = numberField.getText();	
		if(!s.equals("")&&oldValue.length()<newValue.length()) {
			if(type == Integer.class&&(s.charAt(s.length()-1)<'0'||s.charAt(s.length()-1)>'9'||
					Integer.parseInt(newValue)>MAX)||
					type == Float.class&&((s.charAt(s.length()-1)<'0'||s.charAt(s.length()-1)>'9')&&
					(s.charAt(s.length()-1)!='.'||s.length()<2||oldValue.contains("."))||
					Float.parseFloat(newValue)>MAX)||
					(oldValue.length()>3&&oldValue.charAt(oldValue.length()-3)=='.'))
				numberField.setText(s.substring(0, s.length()-1));	
		}
	}
}
