package de.aspera.dataexport.cmd;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class HelpCommand implements CommandRunnable {
	@Autowired
	private CommandContext cmdContext;

	@Override
	public void run() {
		String argument = cmdContext.nextArgument();
		if (StringUtils.isEmpty(argument))
			argument = "";

		switch (argument.toLowerCase()) {
		case "export":
		case "e":
			runHelpExportCmd();
			break;
		case "exed":
		case "exportedit":
			runHelpExEdCmd();
			break;
		case "list-cmd":
			showAvailableCommands();
			break;

		default:
			runWithoutArguments();
			break;
		}
	}

	private void runHelpExEdCmd() {
		System.out.println("Export and edit dataset command:  \n");
		System.out.println(
				"\tTo execute an export command please use the dataExporter_connections.json, dataExporter_Exportcommands.json and GroovyScript.groovy.");
		System.out.println("\tThe connection and command files are located in [user]/.dataexporter folder.");
		System.out
				.println("\tThe Database form which the data will be exported must be defined in the connection file.");
		System.out.println("\tThe commands to edit the dataset are all defined in the groovyScript.groovy file.");
		System.out.println("\tThe export commands must be defined in the command file.");
		System.out.println("\tTo execute an export and edit command type: exportEdit [command-Id].");
		System.out.println("\tPlease refer to the examples in the command, connection, GroovyScript files.");
	}

	private void showAvailableCommands() {
		int i = 0;
		for (String cmd : cmdContext.getCommands()) {
			if (cmd.length() > 2) {
				System.out.print(i <= 0 ? cmd : "|" + cmd);
				i++;
			}
		}
	}

	private void runHelpExportCmd() {
		System.out.println("Export command:  \n");
		System.out.println(
				"\tTo execute an export command please use the dataExporter_connections.json and dataExporter_Exportcommands.json.");
		System.out.println("\tThe connection and command files are located in [user]/.dataexporter folder.");
		System.out
				.println("\tThe Database form which the data will be exported must be defined in the connection file.");
		System.out.println("\tThe export commands must be defined in the command file.");
		System.out.println("\tTo execute an export command type: export [command-Id].");
		System.out.println("\tPlease refer to the examples in the command and connection files.");
	}

	private void runWithoutArguments() {
		System.out.println("List of commands: \n");
		System.out.println("\t(q)quit: \t\t\t\t\tQuit the program.");
		System.out.println("\t(e)export cmd-id\t\t\t\tCommand-Id of configure export commands");
		System.out.println("\t(im)import [-c] path_to_file database_ident\tImport a dbunit xml file into a database.");
		System.out.println("\t(exEd)export and Edit cmd-id \t\t\tCommand-Id of configure export commands");
		System.out.println("\tCommand options mandatory: \t\t\tCommand parameters without brackets are mandatory");
		System.out.println("\tCommand options optional: \t\t\tCommand parameters inside brackets are optional\n");
		System.out.println("\tFor more details about a command type: help [command, list-cmd]");
		System.out.println("\t(h)elp: \t\t\t\tPrint this!\n\n");
	}
}
