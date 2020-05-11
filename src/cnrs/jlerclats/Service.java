package cnrs.jlerclats;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import toools.io.Utilities;
import toools.text.TextUtilities;
import toools.thread.Threads;

public class Service
{
	private final String cmdLine;
	private final Access sshConfig;
	private SSHListener specificListener = null;
	private int nbCalls;

	public static SSHListener globalListener = new SilentSSHListener();
	// public static SSHListener globalListener = new DefaultSSHListener();

	public Service(Access sshConfig, String cmdLine)
	{
		this.cmdLine = cmdLine;
		this.sshConfig = sshConfig;
	}

	public Access getSSHConfig()
	{
		return sshConfig;
	}

	public SSHListener getListener()
	{
		if (specificListener == null)
		{
			return globalListener;
		}
		else
		{
			return specificListener;
		}
	}

	public void setSpecificListener(SSHListener l)
	{
		this.specificListener = l;
	}

	public SSHListener getSpecificListener()
	{
		return specificListener;
	}

	public String getName()
	{
		return cmdLine;
	}

	public int getNumberOfCalls()
	{
		return nbCalls;
	}

	public synchronized CommandLineApplicationOutput doitAndEnsureExitStatusIsZero(
			CommandLineApplicationInput input) throws ServiceException, IOException
	{
		CommandLineApplicationOutput r = doit(input);

		if (r.exitStatus != 0)
		{
			throw new ServiceException(r);
		}

		return r;
	}

	public synchronized CommandLineApplicationOutput doit(
			CommandLineApplicationInput input) throws ServiceException, IOException
	{
		++nbCalls;

		if (input == null)
			throw new IllegalArgumentException("null input");

		try
		{
			if (sshConfig.sessionCache == null)
			{
				getListener().connectingUsing(sshConfig);
				sshConfig.sessionCache = createSSHSession(sshConfig);
				sshConfig.nbUse = 0;
			}
			else
			{
				++sshConfig.nbUse;
			}

			getListener().running(cmdLine, input.cmdLineParameters);
			CommandLineApplicationOutput res = runShellCommand(sshConfig.sessionCache,
					cmdLine, input);

			if ( ! sshConfig.reuseConnection)
			{
				ensureConnectionClosed();
			}

			return res;
		}
		catch (Throwable e)
		{
			throw new ServiceException(e);
		}
	}

	public void ensureConnectionClosed()
	{
		if (sshConfig.sessionCache != null)
		{
			getListener().disconnecting();
			sshConfig.sessionCache.disconnect();
			sshConfig.sessionCache = null;
			getListener().disconnected();
		}
	}

	private Session createSSHSession(Access sshConfig) throws JSchException
	{
		JSch jsch = new JSch();
		jsch.addIdentity(sshConfig.getPrivateKeyFile().getPath());
		jsch.setKnownHosts(sshConfig.getKnownHostFile().getPath());
		Session session = jsch.getSession(sshConfig.getUserName(),
				sshConfig.getServer().getHostName(), 22);

		// bypass .ssh/known_hosts file
		Properties props = new Properties();
		props.put("StrictHostKeyChecking", "no");
		session.setConfig(props);

		session.connect();

		if (sshConfig.getAliveMessageIntervalS() > 0)
		{
			session.setServerAliveCountMax(Integer.MAX_VALUE);
			session.setServerAliveInterval(sshConfig.getAliveMessageIntervalS());
		}

		return session;
	}

	private static CommandLineApplicationOutput runShellCommand(Session session,
			String name, CommandLineApplicationInput input)
			throws JSchException, IOException
	{
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(
				name + " " + TextUtilities.concatene(input.cmdLineParameters, " "));
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		InputStream err = channel.getErrStream();
		channel.connect();

		if (input.stdin != null)
		{
			out.write(input.stdin);
			out.close();
		}

		CommandLineApplicationOutput output = new CommandLineApplicationOutput();
		output.stdout = Utilities.readUntilEOF(in);
		output.stderr = new String(Utilities.readUntilEOF(err));

		while ( ! channel.isClosed())
		{
			Threads.sleepMs(100);
		}

		output.exitStatus = channel.getExitStatus();
		channel.disconnect();
		return output;
	}
}
