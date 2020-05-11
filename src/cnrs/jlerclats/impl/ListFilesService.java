package cnrs.jlerclats.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import toools.text.TextUtilities;
import cnrs.jlerclats.Access;
import cnrs.jlerclats.CommandLineApplicationInput;
import cnrs.jlerclats.CommandLineApplicationOutput;
import cnrs.jlerclats.DefaultSSHListener;
import cnrs.jlerclats.Service;
import cnrs.jlerclats.ServiceException;

public class ListFilesService extends Service
{
	public ListFilesService(Access sshConfig)
	{
		super(sshConfig, "ls");
	}

	public List<String> ls(String... pattern) throws ServiceException, IOException
	{
		CommandLineApplicationInput r = new CommandLineApplicationInput();

		for (String p : pattern)
		{
			r.cmdLineParameters.add(p);
		}

		CommandLineApplicationOutput out = doitAndEnsureExitStatusIsZero(r);
		return TextUtilities.splitInLines(new String(out.stdout));
	}

	public static void main(String[] args) throws ServiceException, IOException
	{
		Access sshConfig = new Access(InetAddress.getByName("musclotte.inria.fr"));
		sshConfig.setReuseConnection(true);
		ListFilesService LsService = new ListFilesService(sshConfig);
		LsService.setSpecificListener(new DefaultSSHListener());
		System.out.println(LsService.ls());
		System.out.println(LsService.ls());
		System.out.println(LsService.ls());
		System.out.println(LsService.ls());
		LsService.ensureConnectionClosed();
	}
}
