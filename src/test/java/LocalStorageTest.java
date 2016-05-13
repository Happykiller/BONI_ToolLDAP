import com.bonitaSoft.tools.LocalStorage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
/**
 * LibJava Tester.
 *
 * @author <Authors name>
 * @since <pre>nov. 4, 2015</pre>
 * @version 1.0
 */
public class LocalStorageTest {

    private static LocalStorage localStorage = new LocalStorage();

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     *
     * Method: sayHelloMessage()
     *
     */
    @Test
    public void testGet() throws Exception {
        String receive;
        String waiting = "test";
        receive = localStorage.get("test","test");
        Assert.assertEquals(receive,waiting);
    }
}