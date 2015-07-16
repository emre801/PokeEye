package com.johnerdo.imageCompare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import org.johnerdo.globalInfo.PokemonList;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class MatchingMethod {

	public static String screenInfoScreenShot = "C:/Users/John/Desktop/ScreenInfo/ScreenShot";
	public static String screenInfoScreen = "C:/Users/John/Desktop/ScreenInfo/";
	public static String resultInfo = "C:/Users/John/Desktop/ScreenInfo/Results/";
	public static String pokemonInfo = "C:/Users/John/Desktop/PokemonInfo/";

	public static HashMap<String, Integer> pokeHash = new HashMap<String, Integer>();

	public static void setupScreenInfoScreenShot() {

	}

	public static LinkedList<Double> getHealthBars() {
		LinkedList<Double> result = new LinkedList<Double>();
		System.out.println(screenInfoScreenShot + "/00002.png");
		Mat health1 = screenRegion(screenInfoScreenShot + "/00002.png",
				screenInfoScreen + "h1.png", new Rect(75, 220, 50, 6));
		Mat health2 = screenRegion(screenInfoScreenShot + "/00002.png",
				screenInfoScreen + "h2.png", new Rect(330, 18, 50, 6));
		result.add(getHealthPercent(health1) * 100);
		result.add(getHealthPercent(health2) * 100);
		return result;
	}

	public static double getHealthPercent(Mat health1) {
		int p1Health = 0;
		for (int i = 48; i > 0; i--) {
			double[] pix = health1.get(1, i);
			if (pix[0] == pix[1] && pix[0] == pix[2])
				p1Health++;
		}
		double health = 1d - p1Health / 48d;
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(health));
	}

	public static Mat screenRegion(String screenshot, String output, Rect rect) {
		Mat screen = Highgui.imread(screenshot);
		Imgproc.cvtColor(screen, screen, Imgproc.COLOR_BGR2GRAY);
		Mat slot1 = screen.submat(rect);
		// System.out.println(slot1.dump());
		Highgui.imwrite(output, slot1);
		return slot1;
	}

	public static void removeGreen(Mat mat) {
		double[] green = { 94, 229, 158 };
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				double[] color = mat.get(j, i);
				if (color[0] == green[0] && color[1] == green[1]
						&& color[2] == green[2]) {

				}
			}
		}
	}

	public static String getLatestScreenShot() {
		File folder = new File(screenInfoScreenShot);
		long newest = Integer.MIN_VALUE;
		File newestFile = null;
		for (File file : folder.listFiles()) {
			if (file.lastModified() > newest) {
				newest = file.lastModified();
				newestFile = file;
			}
		}
		return newestFile.getAbsolutePath();

	}

	public static LinkedList<Mat> getScreenInfo() {
		LinkedList<Mat> results = new LinkedList<Mat>();
		int x = 5;
		int y = 5;
		int box = 25, box2 = 20;
		Rect rect1 = new Rect(140 + x, 40 + y, box, box2);
		// String screenShot = screenInfoScreenShot +"Screenshot.png";
		String screenShot = getLatestScreenShot();
		results.add(screenRegion(screenShot, resultInfo + "/slot1.png", rect1));
		Rect rect2 = new Rect(210 + x, 40 + y, box, box2);
		results.add(screenRegion(screenShot, resultInfo + "/slot2.png", rect2));
		Rect rect3 = new Rect(140 + x, 100 + y, box, box2);
		results.add(screenRegion(screenShot, resultInfo + "/slot3.png", rect3));
		Rect rect4 = new Rect(210 + x, 100 + y, box, box2);
		results.add(screenRegion(screenShot, resultInfo + "/slot4.png", rect4));
		Rect rect5 = new Rect(140 + x, 160 + y, box, box2);
		results.add(screenRegion(screenShot, resultInfo + "/slot5.png", rect5));
		Rect rect6 = new Rect(210 + x, 160 + y, box, box2);
		results.add(screenRegion(screenShot, resultInfo + "/slot6.png", rect6));

		return results;
	}

	public static LinkedList<Mat> getImages() {
		int count = 1;
		LinkedList<Mat> pokemonMats = new LinkedList<Mat>();
		for (int j = 0; j < 29; j++) {
			for (int i = -1; i < 25; i++) {
				int x = 47 + (i * 38);
				int y = 94 + (j * 38);
				Rect rect = new Rect(x, y, 32, 32);
				// if(j ==27 && i<=3 && count < 719)
				// continue;
				System.out.println("--");
				Mat pok = screenRegion(pokemonInfo + "/PokemonSprites.png",
						pokemonInfo + "/Pokemon/pok" + count + ".png", rect);
				// pok.dump();
				pokemonMats.add(pok);
				// Highgui.imwrite(pokemonInfo + "/Pokemon/pok"+count+".png",
				// pok);
				count++;
			}
		}
		return pokemonMats;
	}

	public static HashMap<Rect, Integer> getImageRect() {
		int count = 1;
		HashMap<Rect, Integer> pokemonMats = new HashMap<Rect, Integer>();
		for (int j = 0; j < 29; j++) {
			for (int i = -1; i < 25; i++) {
				int x = 47 + (i * 38);
				int y = 94 + (j * 38);
				Rect rect = new Rect(x, y, 32, 32);
				// if(j ==27 && i<=3 && count < 719)
				// continue;
				pokemonMats.put(rect, count);
				count++;
			}
		}
		return pokemonMats;
	}

	public static void match(Mat img, Mat templ, int match_method,
			int pokeNumber) {

		// System.out.println("\nRunning Template Matching");

		FeatureDetector cvFeatureDetector;
		cvFeatureDetector = FeatureDetector.create(FeatureDetector.GFTT);

		MatOfKeyPoint keyPoint1;
		keyPoint1 = new MatOfKeyPoint();
		cvFeatureDetector.detect(img, keyPoint1);

		MatOfKeyPoint keyPoint2;
		keyPoint2 = new MatOfKeyPoint();
		cvFeatureDetector.detect(templ, keyPoint2);

		// FeatureDetector detector =
		// FeatureDetector.create(FeatureDetector.ORB);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, templ, match_method);
		Core.normalize(templ, templ, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(templ);

		System.out.println(pokeNumber);
		System.out.println(Math.abs(mmr.maxVal));
		System.out.println(Math.abs(mmr.minVal));
		System.out.println();

	}

	public static LinkedList<String> getPokemonNamesOnScreen() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		LinkedList<Mat> pokemons = getScreenInfo();
		HashMap<Rect, Integer> squares = getImageRect();
		LinkedList<String> pokemonName = new LinkedList<String>();
		Mat img = Highgui.imread(pokemonInfo + "/PokemonSprites.png");
		Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
		// int count = 1;
		for (Mat ok : pokemons) {
			// revenge(img, ok,pokemonInfo +"/Testing" +
			// count + ".png",Imgproc.TM_SQDIFF);
			Point midPoint = findMidPoint(img, ok, Imgproc.TM_SQDIFF);
			for (Rect rect : squares.keySet()) {
				if (rect.contains(midPoint)) {
					// System.out.println("PokemonNumber = " +
					// squares.get(rect));
					String pokemon = PokemonList.pokemonNames[squares.get(rect) - 1];
					pokemonName.add(pokemon);

					// System.out.println("Name  = " +
					// PokemonList.pokemonNames[squares.get(rect) -1]);
				}
			}
			// count++;
		}
		return pokemonName;
	}

	public static LinkedList<Integer> getPokemonNumbersOnScreen() {
		//deserializeHash();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		LinkedList<Mat> pokemons = getScreenInfo();
		System.out.println("Pokemon Info " + pokemons.size());
		HashMap<Rect, Integer> squares = getImageRect();
		LinkedList<Integer> pokemonName = new LinkedList<Integer>();
		System.out.println(pokemonInfo + "/PokemonSprites.png");
		Mat img = Highgui.imread(pokemonInfo + "/PokemonSprites.png");
		Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
		// int count = 1;
		for (Mat ok : pokemons) {
			// revenge(img, ok,pokemonInfo +"/Testing" +
			// count + ".png",Imgproc.TM_SQDIFF);

			if (pokeHash.containsKey(ok.toString())) {
				pokemonName.add(pokeHash.get(ok.toString()));
				System.out.println("ShortCut");
				continue;
			}
			Point midPoint = findMidPoint(img, ok, Imgproc.TM_SQDIFF);
			for (Rect rect : squares.keySet()) {
				if (rect.contains(midPoint)) {
					// System.out.println("PokemonNumber = " +
					// squares.get(rect));
					Integer pokemon = squares.get(rect);
					System.out.println(pokemon + " LALALA");
					pokeHash.put(ok.toString(), pokemon);
					pokemonName.add(pokemon);
					// System.out.println("Name  = " +
					// PokemonList.pokemonNames[squares.get(rect) -1]);
				}
			}
			// count++;
		}
		//serializeHash();
		return pokemonName;
	}

	public static void copyGifs(LinkedList<String> names) {
		int count = 0;
		for (String name : names) {

			System.out.println(name);
			int i = PokemonList.nameToDex.get(name);
			String numZeros = "";
			if (i < 100)
				numZeros = "0";
			if (i < 10)
				numZeros = "00";
			numZeros = numZeros + i;
			System.out.println("---" + numZeros);
			File source = new File(pokemonInfo + "/Sprires/xy-animated/"
					+ numZeros + ".gif");
			File target = new File(pokemonInfo + "/Sprires/Current/" + count
					+ ".gif");
			File source2 = new File(pokemonInfo + "/Pokemon/pok" + i + ".png");
			File target2 = new File(pokemonInfo + "/Sprires/CurrentGreen/"
					+ count + ".png");
			count++;
			try {
				copyFileUsingFileStreams(source, target);
				System.out.println("---");
				copyFileUsingFileStreams(source2, target2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void copyGifsName(LinkedList<Integer> names) {
		int count = 0;
		for (Integer i : names) {

			String numZeros = "";
			if (i < 100)
				numZeros = "0";
			if (i < 10)
				numZeros = "00";
			numZeros = numZeros + i;
			File source = new File(pokemonInfo + "/Sprires/xy-animated/"
					+ numZeros + ".gif");
			File target = new File(pokemonInfo + "/Sprires/Current/" + count
					+ ".gif");
			File source2 = new File(pokemonInfo + "/Pokemon/pok" + i + ".png");
			File target2 = new File(pokemonInfo + "/Sprires/CurrentGreen/"
					+ count + ".png");
			count++;
			try {
				copyFileUsingFileStreams(source, target);
				copyFileUsingFileStreams(source2, target2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void copyFileUsingFileStreams(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			System.out.println(source.getName());
			input.close();
			output.close();
		}
	}

	public static void revenge(Mat img, Mat templ, String outFile,
			int match_method) {

		// / Create the result matrix
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		// System.out.println(result_cols + " " + result_rows);
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
		// Mat());

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = mmr.minLoc;
		} else {
			matchLoc = mmr.maxLoc;
		}

		// Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
		// matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		Rect rect = new Rect(matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()));

		// System.out.print(rect);
		int midX = rect.x + rect.width / 2;
		int midY = rect.y + rect.height / 2;
		Point midPoint = new Point(midX, midY);
		System.out.println(midPoint);
		Mat out = img.submat(rect);
		System.out.println("Writing " + outFile);
		// Highgui.imwrite(outFile, out);

	}

	public static Point findMidPoint(Mat img, Mat templ, int match_method) {
		// / Create the result matrix
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = mmr.minLoc;
		} else {
			matchLoc = mmr.maxLoc;
		}
		Rect rect = new Rect(matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()));
		int midX = rect.x + rect.width / 2;
		int midY = rect.y + rect.height / 2;
		Point midPoint = new Point(midX, midY);
		return midPoint;

	}

	public static void getPokemonOnScreen() {
		LinkedList<Mat> pokemonMats = getImages();
		LinkedList<Mat> pokemonOnScreen = getScreenInfo();
		int pokeCount = 1;
		Mat slot1 = pokemonOnScreen.get(0);
		for (Mat pokemonMat : pokemonMats) {
			match(slot1, pokemonMat, Imgproc.TM_SQDIFF, pokeCount++);
		}
	}

	public static void serializeHash() {
		try {
			FileOutputStream fileOut = new FileOutputStream("PokeHash.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(pokeHash);
			out.close();
			fileOut.close();
			System.out.println(pokeHash.size());
			System.out.println("Serialized data is saved in PokeHash.txt");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static void deserializeHash() {
		try {
			FileInputStream fileIn = new FileInputStream("PokeHash.txt");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			pokeHash = (HashMap<String, Integer>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
	}

	
	
	
	
	public static void main(String[] args) {
		/*
		 * System.loadLibrary(Core.NATIVE_LIBRARY_NAME); LinkedList<String>
		 * pokemonNames = getPokemonNamesOnScreen(); int count = 0; for (String
		 * name : pokemonNames) { System.out.print(name + "\t\t"); count++; if
		 * (count % 2 == 0) System.out.println("\n"); }
		 */
		// System.out.println(getLatestScreenShot());
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// getHealthBars();
		getImages();
	}
}
