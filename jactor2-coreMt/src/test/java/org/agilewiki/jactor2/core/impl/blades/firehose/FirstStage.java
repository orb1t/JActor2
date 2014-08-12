package org.agilewiki.jactor2.core.impl.blades.firehose;

import java.util.ArrayList;
import java.util.List;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.BoundResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

public class FirstStage extends IsolationBladeBase implements Runnable {

    private DataProcessor next;

    private long count;

    private long ndx;

    private int maxWindowSize;

    private int ackCount;

    private List<Long> list;

    private FirehoseData firehoseData;

    private BoundResponseProcessor<Void> ack;

    Thread mainThread;

    long t0;

    public FirstStage(final DataProcessor _next, final long _count,
            final int _maxWindowSize) throws Exception {
        super(new IsolationReactor());
        getReactor().setIdle(this);
        mainThread = Thread.currentThread();
        next = _next;
        count = _count;
        maxWindowSize = _maxWindowSize;
        ack = new BoundResponseProcessor<Void>(this,
                new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void response) {
                        ackCount -= 1;
                        if (list != null) {
                            send();
                        }
                        if ((ackCount == 0) && (ndx >= _count)) {
                            final long t1 = System.currentTimeMillis();
                            final long d = t1 - t0;
                            System.out.println("time in millis: " + d);
                            System.out.println("number of Long: " + count);
                            System.out.println("window size: " + maxWindowSize);
                            if (d > 0) {
                                System.out
                                        .println("Longs/second through the firehose: "
                                                + ((count * 1000L) / d));
                                System.out
                                        .println("Longs/second passed between stages: "
                                                + ((10L * count * 1000L) / d));
                            }
                            mainThread.interrupt();
                        }
                    }
                });
        t0 = System.currentTimeMillis();

        new SOp<Void>("null", this.getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                return null;
            }
        }.signal();
    }

    private void createList() {
        if (list != null) {
            System.out.println("!!! bug !!!");
            return;
        }
        list = new ArrayList<Long>();
        firehoseData = new FirehoseData(ack, list);
    }

    private void send() {
        send(next.processDataAOp(firehoseData));
        list = null;
        firehoseData = null;
        ackCount += 1;
    }

    private void exception(final Exception e) {
        e.printStackTrace();
        try {
            Plant.close();
        } catch (final Exception e1) {
            e1.printStackTrace();
            return;
        }
    }

    private void add() {
        ndx += 1;
        list.add(ndx);
    }

    @Override
    public void run() {
        if (ack == null)
            return;
        while ((ndx < count) && (ackCount < maxWindowSize)) {
            createList();
            add();
            try {
                send();
            } catch (final Exception e) {
                exception(e);
            }
        }
        if (ndx >= count) {
            return;
        }
        createList();
        while (getReactor().isInboxEmpty() && (ndx < count)) {
            add();
        }
    }
}