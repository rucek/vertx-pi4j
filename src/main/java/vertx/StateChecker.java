package vertx;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

import java.io.IOException;

/**
 * @author Jacek Kunicki
 */
public class StateChecker extends BaseVerticle {

    public static final String NAME = StateChecker.class.getSimpleName();

    @Override
    public void start() {
        log("Started");

        vertx.eventBus().registerHandler(NAME, new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                final String messageBody = message.body();
                log("Received message: " + messageBody);

                if (messageBody.equals(Events.BUTTON_PRESSED)) {
                    vertx.eventBus().publish(GpioVerticle.NAME, isSuccessful() ? Events.SUCCESS : Events.FAILURE);
                }
            }
        });
    }

    private boolean isSuccessful() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpGet httpGet = new HttpGet("http://jacek.kunicki.org/status.json");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String status = EntityUtils.toString(response.getEntity());
            return status.startsWith("true");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return false;
    }
}
