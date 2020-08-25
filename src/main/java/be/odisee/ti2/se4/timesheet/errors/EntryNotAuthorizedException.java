package be.odisee.ti2.se4.timesheet.errors;

public class EntryNotAuthorizedException extends Exception {

    public EntryNotAuthorizedException(long id) {
        super("Entry id "+id+" not authorized");
    }

}
