package atj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Attachment
{
	private File file;
	private String sessionID;
	private boolean isReceiving;
	
	public Attachment(String sID, ByteBuffer buf)
	{
		setReceiving(true);
		setSession(sID);
		
		try 
		{
			setFile(File.createTempFile("temp", ".tmp"));
			add(buf);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public boolean checkWritable(String sID)
	{
		if( !(isReceiving && sessionID.equals(sID)) ) return false;
		
		return true;
	}
	
	public void add(ByteBuffer buf)
	{
		FileOutputStream fos;
		FileChannel channel;
		try
		{
			fos = new FileOutputStream(file , true) ;
			channel = fos.getChannel();
			channel.write(buf);
			channel.close();
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void stopReceving()
	{
		setReceiving(false);
	}
	
	public void setReceiving(boolean bool)
	{
		isReceiving = bool;
	}
	
	public void setSession(String sID)
	{
		sessionID = sID;
	}
	
	public void setFile(File f)
	{
		file = f;
	}
	
	public File getFile()
	{
		return file;
	}
}
