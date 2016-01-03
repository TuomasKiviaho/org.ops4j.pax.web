package org.ops4j.pax.web.itest.jetty;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.net.URI;
import java.util.Dictionary;
import java.util.concurrent.Callable;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.eclipse.jetty.websocket.jsr356.JettyClientContainerProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.swissbox.core.ContextClassLoaderUtils;
import org.ops4j.pax.web.itest.jetty.support.SimpleWebSocket;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Achim Nierbeck
 */
@RunWith(PaxExam.class)
public class WebSocketWhiteBoardIntegrationTest extends ITestBase {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketWhiteBoardIntegrationTest.class);

	@Configuration
	public static Option[] configure() {
		return combine(
				configureWebSocketJetty());
	}

	@Before
	public void setUp() throws BundleException, InterruptedException {
		LOG.info("Setting up test");

		initWebListener();
		waitForWebListener();
	}

	/**
	 * You will get a list of bundles installed by default plus your testcase,
	 * wrapped into a bundle called pax-exam-probe
	 */
	@Test
	public void listBundles() {
		for (Bundle b : bundleContext.getBundles()) {
			if (b.getState() != Bundle.ACTIVE) {
				fail("Bundle should be active: " + b);
			}

			Dictionary<String,String> headers = b.getHeaders();
			String ctxtPath = (String) headers.get(WEB_CONTEXT_PATH);
			if (ctxtPath != null) {
				System.out.println("Bundle " + b.getBundleId() + " : "
						+ b.getSymbolicName() + " : " + ctxtPath);
			} else {
				System.out.println("Bundle " + b.getBundleId() + " : "
						+ b.getSymbolicName());
			}
		}

	}
	
	@Test
	@Ignore("Only works with an external websocket test tool like 'Simple Websocket client' a chrome extension")
	public void testWebsocket() throws Exception {

	    SimpleWebSocket simpleWebSocket = new SimpleWebSocket();
	    
	    bundleContext.registerService(Object.class.getName(), simpleWebSocket, null);
	    
	    Thread.sleep(1000);
	    
	    
	    
//	    ContextClassLoaderUtils.doWithClassLoader(getClass().getClassLoader(),
//                new Callable<Void>() {
//
//                    @Override
//                    public Void call() throws Exception {
//                        WebSocketClient webSocketClient = new WebSocketClient();
//                        boolean test = webSocketClient.test();
//                        assertTrue(test);
//                        return null;
//                    }
//
//                });
		
	}
	
	
	public class WebSocketClient {
	    public boolean test() throws Exception{
	        
	        URI uri = URI.create("ws://127.0.0.1:8181/simple/");

	        ClientContainer container = new ClientContainer();
	        
            try
            {
                // Attempt Connect
                Session session = container.connectToServer(SimpleWebSocket.class,uri);
                // Send a message
                session.getBasicRemote().sendText("Hello");
                // Close session
                session.close();
            }
            finally
            {
                // Force lifecycle stop when done with container.
                // This is to free up threads and resources that the
                // JSR-356 container allocates. But unfortunately
                // the JSR-356 spec does not handle lifecycles (yet)
                if (container instanceof LifeCycle)
                {
                    ((LifeCycle)container).stop();
                }
            }
	        return true;
	    }
	}

}

