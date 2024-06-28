package vn.mekosoft.backup.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty dateTime;
    private final StringProperty action;
    private final StringProperty result;

    public LogEntry(String dateTime, String action, String result) {
        this.dateTime = new SimpleStringProperty(dateTime);
        this.action = new SimpleStringProperty(action);
        this.result = new SimpleStringProperty(result);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public String getAction() {
        return action.get();
    }

    public StringProperty actionProperty() {
        return action;
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public String getResult() {
        return result.get();
    }

    public StringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }

}
