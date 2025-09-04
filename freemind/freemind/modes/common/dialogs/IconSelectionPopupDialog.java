package freemind.modes.common.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import freemind.main.FreeMindMain;
import freemind.modes.IconInformation;
import freemind.modes.mindmapmode.actions.RemoveAllIconsAction;
import freemind.modes.mindmapmode.actions.RemoveIconAction;

public class IconSelectionPopupDialog extends JDialog implements KeyListener,
        MouseListener {

    private Vector icons;
    private int result;
    private JPanel iconPanel = new JPanel();
    private JLabel[] iconLabels;
    private JLabel descriptionLabel;
    private JLabel searchLabel;
    private int numOfIcons;
    private int xDimension;
    private int yDimension;
    private Position selected = new Position(0, 0);
    private static Position lastPosition = new Position(0, 0);
    private FreeMindMain freeMindMain;
    private int mModifiers;

    private void filterIcons(String search) {
        boolean firstMatchFound = false;
        for (int i = 0; i < numOfIcons; ++i) {
            IconInformation icon = (IconInformation) icons.get(i);
            JLabel label = iconLabels[i];
            String description = icon.getDescription().toLowerCase();
            boolean matches = description.contains(search);
            label.setVisible(matches);
            if (!firstMatchFound && matches) {
                firstMatchFound = true;
                Position firstMatchPosition = getPositionFromIndex(i);
                setSelectedPosition(firstMatchPosition);
                select(getSelectedPosition());
            }
        }
        searchLabel.setText(search);
        iconPanel.revalidate();
    }

    public IconSelectionPopupDialog(JFrame caller, Vector icons,
            FreeMindMain freeMindMain) {

        super(caller, freeMindMain.getResourceString("select_icon"));
        getContentPane().setLayout(new BorderLayout());
        this.freeMindMain = freeMindMain;
        this.icons = icons;

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                close();
            }
        });

        // we will build a button-matrix which is closest to quadratical
        numOfIcons = icons.size();
        xDimension = new Double(Math.ceil(Math.sqrt(numOfIcons))).intValue();
        if (numOfIcons <= xDimension * (xDimension - 1)) {
            yDimension = xDimension - 1;
        } else {
            yDimension = xDimension;
        }

        GridLayout gridlayout = new GridLayout(0, xDimension);
        gridlayout.setHgap(3);
        gridlayout.setVgap(3);
        iconPanel.setLayout(gridlayout);

        iconLabels = new JLabel[numOfIcons];
        Collections.sort(icons, new Comparator<IconInformation>() {

            @Override
            public int compare(IconInformation o1, IconInformation o2) {
                // TODO Auto-generated method stub
                return o1.getDescription().compareTo(o2.getDescription());
            }

        });
        for (int i = 0; i < numOfIcons; ++i) {
            final IconInformation icon = (IconInformation) icons.get(i);
            JLabel label = new JLabel(icon.getIcon());
            label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            label.addMouseListener(this);
            iconLabels[i] = label;
            iconPanel.add(label);
        }
        int perIconSize = 35;
        iconPanel.setPreferredSize(new Dimension(xDimension * perIconSize,
                yDimension * perIconSize));
        iconPanel.setMinimumSize(new Dimension(xDimension * perIconSize,
                yDimension * perIconSize));
        iconPanel.setMaximumSize(new Dimension(xDimension * perIconSize,
                yDimension * perIconSize));
        iconPanel.setSize(new Dimension(xDimension * perIconSize, yDimension
                * perIconSize));

        getContentPane().add(iconPanel, BorderLayout.CENTER);
        descriptionLabel = new JLabel(" ");
        searchLabel = new JLabel("Use / to search");
        // descriptionLabel.setEnabled(false);
        getContentPane().add(descriptionLabel, BorderLayout.SOUTH);
        getContentPane().add(searchLabel, BorderLayout.NORTH);
        //getContentPane().add(search, BorderLayout.NORTH);
        setSelectedPosition(lastPosition);
        select(getSelectedPosition());
        addKeyListener(this);
        //search.addKeyListener(this);
        pack();
    }

    private boolean canSelect(Position position) {
        return ((position.getX() >= 0) && (position.getX() < xDimension)
                && (position.getY() >= 0) && (position.getY() < yDimension) && (calculateIndex(position) < numOfIcons));
    }

    private int calculateIndex(Position position) {
        return position.getY() * xDimension + position.getX();
    }

    private Position getPosition(JLabel caller) {
        int index = 0;
        for (index = 0; index < iconLabels.length; index++) {
            if (caller == iconLabels[index]) {
                break;
            }
        }
        return getPositionFromIndex(index);
    }

    private Position getPositionFromIndex(int index) {
        return new Position(index % xDimension, index / xDimension);
    }

    private void setSelectedPosition(Position position) {
        selected = position;
        lastPosition = position;
    }

    private Position getSelectedPosition() {
        return selected;
    }

    //Icon selection
    private void select(Position position) {
        unhighlight(getSelectedPosition());
        setSelectedPosition(position);
        highlight(position);
        final int index = calculateIndex(position);
        final IconInformation iconInformation = (IconInformation) icons
                .get(index);
        final String keyStroke = freeMindMain
                .getAdjustableProperty(iconInformation
                        .getKeystrokeResourceName());
        if (keyStroke != null) {
            descriptionLabel.setText(iconInformation.getDescription() + ", "
                    + keyStroke);
        }
        else {
            descriptionLabel.setText(iconInformation.getDescription());
        }
    } 

    private void unhighlight(Position position) {
        iconLabels[calculateIndex(position)].setBorder(BorderFactory
                .createBevelBorder(BevelBorder.RAISED));
    }

    private void highlight(Position position) {
        iconLabels[calculateIndex(position)].setBorder(BorderFactory
                .createBevelBorder(BevelBorder.LOWERED, Color.GREEN, Color.DARK_GRAY));
    }

    private void cursorLeft() {
        Position newPosition = new Position(getSelectedPosition().getX() - 1,
                getSelectedPosition().getY());
        if (canSelect(newPosition)) {
            System.out.println(newPosition + "Esquerda");
            select(newPosition);
        }
    }

    private void cursorRight() {
        Position newPosition = new Position(getSelectedPosition().getX() + 1,
                getSelectedPosition().getY());
        if (canSelect(newPosition)) {
            System.out.println(newPosition + "Direita");
            select(newPosition);
        }
    }

    private void cursorUp() {
        Position newPosition = new Position(getSelectedPosition().getX(),
                getSelectedPosition().getY() - 1);
        if (canSelect(newPosition)) {
            System.out.println(newPosition + "Cima");
            select(newPosition);
        }
    }

    private void cursorDown() {
        Position newPosition = new Position(getSelectedPosition().getX(),
                getSelectedPosition().getY() + 1);
        if (canSelect(newPosition)) {
            System.out.println(newPosition + "Baixo");
            select(newPosition);
        }
    }

    private void addIcon(int pModifiers) {
        result = calculateIndex(getSelectedPosition());
        mModifiers = pModifiers;
        this.dispose();
    }

    public int getResult() {
        return result;
    }

    /**
     * Transfer shift masks from InputEvent to ActionEvent. But, why don't they
     * use the same constants???? Java miracle.
     */
    public int getModifiers() {
        int m = mModifiers;
        if ((mModifiers & (ActionEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK)) != 0) {
            m |= ActionEvent.SHIFT_MASK;
        }
        if ((mModifiers & (ActionEvent.CTRL_MASK | InputEvent.CTRL_DOWN_MASK)) != 0) {
            m |= ActionEvent.CTRL_MASK;
        }
        if ((mModifiers & (ActionEvent.ALT_MASK | InputEvent.ALT_DOWN_MASK)) != 0) {
            m |= ActionEvent.ALT_MASK;
        }
        return m;
    }

    private boolean isCapturingSearchText = false;
    private String searchText = "";

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == '/') {
            isCapturingSearchText = true;
            searchText = "";
            searchLabel.setText("Search: ");
            keyEvent.consume();
            return;
        }

        if (isCapturingSearchText) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    isCapturingSearchText = false;
                    searchLabel.setText("Use / to search");
                    break;
                case KeyEvent.VK_ESCAPE:
                    isCapturingSearchText = false;
                    searchText = "";
                    filterIcons(searchText);
                    searchLabel.setText("Use / to search");
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (!searchText.isEmpty()) {
                        searchText = searchText.substring(0, searchText.length() - 1);
                        filterIcons(searchText);
                    } else {
                        isCapturingSearchText = false;
                        searchLabel.setText("Use / to search");
                        triggerRemoveLastIconAction(keyEvent.getModifiers());
                    }
                    break;
                default:
                    if (Character.isLetterOrDigit(keyEvent.getKeyChar())) {
                        searchText += keyEvent.getKeyChar();
                        filterIcons(searchText);
                    }
            }
            keyEvent.consume();
            return;
        }

        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                triggerRemoveAllIconsAction(keyEvent.getModifiers());
                keyEvent.consume();
                break;
            case KeyEvent.VK_BACK_SPACE:
                triggerRemoveLastIconAction(keyEvent.getModifiers());
                keyEvent.consume();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                cursorRight();
                return;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                cursorLeft();
                return;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                cursorDown();
                return;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                cursorUp();
                return;
            case KeyEvent.VK_ESCAPE:
                keyEvent.consume();
                close();
                return;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                addIcon(keyEvent.getModifiers());
                keyEvent.consume();
                return;
        }

        int index = findIndexByKeyEvent(keyEvent);
        if (index != -1) {
            result = index;
            lastPosition = getPositionFromIndex(index);
            mModifiers = keyEvent.getModifiers();
            keyEvent.consume();
            this.dispose();
        }
    }

    private void triggerRemoveLastIconAction(int modifiers) {
        for (int i = 0; i < icons.size(); i++) {
            Action action = (Action) icons.get(i);
            if (action instanceof RemoveIconAction) {
                result = i;
                mModifiers = modifiers;
                this.dispose();
                return;
            }
        }
    }

    private void triggerRemoveAllIconsAction(int modifiers) {
        for (int i = 0; i < icons.size(); i++) {
            Action action = (Action) icons.get(i);
            if (action instanceof RemoveAllIconsAction) {
                result = i;
                mModifiers = modifiers;
                this.dispose();
                return;
            }
        }
    }
    
    private KeyStroke getKeyStrokeForEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() != 0) {
            return KeyStroke.getKeyStroke(keyEvent.getKeyChar());
        }
        return KeyStroke.getKeyStroke(keyEvent.getKeyCode(), 0);
    }

    private int findIndexByKeyEvent(KeyEvent keyEvent) {
        for (int i = 0; i < icons.size(); i++) {
            IconInformation info = (IconInformation) icons.get(i);
            final KeyStroke iconKeyStroke = info.getKeyStroke();
            if (iconKeyStroke != null
                    && (keyEvent.getKeyCode() == iconKeyStroke.getKeyCode()
                    && keyEvent.getKeyCode() != 0
                    && (iconKeyStroke.getModifiers() & KeyEvent.SHIFT_MASK) == (keyEvent
                    .getModifiers() & KeyEvent.SHIFT_MASK) || keyEvent
                            .getKeyChar() == iconKeyStroke.getKeyChar())
                    && keyEvent.getKeyChar() != 0
                    && keyEvent.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                return i;
            }
        }
        return -1;
    }

    private void close() {
        result = -1;
        mModifiers = 0;
        this.dispose();
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        addIcon(mouseEvent.getModifiers());
    }

    public void mouseEntered(MouseEvent arg0) {
        select(getPosition((JLabel) arg0.getSource()));
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    static class Position {

        private int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String toString() {
            return ("(" + getX() + "," + getY() + ")");
        }
    }
}