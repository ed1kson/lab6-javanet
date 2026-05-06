package myupd;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        int serverPort = 8080;

        String hostname;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter IP->");
        hostname = in.nextLine();

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(hostname);

            System.out.print("Enter the message:");
            byte[] msgBytes = (in.nextLine()).getBytes();
            DatagramPacket msg = new DatagramPacket(msgBytes, msgBytes.length, address, serverPort);

            byte[] responseBytes = new byte[1024];

            long start = System.nanoTime();
            socket.send(msg);

            DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length);
            socket.receive(response);
            long time = System.nanoTime() - start;

            String result = new String(response.getData(),0, response.getLength());
            System.out.printf("Server's echo(%d ns):\n", time);
            System.out.println(result);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
