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
	boolean isReceiving = false;
	
	private ArrayList<File> attachedFiles = new ArrayList<File>();

	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose which file you want to attach");
		File file = fileChooser.showOpenDialog(null);

		return file;
	}

	public void addFile(ByteBuffer buf)
	{
		try 
		{
			if(!isReceiving)
			{
				attachedFiles.add(File.createTempFile("temp", ".tmp"));
				isReceiving = true;
			}
			
			FileOutputStream fos;
			FileChannel channel;
				
			fos = new FileOutputStream( attachedFiles.get( attachedFiles.size() - 1 ) , true) ;
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
				fis = new FileInputStream(attachedFiles.get(index));
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
	
	public void stopReceiving()
	{
		isReceiving = false;
	}
}
