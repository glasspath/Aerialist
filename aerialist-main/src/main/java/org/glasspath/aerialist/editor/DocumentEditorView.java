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
package org.glasspath.aerialist.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.glasspath.aerialist.swing.view.LayeredPageView;
import org.glasspath.aerialist.swing.view.PageView;
import org.glasspath.common.swing.graphics.NinePatch;
import org.glasspath.common.swing.theme.Theme;

public class DocumentEditorView extends EditorView<DocumentEditorPanel> {

	public static final Color GRID_COLOR = new Color(200, 200, 200);
	public static final Color GUIDE_COLOR = new Color(150, 195, 255, 75);
	public static final Color HEADER_FOOTER_GUIDE_COLOR = new Color(150, 195, 255, 150);

	private final NinePatch shadow = new NinePatch(new ImageIcon(getClass().getClassLoader().getResource("org/glasspath/common/swing/graphics/shadow.png")).getImage(), 10, 10); //$NON-NLS-1$

	public DocumentEditorView(DocumentEditorPanel context) {
		super(context);
	}

	@Override
	public void drawEditorBackground(Graphics2D g2d, JPanel pageContainer) {

		if (Theme.isDark()) {
			g2d.setColor(new Color(48, 50, 52));
		} else {
			g2d.setColor(new Color(242, 242, 242));
		}
		g2d.fillRect(0, 0, pageContainer.getWidth(), pageContainer.getHeight());

		Component component;
		PageView pageView;
		Rectangle bounds;
		for (int i = 0; i < pageContainer.getComponentCount(); i++) {

			component = pageContainer.getComponent(i);
			if (component instanceof PageView) {

				pageView = (PageView) component;

				bounds = component.getBounds();

				shadow.paintNinePatch(g2d, bounds.x - 7, bounds.y - 7, bounds.width + 14, bounds.height + 14);

				g2d.setColor(Color.white);
				g2d.fill(bounds);

				if (context.getPageContainer().isEditingHeader()) {

					if (pageView == context.getPageContainer().getHeaderView()) {

						Rectangle bolowHeaderBounds = new Rectangle(bounds);
						bolowHeaderBounds.y += context.getPageContainer().getHeaderHeight();
						bolowHeaderBounds.height -= context.getPageContainer().getHeaderHeight();

						g2d.setColor(new Color(248, 248, 248));
						g2d.fill(bolowHeaderBounds);

					}

				} else if (context.getPageContainer().isEditingFooter()) {

					if (pageView == context.getPageContainer().getFooterView()) {

						Rectangle aboveFooterBounds = new Rectangle(bounds);
						aboveFooterBounds.height -= context.getPageContainer().getFooterHeight();

						g2d.setColor(new Color(248, 248, 248));
						g2d.fill(aboveFooterBounds);

					}

				} else {

					if (pageView instanceof LayeredPageView) {
						for (PageView layerView : ((LayeredPageView) pageView).getLayers()) {
							drawLayerView(g2d, pageView, layerView);
						}
					}

					g2d.setColor(new Color(255, 255, 255, 200));
					g2d.fill(bounds);

				}

				if (context.isGridVisible()) {
					drawGrid(g2d, pageView);
				}

				if (context.isGuidesVisible()) {
					drawGuides(g2d, pageView);
				}

			}

		}

	}

	@Override
	public void drawEditorForeground(Graphics2D g2d, JPanel pageContainer) {

		Component component;
		Rectangle bounds;
		for (int i = 0; i < pageContainer.getComponentCount(); i++) {

			component = pageContainer.getComponent(i);
			if (component instanceof PageView) {

				bounds = component.getBounds();

				if (!Theme.isDark()) {
					g2d.setColor(Color.lightGray);
					g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}

				if (context.selection.contains(component)) {
					g2d.setStroke(PAGE_SELECTION_STROKE);
					g2d.setColor(PAGE_SELECTION_COLOR);
					g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}

			}

		}

	}

	@Override
	public void drawLayerView(Graphics2D g2d, PageView pageView, PageView layerView) {
		SwingUtilities.paintComponent(g2d, layerView, context.getPageContainer(), pageView.getX(), pageView.getY(), pageView.getWidth(), pageView.getHeight());
	}

	protected void drawGrid(Graphics2D g2d, PageView pageView) {

		g2d.setColor(GRID_COLOR);

		for (int x = context.getGridSpacing(); x < pageView.getWidth() - (context.getGridSpacing() / 2); x += context.getGridSpacing()) {
			for (int y = context.getGridSpacing(); y < pageView.getHeight() - (context.getGridSpacing() / 2); y += context.getGridSpacing()) {
				g2d.fillRect(pageView.getX() + x, pageView.getY() + y, 1, 1);
			}
		}

	}

	protected void drawGuides(Graphics2D g2d, PageView pageView) {

		// TODO
		int left = 60;
		int right = 65;
		int top = 60;
		int bottom = 60;

		int x = pageView.getX();
		int y = pageView.getY();
		int w = pageView.getWidth();
		int h = pageView.getHeight();

		g2d.setColor(GUIDE_COLOR);
		g2d.drawLine(x + left, y, x + left, y + h);
		g2d.drawLine(x + w - right, y, x + w - right, y + h);
		g2d.drawLine(x, y + top, x + w, y + top);
		g2d.drawLine(x, y + h - bottom, x + w, y + h - bottom);

		// TODO
		top = 90;
		bottom = 90;

		g2d.setColor(HEADER_FOOTER_GUIDE_COLOR);
		g2d.drawLine(x, y + top, x + w, y + top);
		g2d.drawLine(x, y + h - bottom, x + w, y + h - bottom);

	}

}
