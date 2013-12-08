package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

class Hanger extends BladeBase {
    Hanger(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    SyncRequest<Void> hangSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                while (true) {}
            }
        };
    }
}
