import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class NumberPickerCell<S, T extends Number> extends TableCell<Student, T> {

	private NumberPicker np;
	private boolean isEditing = false;
	private boolean chnageFocus = true;
	private boolean isCancel = false;
	//private boolean isfirst = true;

	public NumberPickerCell(Class<?> t) {
		super();
		np = new NumberPicker(t);
		init();
	}

	public NumberPickerCell(Class<?> t,int min) {
		super();
		np = new NumberPicker(t,min);
		init();
	}

	public NumberPickerCell(Class<?> t,int min,int max) {
		super();
		np = new NumberPicker(t,min,max);
		init();
	}

	private void init() {		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				np.requestFocus();
			}
		});

		addEventFilter(MouseEvent.MOUSE_CLICKED, e-> {
			if(!np.getTextField().isFocused())
				np.requestFocus();
		});

		np.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@SuppressWarnings("unchecked")
			public void handle(KeyEvent event) {
				if(event.getCode()==KeyCode.ENTER)
					commitEdit((T)np.getNumber());
			}
		});
	}

	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if(np==null)
			System.out.println("Field is null");

		if (empty) {
			setText(null);
			setGraphic(null);
		} 
		else {
			np.setNumber(item);
			if(!isCancel) 
				setText(item+"");
			
			setGraphic(np);

			if (isEditing) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			} else {         	             
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
			setAlignment(Pos.CENTER);
		}						
	}

	@Override
	public void startEdit() {
		isEditing = true;
		np.getTextField().requestFocus();
		super.startEdit();    
		if(chnageFocus)
			setFocused(true);
	}

	@Override
	public void commitEdit(T number) {
		isEditing = false;	
		if(number!=null) {	 
			super.commitEdit(number);
			if(chnageFocus)
				setFocused(false);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void cancelEdit() {
		isEditing = false;	
		isCancel = true;
		super.cancelEdit();
		updateItem((T)np.getNumber(), false);
		if(chnageFocus)
			setFocused(false);
		isCancel = false;
	}
}
