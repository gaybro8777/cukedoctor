package com.github.cukedoctor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.cukedoctor.api.CukedoctorReporter;
import com.github.cukedoctor.api.DocumentAttributes;
import com.github.cukedoctor.api.model.Feature;
import com.github.cukedoctor.parser.FeatureParser;
import com.github.cukedoctor.util.FileUtil;
import org.asciidoctor.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.asciidoctor.OptionsBuilder.options;

/**
 * Created by pestano on 08/06/15.
 * <p/>
 * java -jar cukedoctor.jar -o docName -f html|pdf -p /path/to/cucumber-json-files/
 */
public class CukedoctorMain {

	@Parameter(names = "-f", description = "Document format - supported html5(default), html and pdf", required = false, echoInput = true)
	private String format;

	@Parameter(names = "-p", description = "Path to cucumber json output files (can be a directory or a file) ", required = true)
	private String path;

	@Parameter(names = "-t", description = "Documentation title (first section)", required = true)
	private String title;

	@Parameter(names = "-o", description = "File output name, can be a path eg: /home/doc which will result in doc.html|pdf in /home dir. Document title will be used if output name is not provided", required = false)
	private String outputName;


	public String execute(String args[]) {
		JCommander commandLine = null;
		try {
			commandLine = new JCommander(this);
			commandLine.parse(args);
		} catch (ParameterException pe) {
			commandLine.usage();
			throw pe;
		}
		if (outputName == null) {
			outputName = title.replaceAll(" ", "-");
		}

		if (format == null || (!format.equals("html") && !format.equals("html5") && !format.equals("pdf"))) {
			format = "html5";
		}


		System.out.println("Generating living documentation with args:");

		System.out.println("-f" + ": " + format);
		System.out.println("-p" + ": " + path);
		System.out.println("-t" + ": " + title);
		System.out.println("-o" + ": " + outputName);

		List<Feature> features = null;
		if (path.endsWith(".json")) {
			features = FeatureParser.parse(FileUtil.findJsonFile(path));
		} else {
			features = FeatureParser.parse(FileUtil.findJsonFiles(path));
		}

		if (features == null || features.isEmpty()) {
			System.out.println("No features found in path:" + path);
			return null;
		} else {
			System.out.println("Found " + features.size() + " feature(s)");
		}

		DocumentAttributes documentAttributes = new DocumentAttributes().backend(format);
		if(format.equalsIgnoreCase("pdf")){
			documentAttributes.pdfTheme(true).docInfo(false);
		}else {
			documentAttributes.docInfo(true).pdfTheme(false);
		}
		CukedoctorReporter reporter = Cukedoctor.instance(features, title,documentAttributes);
		//reporter filename needs to end with adoc
		String reporterFinename = "";
		if (outputName.contains(".")) {
			reporterFinename = outputName.substring(0, outputName.lastIndexOf(".")) + ".adoc";
		} else {
			reporterFinename = outputName + ".adoc";
		}
		reporter.setFilename(reporterFinename);
		String doc = reporter.renderDocumentation();
		File adocFile = FileUtil.saveFile(reporterFinename, doc);
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		asciidoctor.convertFile(adocFile, OptionsBuilder.options().backend(documentAttributes.getBackend()).safe(SafeMode.UNSAFE).asMap());

		return doc;
	}

	public static void main(String args[]) {
		CukedoctorMain main = new CukedoctorMain();
		main.execute(args);
	}

}