package de.mhus.cur.core.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;

import de.mhus.cur.api.Conductor;
import de.mhus.cur.api.ConductorPlugin;
import de.mhus.cur.api.CurUtil;
import de.mhus.cur.api.ExecutePlugin;
import de.mhus.cur.api.Plugin;
import de.mhus.cur.core.ConductorImpl;
import de.mhus.cur.core.ConfigTypesImpl;
import de.mhus.cur.core.ConfiguratorDefault;
import de.mhus.cur.core.ContextImpl;
import de.mhus.cur.core.ExecutorDefault;
import de.mhus.cur.core.FileScheme;
import de.mhus.cur.core.MavenScheme;
import de.mhus.cur.core.SchemesImpl;
import de.mhus.cur.core.YmlConfigType;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.NotFoundException;

public class SchemeTest {

	@Test
	public void testMavenScheme() throws IOException, NotFoundException {
		MavenScheme scheme = new MavenScheme();
        Conductor cur = new ConductorImpl(new File("../example/sample-parent"));
        
        String mvnPath = CurUtil.cmdLocation("mvn");
        if (new File(mvnPath).exists()) {
	        ((MProperties)cur.getProperties()).put(CurUtil.PROPERTY_MVN_PATH, mvnPath);
	        
			MUri uri = MUri.toUri("mvn:com.google.guava/guava/15.0");
			
			File loc = scheme.getArtifactLocation("TEST",cur, uri);
			System.out.println(loc);
			
			if (loc.exists())
				loc.delete();
			
			scheme.load(cur, uri);
			
			assertTrue(loc.exists());
        } else {
        	System.err.println("Maven not found, skip test: " + mvnPath);
        }
	}
	
	@Test
	public void loadPlugin() throws Exception {
		MApi.setDirtyTrace(false);
        ConductorImpl cur = new ConductorImpl(new File("../example/sample-parent"));

        String mvnPath = CurUtil.cmdLocation("mvn");
        if (new File(mvnPath).exists()) {

	        ConfiguratorDefault config = new ConfiguratorDefault();
	        ((SchemesImpl)config.getSchemes()).put("file", new FileScheme() );
	        ((SchemesImpl)config.getSchemes()).put("mvn", new DummyScheme() );
	        ((ConfigTypesImpl)config.getTypes()).put("yml", new YmlConfigType());
	        ((ConfigTypesImpl)config.getTypes()).put("yaml", new YmlConfigType());

	        URI uri = URI.create("file:conductor.yml");
	        config.configure(uri, cur, null);

	        ((MProperties)cur.getProperties()).put(CurUtil.PROPERTY_MVN_PATH, mvnPath);
	        ((MProperties)cur.getProperties()).put("deploy.version", TestUtil.currentVersion());
	        ((SchemesImpl)cur.getSchemes()).put("mvn", new MavenScheme());

	        ContextImpl context = new ContextImpl(cur);
	        context.init(null, cur.getPlugins().get("test"), null);
	        
	        ExecutorDefault exec = new ExecutorDefault();
	        
	        Plugin plugin = context.getPlugin();

	        ConductorPlugin mojo = exec.createMojo(cur, plugin);
	        
	        assertNotNull(mojo);
	        ((ExecutePlugin)mojo).execute(context);
	        
        } else {
        	System.err.println("Maven not found, skip test: " + mvnPath);
        }
	}
	
}
