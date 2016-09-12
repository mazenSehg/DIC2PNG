package Converter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.util.Utils;

import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;

public class DicomToPNG {
	static String x = System.getProperty("user.dir");
	File input = new File(x + "");
	static File output = new File(x + "");
	int indice;

	boolean Check;
	static List<Integer> pointArrayX = new ArrayList<Integer>();
	static List<Integer> pointArrayY = new ArrayList<Integer>();

	private BufferedImage myPngImage = null;
	
	

	
	

	public void dcmconvpng(File file, int indice, File fileOutput)
			throws IOException {
		ImageIO.scanForPlugins();
		Iterator<ImageReader> iter = ImageIO
				.getImageReadersByFormatName("DICOM");

		ImageReader readers = (ImageReader) iter.next();
		DicomImageReadParam param = (DicomImageReadParam) readers
				.getDefaultReadParam();// return DicomImageReadParam
		// Adjust the values of Rows and Columns in it and add a Pixel Data
		// attribute with the byte array from the DataBuffer of the scaled
		// Raster

		ImageInputStream iis = ImageIO.createImageInputStream(file);
		readers.setInput(iis, true);// sets the input source to use the given
									// ImageInputSteam or other Object

		myPngImage = readers.read(indice, param); // read dicom
		/*
		 * BufferedImage dimg = Thumbnails.of(myPngImage) .size(2048, 2560)
		 * .asBufferedImage();
		 */
		BufferedImage dimg = myPngImage;
		// BufferedImage dimg = new BufferedImage(2048, 2560,
		// myPngImage.getType());
		// dimg.createGraphics().drawImage(myPngImage, 0, 0, null);
		// fileOutput.mkdirs();//give new folder
		File myPngFile = fileOutput;// new File(fileOutput + "/"+file.getName()+
									// "(" +(indice+1)+ ")"+".png");
		OutputStream output = new BufferedOutputStream(new FileOutputStream(
				myPngFile));
		PNGEncodeParam.RGB param2 = new PNGEncodeParam.RGB();
		ImageEncoder enc = ImageCodec.createImageEncoder("PNG", output, param2);
		// enc.encode(myPngImage);
		enc.encode(dimg);
		output.close();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		
		
		
		// Get the global registry
		IIORegistry registry = IIORegistry.getDefaultInstance();
		// Lookup the known TIFF providers
		ImageReaderSpi jaiProvider = lookupProvider(registry, "com.github.jaiimageio.impl.plugins.raw.RawImageReaderSpi");
		ImageReaderSpi geoProvider = lookupProvider(registry, "com.sun.media.imageio.impl.plugins.raw.RawImageReaderSpi");
		if (jaiProvider != null && geoProvider != null) {
		    registry.deregisterServiceProvider(jaiProvider);
		
		    // OR
		    // order the it.geosolutions provider BEFORE the com.sun (JAI) provider
		    registry.setOrdering(ImageReaderSpi.class, geoProvider, jaiProvider);
		}
		
		
		
		
		
		// TODO Auto-generated method stub
		DicomToPNG a = new DicomToPNG();
		a.setDirectory();
	}
	
	private static ImageReaderSpi lookupProvider(final IIORegistry registry, final String className) {
	    Iterator<ImageReaderSpi> providers = registry.getServiceProviders(ImageReaderSpi.class, new ServiceRegistry.Filter() {
	        @Override
	        public boolean filter(final Object provider) {
	            return provider.getClass().getName().equals(className);
	        }
	    }, true);

	    return providers.hasNext() ? providers.next() : null;
	}

	@SuppressWarnings("resource")
	public void options() throws IOException {
		System.out.println("Select an option: " + "\n");
		System.out.println("1) Configure Directories");
		System.out.println("2) Convert files");
		System.out.println("3) Black out custom area");
		System.out.println("4) OCR remove text from images");
		System.out.println("5) exit");
		Scanner dis = new Scanner(System.in);
		String line;
		@SuppressWarnings("unused")
		DicomToPNG converter = new DicomToPNG();

		line = dis.nextLine();

		System.out.println("Change directories");

		switch (line) {
		case "1":
			directoryConfig();

		case "2":
			prepConv(input, output);
			// copyFolder(input, output, converter);
			break;

		case "3":
			prepBlackout();
			// copyFolder(input, output, converter);
			break;

		case "4":
			prepOCR();
			// copyFolder(input, output, converter);
			break;
			
			
		case "5":
			break;

		}
	}

