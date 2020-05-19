package de.aspera.dataexport.cmd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandContext {

	@Autowired
	private QuitCommand quitCommand;
	@Autowired
	private HelpCommand helpCommand;
	@Autowired
	private ConfigInitCommand configInitCommand;
	@Autowired
	private ExportDatasetCommand exportDatasetCommand;
	@Autowired
	private ImportDatasetCommand importDatasetCommand;
	@Autowired
	private ExportAndEditDatasetCommand exportAndEditDatasetCommand;
	
	
    private static Map<String, CommandRunnable> commandMap = new HashMap<>();
    private static Queue<String> argumentStack = new LinkedList<>();
   
   
    public CommandContext() {
    	loadCommands();
    }

    public void executeCommand(String command) throws CommandException {
        ((CommandRunnable) commandMap.get(command)).run();
        clearArguments();
    }

    public void addCommand(String key, CommandRunnable command) {
        commandMap.put(key, command);
    }

    public void removeCommand(String key) {
        commandMap.remove(key);
    }

    public boolean isCommand(String key) {
        return commandMap.containsKey(key);
    }

    public void addArgument(String arg) {
        argumentStack.add(arg);
    }

    public String nextArgument() {
        return argumentStack.poll();
    }

    public String[] allArguments() {
        return argumentStack.toArray(new String[0]);
    }

    public int sizeOfArguments() {
        return argumentStack.size();
    }

    public void clearArguments() {
        argumentStack.clear();
    }
    
    public Set<String> getCommands() {
    	return commandMap.keySet();
    }

    /**
     * Register commands on the CommandContext.
     */
    public void loadCommands() {
        addCommand("quit", quitCommand);
        addCommand("q", quitCommand);
        addCommand("h", helpCommand);
        addCommand("help", helpCommand);
        addCommand("init", configInitCommand);
        addCommand("i", configInitCommand);
        addCommand("e", exportDatasetCommand);
        addCommand("export", exportDatasetCommand);
        addCommand("import", importDatasetCommand);
        addCommand("im", importDatasetCommand);
        addCommand("exEd", exportAndEditDatasetCommand);
        addCommand("exportEdit", exportAndEditDatasetCommand);
    }
}
