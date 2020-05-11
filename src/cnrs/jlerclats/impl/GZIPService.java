package cnrs.jlerclats.impl;

import java.io.IOException;
import java.net.InetAddress;

import cnrs.jlerclats.Access;
import cnrs.jlerclats.CommandLineApplicationInput;
import cnrs.jlerclats.CommandLineApplicationOutput;
import cnrs.jlerclats.DefaultSSHListener;
import cnrs.jlerclats.FileProcessingService;
import cnrs.jlerclats.ServiceException;
import toools.io.file.RegularFile;

public class GZIPService extends FileProcessingService
{
	public GZIPService(Access sshConfig)
	{
		super(sshConfig, "gzip");
	}

	public RegularFile gzip(RegularFile f, boolean overwrite) throws ServiceException, IOException
	{
		CommandLineApplicationInput in = new CommandLineApplicationInput();
		in.cmdLineParameters.add(f);
		CommandLineApplicationOutput out = doitAndEnsureExitStatusIsZero(in);
		return retrieveFile(f.getName() + ".gz", overwrite);
	}

	public static void main(String[] args) throws ServiceException, IOException
	{
		String filename = "test.txt";
		String hostname = "musclotte.inria.fr";
		RegularFile f = new RegularFile(filename);

		Access sshConfig = new Access(InetAddress.getByName(hostname));
		sshConfig.setReuseConnection(true);
		GZIPService s = new GZIPService(sshConfig);
		s.setSpecificListener(new DefaultSSHListener());
		s.getSSHConfig().setAliveMessageIntervalS(100);

		
		RegularFile zipFile = s.gzip(f, true);
		System.out.println(zipFile);
		RegularFile zipFile2 = s.gzip(f, true);
		System.out.println(zipFile2);
		RegularFile zipFile3 = s.gzip(f, true);
		System.out.println(zipFile2);
		RegularFile zipFile4 = s.gzip(f, true);
		System.out.println(zipFile2);
		s.ensureConnectionClosed();
	}
}
