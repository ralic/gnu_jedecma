/*  
 * Stat2.java - very basic pathology statistics
 * 
 * Copyright (c) 2017 Stefano Marchetti
 * 
 * This file is part of Jedecma - breast ultrasound examinations archiving software
 * 
 * Jedecma is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jedecma is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jedecma.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package jedecma;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.awt.print.*;

public class Stat2 implements Menuable {
	private MyJlabel pcsd, seld, perc;
	private static String pcsString = Jedecma.localMessagesBundle
			.getString("SEARCH");
	private static String prtString = Jedecma.localMessagesBundle
			.getString("PRINT");
	private String queryToPrint;
	private JButton pcsBut, prtBut;
	private JPanel mainPan, qryPan, butPan;
	private MyJTextField dgneco, ageFrom, ageTo, spsghiFrom, spsghiTo;
	private MyJComboBox tmmica, arprol, isteco, citeco;
	private JCheckBox dgnecoExact;

	public Stat2() {
	}

	public void start() {
		doGui(Jedecma.mainPan);

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		Uti1.bldConst(gbc, 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gb.setConstraints(mainPan, gbc);
		Jedecma.mainPan.setLayout(gb);
		Jedecma.mainPan.add(mainPan);

		Jedecma.mf.pack();
		Jedecma.mf.setVisible(true);
	}

	public void doGui(Container contPan) {
		int cbw = 7;
		mainPan = new JPanel();
		GridBagLayout gbMPan = new GridBagLayout();
		GridBagConstraints gbcMPan = new GridBagConstraints();
		mainPan.setLayout(gbMPan);

		qryPan = new JPanel();
		qryPan.setBorder(BorderFactory.createTitledBorder(" "
				+ Jedecma.localMessagesBundle.getString("STAT2_TIT") + " "));
		Uti1.bldConst(gbcMPan, 0, 0, 1, 10, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		gbMPan.setConstraints(qryPan, gbcMPan);
		mainPan.add(qryPan);

		butPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Uti1.bldConst(gbcMPan, 0, 11, GridBagConstraints.REMAINDER, 1, 1, 0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
		gbMPan.setConstraints(butPan, gbcMPan);
		mainPan.add(butPan);

		pcsBut = new JButton(pcsString);
		pcsBut.setMnemonic(KeyEvent.VK_C);
		pcsBut.addActionListener(new QryListener());
		pcsBut.setActionCommand(pcsString);
		butPan.add(pcsBut);

		prtBut = new JButton(prtString);
		prtBut.addActionListener(new PrtListener());
		prtBut.setActionCommand(prtString);
		prtBut.setMnemonic(KeyEvent.VK_P);
		prtBut.setEnabled(false);
		butPan.add(prtBut);

		GridBagLayout gbQPan = new GridBagLayout();
		GridBagConstraints gbcQPan = new GridBagConstraints();
		qryPan.setLayout(gbQPan);

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("ECO_DIAG"));
			Uti1.bldConst(gbcQPan, 0, 0, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			dgneco = new MyJTextField(20, "", new float[] {}, 7);
			dgneco.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 8, 0, 14, 1, 1, 0);
			gbQPan.setConstraints(dgneco, gbcQPan);
			qryPan.add(dgneco);
		}

		{
			dgnecoExact = new JCheckBox(
					Jedecma.localMessagesBundle.getString("EXACT"));
			dgnecoExact.setSelected(false);
			Uti1.bldConst(gbcQPan, 22, 0, 4, 1, 1, 0);
			gbQPan.setConstraints(dgnecoExact, gbcQPan);
			qryPan.add(dgnecoExact);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PAT_AGE_START"));
			Uti1.bldConst(gbcQPan, 0, 1, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			ageFrom = new MyJTextField(4, String.valueOf(0), new float[] { 0,
					999 }, 1);
			ageFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 8, 1, 4, 1, 0, 0,
					GridBagConstraints.NORTHWEST, 0);
			gbQPan.setConstraints(ageFrom, gbcQPan);
			qryPan.add(ageFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PAT_AGE_END"));
			Uti1.bldConst(gbcQPan, 12, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			ageTo = new MyJTextField(4, String.valueOf(0),
					new float[] { 0, 999 }, 1);
			ageTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 14, 1, 4, 1, 0, 0,
					GridBagConstraints.NORTHWEST, 0);
			gbQPan.setConstraints(ageTo, gbcQPan);
			qryPan.add(ageTo);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("GL_TICK_START"));
			Uti1.bldConst(gbcQPan, 0, 2, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			spsghiFrom = new MyJTextField(4, String.valueOf(0.0), new float[] {
					0, 999 }, 2);
			spsghiFrom.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 8, 2, 4, 1, 0, 0,
					GridBagConstraints.NORTHWEST, 0);
			gbQPan.setConstraints(spsghiFrom, gbcQPan);
			qryPan.add(spsghiFrom);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("GL_TICK_END"));
			Uti1.bldConst(gbcQPan, 12, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			spsghiTo = new MyJTextField(4, String.valueOf(0.0), new float[] {
					0, 999 }, 2);
			spsghiTo.addFocusListener(new CtrTextField());
			Uti1.bldConst(gbcQPan, 14, 2, 4, 1, 0, 0,
					GridBagConstraints.NORTHWEST, 0);
			gbQPan.setConstraints(spsghiTo, gbcQPan);
			qryPan.add(spsghiTo);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("MICROCAL"));
			Uti1.bldConst(gbcQPan, 0, 3, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			tmmica = new MyJComboBox(new String[] {
					"0=" + Jedecma.localMessagesBundle.getString("CB_NA"),
					"1=" + Jedecma.localMessagesBundle.getString("CB_NO"),
					"2=" + Jedecma.localMessagesBundle.getString("CB_YES") },
					String.valueOf(0));
			Uti1.bldConst(gbcQPan, 8, 3, cbw, 1, 1, 0);
			gbQPan.setConstraints(tmmica, gbcQPan);
			qryPan.add(tmmica);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PROL_AREA"));
			Uti1.bldConst(gbcQPan, 0, 4, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			arprol = new MyJComboBox(new String[] {
					"0=" + Jedecma.localMessagesBundle.getString("CB_NA"),
					"1=" + Jedecma.localMessagesBundle.getString("CB_NO"),
					"2=" + Jedecma.localMessagesBundle.getString("CB_YES") },
					String.valueOf(0));
			Uti1.bldConst(gbcQPan, 8, 4, cbw, 1, 1, 0);
			gbQPan.setConstraints(arprol, gbcQPan);
			qryPan.add(arprol);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("ECODGN_EQ_ISTECO"));
			Uti1.bldConst(gbcQPan, 0, 5, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			isteco = new MyJComboBox(new String[] {
					"0=" + Jedecma.localMessagesBundle.getString("CB_NA"),
					"1=" + Jedecma.localMessagesBundle.getString("CB_NO"),
					"2=" + Jedecma.localMessagesBundle.getString("CB_YES") },
					String.valueOf(0));
			Uti1.bldConst(gbcQPan, 8, 5, cbw, 1, 1, 0);
			gbQPan.setConstraints(isteco, gbcQPan);
			qryPan.add(isteco);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("ECODGN_EQ_CITECO"));
			Uti1.bldConst(gbcQPan, 0, 6, 8, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			citeco = new MyJComboBox(new String[] {
					"0=" + Jedecma.localMessagesBundle.getString("CB_NA"),
					"1=" + Jedecma.localMessagesBundle.getString("CB_NO"),
					"2=" + Jedecma.localMessagesBundle.getString("CB_YES") },
					String.valueOf(0));
			Uti1.bldConst(gbcQPan, 8, 6, cbw, 1, 1, 0);
			gbQPan.setConstraints(citeco, gbcQPan);
			qryPan.add(citeco);
		}

		{
			MyJlabel lab = new MyJlabel("    ");
			Uti1.bldConst(gbcQPan, 0, 7, 2, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
		}

		{
			MyJlabel lab = new MyJlabel("    ");
			Uti1.bldConst(gbcQPan, 0, 8, 2, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
		}

		{
			MyJlabel lab = new MyJlabel("    ");
			Uti1.bldConst(gbcQPan, 0, 9, 2, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
		}

		{
			MyJlabel lab = new MyJlabel("    ");
			Uti1.bldConst(gbcQPan, 0, 10, 2, 1, 0, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PROCESSED"));
			Uti1.bldConst(gbcQPan, 0, 11, 6, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			pcsd = new MyJlabel("0");
			pcsd.setBorder(BorderFactory.createLoweredBevelBorder());
			Uti1.bldConst(gbcQPan, 6, 11, 5, 1, 1, 0);
			gbQPan.setConstraints(pcsd, gbcQPan);
			qryPan.add(pcsd);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("SELECTED"));
			Uti1.bldConst(gbcQPan, 11, 11, 5, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			seld = new MyJlabel("0");
			seld.setBorder(BorderFactory.createLoweredBevelBorder());
			Uti1.bldConst(gbcQPan, 16, 11, 5, 1, 1, 0);
			gbQPan.setConstraints(seld, gbcQPan);
			qryPan.add(seld);
		}

		{
			MyJlabel lab = new MyJlabel(
					Jedecma.localMessagesBundle.getString("PERC"));
			Uti1.bldConst(gbcQPan, 21, 11, 4, 1, 1, 0);
			gbQPan.setConstraints(lab, gbcQPan);
			qryPan.add(lab);
			perc = new MyJlabel("0.00");
			perc.setBorder(BorderFactory.createLoweredBevelBorder());
			Uti1.bldConst(gbcQPan, 25, 11, 5, 1, 1, 0);
			gbQPan.setConstraints(perc, gbcQPan);
			qryPan.add(perc);
		}

	} //

	public void stop() {
		Jedecma.mainPan.remove(mainPan);
		Jedecma.mf.repaint();
	}

	class QryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			float percent;
			ResultSet rSet;
			String q;

			long all, selected;
			all = 0;
			q = "SELECT count(*) FROM EDECMA WHERE numarc is NOT NULL";
			rSet = Jedecma.dbmgr.executeQuery(q);
			try {
				if (rSet.next()) {
					// all = rSet.getInt("count(*)");
					all = rSet.getInt(1);
				}
			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error("SQL error", true);
			}

			q = "";
			{
				dgneco.setText(dgneco.getText().trim());
				String s = Uti1.escape(dgneco.getText()); // in MySQL LIKE e'
															// case-insensitive
															// per default!
				if (s.length() > 0) {
					if (dgnecoExact.isSelected()) {
						q += " AND dgneco = '" + s + "'";
					} else {
						q += " AND dgneco like '%" + s + "%'";
					}
				}
			}

			{
				int n = Integer.parseInt(ageFrom.getText());
				if (n > 0) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(new java.util.Date());
					calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - n);
					java.util.Date d = calendar.getTime();
					// System.out.println(d);
					String s = Uti1.date2Ansi(Uti1.date2String(d));
					// System.out.println(s);
					q += " AND datnas <= '" + s + "'";
				}
			}

			{
				int n = Integer.parseInt(ageTo.getText());
				if (n > 0) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(new java.util.Date());
					calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - n);
					java.util.Date d = calendar.getTime();
					String s = Uti1.date2Ansi(Uti1.date2String(d));
					q += " AND datnas >= '" + s + "'";
				}
			}

			{
				double n = Double.parseDouble(spsghiFrom.getText());
				if (n > 0) {
					double eps = 1.0e-6;
					n -= eps;
					q += " AND (spsghi > " + String.valueOf(n);
					q += " OR spsgh1 > " + String.valueOf(n) + ")";
				}
			}

			{
				double n = Double.parseDouble(spsghiTo.getText());
				if (n > 0) {
					double eps = 1.0e-6;
					n += eps;
					q += " AND (spsghi < " + String.valueOf(n);
					q += " OR spsgh1 < " + String.valueOf(n) + ")";
				}
			}

			{
				int n = Integer.parseInt(tmmica.getInpValue());
				if (n > 0) {
					q += " AND tmmica = " + String.valueOf(n);
				}
			}

			{
				int n = Integer.parseInt(arprol.getInpValue());
				if (n > 0) {
					q += " AND arprol = " + String.valueOf(n);
				}
			}

			{
				int n = Integer.parseInt(isteco.getInpValue());
				if (n > 0) {
					q += " AND isteco = " + String.valueOf(n);
				}
			}

			{
				int n = Integer.parseInt(citeco.getInpValue());
				if (n > 0) {
					q += " AND citeco = " + String.valueOf(n);
				}
			}

			queryToPrint = q;
			q = "SELECT count(*) FROM EDECMA WHERE numarc is NOT NULL" + q;

			selected = 0;
			rSet = Jedecma.dbmgr.executeQuery(q);
			try {
				if (rSet.next()) {
					// selected = rSet.getInt("count(*)");
					selected = rSet.getInt(1);
				}
			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error("SQL error", true);
			}

			System.out.println("all = " + all + " selected = " + selected);
			percent = ((float) selected) / (float) all;
			percent = percent * 100000;
			percent = Math.round(percent);
			percent = percent / 1000;

			perc.setText(String.valueOf(percent));
			pcsd.setText(String.valueOf(all));
			seld.setText(String.valueOf(selected));

			prtBut.setEnabled(true);
			seld.repaint();

		}
	}

	class PrtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String q = queryToPrint;
			ResultSet rSet;
			MyTxtPageSet pageSet = new MyTxtPageSet();
			pageSet.setHeader(
					Jedecma.localMessagesBundle.getString("STAT2_TIT"),
					Jedecma.localMessagesBundle.getString("STAT2_HDR"));

			q = "SELECT numarc, datesa, cognom, datnas, ecbprv, dgneco FROM EDECMA WHERE numarc is NOT NULL"
					+ q + " ORDER by numarc";
			rSet = Jedecma.dbmgr.executeQuery(q);

			java.util.Date wDate;
			try {
				ResultSetMetaData md = rSet.getMetaData();
				while (rSet.next()) {
					String line = "";
					line += Uti1.rightSet(
							String.valueOf(rSet.getInt("numarc")), 6, '0');
					line += ' ';
					try {
						wDate = rSet.getDate("datesa");
					} catch (java.sql.SQLException ex) {
						wDate = Uti1.string2Date("0000-00-00");
					}
					line += Uti1.date2String(wDate);
					line += ' ';
					line += Uti1.leftSet(rSet.getString("cognom"), 30, ' ');
					line += ' ';
					try {
						wDate = rSet.getDate("datnas");
					} catch (java.sql.SQLException ex) {
						wDate = Uti1.string2Date("0000-00-00");
					}
					line += Uti1.date2String(wDate);
					line += ' ';
					line += Uti1.leftSet(rSet.getString("ecbprv"), 4, ' ');
					line += ' ';
					line += Uti1.leftSet(rSet.getString("dgneco"), 30, ' ');
					pageSet.writeLine(line);
				}
			} catch (SQLException ex) {
				System.err.println(ex);
				Uti1.error("SQL error", true);
			}

			pageSet.writeLine(String.valueOf(seld.getText()) + " "
					+ Jedecma.localMessagesBundle.getString("STAT_FTR"));
			pageSet.writeLine(Jedecma.localMessagesBundle
					.getString("PROCESSED")
					+ ": "
					+ String.valueOf(pcsd.getText())
					+ " "
					+ Jedecma.localMessagesBundle.getString("PERC")
					+ ": "
					+ String.valueOf(perc.getText()));

			pageSet.writeLine("*END*");

			PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
			aset.add(MediaSizeName.ISO_A4);
			aset.add(new Copies(1));
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			PrintService[] pservices = PrinterJob.lookupPrintServices();
			if (pservices.length > 0) {
				System.out.println("selected printer " + pservices[0]);
				printerJob.setPageable(pageSet.book);
				try {
					printerJob.setPrintService(pservices[0]);
					if (printerJob.printDialog(aset)) {
						printerJob.print(aset);
					}
				} catch (PrinterException pe) {
					System.err.println(pe);
				}
			}
		}
	}

} // End Stat2 class
