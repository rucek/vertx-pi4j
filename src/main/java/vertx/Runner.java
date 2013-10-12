package vertx;

/**
 * @author Jacek Kunicki
 */
public class Runner extends BaseVerticle {

    @Override
    public void start() {
        log("Running vertx.GpioVerticle");
        getContainer().deployVerticle("vertx.GpioVerticle");
        getContainer().deployVerticle("vertx.StateChecker");
    }
}
