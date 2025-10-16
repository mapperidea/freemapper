package freemind.view.mindmapview;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class FindAndReplacePanel extends JPanel {

    private final JTextField searchField;
    private final JTextField replaceField;
    private final JCheckBox matchCaseCheckBox;
    private final JButton findPreviousButton;
    private final JButton findNextButton;
    private final JLabel matchCountLabel;
    private final JButton replaceButton;
    private final JButton replaceAllButton;
    private final JButton closeButton;

    public FindAndReplacePanel() {
        super(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(15);
        replaceField = new JTextField(15);
        matchCaseCheckBox = new JCheckBox("Aa");
        matchCaseCheckBox.setToolTipText("Diferenciar maiúsculas/minúsculas");
        findPreviousButton = new JButton("Anterior");
        findNextButton = new JButton("Próximo");
        matchCountLabel = new JLabel("");
        replaceButton = new JButton("Substituir");
        replaceAllButton = new JButton("Substituir Todos");
        closeButton = new JButton("X");

        add(new JLabel("Localizar:"));
        add(searchField);
        add(findPreviousButton);
        add(findNextButton);
        add(matchCountLabel);
        add(new JLabel("Substituir por:"));
        add(replaceField);
        add(replaceButton);
        add(replaceAllButton);
        add(matchCaseCheckBox);
        add(closeButton);
    }

    public String getSearchTerm() {
        return searchField.getText();
    }

    public String getReplaceTerm() {
        return replaceField.getText();
    }

    public boolean isMatchCase() {
        return matchCaseCheckBox.isSelected();
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public void updateMatchCount(int current, int total) {
        if (total == 0) {
            matchCountLabel.setText("  0 de 0");
        } else {
            matchCountLabel.setText("  " + current + " de " + total);
        }
    }

    public void addFindNextListener(ActionListener listener) {
        findNextButton.addActionListener(listener);
    }

    public void addFindPreviousListener(ActionListener listener) {
        findPreviousButton.addActionListener(listener);
    }

    public void addSearchFieldActionListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }

    public void addReplaceListener(ActionListener listener) {
        replaceButton.addActionListener(listener);
    }

    public void addReplaceAllListener(ActionListener listener) {
        replaceAllButton.addActionListener(listener);
    }

    public void addMatchCaseListener(ActionListener listener) {
        matchCaseCheckBox.addActionListener(listener);
    }

    public JButton getFindNextButton() {
        return findNextButton;
    }

    public void addCloseListener(ActionListener listener) {
        closeButton.addActionListener(listener);
    }

    public void requestFocusOnSearchField() {
        searchField.requestFocusInWindow();
    }
}