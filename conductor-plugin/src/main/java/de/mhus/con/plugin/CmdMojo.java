package de.mhus.con.plugin;

import java.io.File;

import de.mhus.con.api.AMojo;
import de.mhus.con.api.ConUtil;
import de.mhus.con.api.Context;
import de.mhus.con.api.MojoException;
import de.mhus.lib.core.MString;

@AMojo(name="cmd")
public class CmdMojo extends AbstractMavenExecute {

	@Override
	public boolean execute2(File dir, String moduleName, Context context) throws Exception {
		String cmd = MString.join(context.getStep().getArguments(), " "); //TODO add quotes and or escapes
		if (context.getConductor().getProperties().getBoolean(ConUtil.PROPERTY_TRY, false)) {
		    log().i("Would Execute",cmd,dir);
		    return true;
		}
		String[] res = ConUtil.execute(getCmdName(context, moduleName), dir, cmd, true);
		if (!res[2].equals("0") && !context.getProperties().getBoolean(ConUtil.PROPERTY_STEP_IGNORE_RETURN_CODE, false))
		    throw new MojoException(context, "not successful",cmd,res[1],res[2]);
		return true;
	}

}
