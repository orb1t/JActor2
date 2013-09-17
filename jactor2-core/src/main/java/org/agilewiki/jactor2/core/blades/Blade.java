package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * <p>
 * Blades must implement the Blade interface to provide access to their reactor.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class BladeSample implements Blade {
 *     private final Reactor processor;
 *
 *     BladeSample(final Reactor _processor) {
 *         processor = _processor;
 *     }
 *
 *     {@literal @}Override
 *     public final Reactor getReactor() {
 *         return processor;
 *     }
 * }
 * </pre>
 */
public interface Blade {
    /**
     * Returns the reactor associated with this blade.
     *
     * @return The blade's reactor.
     */
    Reactor getReactor();
}