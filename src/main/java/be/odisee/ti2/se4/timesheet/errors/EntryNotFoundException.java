package be.odisee.ti2.se4.timesheet.errors;

public class EntryNotFoundException extends Exception {

    public EntryNotFoundException(long id) {
        super("Entry not found for id "+id);
    }
}
