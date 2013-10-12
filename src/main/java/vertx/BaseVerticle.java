package vertx;

import org.vertx.java.platform.Verticle;

/**
 * @author Jacek Kunicki
 */
public class BaseVerticle extends Verticle {

    protected void log(String message) {
        getContainer().logger().info(String.format("[%s] %s", getClass().getSimpleName(), message));
    }
}
