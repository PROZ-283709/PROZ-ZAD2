package atj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javafx.stage.FileChooser;

public class FileHandler
{

	private ArrayList<ByteBuffer> attachedFiles = new ArrayList<ByteBuffer>();

	public ByteBuffer converFileToByteBuffer(File file)
	{
		try 
		{
			ByteBuffer buf = ByteBuffer.allocateDirect((int) file.length());
			InputStream is = new FileInputStream(file);
			
			int b;
			while ((b = is.read()) != -1)
			{
				buf.put((byte) b);
			}

			is.close();
			buf.flip();
			return buf;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose which file you want to attach");
		File file = fileChooser.showOpenDialog(null);

		return file;
	}

	public void addFile(ByteBuffer buf)
	{
		attachedFiles.add(buf);
	}

	public void downloadFile(int index)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose where you want to save this file");
		File file = fileChooser.showSaveDialog(null);

		if (file != null)
		{
			try 
			{
				FileOutputStream str = new FileOutputStream(file, false);
				FileChannel channel = str.getChannel();

				channel.write(attachedFiles.get(index));
				str.close();

			}
			catch (IOException e)
			{

				System.out.println("I/O exception");

				e.printStackTrace();
			}
		}
	}
}
