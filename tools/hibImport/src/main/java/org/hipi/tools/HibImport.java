package org.hipi.tools;

import org.hipi.imagebundle.HipiImageBundle;
import org.hipi.image.HipiImageHeader.HipiImageFormat;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FSDataInputStream;

import java.io.File;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

import java.net.URL;
import java.net.URLClassLoader;
 
public class HibImport {

  private static final Options options = new Options();
  private static final Parser parser = (Parser)new BasicParser();
  static {
    options.addOption("f", "force", false, "force overwrite if output HIB already exists");
    options.addOption("h", "hdfs-input", false, "assume input directory is on HDFS");
  }

  private static void usage() {
    // usage
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("hibImport.jar [options] <image directory> <output HIB>", options);
    System.exit(0);
  }

  public static void main(String[] args) throws IOException  {

    // Attempt to parse the command line arguments
    CommandLine line = null;
    try {
      line = parser.parse(options, args);
    }
    catch( ParseException exp ) {
      usage();
    }
    if (line == null) {
      usage();
    }

    String [] leftArgs = line.getArgs();
    if (leftArgs.length != 2) {
      usage();
    }

    String imageDir = leftArgs[0];
    String outputHib = leftArgs[1];

    boolean overwrite = false;
    if (line.hasOption("f")) {
      overwrite = true;
    }

    boolean hdfsInput = false;
    if (line.hasOption("h")) {
      hdfsInput = true;
    }

    System.out.println("Input image directory: " + imageDir);
    System.out.println("Input FS: " + (hdfsInput ? "HDFS" : "local FS"));
    System.out.println("Output HIB: " + outputHib);
    System.out.println("Overwrite HIB if it exists: " + (overwrite ? "true" : "false"));

    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(conf);

    if (hdfsInput) {

      FileStatus[] files = fs.listStatus(new Path(imageDir));
      if (files == null) {
        System.err.println(String.format("Did not find any files in the HDFS directory [%s]", imageDir));
        System.exit(0);
      }
      Arrays.sort(files);

      HipiImageBundle hib = new HipiImageBundle(new Path(outputHib), conf);
      hib.openForWrite(overwrite);

      for (FileStatus file : files) {
        FSDataInputStream fdis = fs.open(file.getPath());
        String source = file.getPath().toString();
        HashMap<String, String> metaData = new HashMap<String,String>();
        metaData.put("source", source);
        String fileName = file.getPath().getName().toLowerCase();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (suffix.compareTo(".jpg") == 0 || suffix.compareTo(".jpeg") == 0) {
         hib.addImage(fdis, HipiImageFormat.JPEG, metaData);
         System.out.println(" ** added: " + fileName);
       } else if (suffix.compareTo(".png") == 0) {
         hib.addImage(fdis, HipiImageFormat.PNG, metaData);
         System.out.println(" ** added: " + fileName);
       } else if (suffix.compareTo(".tif") == 0) {
         hib.addImage(fdis, HipiImageFormat.TIF, metaData);
         System.out.println(" ** added: " + fileName);
       }
     }

     hib.close();

    } else {

      ArrayList<String> dirs = new ArrayList<String>();
      dirs.add(imageDir);
      HipiImageBundle hib = new HipiImageBundle(new Path(outputHib), conf);
      hib.openForWrite(overwrite);

      while (dirs.size() > 0){
          String nextDir = dirs.get(0);
	  dirs.remove(0);
	  File folder = new File(nextDir);
	  File[] files = folder.listFiles();

	  if (files == null) {
	    System.err.println(String.format("Did not find any files in the local FS directory [%s]", imageDir));
	  } else {

	      Arrays.sort(files);
	      for (File file : files) {
	          if (file.isDirectory() == true) {
		      System.out.println("Directory " + file.getPath());
		      dirs.add(file.getPath());
		  }
		  if (Files.isRegularFile(file.toPath()) == false)
		      continue;
		  FileInputStream fis;
		  try {
		      fis = new FileInputStream(file);
		  }catch(Exception ex){
		      System.out.println("Can't open " + file.getPath() + " " 
		                         + ex.getMessage());
		      continue;
		  }
		  String localPath = file.getPath();
		  HashMap<String, String> metaData = new HashMap<String,String>();
                  String fileName = file.getName().toLowerCase();
		  int idx = fileName.lastIndexOf('.');
		  if (idx != -1) {
		      String suffix = 
		             fileName.substring(idx);
		      try {
			  if (suffix.compareTo(".jpg") == 0 || 
			     suffix.compareTo(".jpeg") == 0) {
			    hib.addImage(fis, HipiImageFormat.JPEG, metaData);
			    System.out.println(" ** added: " + fileName);
			  }
			  else if (suffix.compareTo(".png") == 0) {
			      hib.addImage(fis, HipiImageFormat.PNG, metaData);
			      System.out.println(" ** added: " + fileName);
			  } else if (suffix.compareTo(".tif") == 0) {
			     hib.addImage(fis, HipiImageFormat.TIF, metaData);
			     System.out.println(" ** added: " + fileName);
			  } 
			  metaData.put("source", localPath);
		     } catch(Exception ex) {
		         System.out.println("Exception Skipping file " + localPath + " " + ex.getMessage());
		     }
	         }
		 fis.close();
             }
          }
      }
      hib.close();
    }

    
    System.out.println("Created: " + outputHib + " and " + outputHib + ".dat");
  }

}
