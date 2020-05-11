package cnrs.jlerclats;


public class ServiceException extends Exception
{

	private CommandLineApplicationOutput o;

	public ServiceException(Throwable e)
	{
		super(e);
	}

	public ServiceException(String e)
	{
		super(e);
	}

	public ServiceException(CommandLineApplicationOutput r)
	{
		this.o = r;
	}

	@Override
	public String toString()
	{
		if (o == null)
		{
			return super.toString();
		}
		else
		{
			return "exit status: "+ o.exitStatus + " stderr: " + o.stderr;
		}
	}
}
