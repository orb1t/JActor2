package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final Facility facility = new Facility();
        final Reactor reactor = new IsolationReactor(facility);
        final Blade1 blade1 = new Blade1(reactor);
        final String result = blade1.hiSReq().call();
        assertEquals("Hello world!", result);
        facility.close();
    }
}
