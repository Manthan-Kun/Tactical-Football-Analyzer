import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to safely launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Tactical Football Pitch Analyzer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1050, 680); // 10:5 x 6:8 pitch ratio
                frame.setLocationRelativeTo(null); // Center on screen

                PitchPanel pitchPanel = new PitchPanel();
                frame.add(pitchPanel);

                frame.setVisible(true); // Make the window visible and keep it open!
            }
        });
    }
}