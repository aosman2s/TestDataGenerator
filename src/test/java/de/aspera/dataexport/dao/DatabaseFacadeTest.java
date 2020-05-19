package de.aspera.dataexport.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import de.aspera.dataexport.dto.Config;

@RunWith(SpringRunner.class)
public class DatabaseFacadeTest extends BasicFacadeTest {


    @Test
    public void testConfig() throws DatabaseException {
        Config config = new Config();
        config.setKey("aKey");
        String[] valueArray = new String[] { "a", "b", "c" };
        config.setValue(valueArray);
        ConfigFacade configFacade = new ConfigFacade();
        configFacade.create(config);

        // get it back
        String[] returnValues = configFacade.getValue("aKey");
        Assert.assertTrue(returnValues.length == valueArray.length);
        Assert.assertArrayEquals(returnValues, valueArray);

    }

    @Override
    public Class<?> getLoggerClass() {
        return this.getClass();
    }
}
