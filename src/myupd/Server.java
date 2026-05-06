package myupd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
    public static void main(String[] args) {
        int port = 8080;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024];

            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Local IP Address: " + localHost.getHostAddress());
            while (true) {
                DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
                socket.receive(msg);

                DatagramPacket echoPacket = new DatagramPacket(
                        msg.getData(),
                        msg.getLength(),
                        msg.getAddress(),
                        msg.getPort()
                );
                socket.send(echoPacket);

                String stringMessage = new String( msg.getData(), 0, msg.getLength() );
                System.out.printf("Message: %s\nIP: %s\nport: %d", stringMessage, msg.getAddress().getHostAddress(), msg.getPort());
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
