package freemind.view.mindmapview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FindAndReplacePanel extends JPanel {

    private final JTextField searchField;
    private final JTextField replaceField;
    private final JCheckBox matchCaseCheckBox;
    private final JButton findNextButton;
    private final JButton replaceButton;
    private final JButton replaceAllButton;
    private final JButton closeButton;

    public FindAndReplacePanel() {
        super(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        replaceField = new JTextField(20);
        matchCaseCheckBox = new JCheckBox("Diferenciar maiúsculas/minúsculas");
        findNextButton = new JButton("Localizar Próximo");
        replaceButton = new JButton("Substituir");
        replaceAllButton = new JButton("Substituir Todos");
        closeButton = new JButton("X");

        add(new JLabel("Localizar:"));
        add(searchField);
        add(new JLabel("Substituir por:"));
        add(replaceField);
        add(findNextButton);
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

    public void addFindNextListener(ActionListener listener) {
        findNextButton.addActionListener(listener);
    }

    public void addReplaceListener(ActionListener listener) {
        replaceButton.addActionListener(listener);
    }

        public void addReplaceAllListener(ActionListener listener) {

            replaceAllButton.addActionListener(listener);

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
