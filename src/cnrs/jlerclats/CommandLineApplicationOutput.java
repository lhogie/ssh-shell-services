package cnrs.jlerclats;


public class CommandLineApplicationOutput
{
	public byte[] stdout;
	public String stderr;
	public int exitStatus;

	@Override
	public String toString()
	{
		return "[stdout=" + new String(stdout) + ", stderr=" + stderr + ", returnCode=" + exitStatus + "]";
	}

}
