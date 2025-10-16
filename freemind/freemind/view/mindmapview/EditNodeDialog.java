/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 02.05.2004
 */
/*$Id: EditNodeDialog.java,v 1.1.4.1.16.20 2009/06/24 20:40:19 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.inet.jortho.SpellChecker;

import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author foltin
 * 
 */
public class EditNodeDialog extends EditNodeBase {
	private KeyEvent firstEvent;

	/** Private variable to hold the last value of the "Enter confirms" state. */
	private static Tools.BooleanHolder booleanHolderForConfirmState;

	public EditNodeDialog(final NodeView node, final String text,
			final KeyEvent firstEvent, ModeController controller,
			EditControl editControl) {
		super(node, text, controller, editControl);
		this.firstEvent = firstEvent;
	}

	class LongNodeDialog extends EditDialog {
		private static final long serialVersionUID = 6185443281994675732L;
		private JTextArea textArea;
		private FindAndReplacePanel findAndReplacePanel;
		private boolean isInFindMode = false;
		private List<Integer> matchPositions;
		private int currentMatchIndex;

		LongNodeDialog() {
			super(EditNodeDialog.this);
			textArea = new JTextArea(getText());
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			final JScrollPane editorScrollPane = new JScrollPane(textArea);
			editorScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			// Add Undo/Redo functionality
			final javax.swing.undo.UndoManager undoManager = new javax.swing.undo.UndoManager();
			textArea.getDocument().addUndoableEditListener(undoManager);

			textArea.getActionMap().put("Undo", new javax.swing.AbstractAction("Undo") {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								if (undoManager.canUndo()) {
									undoManager.undo();
								}
							} catch (javax.swing.undo.CannotUndoException e) {
							}
						}
					});
				}
			});
			textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Undo");

			textArea.getActionMap().put("Redo", new javax.swing.AbstractAction("Redo") {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								if (undoManager.canRedo()) {
									undoManager.redo();
								}
							} catch (javax.swing.undo.CannotRedoException e) {
							}
						}
					});
				}
			});
			textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Redo");
			
			// Find and Replace panel setup
			findAndReplacePanel = new FindAndReplacePanel();
			findAndReplacePanel.setVisible(false);
			matchPositions = new ArrayList<Integer>();
            currentMatchIndex = -1;

			findAndReplacePanel.addCloseListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (findAndReplacePanel.isVisible()) {
						findAndReplacePanel.setVisible(false);
						isInFindMode = false;
						LongNodeDialog.this.pack();
					}
				}
			});

			javax.swing.AbstractAction showFindAction = new javax.swing.AbstractAction("ShowFind") {
				public void actionPerformed(ActionEvent e) {
					if (!findAndReplacePanel.isVisible()) {
						findAndReplacePanel.setVisible(true);
						isInFindMode = true;
						LongNodeDialog.this.pack();
					}
					findAndReplacePanel.requestFocusOnSearchField();
					updateSearch();
				}
			};

			textArea.getActionMap().put("ShowFind", showFindAction);
			textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ShowFind");
			textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK), "ShowFind");

			findAndReplacePanel.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { updateSearch(); }
                public void removeUpdate(DocumentEvent e) { updateSearch(); }
                public void changedUpdate(DocumentEvent e) { updateSearch(); }
            });

            findAndReplacePanel.addMatchCaseListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateSearch();
                }
            });

			findAndReplacePanel.addFindNextListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (matchPositions.isEmpty()) {
						return;
					}
					currentMatchIndex++;
					if (currentMatchIndex >= matchPositions.size()) {
						currentMatchIndex = 0; // Wrap around
					}
					highlightCurrentMatch();
				}
			});

			findAndReplacePanel.addFindPreviousListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (matchPositions.isEmpty()) {
						return;
					}
					currentMatchIndex--;
					if (currentMatchIndex < 0) {
						currentMatchIndex = matchPositions.size() - 1; // Wrap around
					}
					highlightCurrentMatch();
				}
			});

			findAndReplacePanel.addReplaceListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					isInFindMode = false;
					String selectedText = textArea.getSelectedText();
			
					if (selectedText == null) {
						// If nothing is selected, just find the next occurrence.
						findAndReplacePanel.getFindNextButton().doClick();
						return;
					}
			
					String searchTerm = findAndReplacePanel.getSearchTerm();
					boolean matchCase = findAndReplacePanel.isMatchCase();
					boolean selectionMatches = matchCase ? selectedText.equals(searchTerm) : selectedText.equalsIgnoreCase(searchTerm);
			
					if (selectionMatches) {
						// This is the path where the bug occurs. Apply the robust CompoundEdit pattern here.
						final String replaceTerm = findAndReplacePanel.getReplaceTerm();
			
						final javax.swing.undo.CompoundEdit compoundEdit = new javax.swing.undo.CompoundEdit();
						final javax.swing.event.UndoableEditListener tempListener = new javax.swing.event.UndoableEditListener() {
							public void undoableEditHappened(javax.swing.event.UndoableEditEvent evt) {
								compoundEdit.addEdit(evt.getEdit());
							}
						};
			
						textArea.getDocument().removeUndoableEditListener(undoManager);
						textArea.getDocument().addUndoableEditListener(tempListener);
			
						// Actions to group:
						textArea.replaceSelection(replaceTerm);
						updateSearch();
						findAndReplacePanel.getFindNextButton().doClick();
			
						// Finalize
						textArea.getDocument().removeUndoableEditListener(tempListener);
						compoundEdit.end();
						undoManager.addEdit(compoundEdit);
						textArea.getDocument().addUndoableEditListener(undoManager);
			
					} else {
						// If selection doesn't match, just find next. No text change, no bug.
						findAndReplacePanel.getFindNextButton().doClick();
					}
				}
			});

			findAndReplacePanel.addReplaceAllListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					isInFindMode = false;
					String searchTerm = findAndReplacePanel.getSearchTerm();
					if (searchTerm.isEmpty()) {
						return;
					}
					String replaceTerm = findAndReplacePanel.getReplaceTerm();
					String originalText = textArea.getText();
					String newText;

					if (findAndReplacePanel.isMatchCase()) {
						newText = originalText.replace(searchTerm, replaceTerm);
					} else {
						StringBuilder sb = new StringBuilder();
						int index = 0;
						int searchLength = searchTerm.length();
						String lowerCaseText = originalText.toLowerCase();
						String lowerCaseSearchTerm = searchTerm.toLowerCase();
						int found;

						while ((found = lowerCaseText.indexOf(lowerCaseSearchTerm, index)) != -1) {
							sb.append(originalText, index, found);
							sb.append(replaceTerm);
							index = found + searchLength;
						}
						sb.append(originalText.substring(index));
						newText = sb.toString();
					}
					
					final String textToSet = newText;
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							final javax.swing.undo.CompoundEdit compoundEdit = new javax.swing.undo.CompoundEdit();
							final javax.swing.event.UndoableEditListener tempListener = new javax.swing.event.UndoableEditListener() {
								public void undoableEditHappened(javax.swing.event.UndoableEditEvent e) {
									compoundEdit.addEdit(e.getEdit());
								}
							};

							textArea.getDocument().removeUndoableEditListener(undoManager);
							textArea.getDocument().addUndoableEditListener(tempListener);

							textArea.setText(textToSet);
							textArea.requestFocusInWindow();
							findAndReplacePanel.setVisible(false);
							LongNodeDialog.this.pack();

							textArea.getDocument().removeUndoableEditListener(tempListener);
							compoundEdit.end();
							undoManager.addEdit(compoundEdit);
							textArea.getDocument().addUndoableEditListener(undoManager);
						}
					});
				}
			});

			findAndReplacePanel.addSearchFieldActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findAndReplacePanel.getFindNextButton().doClick();
				}
			});

			// int preferredHeight = new
			// Integer(getFrame().getProperty("el__default_window_height")).intValue();
			int preferredHeight = getNode().getHeight();
			preferredHeight = Math.max(
					preferredHeight,
					Integer.parseInt(getFrame().getProperty(
							"el__min_default_window_height")));
			preferredHeight = Math.min(
					preferredHeight,
					Integer.parseInt(getFrame().getProperty(
							"el__max_default_window_height")));

			int preferredWidth = getNode().getWidth();
			preferredWidth = Math.max(
					preferredWidth,
					Integer.parseInt(getFrame().getProperty(
							"el__min_default_window_width")));
			preferredWidth = Math.min(
					preferredWidth,
					Integer.parseInt(getFrame().getProperty(
							"el__max_default_window_width")));

			editorScrollPane.setPreferredSize(new Dimension(preferredWidth,
					preferredHeight));
			// textArea.setPreferredSize(new Dimension(500, 160));

			final JPanel panel = new JPanel();

			// String performedAction;
			final JButton okButton = new JButton();
			final JButton cancelButton = new JButton();
			final JButton splitButton = new JButton();
			final JCheckBox enterConfirms = new JCheckBox("",
					binOptionIsTrue("el__enter_confirms_by_default"));

			Tools.setLabelAndMnemonic(okButton, getText("ok"));
			Tools.setLabelAndMnemonic(cancelButton, getText("cancel"));
			Tools.setLabelAndMnemonic(splitButton, getText("split"));
			Tools.setLabelAndMnemonic(enterConfirms, getText("enter_confirms"));

			if (booleanHolderForConfirmState == null) {
				booleanHolderForConfirmState = new Tools.BooleanHolder();
				booleanHolderForConfirmState.setValue(enterConfirms
						.isSelected());
			} else {
				enterConfirms.setSelected(booleanHolderForConfirmState
						.getValue());
			}

			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// next try to avoid bug 1159: focus jumps to file-menu after closing html-editing-window
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							submit();
						}
					});
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});

			splitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					split();
				}
			});

			enterConfirms.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textArea.requestFocus();
					booleanHolderForConfirmState.setValue(enterConfirms
							.isSelected());
				}
			});

			// On Enter act as if OK button was pressed

			textArea.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					// If find panel is visible and we are in find mode, Enter finds the next match
					if (e.getKeyCode() == KeyEvent.VK_ENTER && findAndReplacePanel.isVisible() && isInFindMode) {
						// Don't treat this as a newline or submit
						e.consume();
						findAndReplacePanel.getFindNextButton().doClick();
						return; // Stop further processing of this event
					}
					
					// escape key in long text editor (PN)
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						e.consume();
						if (findAndReplacePanel.isVisible()) {
							findAndReplacePanel.setVisible(false);
							isInFindMode = false;
							LongNodeDialog.this.pack();
						} else {
							confirmedCancel();
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (enterConfirms.isSelected()
								&& (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
							e.consume();
							textArea.insert("\n", textArea.getCaretPosition());
						} else if (enterConfirms.isSelected()
								|| ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
							e.consume();
							submit();
						} else {
							e.consume();
							textArea.insert("\n", textArea.getCaretPosition());
						}
					}
				}

				public void keyTyped(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
				}
			});

			textArea.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
					conditionallyShowPopup(e);
				}

				public void mouseReleased(MouseEvent e) {
					conditionallyShowPopup(e);
				}

				private void conditionallyShowPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						JPopupMenu popupMenu = new EditPopupMenu(textArea);
						if (checkSpelling) {
							popupMenu.add(SpellChecker.createCheckerMenu());
							popupMenu.add(SpellChecker.createLanguagesMenu());
						}
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
						e.consume();
					}
				}
			});

			Font font = getNode().getTextFont();
			font = Tools.updateFontSize(font, getView().getZoom(),
					font.getSize());
			textArea.setFont(font);

			final Color nodeTextColor = getNode().getTextColor();
			textArea.setForeground(nodeTextColor);
			final Color nodeTextBackground = getNode().getTextBackground();
			textArea.setBackground(nodeTextBackground);
			textArea.setCaretColor(nodeTextColor);

			// panel.setPreferredSize(new Dimension(500, 160));
			// editorScrollPane.setPreferredSize(new Dimension(500, 160));

			JPanel buttonPane = new JPanel();
			buttonPane.add(enterConfirms);
			buttonPane.add(okButton);
			buttonPane.add(cancelButton);
			buttonPane.add(splitButton);
			buttonPane.setMaximumSize(new Dimension(1000, 20));

			panel.add(findAndReplacePanel);
			if (getFrame().getProperty("el__buttons_position").equals("above")) {
				panel.add(buttonPane);
				panel.add(editorScrollPane);
			} else {
				panel.add(editorScrollPane);
				panel.add(buttonPane);
			}

			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			setContentPane(panel);

			if (firstEvent instanceof KeyEvent) {
				redispatchKeyEvents(textArea, firstEvent);
			} // 1st key event defined
			else {
				textArea.setCaretPosition(getText().length());
			}

			if (checkSpelling) {
				SpellChecker.register(textArea, false, true, true, true);
			}
		}

		private void updateSearch() {
            String searchTerm = findAndReplacePanel.getSearchTerm();

            if (searchTerm.isEmpty()) {
                matchPositions.clear();
                currentMatchIndex = -1;
                findAndReplacePanel.updateMatchCount(0, 0);
                return;
            }

            matchPositions.clear();
            currentMatchIndex = -1;

            String text = textArea.getText();
			boolean matchCase = findAndReplacePanel.isMatchCase();
            String textToSearch = matchCase ? text : text.toLowerCase();
            String termToSearch = matchCase ? searchTerm : searchTerm.toLowerCase();

            int index = textToSearch.indexOf(termToSearch);
            while (index >= 0) {
                matchPositions.add(index);
                index = textToSearch.indexOf(termToSearch, index + 1);
            }

            findAndReplacePanel.updateMatchCount(0, matchPositions.size());
        }

        private void highlightCurrentMatch() {
            if (currentMatchIndex >= 0 && currentMatchIndex < matchPositions.size()) {
                int start = matchPositions.get(currentMatchIndex);
                int end = start + findAndReplacePanel.getSearchTerm().length();
                textArea.select(start, end);
                textArea.requestFocusInWindow();
                findAndReplacePanel.updateMatchCount(currentMatchIndex + 1, matchPositions.size());
            } else {
                textArea.select(textArea.getCaretPosition(), textArea.getCaretPosition());
                findAndReplacePanel.updateMatchCount(0, matchPositions.size());
            }
        }

		public void show() {
			textArea.requestFocus();
			super.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#cancel()
		 */
		protected void cancel() {
			getEditControl().cancel();
			super.cancel();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#split()
		 */
		protected void split() {
			getEditControl().split(textArea.getText(),
					textArea.getCaretPosition());
			super.split();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#submit()
		 */
		protected void submit() {
			getEditControl().ok(textArea.getText());
			super.submit();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#isChanged()
		 */
		protected boolean isChanged() {
			return !getText().equals(textArea.getText());
		}

		public Component getMostRecentFocusOwner() {
			if (isFocused()) {
				return getFocusOwner();
			} else {
				return textArea;
			}
		}
	}

	public void show() {
		final EditDialog dialog = new LongNodeDialog();

		dialog.pack(); // calculate the size

		// set position
		getView().scrollNodeToVisible(getNode(), 0);
		Tools.setDialogLocationRelativeTo(dialog, getNode());
		dialog.show();
	}
}