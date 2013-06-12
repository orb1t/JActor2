package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Mailbox;

public class Load extends StageBase {
    int delay;

    public Load(final FirehoseMailbox _mailbox, final int _delay) {
        super(_mailbox);
        delay = _delay;
    }

    @Override
    public Object process(Engine _engine, Object data) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
        }
        return data;
    }
}
