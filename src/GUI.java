/**
 * Created by Filip Štimac on 3.1.2018..
 */

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GUI {

    static Logger log = Logger.getLogger(GUI.class.getName());

    public static void createAndShowGUI() {

        JFrame jf = new JFrame("Wikipedia writers search");
        jf.setLayout(new BorderLayout());

        JTextField jtf = new JTextField();
        jtf.setPreferredSize(new Dimension(700, 50));
        jtf.setFont(new Font("serif", Font.PLAIN, 20));
        jf.getContentPane().add(jtf, BorderLayout.NORTH);

        JPanel jp = new JPanel(new BorderLayout());
        jf.getContentPane().add(jp, BorderLayout.SOUTH);

        JEditorPane jep = new JEditorPane();
        jep.setContentType("text/html");

        JScrollPane jsp = new JScrollPane(jep);
        jsp.setPreferredSize(new Dimension(700, 700));
        jp.add(jsp);

        JButton jb1 = new JButton("Search Wikipedia");
        jb1.addActionListener(new MyActionListener(jep, jtf));
        jb1.setPreferredSize(new Dimension(700, 50));
        jf.getContentPane().add(jb1, BorderLayout.CENTER);

        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                log.info("Graphical interface successfully initiated.");
            }
        });
    }
}

class MyActionListener implements ActionListener {
    private ContentGetter contentGetter;
    private JEditorPane jEditorPane;
    private JTextField jTextField;
    static Logger log = Logger.getLogger(MyActionListener.class.getName());

    MyActionListener(JEditorPane jep, JTextField jtf) {
        contentGetter = new ContentGetter();
        jEditorPane = jep;
        jTextField = jtf;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        log.info("Search button pressed.");
        String input = jTextField.getText();
        String result = contentGetter.getContent(input);
        jEditorPane.setText(result);
    }
}