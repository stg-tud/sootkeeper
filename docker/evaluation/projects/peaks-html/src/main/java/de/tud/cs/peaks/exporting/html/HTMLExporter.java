package de.tud.cs.peaks.exporting.html;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.MathTool;

import de.tud.cs.peaks.exporting.Exporter;
import de.tud.cs.peaks.results.PeaksResult;
import de.tud.cs.peaks.results.helper.ResultHelper;

public class HTMLExporter implements Exporter{
		
	@Override
	public void export(PeaksResult result, String path) {
		try{		
			// load bootstrap
			File bootstrap = new File(getClass().getResource("/template/bootstrap").toURI());
			File dir = new File(path);
			FileUtils.copyDirectory(bootstrap, dir);
			
			File images = new File(getClass().getResource("/template/images").toURI());
			FileUtils.copyDirectory(images, dir);

			// create output file
			File out = new File(path + "/result.html");
			out.getParentFile().mkdirs();
			out.createNewFile();
			
			// create properties for resource loading
			Properties props = new Properties();
			props.put("resource.loader", "class");
			props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			
			// create engine
			VelocityEngine ve = new VelocityEngine();
			ve.init(props);

			// filling data
			VelocityContext context = new VelocityContext();
			
			// push result to context stack
			context.put("result", result);
			context.put("resultHelper", new ResultHelper());
			context.put("math", new MathTool());

			// parse template
			Template template = ve.getTemplate("/template/template.vm");
			
			// write output
			FileWriter fw = new FileWriter(out);
			template.merge( context, fw );
			
			fw.close();
		}
		catch( ResourceNotFoundException rnfe )
		{
			System.err.println("Couldn't find the template");
			rnfe.printStackTrace();
		}
		catch( ParseErrorException pee )
		{
			System.err.println("Syntax error: problem parsing the template");
			pee.printStackTrace();
		}
		catch( MethodInvocationException mie )
		{
			System.err.println("Something invoked in the template threw an exception");
			mie.printStackTrace();
		  
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
