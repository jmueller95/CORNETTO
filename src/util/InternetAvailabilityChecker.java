package util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class InternetAvailabilityChecker {
    /**
     * checks 4 common websites, at least one of them should always be online
     *
     * @return
     * @throws IOException
     */
    public static boolean isInternetAvailable() throws IOException {
        return isHostAvailable("google.com") || isHostAvailable("amazon.com") || isHostAvailable("facebook.com") || isHostAvailable("apple.com");
    }

    /**
     * checks for the availability of a website
     *
     * @param hostName
     * @return
     * @throws IOException
     */
    private static boolean isHostAvailable(String hostName) throws IOException {
        try (Socket socket = new Socket()) {
            int port = 80;
            InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
            socket.connect(socketAddress, 3000);

            return true;
        } catch (UnknownHostException unknownHost) {
            return false;
        }
    }
}