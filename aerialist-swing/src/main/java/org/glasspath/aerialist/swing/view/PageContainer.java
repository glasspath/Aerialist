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
package org.glasspath.aerialist.swing.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.undo.UndoableEdit;

import org.glasspath.aerialist.Document;
import org.glasspath.aerialist.HeightPolicy;
import org.glasspath.aerialist.Page;

public abstract class PageContainer extends JPanel implements ISwingViewContext {

	private LayoutPhase layoutPhase = LayoutPhase.IDLE;
	private boolean yPolicyEnabled = false;
	private ExportPhase exportPhase = ExportPhase.IDLE;

	private int headerHeight = 0;
	private int footerHeight = 0;
	private PageView headerView = null;
	private PageView footerView = null;
	private List<PageView> pageViews = new ArrayList<>();

	public PageContainer() {

		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

	}

	public void init(Document document) {

		headerHeight = document.getHeaderHeight();
		footerHeight = document.getFooterHeight();

		if (document.getHeader() != null) {
			headerView = createPageView(document.getHeader(), this);
		} else {
			headerView = null;
		}

		if (document.getFooter() != null) {
			footerView = createPageView(document.getFooter(), this);
		} else {
			footerView = null;
		}

		pageViews = createLayeredPageViews(document.getPages(), this);

		loadPageViews();

	}

	public Document toDocument() {

		Document document = new Document();

		document.setHeaderHeight(headerHeight);
		document.setFooterHeight(footerHeight);

		if (headerView != null) {
			document.setHeader(headerView.toPage());
		}

		if (footerView != null) {
			document.setFooter(footerView.toPage());
		}

		for (PageView pageView : pageViews) {
			document.getPages().add(pageView.toPage());
		}

		return document;

	}

	@Override
	public LayoutPhase getLayoutPhase() {
		return layoutPhase;
	}

	@Override
	public void setLayoutPhase(LayoutPhase layoutPhase) {
		this.layoutPhase = layoutPhase;
	}

	@Override
	public boolean isHeightPolicyEnabled() {
		return layoutPhase == LayoutPhase.IDLE || layoutPhase == LayoutPhase.LAYOUT_CONTENT;
	}

	@Override
	public boolean isYPolicyEnabled() {
		return yPolicyEnabled;
	}

	@Override
	public void setYPolicyEnabled(boolean yPolicyEnabled) {
		this.yPolicyEnabled = yPolicyEnabled;
	}

	@Override
	public ExportPhase getExportPhase() {
		return exportPhase;
	}

	@Override
	public void setExportPhase(ExportPhase exportPhase) {
		this.exportPhase = exportPhase;
	}

	@Override
	public void focusGained(JComponent component) {

	}

	@Override
	public void undoableEditHappened(UndoableEdit edit) {

	}

	@Override
	public void refresh(Component component) {

	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(int headerHeight) {
		this.headerHeight = headerHeight;
	}

	public int getFooterHeight() {
		return footerHeight;
	}

	public void setFooterHeight(int footerHeight) {
		this.footerHeight = footerHeight;
	}

	public PageView getHeaderView() {
		return headerView;
	}

	public void setHeaderView(PageView headerView) {
		this.headerView = headerView;
	}

	public PageView getFooterView() {
		return footerView;
	}

	public void setFooterView(PageView footerView) {
		this.footerView = footerView;
	}

	public List<PageView> getPageViews() {
		return pageViews;
	}

	public void setPageViews(List<PageView> pageViews) {
		this.pageViews = pageViews;
	}

	protected void loadPageViews() {

		removeAll();

		add(Box.createRigidArea(new Dimension(25, 20)));

		for (PageView pageView : pageViews) {
			add(pageView);
			add(Box.createRigidArea(new Dimension(25, 25)));
		}

	}

	public void insertPageView(PageView pageView, int index) {

		if (index >= 0 && index <= pageViews.size()) {

			pageViews.add(index, pageView);

			int viewIndex = 1 + (index * 2);
			if (viewIndex >= 1 && viewIndex <= getComponentCount()) {

				add(pageView, viewIndex);
				add(Box.createRigidArea(new Dimension(25, 25)), viewIndex + 1);

			}

		}

	}

	public void removePageView(PageView pageView) {

		int index = pageViews.indexOf(pageView);
		if (index >= 0) {

			int viewIndex = 1 + (index * 2);

			pageViews.remove(index);

			remove(viewIndex + 1);
			remove(viewIndex);

		}

	}

	protected void updateLayers() {
		for (PageView pageView : pageViews) {
			createLayers((LayeredPageView) pageView);
		}
	}

	protected void createLayers(LayeredPageView pageView) {

		pageView.getLayers().clear();

		if (headerView != null) {
			pageView.getLayers().add(new PageView(headerView, this));
		}

		if (footerView != null) {
			pageView.getLayers().add(new PageView(footerView, this));
		}

	}

	public void invalidate(HeightPolicy heightPolicy) {
		for (PageView pageView : pageViews) {
			pageView.invalidate(heightPolicy);
		}
	}

	public static List<PageView> createLayeredPageViews(List<Page> pages, PageContainer pageContainer) {

		List<PageView> pageViews = new ArrayList<>();

		for (Page page : pages) {
			pageViews.add(createLayeredPageView(page, pageContainer));
		}

		return pageViews;

	}

	public static PageView createLayeredPageView(Page page, PageContainer pageContainer) {

		LayeredPageView pageView = new LayeredPageView(pageContainer);
		pageView.init(page);

		pageContainer.createLayers(pageView);

		return pageView;

	}

	public static PageView createPageView(Page page, PageContainer pageContainer) {

		PageView pageLayerView = new PageView(pageContainer);
		pageLayerView.init(page);

		return pageLayerView;

	}

}
