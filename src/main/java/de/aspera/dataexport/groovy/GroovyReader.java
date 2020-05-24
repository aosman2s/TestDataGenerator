package de.aspera.dataexport.groovy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.stereotype.Component;

import de.aspera.dataexport.cmd.DatasetEditorUserFacade;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
@Component
public class GroovyReader {
	public final static String PROJECT_NAME = "dataExporter";
	private File script;
	private GroovyObject groovyObj;
	private static final GroovyClassLoader classLoader = new GroovyClassLoader();

	public GroovyReader() throws GroovyReaderException {
//		readGroovyScript();
	}

	public void readGroovyScript() throws GroovyReaderException {
		script = getGroovyScriptFile();
		try {
			Class groovyClass = classLoader.parseClass(script);
			groovyObj = (GroovyObject) groovyClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | CompilationFailedException | IOException e) {
			throw new GroovyReaderException(e.getMessage(), e);
		}

	}

	private File getGroovyScriptFile() {
		String filePath;
		filePath = System.getProperty("user.home") + File.separator + "." + PROJECT_NAME + File.separator
				+ "GroovyScript.groovy";
		Path pathOfFile = Paths.get(filePath);
		return pathOfFile.toFile();
	}

	public void executeGroovyScript(DatasetEditorUserFacade userFacade) {
		groovyObj.invokeMethod("runScript", userFacade);
	}

}
