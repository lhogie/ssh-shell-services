package cnrs.jlerclats;

import java.util.ArrayList;
import java.util.List;

public class CommandLineApplicationInput
{
	public byte[] stdin;
	
	// contains String, numbers, files, etc so it cannot be parameterized
	public final List cmdLineParameters = new ArrayList();
}
