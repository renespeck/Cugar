package de.uni_leipzig.bf.cluster;
import gnu.getopt.Getopt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import de.uni_leipzig.bf.eval.Timer;
import de.uni_leipzig.cugar.harden.Harden;
import de.uni_leipzig.cugar.harden.HardenMaxQuality;
import de.uni_leipzig.cugar.harden.HardenSuperset;
import de.uni_leipzig.cugar.harden.QualityMeasure;
import de.uni_leipzig.cugar.harden.QualityMeasureRelativeFlow;
import de.uni_leipzig.cugar.harden.QualityMeasureSilhouette;

public class Main {

	public static enum HardenStrategy{OFF,SUPERSET,RELATVE_FLOW,SILHOUETTE};
	/**
	 *
	 *BorderFlow CLI version.
	 *
	 */
	public static void borderFlowDemo(final String input, final String output,final double threshold, final boolean mode,
			final boolean heuristic,final boolean caching, final HardenStrategy hardenStrategy){

		if(hardenStrategy.equals(HardenStrategy.OFF)){
			new BorderFlow(input,null).clusterToFile(output, threshold, mode, heuristic);
			return;
		}

		Harden harden = null;
		QualityMeasure qualityMeasure = null;
		switch(hardenStrategy){
		case SUPERSET: 
			harden = new HardenSuperset();
			break;
		case SILHOUETTE: 
			qualityMeasure = new QualityMeasureSilhouette();
			harden = new HardenMaxQuality(qualityMeasure);
			break;
		case RELATVE_FLOW:
			qualityMeasure = new QualityMeasureRelativeFlow();
			harden = new HardenMaxQuality(qualityMeasure);			
		}
		// use hardening
		final BorderFlow bf = new BorderFlow(input,null);				
		Map m = bf.cluster(threshold, mode, heuristic);
		System.out.println("*  Hardening starts with: " + m.size());
		System.out.print("*  " + harden.toString());	

		if(qualityMeasure!=null)
			System.out.print(" with " + qualityMeasure.toString());			

		final long startSystemTimeNano = Timer.getSystemTime();
		final long startUserTimeNano = Timer.getUserTime();
		m = harden.harden(m, bf.getGraph());
		final long taskUserTimeNano = Timer.getUserTime() - startUserTimeNano;
		final long taskSystemTimeNano = Timer.getSystemTime() - startSystemTimeNano;

		final String s ="\n*  Time in ms.\n*  User: " + taskUserTimeNano / 1000000
		+ "\n*  System: " + taskSystemTimeNano / 1000000
		+ "\n*  CPU: " + (taskUserTimeNano + taskSystemTimeNano)/ 1000000;
		System.out.println(s);					
		harden.removeDuplicateAndEmptyClusters(m);	
		System.out.println("*  Hardening ends with: " + m.size());	
		System.out.println("*********************************************");

		try {
			final PrintWriter writer = new PrintWriter(new BufferedWriter( new FileWriter(output)));
			writer.println(bf.writeToString(m));
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/** Main method arguments information.*/
	public static String usage() {
		return 	""
		+ "-i:\tInput file or directory. \n"
		+ "-o:\tOutput file or directory.\n"
		+ "-m:\tChoose mode for testing termination.\n\tTRUE chooses test on one node. FALSE uses the whole Cf(X) to test termination."
		+"\n\tDefault is FALSE. \n"
		+ "-h:\tSwitch for using either heuristic version of clustering (for large graphs, faster) or optimal version."
		+ "\n\tDefault is TRUE. \n"
		+ "-t:\tThreshold gives the percentage of the maximal connectivity that a node can maximally have to be used as seed.\n"
		+ "\tDefault is 1.0, use ALL for all nodes as seeds.\n"
		+ "-c:\tSwitch for cache version.\n"
		+ "\tDefault is TRUE.\n"

		//		+ "-p:\tProcessing strategy for hardening.\n"
		//		+ "\tDefault is soft clustering, use SUPERSET or MAXQUALITY\n"
		//		+ "-q:\tQuality measure for hardening. Only available for MAXQUALITY strategy with RELATIVEFLOW or SILHOUETTE.\n"
		//		+ "\tDefault is set to RELATIVEFLOW.\n";

		+ "-p:\tProcessing strategy for hardening.\n"
		+ "\tDefault is soft clustering, use SUPERSET, RELATIVEFLOW or SILHOUETTE\n";
	}
	/**
	 * Entry point.
	 */
	public static void main(final String[] args) {
		org.apache.log4j.BasicConfigurator.configure();
		try {
			final Getopt g = new Getopt(Main.class.getClass().getName(), args, "i:o:t:m:h:c:p:q:x");
			String input = null, output = null;
			double threshold = 1;
			// This combination is the fastest at the moment
			boolean mode = true; // false = full insertion
			boolean heuristic = true; // true = use heuristic version
			boolean caching = true; // true = use caching.

			HardenStrategy hardenStrategy = Main.HardenStrategy.OFF;			

			boolean wrongArg = false;
			int arg;
			while ((arg = g.getopt()) != -1 && !wrongArg) {
				switch (arg) {
				case 'i':
					input = g.getOptarg();
					break;

				case 'o':
					output = g.getOptarg();
					break;

				case 't':
					if (g.getOptarg().toLowerCase().equals("all"))
						threshold = -1;
					else
						threshold = Double.parseDouble(g.getOptarg());
					break;

				case 'm':
					if ((g.getOptarg().toLowerCase().startsWith("f")))
						mode = false;
					break;

				case 'h':
					if ((g.getOptarg().toLowerCase().startsWith("f")))
						heuristic = false;
					break;

				case 'c':
					if ((g.getOptarg().toLowerCase().startsWith("f")))
						caching = false;
					break;

				case 'p':
					if ((g.getOptarg().toLowerCase().startsWith("su")))
						hardenStrategy = Main.HardenStrategy.SUPERSET;

					if ((g.getOptarg().toLowerCase().startsWith("re")))
						hardenStrategy = Main.HardenStrategy.RELATVE_FLOW;

					if ((g.getOptarg().toLowerCase().startsWith("si")))
						hardenStrategy = Main.HardenStrategy.SILHOUETTE;
					break;

				default:
					System.out.println(usage());
					return;
				}
			}

			if (input == null || output == null)
				System.out.println(usage());
			else if (new File(input).isDirectory()) {
				System.out.println("Input is directory");
				final String files[] = new File(input).list();
				for (int i = 0; i < files.length; i++) {
					System.out.println("Results will be written in " + output + "/" + files[i]);
					if (!new File(output).exists())
						new File(output).mkdir();
					borderFlowDemo(input + "/" + files[i], output + "/" + files[i],threshold, mode, heuristic,caching, hardenStrategy);
				}
			}else{
				System.out.println("Results will be written in " + output);
				borderFlowDemo(input, output ,threshold, mode, heuristic,caching, hardenStrategy);
			}

		} catch (final Exception e) {
			System.err.println("Error while initializing");
			e.printStackTrace();
		}
	}
}