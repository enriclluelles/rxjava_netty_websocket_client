package socket;

import rx.*;
import rx.exceptions.Exceptions;
import rx.internal.schedulers.ScheduledAction;
import rx.internal.util.IndexedRingBuffer;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static java.util.Arrays.asList;

public class Main {
  public static void main(String[] args) throws IOException {
    RxWebsocketClient client = new RxWebsocketClient("");
  }
}
