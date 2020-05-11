package cnrs.jlerclats.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import cnrs.jlerclats.Access;
import cnrs.jlerclats.CommandLineApplicationInput;
import cnrs.jlerclats.CommandLineApplicationOutput;
import cnrs.jlerclats.Service;
import cnrs.jlerclats.ServiceException;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class DownloadService extends Service
{
	public DownloadService(Access sshConfig)
	{
		super(sshConfig, "cat ");
	}

	public byte[] download(String filename) throws ServiceException, IOException
	{
		if (filename.trim().isEmpty())
			throw new IllegalArgumentException("empty file name");

		CommandLineApplicationInput r = new CommandLineApplicationInput();
		r.cmdLineParameters.add(filename);
		CommandLineApplicationOutput out = doitAndEnsureExitStatusIsZero(r);
		return out.stdout;
	}

	public void download(String filename, RegularFile outFile, boolean overwrite)
			throws ServiceException, IOException
	{
		if (outFile.exists())
		{
			if (overwrite)
			{
				outFile.setContent(download(filename));
			}
			else
			{
				throw new IOException("file already exists: " + outFile.getPath());
			}
		}
		else
		{
			outFile.setContent(download(filename));
		}
	}

	public List<RegularFile> download(List<String> filenames, Directory targetDirectory,
			boolean overwrite) throws ServiceException, IOException
	{
		List<RegularFile> files = new ArrayList<RegularFile>();

		for (String filename : filenames)
		{
			RegularFile targetFile = targetDirectory.getChildRegularFile(filename);
			download(filename, targetFile, overwrite);
			files.add(targetFile);
		}

		return files;
	}

	public static void main(String[] args) throws ServiceException, IOException
	{
		Access sshConfig = new Access(InetAddress.getByName("musclotte"));
		byte[] bytes = new DownloadService(sshConfig).download("hs_err_pid10731.lo");

		System.out.println(new String(bytes));
		System.out.println("ok");
	}
}
