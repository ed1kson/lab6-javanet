package mymulticast;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class SwingConference extends JFrame {
    public static final String GROUP_ADDRESS = "224.0.0.1";
    public static final int PORT = 8080;
    MulticastSocket socket;
    InetAddress group;
    String name = null;
    JTextField textField;
    JTextArea chat;
    JLabel label;

    SwingConference() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.leaveGroup(group);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                socket.close();

                dispose();
                System.exit(0);
            }
        });
        setSize(300, 500);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        label = new JLabel("Enter your name");
        mainPanel.add(label, BorderLayout.NORTH);

        JScrollPane chatScrollPane = new JScrollPane();
        chatScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        chat = new JTextArea();
        chat.setEditable(false);
        chat.setFocusable(false);
        chatScrollPane.setViewportView(chat);

        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel(new BorderLayout(5, 5));
        textField = new JTextField();
        JButton sendButton = new JButton("Send");

        lowerPanel.add(textField, BorderLayout.CENTER);
        lowerPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(lowerPanel, BorderLayout.SOUTH);

        chat.setText("Enter your name");

        joinGroup();
        sendButton.addActionListener( e -> onButtonPress());
        textField.addActionListener( e -> onButtonPress());

        setVisible(true);
    }

    public void joinGroup() {
        try {
            group = InetAddress.getByName(GROUP_ADDRESS);
            socket = new MulticastSocket(PORT);

            socket.joinGroup(group);

            //reader
            Thread receiver = new Thread( () -> {
                try {
                    byte[] buffer = new byte[1024];
                    while ( true ) {
                        DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
                        socket.receive(msg);

                        chat.append(new String( msg.getData(), 0, msg.getLength() ) + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Smth went wrong");
                }
            } );
            receiver.setDaemon(true);
            receiver.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonPress() {
        if ( name == null ) {
            name = textField.getText();
            if (name.isEmpty()) {
                chat.setText("Please enter the name in the field below");
                name = null;
            } else {
                chat.setText("");
                label.setText("You are visible to others as " + name);
                textField.setText("");
            }
            return;
        }

        String smsg = textField.getText();
        String fullSMsg = String.format("[%s]: %s", name, smsg );
        byte[] bmsg = fullSMsg.getBytes();
        textField.setText("");

        DatagramPacket msg = new DatagramPacket(bmsg, bmsg.length, group, PORT);
        try {
            socket.send(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new SwingConference();
    }
}
