package mymulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Conference {
    public static final String GROUP_ADDRESS = "224.0.0.1";
    public static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(GROUP_ADDRESS);
            MulticastSocket socket = new MulticastSocket(PORT);

            socket.joinGroup(group);

            Scanner in = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String name = in.nextLine();

            //reader
            Thread receiver = new Thread( () -> {
                try {
                    byte[] buffer = new byte[1024];
                    while ( true ) {
                        DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
                        socket.receive(msg);

                        System.out.println(new String( msg.getData(), 0, msg.getLength() ));
                    }
                } catch (IOException e) {
                    System.out.println("Smth went wrong");
                }
            } );
            receiver.setDaemon(true);
            receiver.start();

            //sender
            while (true) {
                String smsg = in.nextLine();
                if ( smsg.equalsIgnoreCase("/exit")) {
                    break;
                }
                String fullSMsg = String.format("[%s]: %s", name, smsg );
                byte[] bmsg = fullSMsg.getBytes();

                DatagramPacket msg = new DatagramPacket(bmsg, bmsg.length, group, PORT);
                socket.send(msg);
            }

            socket.leaveGroup(group);
            socket.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
