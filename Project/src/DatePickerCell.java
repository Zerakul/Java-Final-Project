import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

public class DatePickerCell<S, T> extends TableCell<Student, Date> {

    private DatePicker datePicker;
    SimpleDateFormat smp = new SimpleDateFormat("dd/MM/yyyy");
    boolean isEditing = false;

    public DatePickerCell() {

        super();

        if (datePicker == null) {
            createDatePicker();
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                datePicker.requestFocus();
            }
        });
    }

    @Override
    public void updateItem(Date item, boolean empty) {

        super.updateItem(item, empty);

        if (null == this.datePicker) {
            System.out.println("datePicker is NULL");
        }

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {

        	 setDatepikerDate(smp.format(item));
             setText(smp.format(item));
             setGraphic(this.datePicker);
             
            if (isEditing) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {         	          
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void setDatepikerDate(String dateAsStr) {

        LocalDate ld = null;
        int year,month,day;

        year = month = day = 0;
        try {
            day = Integer.parseInt(dateAsStr.substring(0, 2));
            month = Integer.parseInt(dateAsStr.substring(3, 5));
            year = Integer.parseInt(dateAsStr.substring(6, dateAsStr.length()));
        } catch (NumberFormatException e) {
            System.out.println("setDatepikerDate / unexpected error " + e);
        }

        ld = LocalDate.of(year, month, day);
        datePicker.setValue(ld);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createDatePicker() {
        this.datePicker = new DatePicker();
        this.datePicker.setEditable(false);

        datePicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                LocalDate date = datePicker.getValue();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
                cal.set(Calendar.MONTH, date.getMonthValue() - 1);
                cal.set(Calendar.YEAR, date.getYear());

                setText(smp.format(cal.getTime()));
                commitEdit(cal.getTime());
            }
        });
        
        setAlignment(Pos.CENTER);
    }

    @Override
    public void startEdit() {
    	isEditing = true;
        super.startEdit();       
    }
    
	@Override
	public void commitEdit(Date newValue) {
		isEditing = false;
		super.commitEdit(newValue);
	}

	@Override
    public void cancelEdit() {
		isEditing = false;
        super.cancelEdit();  
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
    
    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

}