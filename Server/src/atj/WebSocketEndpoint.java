package atj;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/websocketendpoint")
public class WebSocketEndpoint {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Started Session with id: " + session.getId());
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("Closed Session with id: " + session.getId());
	}
	
	@OnError
	public void onError(Throwable error) {
		System.out.println("Error occured: "+ error.getMessage());
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			for (Session oneSession : session.getOpenSessions())
			{
				if (oneSession.isOpen())
				{
					oneSession.getBasicRemote().sendText(message);
				}
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	@OnMessage
	public void onMessage(ByteBuffer buf, Session session)
	{

		try 
		{
			for (Session oneSession : session.getOpenSessions())
			{
				if (oneSession.isOpen())
				{
					oneSession.getBasicRemote().sendBinary(buf);
				}
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}