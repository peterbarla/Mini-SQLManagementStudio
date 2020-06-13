package Server;

import javax.swing.*;

public class ResultText extends JFrame {

    private final int frameWidth = 800;
    private final int frameHeight = 350;

    private JTextArea resultArea = new JTextArea(10, 10);
    private JScrollPane scrollBar = new JScrollPane(resultArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    private final int resultAreaWidth = 750;
    private final int resultAreaHeight = 300;

    public ResultText(){
        setTitle("Results");
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(frameWidth, frameHeight);

        add(scrollBar);

        scrollBar.setBounds(20, 20, resultAreaWidth, resultAreaHeight);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setText(String text){
        resultArea.append(text);
    }

    public void cleartext(){
        resultArea.append("");
    }
}
