import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class StackOverflow extends NonBlockingBladeBase {
    
    void recur() {
        recur();
    }

    SyncRequest<Void> recurSReq() {
        return new SyncBladeRequest() {
            @Override
            public Void processSyncRequest() {
                recur();
                return null;
            }
        };
    }
    
    public static void main(final String[] args) throws Exception {
        Plant plant = new Plant();
        try {
            new StackOverflow().recurSReq().call();
        } catch (final ReactorClosedException _rce) {
            System.out.println("\nCaught " + _rce + "\n");
        } finally {
            plant.close();
        }
    }
}