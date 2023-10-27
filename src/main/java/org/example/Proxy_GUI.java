package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Proxy_GUI {
    public JPanel panel1;
    private JRadioButton HTTPRadioButton;
    private JRadioButton SOCKSRadioButton;
    private JTextField a127001TextField;



    private JTextField a7890TextField;
    private JTextField textField3;
    private JTextField textField4;
    private JCheckBox authenticationCheckBox;
    private JButton enableProxyButton;
public Proxy_GUI() {
    SOCKSRadioButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(SOCKSRadioButton.isSelected()){
                HTTPRadioButton.setSelected(false);
            }
        }
    });
    HTTPRadioButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(HTTPRadioButton.isSelected()){
                SOCKSRadioButton.setSelected(false);
            }
        }
    });
    authenticationCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(authenticationCheckBox.isSelected()){
                textField3.setEnabled(true);
                textField4.setEnabled(true);
            }else{
                textField3.setEnabled(false);
                textField4.setEnabled(false);
            }
        }
    });
    enableProxyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String Address = a127001TextField.getText();
            String Port = a7890TextField.getText();
            String Username = textField3.getText();
            String Password = textField4.getText();
            boolean AuthMode = authenticationCheckBox.isSelected();
            if(SOCKSRadioButton.isSelected()){
                System.setProperty("socksProxyHost", Address);
                System.setProperty("socksProxyPort", Port);
                if(AuthMode){
                    System.setProperty("java.net.socks.username", Username);
                    System.setProperty("java.net.socks.password", Password);
                }

            }else if(HTTPRadioButton.isSelected()){
                System.setProperty("http.proxyHost", Address);
                System.setProperty("http.proxyPort", Port);
                if (AuthMode){
                    System.setProperty("http.proxyUser", Username);
                    System.setProperty("http.proxyPassword", Password);
                }

            }else{
                JOptionPane.showMessageDialog(null,"Unknown exception","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    });
}
}
