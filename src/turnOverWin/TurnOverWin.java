package turnOverWin;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import code.TurnOverL;
import code.TurnOverR;

public class TurnOverWin {

	private JFrame frame;
	private JTextField txtDtempcaseroltxt;
	private JTextField txtDtempcaserortxt;
	private JTextField textField;
	private JTextField reactionParam;
	private JTextField textField_1;
	private JTextField textFieldCaseId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TurnOverWin window = new TurnOverWin();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TurnOverWin() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 576, 603);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 559, 184);
		frame.getContentPane().add(panel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 189, 267, 336);
		frame.getContentPane().add(scrollPane);

		final JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(287, 189, 262, 336);
		frame.getContentPane().add(scrollPane_1);

		final JTextArea textArea_1 = new JTextArea();
		scrollPane_1.setViewportView(textArea_1);

		txtDtempcaseroltxt = new JTextField();
		txtDtempcaseroltxt.setText("D:/temp/casEroL.txt");
		txtDtempcaseroltxt.setBounds(10, 55, 164, 21);
		panel.add(txtDtempcaseroltxt);
		txtDtempcaseroltxt.setColumns(10);

		final JCheckBox chckbxR = new JCheckBox(
				"r\u539F\u503C\u70BA\u9AD4\u91CD");
		chckbxR.setBounds(10, 124, 97, 23);
		panel.add(chckbxR);

		final JRadioButton rdbtnM = new JRadioButton("\u4E58");
		rdbtnM.setSelected(true);
		rdbtnM.setBounds(295, 115, 49, 23);
		JRadioButton rdbtnD = new JRadioButton("\u9664");
		rdbtnD.setBounds(295, 140, 43, 23);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnM);
		bg.add(rdbtnD);
		panel.add(rdbtnM);
		panel.add(rdbtnD);

		textFieldCaseId = new JTextField();
		textFieldCaseId.setBounds(71, 10, 37, 21);
		panel.add(textFieldCaseId);
		textFieldCaseId.setColumns(10);

		JLabel lblCaseId = new JLabel("case ID\uFF1A");
		lblCaseId.setBounds(10, 13, 57, 15);
		panel.add(lblCaseId);

		final JCheckBox checkRIsWeight = new JCheckBox("\u6AA2\u67E5\u53CD\u4F5C\u7528\u529B");
		checkRIsWeight.setSelected(true);
		checkRIsWeight.setBounds(240, 9, 109, 23);
		panel.add(checkRIsWeight);


		final JButton btnNewButton = new JButton("\u7522\u751Fx");
		btnNewButton.setBounds(183, 45, 87, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String calMode = rdbtnM.isSelected() ? "M" : "D";// 計算反作用力，乘M，除D
				float calVal = Float.parseFloat(reactionParam.getText());// 計算反作用力的值
				// TurnOver.CalMode = "D";//計算反作用力，乘M，除D
				// TurnOver.CalVal = 1.8f;//計算反作用力的值
				int step = 1;
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaseroltxt.getText();
					TurnOverL objL = new TurnOverL();
					String result = objL.lRun(fileName, null, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected(), objL, calMode, calVal);
					Float avgR = objL.getAvgRCal();
					textArea_1.setText(result);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					textArea_1.setCaretPosition(0);
					copyText(result);
					textField.setText(null);
					if (chckbxR.isSelected())
						textField.setText(new DecimalFormat("#.0").format(
								objL.getAvgR()).toString());
					else
						textField.setText(new DecimalFormat("#.0").format(
								objL.getAvgRCal()).toString());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}

			}
		});
		panel.setLayout(null);
		panel.add(btnNewButton);

		final JButton btnNewButton_1 = new JButton("\u6574\u7406x");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int step = 3;
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaseroltxt.getText();
					String result = new TurnOverL().lRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected());
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		btnNewButton_1.setBounds(94, 156, 87, 23);
		panel.add(btnNewButton_1);

		final JButton btnNewButton_2 = new JButton("\u7522\u751Fr");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = txtDtempcaseroltxt.getText();
				int step = 2;
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String result = new TurnOverL().lRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected());
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		btnNewButton_2.setBounds(183, 156, 87, 23);
		panel.add(btnNewButton_2);

		final JButton button = new JButton("\u7522\u751Fr");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = txtDtempcaserortxt.getText();
				int step = 2;
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String result = new TurnOverR().rRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected());
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		button.setBounds(462, 156, 87, 23);
		panel.add(button);

		final JButton button_1 = new JButton("\u7522\u751Fx");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int step = 1;
				String calMode = rdbtnM.isSelected() ? "M" : "D";// 計算反作用力，乘M，除D
				float calVal = Float.parseFloat(reactionParam.getText());// 計算反作用力的值
				// TurnOver.CalMode = "D";//計算反作用力，乘M，除D
				// TurnOver.CalVal = 1.8f;//計算反作用力的值
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaserortxt.getText();
					TurnOverR objR = new TurnOverR();
					String result = objR.rRun(fileName, null, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected(), objR, calMode, calVal);
					Float avgR = objR.getAvgRCal();
					textArea_1.setText(result);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
					textArea_1.setCaretPosition(0);
					textField_1.setText(null);
					if (chckbxR.isSelected())
						textField_1.setText(new DecimalFormat("#.0").format(
								objR.getAvgR()).toString());
					else
						textField_1.setText(new DecimalFormat("#.0").format(
								objR.getAvgRCal()).toString());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		button_1.setBounds(462, 45, 87, 23);
		panel.add(button_1);

		final JButton button_2 = new JButton("\u6574\u7406x");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int step = 3;
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaserortxt.getText();
					String result = new TurnOverR().rRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected());
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		button_2.setBounds(376, 156, 87, 23);
		panel.add(button_2);

		txtDtempcaserortxt = new JTextField();
		txtDtempcaserortxt.setText("D:/temp/casEroR.txt");
		txtDtempcaserortxt.setColumns(10);
		txtDtempcaserortxt.setBounds(287, 55, 164, 21);
		panel.add(txtDtempcaserortxt);

		final JButton button_6 = new JButton("\u7522\u751F\u7D50\u679C");
		button_6.setEnabled(false);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int step = 4;
				String calMode = rdbtnM.isSelected() ? "M" : "D";// 計算反作用力，乘M，除D
				float calVal = Float.parseFloat(reactionParam.getText());// 計算反作用力的值
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaseroltxt.getText();
					TurnOverL objL = new TurnOverL();
					String result = objL.lRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected(), objL, calMode, calVal);
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		button_6.setBounds(183, 70, 87, 23);
		panel.add(button_6);

		final JButton button_7 = new JButton("\u7522\u751F\u7D50\u679C");
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int step = 4;
				String calMode = rdbtnM.isSelected() ? "M" : "D";// 計算反作用力，乘M，除D
				float calVal = Float.parseFloat(reactionParam.getText());// 計算反作用力的值
				String input = textArea.getText();
				textArea_1.setText(null);
				try {
					String fileName = txtDtempcaserortxt.getText();
					TurnOverR objR = new TurnOverR();
					String result = new TurnOverR().rRun(fileName, input, step,
							chckbxR.isSelected(), checkRIsWeight.isSelected(), objR, calMode, calVal);
					textArea_1.setText(result);
					textArea.setText(null);
					textArea_1.requestFocusInWindow();
					textArea_1.selectAll();
					copyText(result);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});
		button_7.setBounds(462, 70, 87, 23);
		panel.add(button_7);

		JButton button_3 = new JButton("\u8907\u88FD\u7D50\u679C");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea_1.requestFocusInWindow();
				textArea_1.selectAll();
				String result = textArea_1.getText();
				copyText(result);
			}
		});
		button_3.setBounds(241, 535, 87, 23);
		frame.getContentPane().add(button_3);

		button.setVisible(false);
		button_1.setEnabled(false);
		button_2.setVisible(false);
		button_7.setEnabled(false);
		btnNewButton.setEnabled(false);
		btnNewButton_1.setVisible(false);
		btnNewButton_2.setVisible(false);

		final JButton button_4 = new JButton("\u5DE6");
		final JButton button_5 = new JButton("\u53F3");
		button_5.setEnabled(false);
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnNewButton.setEnabled(true);
				btnNewButton_1.setEnabled(true);
				btnNewButton_2.setEnabled(true);
				button_6.setEnabled(true);
				button.setEnabled(false);
				button_1.setEnabled(false);
				button_2.setEnabled(false);
				button_7.setEnabled(false);
				button_4.setEnabled(false);
				button_4.setBackground(Color.RED);
				button_5.setEnabled(true);
				button_5.setBackground(button.getBackground());// 隨便找一個按鈕取它的背景
				txtDtempcaseroltxt.setText("D:/temp/casE"
						+ textFieldCaseId.getText() + "roL.txt");
				txtDtempcaserortxt.setText("D:/temp/casEroR.txt");
				textArea_1.setText(null);
			}
		});
		button_4.setBounds(10, 81, 66, 23);
		panel.add(button_4);

		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnNewButton.setEnabled(false);
				btnNewButton_1.setEnabled(false);
				btnNewButton_2.setEnabled(false);
				button_6.setEnabled(false);
				button.setEnabled(true);
				button_1.setEnabled(true);
				button_2.setEnabled(true);
				button_7.setEnabled(true);
				button_4.setEnabled(true);
				button_4.setBackground(button.getBackground());// 隨便找一個按鈕取它的背景
				button_5.setEnabled(false);
				button_5.setBackground(Color.RED);
				txtDtempcaserortxt.setText("D:/temp/casE"
						+ textFieldCaseId.getText() + "roR.txt");
				txtDtempcaseroltxt.setText("D:/temp/casEroL.txt");
				textArea_1.setText(null);
			}
		});
		button_5.setBounds(285, 81, 66, 23);
		panel.add(button_5);

		textField = new JTextField();
		textField.setBounds(114, 125, 37, 21);
		panel.add(textField);
		textField.setColumns(10);

		JLabel label = new JLabel("\u5DE6");
		label.setBounds(157, 128, 17, 15);
		panel.add(label);

		reactionParam = new JTextField();
		reactionParam.setText("30");
		reactionParam.setBounds(344, 125, 37, 21);
		panel.add(reactionParam);
		reactionParam.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(183, 125, 37, 21);
		panel.add(textField_1);

		JLabel label_1 = new JLabel("\u53F3");
		label_1.setBounds(227, 128, 17, 15);
		panel.add(label_1);


		JButton btnOk = new JButton("new case");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtDtempcaseroltxt.setText("D:/temp/casEroL.txt");
				txtDtempcaserortxt.setText("D:/temp/casEroR.txt");
				textField.setText("");
				textField_1.setText("");
				button_4.setBackground(button.getBackground());
				button_4.setEnabled(true);
				button_5.setBackground(button.getBackground());
				button_5.setEnabled(false);
				button_1.setEnabled(false);
				button_7.setEnabled(false);

				String calMode = rdbtnM.isSelected() ? "M" : "D";// 計算反作用力，乘M，除D
				float calVal = Float.parseFloat(reactionParam.getText());// 計算反作用力的值
				int step = 0;
				try {
					String fileName = "D:/temp/casE"
							+ textFieldCaseId.getText() + "roL.txt";
					TurnOverL objL = new TurnOverL();
					String calAvgR = objL.lRun(fileName, null, step, chckbxR.isSelected(), checkRIsWeight.isSelected(), objL,
							calMode, calVal);
					textField.setText(null);
					if (chckbxR.isSelected())
						textField.setText(new DecimalFormat("#.0").format(
								objL.getAvgR()).toString());
					else //R不為體重，直接取計算過的值
						textField.setText(new DecimalFormat("#.0").format(
								Float.parseFloat(calAvgR)).toString());

					fileName = "D:/temp/casE" + textFieldCaseId.getText()
							+ "roR.txt";
					TurnOverR objR = new TurnOverR();
					calAvgR = objR.rRun(fileName, null, step, chckbxR.isSelected(), checkRIsWeight.isSelected(), objR,
							calMode, calVal);
					textField_1.setText(null);
					if (chckbxR.isSelected())
						textField_1.setText(new DecimalFormat("#.0").format(
								objR.getAvgR()).toString());
					else //R不為體重，直接取計算過的值
						textField_1.setText(new DecimalFormat("#.0").format(
								Float.parseFloat(calAvgR)).toString());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage());
				}
			}
		});
		btnOk.setBounds(122, 9, 98, 23);
		panel.add(btnOk);
		

		// Listen for changes in the text
		textFieldCaseId.getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						resetCheckbox();
					}

					public void removeUpdate(DocumentEvent e) {
						resetCheckbox();
					}

					public void insertUpdate(DocumentEvent e) {
						resetCheckbox();
					}

					public void resetCheckbox() {
						reactionParam.setText("30");
						rdbtnM.setSelected(true);

						chckbxR.setSelected(false);
						checkRIsWeight.setSelected(true);
						
						textArea_1.setText(null);
					}
				});
	}

	public void copyText(String myString) {
		StringSelection stringSelection = new StringSelection(myString);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, new ClipboardOwner() {
			public void lostOwnership(Clipboard arg0, Transferable arg1) {
			}
		});
	}
}
