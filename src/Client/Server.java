package Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {

    public static void main(String[] args) {
        final File[] inputFile = new File[1];
        JFrame frame = new JFrame("Socket Server");
        frame.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.25),Toolkit.getDefaultToolkit().getScreenSize().height);
        frame.setResizable(false);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        JPanel titlePanel = new JPanel();

        JLabel title = new JLabel("SOCKET SERVER");
        title.setFont(new Font("Montserrat", Font.BOLD, 25));
        title.setBorder(new EmptyBorder(30,50,30,0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);

        JPanel displayPanel = new JPanel();
        displayPanel.setBorder(new EmptyBorder(30,0,0,0));
        displayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton clearButton = new JButton("Clear Chat");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayPanel.removeAll();
                frame.pack();
                frame.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.25),Toolkit.getDefaultToolkit().getScreenSize().height);
            }
        });

        titlePanel.add(clearButton);

        frame.add(titlePanel);

        JScrollPane scrollPane = new JScrollPane(displayPanel);
        scrollPane.setBorder(new EmptyBorder(0,0,40,0));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setVerticalScrollBarPolicy(scrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollPane);

        JPanel inputFieldPanel = new JPanel();

        JPanel ipPanel = new JPanel();
        JTextField inputField = new JTextField("", 30);
        inputField.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, 40);
        inputField.setFont(new Font("Montserrat", Font.BOLD, 15));
        inputField.setBorder(new EmptyBorder(10,20,50,0));
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        ipPanel.add(inputField);
        inputFieldPanel.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, 60);

        JPanel sendPanel = new JPanel();
        sendPanel.setBorder(new EmptyBorder(0,30, 50 ,30));
        JButton sendImageBtn = new JButton("Send");
        sendImageBtn.setPreferredSize(new Dimension(200,40));
        Image imgg = new ImageIcon("src/Client/imagelogo.png").getImage();
        Image img_scc = imgg.getScaledInstance(50,50,  java.awt.Image.SCALE_SMOOTH);

        JLabel chooseImageBtn = new JLabel(new ImageIcon(img_scc));
        chooseImageBtn.setPreferredSize(new Dimension(50,50));

        chooseImageBtn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose an image to send");
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    inputFile[0] = fileChooser.getSelectedFile();
                    inputField.setText(inputFile[0].getName());
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {}

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {}

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {}

            @Override
            public void mouseExited(MouseEvent mouseEvent) {}

        });

        sendImageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputFile[0] == null){
                    String msg = inputField.getText().toString();
                    if(!msg.equals("")){
                        try {
                            Socket socket = new Socket("localhost", 9876);
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            byte[] contentBytes = new byte[(int)msg.length()];
                            dataOutputStream.writeInt(contentBytes.length);
                            dataOutputStream.write(msg.getBytes());
                            inputField.setText("");

                        } catch (IOException e ) {
                            JLabel errorLabel = new JLabel(e.getMessage());
                            sendPanel.add(errorLabel);
                        }
                    }
                }
                else{
                    try {
                        FileInputStream fileInputStream = new FileInputStream(inputFile[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 9876);
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        byte[] contentBytes = new byte[(int)inputFile[0].length()];
                        dataOutputStream.writeInt(contentBytes.length);
                        dataOutputStream.write(fileInputStream.readAllBytes());
                        inputField.setText("");
                        inputFile[0] = null;

                    } catch (IOException e ) {
                        JLabel errorLabel = new JLabel(e.getMessage());
                        sendPanel.add(errorLabel);
                    }


                }
            }
        });
        sendPanel.add(chooseImageBtn);
        sendPanel.add(sendImageBtn);

        inputFieldPanel.add(ipPanel);
        inputFieldPanel.add(sendPanel);
        frame.getContentPane().add(inputFieldPanel);
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.setVisible(true);
        try {
            ServerSocket socket = new ServerSocket( 6789);
            while (true) {
                Socket socket1 = socket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket1.getInputStream());
                int fileContentLength = dataInputStream.readInt();
                System.out.println(fileContentLength);
                if (fileContentLength > 0) {
                    if(fileContentLength > 30) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes);
                        System.out.println(Arrays.toString(fileContentBytes));
                        ByteArrayInputStream bos = new ByteArrayInputStream(fileContentBytes);
                        BufferedImage img = ImageIO.read(bos);
                        Image img_sc = img.getScaledInstance((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.4), (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.25),  java.awt.Image.SCALE_SMOOTH);
                        JLabel imgContainer = new JLabel(new ImageIcon(img_sc));
                        imgContainer.setBorder(new EmptyBorder(10,30,10,0));
                        imgContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
                        displayPanel.add(imgContainer);
                        frame.pack();
                        frame.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.25),Toolkit.getDefaultToolkit().getScreenSize().height);
                    }
                    else {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes);
                        String msg = new String(fileContentBytes);
                        System.out.println(msg);
                        JPanel msgContainer = new JPanel();
                        msgContainer.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, 30);
                        JLabel msgg = new JLabel("<html>" + msg + "</html>");
                        msgg.setFont(new Font("Montserrat", Font.BOLD, 12));
                        msgg.setAlignmentX(Component.LEFT_ALIGNMENT);
                        msgg.setBorder(new EmptyBorder(5,5,5,5));
                        msgContainer.add(msgg);
                        displayPanel.add(msgContainer);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
