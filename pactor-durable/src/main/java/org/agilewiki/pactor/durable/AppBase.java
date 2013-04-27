package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pautil.Ancestor;

public class AppBase implements App {
    private Durable durable;

    @Override
    public void setDurable(final Durable _durable) {
        if (durable != null)
            throw new IllegalStateException("durable already set");
        durable = _durable;
    }

    @Override
    public Durable getDurable() {
        return durable;
    }

    @Override
    public Mailbox getMailbox() {
        return durable.getMailbox();
    }

    @Override
    public Ancestor getParent() {
        return durable.getParent();
    }
}
