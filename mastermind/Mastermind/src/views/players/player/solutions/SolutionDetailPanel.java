package views.players.player.solutions;

import model.Sequence;

import javax.swing.*;
import java.awt.*;

public class SolutionDetailPanel extends JPanel {
    private final JLabel name, sequence, otherInfo;

    public SolutionDetailPanel() {
        this.name = new JLabel("Name");
        this.add(name);

        this.sequence = new JLabel("Sequence");
        this.add(sequence);

        this.otherInfo = new JLabel("Other info");
        this.add(this.otherInfo);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public String getName() {
        return this.name.getText();
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setSequence(Sequence sequence) {
        this.sequence.setText(sequence.toString());
        this.validate();
    }

    public void setInfo(int rightPlacedNumbers, int rightNumbers) {
        this.otherInfo.setText(String.format("RP: %d - RN: %d", rightPlacedNumbers, rightNumbers));
        this.validate();
    }

    public void showSolved() {
        SwingUtilities.invokeLater(() -> this.sequence.setText(this.sequence.getText() + "(Solved)"));
        SwingUtilities.invokeLater(this::validate);
    }
}