/*
 * This file is part of Glasspath Aerialist.
 * Copyright (C) 2011 - 2022 Remco Poelstra
 * Authors: Remco Poelstra
 * 
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact us at https://glasspath.org. For AGPL licensing, see below.
 * 
 * AGPL licensing:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.glasspath.aerialist.editor.actions;

import javax.swing.Action;
import javax.swing.JComboBox;

import org.glasspath.aerialist.editor.EditorPanel;
import org.glasspath.aerialist.swing.view.TextView;

public class FontSizeAction extends TextStyleAction {

	private final JComboBox<String> fontSizeComboBox;

	public FontSizeAction(EditorPanel<? extends EditorPanel<?>> context, JComboBox<String> fontSizeComboBox) {
		super(context);

		this.fontSizeComboBox = fontSizeComboBox;

		putValue(Action.NAME, "Font size");
		putValue(Action.SHORT_DESCRIPTION, "Font size");

	}

	@Override
	protected void updateTextView(TextView textView) {

		String fontSize = (String) fontSizeComboBox.getSelectedItem();

		try {

			textView.setFontSize(Integer.parseInt(fontSize));

			// MutableAttributeSet attr = new SimpleAttributeSet();
			// StyleConstants.setFontSize(attr, Integer.parseInt(fontSize));
			// setCharacterAttributes(textView, attr, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
