package atj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javafx.stage.FileChooser;

public class FileHandler
{
	private ArrayList<Attachment> attachedFiles = new ArrayList<Attachment>();

	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose which file you want to attach");
		File file = fileChooser.showOpenDialog(null);

		return file;
	}

	public void addFile(ByteBuffer buf, String sessionID)
	{
		int index = getWritableIndex(sessionID);
		
		if( index != -1 )
		{
			attachedFiles.get(index).add(buf);
			return;
		}
		
		attachedFiles.add(new Attachment(sessionID, buf));
		
	}

	public void downloadFile(int index)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose where you want to save this file");
		File file = fileChooser.showSaveDialog(null);

		if (file != null)
		{
			FileOutputStream fos;
			FileInputStream fis;
			FileChannel src;
			FileChannel dest;
			try 
			{
				fis = new FileInputStream(attachedFiles.get(index).getFile());
				fos = new FileOutputStream(file);
				
				src = fis.getChannel();
				dest = fos.getChannel();
				
				dest.transferFrom(src, 0, src.size());
				
				fis.close();
				fos.close();
				src.close();
				dest.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void stopReceiving(String sessionID)
	{
		
			attachedFiles.get( getWritableIndex(sessionID) ).stopReceving();
		
	}
	
	public int getWritableIndex(String sessionID)
	{
		for(int i = 0 ; i < attachedFiles.size() ; ++i)
		{
			if( attachedFiles.get(i).checkWritable(sessionID) ) return i;
		}
		
		return -1;
	}
}
