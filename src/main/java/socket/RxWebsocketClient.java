package socket;

import com.appunite.websocket.*;
import com.appunite.websocket.internal.SecureRandomProviderImpl;
import com.appunite.websocket.internal.SocketProviderImpl;
import rx.*;
import rx.exceptions.Exceptions;
import rx.internal.schedulers.ScheduledAction;
import rx.internal.util.IndexedRingBuffer;
import rx.schedulers.Schedulers;


import com.appunite.websocket.*;

import java.net.URI;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static java.util.Arrays.asList;

public class RxWebsocketClient {
  private NewWebSocket newWebSocket;
  private WebSocketConnection connection;

  public RxWebsocketClient(String ServerURI) {
    newWebSocket = new NewWebSocket(new SecureRandomProviderImpl(), new SocketProviderImpl());
    URI uri = URI.create("ws://echo.websocket.org");

    try {
      connection = newWebSocket.create(uri, new WebSocketListener() {
        @Override
        public void onConnected() throws IOException, InterruptedException, NotConnectedException {
        }

        @Override
        public void onStringMessage(String message) throws IOException, InterruptedException, NotConnectedException {
          System.out.println("I'm the second and I received: " + message);
        }

        @Override
        public void onBinaryMessage(byte[] data) throws IOException, InterruptedException, NotConnectedException {

        }

        @Override
        public void onPing(byte[] data) throws IOException, InterruptedException, NotConnectedException {

        }

        @Override
        public void onPong(byte[] data) throws IOException, InterruptedException, NotConnectedException {

        }

        @Override
        public void onServerRequestedClose(byte[] data) throws IOException, InterruptedException, NotConnectedException {

        }

        @Override
        public void onUnknownMessage(byte[] data) throws IOException, InterruptedException, NotConnectedException {

        }
      });

      connection.connect();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (WrongWebsocketResponse wrongWebsocketResponse) {
      wrongWebsocketResponse.printStackTrace();
    }
  }
}
