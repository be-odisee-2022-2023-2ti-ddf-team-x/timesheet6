package be.odisee.ti2.ddf.timesheet.errors;

public class EntryNotFoundException extends Exception {

    public EntryNotFoundException(long id) {
        super("Entry not found for id "+id);
    }
}
