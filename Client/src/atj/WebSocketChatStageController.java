package atj;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class WebSocketChatStageController
{
	@FXML
	TextField userTextField;
	@FXML
	TextArea chatTextArea;
	@FXML
	TextField messageTextField;
	@FXML
	Button btnSet;
	@FXML
	Button btnSend;
	@FXML
	ListView<String> attachmentsListView;
	
	private String user;
	private WebSocketClient webSocketClient;
	private FileHandler fileHandler;

	@FXML
	private void initialize() 
	{
		webSocketClient = new WebSocketClient();
		user = "User" + (webSocketClient.session.hashCode()%999) ;
		userTextField.setText(user);
		fileHandler = new FileHandler();
	}

	@FXML
	private void btnSet_Click() 
	{
		changeUsername();
	}
	
	@FXML
	private void userTextField_KeyPressed(KeyEvent e )
	{
		if(e.getCode() == KeyCode.ENTER)  changeUsername();
	}

	@FXML
	private void btnSend_Click() 
	{
		sendMessage();
	}
	
	@FXML
	private void messageTextField_KeyPressed(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER)  sendMessage();
	}
	
	@FXML
	private void btnAdd_Click()
	{
		File file = fileHandler.chooseFile();
		
		if(file != null)
		{
			webSocketClient.sendMessage(file.getName(), true);
			webSocketClient.sendFile(file);
		}
	}
	
	@FXML
	private void btnDownload_Click()
	{
		int index = attachmentsListView.getSelectionModel().getSelectedIndex();

		if(index != -1) fileHandler.downloadFile(index);
	}
	
	private void sendMessage()
	{
		if(!messageTextField.getText().trim().isEmpty() )
		{
			webSocketClient.sendMessage(messageTextField.getText(), false);
			messageTextField.clear();
		}
	}
	
	private void changeUsername()
	{
		if (userTextField.getText().trim().isEmpty())
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Wrong Username");
			alert.setHeaderText(null);
			alert.setContentText("Username can not be empty!");
			alert.showAndWait();
			
			userTextField.setText(user);
			return;
		}
		
		if(user.equals(userTextField.getText())) return;
	
		webSocketClient.sendMessage(" CHANGED HIS NICKNAME TO: " + userTextField.getText(), false);
		user = userTextField.getText();
	}

	public void closeSession(CloseReason closeReason)
	{
		try 
		{
			webSocketClient.session.close(closeReason);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@ClientEndpoint
	public class WebSocketClient
	{
		private Session session;

		public WebSocketClient() 
		{
			connectToWebSocket();
		}

		@OnOpen
		public void onOpen(Session session)
		{
			System.out.println("Connection is opened.");
			this.session = session;
		}

		@OnClose
		public void onClose(CloseReason closeReason)
		{
			System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
		}

		@OnError
		public void onError(Throwable throwable)
		{
			System.out.println("Error occured");
			throwable.printStackTrace();
		}

		@OnMessage
		public void onMessage(final String message, Session session)
		{
			System.out.println("Message was received");
			
			if(message.charAt(message.length() - 1) == '0')
			{
				chatTextArea.setText(chatTextArea.getText() + message.substring(0, message.length() - 1) + "\n");
			}
			else
			{
				Platform.runLater( () -> attachmentsListView.getItems().add(message.substring(0, message.length() - 1)) );
			}
		}
		
		@OnMessage
		public void onMessage(ByteBuffer buf, Session session)
		{
			System.out.println("File was received");
			fileHandler.addFile(buf);
		}
		
		private void connectToWebSocket()
		{
			WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
			try 
			{
				URI uri = URI.create("ws://localhost:8080/Server/websocketendpoint");
				webSocketContainer.connectToServer(this, uri);
			} 
				catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message, boolean isFile)
		{
			try
			{
				if(isFile == false)
				{
					System.out.println("Message was sent: " + message);
					session.getBasicRemote().sendText(user + ": " + message + "0");
				}
				else
				{
					session.getBasicRemote().sendText(user + ": Sent a file: " + message + "0");
					session.getBasicRemote().sendText(user + ": " + message + "1");
				}
			} 
			catch(IOException ex)
			{ 
				ex.printStackTrace(); 
			}
		}
		
		public void sendFile(File file)
		{
			try
			{
				ByteBuffer buf = fileHandler.converFileToByteBuffer(file);
				session.getBasicRemote().sendBinary(buf);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}// publicclasWebSocketClient
}// public classWebSocketChatStageControler
