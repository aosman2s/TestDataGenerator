package de.aspera.dataexport.cmd;

/**
 * This standard Command interface is used to define a run method for all
 * command classes.
 *
 * @author Daniel.Weiss
 *
 */
public interface CommandRunnable {

    void run() throws CommandException;
}
