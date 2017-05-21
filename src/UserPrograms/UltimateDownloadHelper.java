package UserPrograms;

import java.io.IOException;

/**
 * Created by kaikias on 20/05/17.
 */
public class UltimateDownloadHelper {

    private static void downloadFile(String file) {
        String args[] = {"wget", file};
        try {
            Process process = new ProcessBuilder(args).start();
        } catch (IOException e) {
            printFileNotFound();
        }
    }

    private static void downloadFileTo(String file, String path) {
        String[] end = file.split(".");
        String args[] = {"wget", file, "-O", path, "download.", end[end.length]};
        try {
            Process process = new ProcessBuilder(args).start();
        } catch (IOException e) {
            printFileNotFound();
        }
    }

    private static void printHelp() {
        System.out.println("UDP 1.0, your friend and helper.");
        System.out.println();
        System.out.println("Usage: UltimateDownloadHelper.java [URL]...");
        System.out.println("       UltimateDownloadHelper.java [URL]... [INSTALLPATH]...");
        System.out.println();
        System.out.println("Startup:");
        System.out.println("   -h,  --help                      print this help");
        System.out.println();
    }

    private static void printFileNotFound() {
        System.err.println("The file was not found. Please provide a valid path.");
    }

    public static void main(String[] args) {

        // The file does only download a file from a given url
        // Maybe one might want to download files automatically ??

        try {
            if (args[0].endsWith("--help") | args[0].endsWith("-h")) {
                printHelp();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Too few arguments. Use --help for more information.");
            System.exit(0);
        }
        String url = args[0];
        if (args.length == 1) {
            downloadFile(args[0]);
        } else {
            String path = args[1];
            downloadFileTo(args[0], args[1]);
        }
    }
}
