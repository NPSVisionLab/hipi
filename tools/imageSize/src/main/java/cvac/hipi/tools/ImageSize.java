package cvac.hipi.tools;

import org.hipi.image.FloatImage;
import org.hipi.image.ByteImage;
import org.hipi.image.HipiImageHeader;
import org.hipi.imagebundle.mapreduce.HibInputFormat;
import org.hipi.util.ByteUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Iterator;

public class ImageSize extends Configured implements Tool {

  public static class ImageSizeMapper extends Mapper<HipiImageHeader, ByteImage, Text, IntWritable> {
    
    private final static IntWritable one = new IntWritable(1);

    @Override
    public void map(HipiImageHeader header, ByteImage image, Context context) throws IOException, InterruptedException  {

      String output;
      if (header == null) {
       output = "Failed to read image header.";
     } else if (image == null) {
       output = "Failed to decode image data.";
     } else {
       int w = header.getWidth();
       int h = header.getHeight();
       //String source = header.getMetaData("source");
       //String cameraModel = header.getExifData("Model");
       output = w + "x" + h;

     }

     context.write(new Text(output), one);
   }

  }
  
  public static class ImageSizeReducer extends Reducer<Text, IntWritable,  Text, IntWritable> {
    
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable value : values) {
        sum += value.get();
      }
      context.write(key, new IntWritable(sum));
   }

  }

  public int run(String[] args) throws Exception {
    
    if (args.length < 2) {
      System.out.println("Usage: ImageSize <input HIB> <output directory>");
      System.exit(0);
    }

    Configuration conf = this.getConf();
    /*
    conf.set("mapreduce.map.java.opts", "-Xmx7500m");
    conf.set("mapreduce.map.memory.mb", "9000");
    conf.set("yarn.app.mapreduce.am.command-opts", "-Xmx9000m");
    conf.set("yarn.app.mapreduce.am.resource.mb", "10000");
    conf.set("yarn.scheduler.maximum-allocation-mb", "10000");
    conf.set("yarn.nodemanager.resource.memory-mb", "10000");
    */

    Job job = Job.getInstance(conf, "ImageSize");

    job.setJarByClass(ImageSize.class);
    job.setMapperClass(ImageSizeMapper.class);
    job.setReducerClass(ImageSizeReducer.class);

    job.setInputFormatClass(HibInputFormat.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    String inputPath = args[0];
    String outputPath = args[1];

    removeDir(outputPath, conf);

    FileInputFormat.setInputPaths(job, new Path(inputPath));
    FileOutputFormat.setOutputPath(job, new Path(outputPath));

    return job.waitForCompletion(true) ? 0 : 1;

  }

  private static void removeDir(String path, Configuration conf) throws IOException {
    Path output_path = new Path(path);
    FileSystem fs = FileSystem.get(conf);
    if (fs.exists(output_path)) {
      fs.delete(output_path, true);
    }
  }

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new ImageSize(), args);
    System.exit(res);
  }

}
