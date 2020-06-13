package Kliens.GUI;

import javax.swing.*;
import java.awt.*;

public class QueryChooserWIndow extends JFrame {
    private JButton withJoin;
    private JButton withoutJoin;

    private final int frameWidth = 225;
    private final int frameHeight = 175;
    private final int buttonWidth = 125;
    private final int buttonHeight = 30;
    private final int distanceFromLeft = (frameWidth - buttonWidth)/2;
    private final int distanceFromTop = 30;
    private final int distanceBetweenButtons = 20;

    public QueryChooserWIndow(String DB, String TB, JList<String> list){
        setTitle("Query");
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(frameWidth, frameHeight);

        initialize();

        add(withJoin);
        add(withoutJoin);

        withJoin.setBounds(distanceFromLeft, distanceFromTop, buttonWidth, buttonHeight);
        withoutJoin.setBounds(distanceFromLeft, distanceFromTop + distanceBetweenButtons + buttonHeight, buttonWidth, buttonHeight);

        withoutJoin.addActionListener(e->{
            this.dispose();
            new QueryWithoutJoinWindow(DB, TB);
        });
        withJoin.addActionListener(e->{
            this.dispose();
            QueryWithJoinWindow.getInstance(DB, TB);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initialize(){
        withJoin = new JButton("With Join");
        withoutJoin = new JButton("No Join");
    }
}
