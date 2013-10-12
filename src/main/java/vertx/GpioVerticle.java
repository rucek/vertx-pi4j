package vertx;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

/**
 * @author Jacek Kunicki
 */
public class GpioVerticle extends BaseVerticle {

    private boolean buttonPressed = false;

    public static final String NAME = GpioVerticle.class.getSimpleName();

    @Override
    public void start() {
        log("Started");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalOutput redLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "redLED", PinState.LOW);
        final GpioPinDigitalOutput greenLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "greenLED", PinState.LOW);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                buttonPressed = !buttonPressed;
                if (buttonPressed) {
                    System.out.println("Button pressed, publishing event"); // log() throws NPE
                    vertx.eventBus().publish(StateChecker.NAME, Events.BUTTON_PRESSED);
                }
            }
        });

        vertx.eventBus().registerHandler(NAME, new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                final String messageBody = message.body();
                log("Received message: " + messageBody);

                if (messageBody.equals(Events.SUCCESS)) {
                    greenLED.blink(500, 5000);
                } else if (messageBody.equals(Events.FAILURE)) {
                    redLED.blink(500, 5000);
                }
            }
        });
    }
}
