package org.agilewiki.jactor2.core.misc;

import org.agilewiki.jactor2.core.BladeBase;
import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Simulates a load.
 */
public class Load extends BladeBase {
    private volatile long i;
    private volatile long j;

    /**
     * Create a Load actor.
     *
     * @param _facility The actor's facility.
     */
    public Load(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    /**
     * Returns a load request.
     *
     * @param _load The extent of the simulated load.
     * @return The delay request.
     */
    public SyncRequest<Void> loadSReq(final long _load) {
        return new SyncRequest<Void>(getReactor()) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                i = 0;
                while (i < _load) {
                    i++;
                    j = 0;
                    while (j < 1000000) {
                        j++;
                    }
                }
                return null;
            }
        };
    }

    public long getI() {
        return i;
    }

    public long getJ() {
        return j;
    }
}
