package org.hipi.image.io;

import org.hipi.image.HipiImageHeader;
import org.hipi.image.HipiImageHeader.HipiImageFormat;
import org.hipi.image.HipiImageHeader.HipiColorSpace;
import org.hipi.image.HipiImage;
import org.hipi.image.HipiImage.HipiImageType;
import org.hipi.image.RasterImage;
import org.hipi.image.HipiImageFactory;
import org.hipi.image.PixelArray;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.HashMap;

import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Extends {@link ImageCodec} and serves as both an {@link ImageDecoder} and 
 * {@link ImageEncoder} for the JPEG image storage format.
 */
public class TifCodec extends ImageCodec {

  private static final TifCodec staticObject = new TifCodec();

  public static TifCodec getInstance() {
    return staticObject;
  }

  public HipiImageHeader decodeHeader(InputStream inputStream, boolean includeExifData) 
    throws IOException, IllegalArgumentException {


    ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
    Iterator readers = ImageIO.getImageReadersByFormatName("tif");
    ImageReader reader = (ImageReader)readers.next();
    reader.setInput(iis, true);
      
    int width = reader.getWidth(0);
    int height = reader.getHeight(0);
    int depth;
    try {
	ImageTypeSpecifier imageType = reader.getRawImageType(0);
        depth = imageType.getNumBands();
    }catch(Exception ex) {
        System.out.println("Tiff format not supported! " + ex.getMessage());
	throw new IOException();
    }
    reader.dispose();
    iis.close();
    
    HashMap<String,String> exifData = null;
    if (includeExifData) {
      iis.reset();
      exifData = ExifDataReader.extractAndFlatten(inputStream);
    }
    
    return new HipiImageHeader(HipiImageFormat.TIF, HipiColorSpace.RGB, 
			       width, height, 3, null, exifData);
  }

  public void encodeImage(HipiImage image, OutputStream outputStream) throws IllegalArgumentException, IOException {

    if (!(RasterImage.class.isAssignableFrom(image.getClass()))) {
      throw new IllegalArgumentException("Tiff encoder supports only RasterImage input types.");
    }    

    if (image.getWidth() <= 0 || image.getHeight() <= 0) {
      throw new IllegalArgumentException("Invalid image resolution.");
    }
    if (image.getColorSpace() != HipiColorSpace.RGB) {
      throw new IllegalArgumentException("TIF encoder supports only RGB color space.");
    }
    if (image.getNumBands() != 3) {
      throw new IllegalArgumentException("Tif encoder supports only three band images.");
    }

    // Find suitable JPEG writer in javax.imageio.ImageReader
    ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("tiff");
    ImageWriter writer = writers.next();
    System.out.println("Using Tiff encoder: " + writer);
    writer.setOutput(ios);

    ImageWriteParam param = writer.getDefaultWriteParam();
    //param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    //param.setCompressionQuality(0.95F); // highest JPEG quality = 1.0F

    encodeRasterImage((RasterImage)image, writer, param);
    ios.close();
    writer.dispose();
  }

}