	@SuppressWarnings({ "resource" })
	private void directoryConfig() throws IOException {

		System.out.println("the current directory for input DICOM files: "
				+ input);
		System.out.println("the current directory for output PNG files: "
				+ output);
		System.out.println("Do you want to use them?");
		String answer;
		Scanner scanner = new Scanner(System.in);
		answer = scanner.next();
		if (answer.equals("yes")) {
			if (!input.exists()) {
				input.mkdir();
			}
			if (!output.exists()) {
				output.mkdir();
			}
			options();
		} else

		{
			FileWriter fw = new FileWriter("config.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			int ina = 0, inb = 0;
			int inc = ina + inb;
			while (inc != 2) {

				while (ina == 0) {
					System.out
							.print("Please input the directory for the input files: ");
					Scanner dis = new Scanner(System.in);
					String line;
					line = dis.nextLine();

					Path path = Paths.get(line + "");

					if (Files.exists(path)) {
						System.out.println("Path selected for input: " + path);
						bw.write(path.toString());
						input = path.toFile();
						bw.close();
						ina = 1;
					} else {
						System.out
								.println("Sorry, that directory doesn't exist.");
						System.out.print("please try again: ");
					}
				}
				while (inb == 0) {

					System.out
							.print("Please input the directory for the output files: ");
					Scanner dist = new Scanner(System.in);
					String linez;
					linez = dist.nextLine();
					Path pathz = Paths.get(linez + "");
					if (Files.exists(pathz)) {
						System.out
								.println("Path selected for output: " + pathz);
						bw = new BufferedWriter(new FileWriter("config.txt",
								true));
						bw.newLine();
						bw.write(pathz.toString());
						bw.close();

						output = pathz.toFile();
						inb = 1;
					} else {
						System.out
								.println("Sorry, that directory doesn't exist.");
						System.out.print("please try again: ");
					}
				}
				BufferedReader in = new BufferedReader(new FileReader(
						"config.txt"));
				String str;
				while ((str = in.readLine()) != null) {
					System.out.println(str);
				}

				in.close();
				options();
			}
		}
	}

	public void setDirectory() throws IOException {

		File f = new File(x + "/config.txt");
		if (f.exists() && !f.isDirectory()) {

			@SuppressWarnings("resource")
			BufferedReader brTest = new BufferedReader(new FileReader(f));
			String FirstLine = brTest.readLine();
			String SecondLine = brTest.readLine();
			File ayy = new File(FirstLine + "");
			input = ayy;
			File ayy2 = new File(SecondLine + "");
			output = ayy2;

			//
			// String line1;
			// String line2;
			//
			// BufferedReader br = new BufferedReader(new FileReader(f));
			// StringBuilder sb = new StringBuilder();
			// String line = br.readLine();
			// while (line != null) {
			// sb.append(line);
			// sb.append(System.lineSeparator());
			// line = br.readLine();
			// }
			// String s = br.readLine();
			// StringBuilder sb2 = new StringBuilder();
			// while(s!=null){
			// s = br.readLine();
			// }
			// System.out.println("line1: "+ sb.toString());
			// System.out.println("line2: "+ sb2.toString());

		} else {
			System.out.println("Setting up default input directory: ");
			File ayy = new File(x + "/input");
			ayy.mkdir();
			input = ayy;

			System.out.println("Setting up a default output directory");
			File ayyy = new File(x + "/output");
			ayyy.mkdir();
			output = ayyy;

		}
		options();
	}

	public void prepConv(File input, File output) throws IOException {
		if (input.isDirectory()) {
			String files[] = input.list();
			System.out.println("There are currently " + files.length
					+ " files in here");
			DicomToPNG a = new DicomToPNG();
			System.out.println("Press \"ENTER\" to continue...");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			copyFolder(input, output, a);
		}
		options();

	}

	public void prepBlackout() throws IOException {
		System.out.println(output);
		setCoordinates();
		if (output.isDirectory()) {
			System.out.println(output);
			String files[] = output.list();
			System.out.println("There are currently " + files.length
					+ " files in here");
			@SuppressWarnings("unused")
			DicomToPNG a = new DicomToPNG();
			System.out.println("Press \"ENTER\" to continue...");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			for (String file : files) {
				File destFile = new File(output, file);
				// recursive copy
				drawCoordinates(destFile);
			}
		}
		options();
	}

	private void prepOCR() throws IOException {
		if (output.isDirectory()) {
			System.out.println(output);
			String files[] = output.list();
			System.out.println("There are currently " + files.length
					+ " files in here");

			System.out.println("Press \"ENTER\" to continue...");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			for (String file : files) {
				File destFile = new File(output, file);
				try {
					OCRout(destFile);
				} catch (TesseractException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// recursive copy
				// drawCoordinates(destFile);
			}
		}

	}

	/**
	 * 
	 * @param src
	 * @param dest
	 * @param converter
	 * @throws IOException
	 */
	/**
	 * 
	 * @param src
	 * @param dest
	 * @param converter
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest, DicomToPNG converter)
			throws IOException {

		if (src.isDirectory()) {

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile, converter);
			}

		} else {
			String fileName = src.toString();
			String fname = "";
			String ext = "";
			int mid = fileName.lastIndexOf(".");
			if (mid != -1) {
				fname = fileName.substring(0, mid);
				ext = fileName.substring(mid + 1, fileName.length());
				if (ext.equals("dcm")) {
					fileName = dest.toString();
					mid = fileName.lastIndexOf(".");
					fname = fileName.substring(0, mid);
					ext = fileName.substring(mid + 1, fileName.length());
					dest = new File(fname + ".png");
					boolean exists = dest.exists();
					if (!exists) {
						converter.dcmconvpng(src, 0, dest);
					}

					System.out.println("File copied from " + src + " to "
							+ dest);

				}
			}
		}
	}

	/**
	 * 
	 * @param dest
	 * @throws IOException
	 */
	public static void setCoordinates() throws IOException {

		String pointNo;
		int pointNo1 = 0;

		boolean check = false;

		while (check == false) {
			@SuppressWarnings("resource")
			Scanner NoPnt = new Scanner(System.in);

			System.out.println("How many points do you wish to add? ");
			pointNo = NoPnt.nextLine();
			pointNo1 = Integer.parseInt(pointNo);
			if (pointNo1 >= 4) {
				check = true;
			} else {
				System.out.println("You must input more than 4 points!");

			}
		}
		System.out
				.println("Please input co-ordinates for point: (x co-ord, y co-ord) ");
		for (int i = 0; i < pointNo1; i++) {

			@SuppressWarnings("resource")
			Scanner dis = new Scanner(System.in);
			int x, y;
			String line;
			String[] lineVector;

			line = dis.nextLine();

			// separate all values by comma
			lineVector = line.split(",");

			// parsing the values to Integer
			x = Integer.parseInt(lineVector[0]);
			y = Integer.parseInt(lineVector[1]);

			pointArrayX.add(x);
			pointArrayY.add(y);
		}

	}

	public static void drawCoordinates(File destFile) throws IOException {

		System.out.println(destFile);
		BufferedImage image = ImageIO.read(new File(destFile + ""));
		System.out.println("image width: " + image.getWidth()
				+ "\n image height: " + image.getHeight());

		Graphics2D g2d = image.createGraphics();

		g2d.setColor(Color.RED);
		BasicStroke bs = new BasicStroke(2);
		g2d.setStroke(bs);

		int[] xPoly = new int[pointArrayX.size()]; // create an array and set
													// its size
		int[] yPoly = new int[pointArrayY.size()]; // create an array and set
													// its size

		for (int i = 0; i < xPoly.length; i++) {
			xPoly[i] = pointArrayX.get(i);
			yPoly[i] = pointArrayY.get(i);
		}

		Polygon poly = new Polygon(xPoly, yPoly, xPoly.length); // use the
																// created array
		poly.getBounds();
		g2d.setPaint(Color.RED);
		g2d.drawPolygon(poly);
		g2d.fillPolygon(xPoly, yPoly, xPoly.length);
		g2d.drawPolygon(xPoly, yPoly, xPoly.length);
		g2d.setStroke(bs);
		g2d.drawPolyline(xPoly, yPoly, xPoly.length);

		g2d.draw(poly);

		Graphics g2 = image.getGraphics();
		int[] xPoints = new int[pointArrayX.size()];
		int[] yPoints = new int[pointArrayY.size()];
		((Graphics2D) g2).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.green);
		g2.setColor(Color.black);
		g2.drawPolygon(xPoints, yPoints, xPoints.length);
		g2.setColor(Color.black);
		g2.fillPolygon(xPoints, yPoints, xPoints.length);
		g2.dispose();

		File outputfile = new File(destFile + "");
		ImageIO.write(image, "png", outputfile);
	}

	public static void OCRout(File destFile) throws IOException,
			TesseractException {
		ImageIO.scanForPlugins();
		ImageIO.scanForPlugins();
		@SuppressWarnings("deprecation")
		Tesseract instance = Tesseract.getInstance();
		System.out.println(destFile);
		File imageFile = new File(destFile + "");
		BufferedImage bi = ImageIO.read(imageFile);
		int level = TessPageIteratorLevel.RIL_SYMBOL;
		System.out.println("PageIteratorLevel: "
				+ Utils.getConstantName(level, TessPageIteratorLevel.class));
		List<Rectangle> result = instance.getSegmentedRegions(bi, level);
		ImageIO.scanForPlugins();
		for (int i = 0; i < result.size(); i++) {
			ImageIO.scanForPlugins();
			Rectangle rect = result.get(i);
			System.out.println(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d",
					i, rect.x, rect.y, rect.width, rect.height));
			Graphics g2 = bi.getGraphics();
			((Graphics2D) g2).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			if (rect.width > 30 && rect.width > 30) {
				g2.setColor(Color.green);
				g2.setColor(Color.black);
				g2.drawRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(Color.black);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.dispose();

				File outputfile = new File(destFile + "");

				ImageIO.write(bi, "png", outputfile);
			}

		}
	}

}
