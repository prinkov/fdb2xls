package xyz.prinkov.selex;

import javax.resource.spi.work.Work;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;



public class GUI extends JFrame{

    public GUI() throws Exception {
        final File[] files = new File[3];
        JPanel panel = new JPanel(new FlowLayout());
        final JTextField inText = new JTextField(15);
        final JTextField outText = new JTextField(15);
        final JTextField paramText = new JTextField(9);
        final JButton inBtn = new JButton("Обзор");
        final JButton outBtn = new JButton("Обзор");
        final JProgressBar progressBar = new JProgressBar();
        final JFileChooser chooser = new JFileChooser();
        final JTabbedPane tpane = new JTabbedPane();
        final JPanel settingPane = new JPanel();
        final JTextField usernameTF = new JTextField(7);
        final JTextField passwordTF = new JTextField(7);
        final JTextField hostTF = new JTextField(7);
        JButton paramBtn = new JButton("Файл запросов");
        JButton saveBtn = new JButton("Сохранить");
        JButton resetBtn = new JButton("Сбросить");
        JButton startBtn = new JButton("Конвертировать");

        saveBtn.setSize(paramBtn.getSize());

        usernameTF.setText(Workspace.username);
        passwordTF.setText(Workspace.password);
        hostTF.setText(Workspace.host);
        paramText.setText(Workspace.paramFile);

        settingPane.add(usernameTF);
        settingPane.add(new JLabel("@"));
        settingPane.add(passwordTF);
        settingPane.add(new JLabel(":"));
        settingPane.add(hostTF);
        settingPane.add(paramText);
        settingPane.add(paramBtn);
        settingPane.add(resetBtn);
        settingPane.add(saveBtn);

        panel.add(inText);
        panel.add(inBtn);
        panel.add(outText);
        panel.add(outBtn);
        panel.add(startBtn);

        inText.setText("Путь к файлу БД");
        outText.setText("Папка для сохранения");

        inBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int res = chooser.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION) {
                    files[0] = chooser.getSelectedFile();
                    inText.setText(files[0].getAbsolutePath());
                }
            }
        });

        outBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int res = chooser.showSaveDialog(null);
                if(res == JFileChooser.APPROVE_OPTION) {
                    files[1] = chooser.getSelectedFile();
                    outText.setText(files[1].getAbsolutePath());
                }
            }
        });


        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(files[0] != null && files[1] != null) {
                    progressBar.setVisible(true);
                    Selex selex = new Selex(files[0].getAbsolutePath(), files[1].getAbsolutePath());
                    Workspace.init();
                    progressBar.setVisible(true);
                    for(int i = 0; i < Workspace.params.size(); i++)
                        selex.exec(Workspace.params.get(i));
                    JOptionPane.showMessageDialog(null, "Конвертация прошла успешно!", "Сообщение", JOptionPane.INFORMATION_MESSAGE);
                    progressBar.setVisible(false);

                }
                else if(files[0] == null)
                    JOptionPane.showMessageDialog(null,  "Выберите файл БД", "Ошибка", JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null,  "Выберите путь для сохранения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        paramBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int res = chooser.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION) {
                    files[2] = chooser.getSelectedFile();
                }
            }
        });

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(files[2] != null)
                    Workspace.paramFile = files[2].getAbsolutePath();
                Workspace.username = usernameTF.getText();
                Workspace.password = passwordTF.getText();
                Workspace.host = hostTF.getText();
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Workspace.reset();
                files[2] = null;
                usernameTF.setText(Workspace.username);
                passwordTF.setText(Workspace.password);
                hostTF.setText(Workspace.host);
                paramText.setText(Workspace.paramFile);
            }
        });

        progressBar.setIndeterminate(true);
        panel.add(progressBar);
        progressBar.setVisible(false);
        tpane.add(panel, "Конвертация");
        tpane.add(settingPane, "Настройки");

        this.add(tpane);
        this.setSize(300, 120);
        this.setResizable(true);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}