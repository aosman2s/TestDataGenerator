
package de.aspera.dataexport.dao;

import java.util.logging.Logger;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;

import de.aspera.dataexport.cmd.CommandContext;
import de.aspera.dataexport.util.Resources;

/**
 * The test creates a test database in h2 for JUnit tests.
 *
 * @author daniel
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public abstract class BasicFacadeTest {
    static {
        Resources.getInstance();
        System.getProperties().put("DBNAME", "test-database");
        System.getProperties().put("DBACTION", "drop-and-create");
        // System.getProperties().put("DBACTION", "create");
    }
//    public static final CommandContext CMDCTX = CommandContext.getInstance();
    protected final Logger logger = Logger.getLogger(getLoggerClass().getName());

    public abstract Class<?> getLoggerClass();
}
