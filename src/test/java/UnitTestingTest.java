import arx.dukalis.unit.api.After;
import arx.dukalis.unit.api.Before;
import arx.dukalis.unit.api.Test;

public class UnitTestingTest {

    @Before
    void hello() {
        System.out.println("Hello");
    }

    @Before
    void there() {
        System.out.println("there!");
    }

    @Test(shouldRaise = Exception.class)
    void annotatedThrow() throws Exception {
        throw new Exception();
    }
    
    @Test()
    void unannotatedThrow() throws Exception {
        throw new Exception();
    }

    void noTestAnnotation() {
        System.out.println("I can't believe it's not a TEST");
    }

    @After
    void kenobi() {
        System.out.println("Kenobi!...");
    }

    @After
    void general() {
        System.out.println("General");
    }
}
