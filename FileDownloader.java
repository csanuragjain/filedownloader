package com.cooltrickshome;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class FileDownloader {

	/**
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 */

	static int downloadinProgress = 0;
	static File f = new File("");

	public static void main(String[] args) throws MalformedURLException,
			IOException {

		Scanner s = new Scanner(System.in);
		String ch = "YES";
		while (!ch.equals("NO")) {
			System.out.println("Enter the url to download from");
			final String downloadURL = s.nextLine();
			new Thread() {
				public void run() {
					new FileDownloader().downloadFile(downloadURL);
				}
			}.start();
			System.out.println("Do you wish to download more files (YES/NO)");
			ch = s.nextLine();
		}
	}

	public void downloadFile(String downloadURL) {
		downloadinProgress++;
		System.out.println("**Download stats: Number of ongoing downloads: "
				+ downloadinProgress);
		BufferedInputStream in = null;
		RandomAccessFile fout = null;
		String fileName = "";
		long fileSize = 0;
		long downloaded = 0;

		try {
			URL u = new URL(downloadURL);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("Range", "bytes=0-");
			fileName = u.getFile();
			fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
			fileSize = uc.getContentLength();
			System.out.println("**Download stats: Downloading " + fileName
					+ " which has size of " + (fileSize / 1000) + "KB");
			in = new BufferedInputStream(uc.getInputStream());
			fout = new RandomAccessFile(fileName, "rw");
			fout.seek(downloaded);

			final byte data[] = new byte[1024];
			int count;
			boolean ALERT_WHEN_50_PERCENT_COMPLETE = true;

			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
				downloaded += count;
				if (ALERT_WHEN_50_PERCENT_COMPLETE
						&& downloaded * 2 >= fileSize) {
					System.out.println("**Download stats: " + fileName
							+ ": Completed 50% download...");
					ALERT_WHEN_50_PERCENT_COMPLETE = false;
				}
			}
			System.out
					.println("**Download stats: Download completed. File at: "
							+ f.getAbsolutePath() + File.separator + fileName);
			downloadinProgress--;
		} catch (Exception e) {
			System.out.println("**Download stats: Download Failed for "
					+ fileName);
			downloadinProgress--;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
