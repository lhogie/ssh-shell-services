package cnrs.jlerclats.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import cnrs.jlerclats.Access;
import cnrs.jlerclats.CommandLineApplicationInput;
import cnrs.jlerclats.CommandLineApplicationOutput;
import cnrs.jlerclats.Service;
import cnrs.jlerclats.ServiceException;
import toools.text.TextUtilities;

public class WhoService extends Service
{
	public WhoService(Access sshConfig)
	{
		super(sshConfig, "who | awk '{print $1;}'");
	}

	public List<String> who() throws ServiceException, IOException
	{
		CommandLineApplicationInput in = new CommandLineApplicationInput();
		CommandLineApplicationOutput out = doitAndEnsureExitStatusIsZero(in);
		return TextUtilities.splitInLines(new String(out.stdout));
	}

	public static void main(String[] args) throws ServiceException, IOException
	{
		Access sshConfig = new Access(InetAddress.getByName("musclotte.inria.fr"));
		List<String> who = new WhoService(sshConfig).who();
		System.out.println(who);
	}
}
