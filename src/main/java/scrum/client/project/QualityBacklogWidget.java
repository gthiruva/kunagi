package scrum.client.project;

import ilarkesto.gwt.client.ButtonWidget;
import scrum.client.common.AScrumWidget;
import scrum.client.common.BlockListWidget;
import scrum.client.common.UserGuideWidget;
import scrum.client.workspace.PagePanel;

import com.google.gwt.user.client.ui.Widget;

public class QualityBacklogWidget extends AScrumWidget {

	public BlockListWidget<Quality> list;

	@Override
	protected Widget onInitialization() {
		list = new BlockListWidget<Quality>(QualityBlock.FACTORY);
		list.setAutoSorter(Quality.LABEL_COMPARATOR);

		PagePanel page = new PagePanel();
		page.addHeader("Quality Backlog", new ButtonWidget(new CreateQualityAction()));
		page.addSection(list);
		page.addSection(new UserGuideWidget(getLocalizer().views().qualities(), getCurrentProject().getQualitys()
				.size() < 5, getCurrentUser().getHideUserGuideQualityBacklogModel()));
		return page;
	}

	@Override
	protected void onUpdate() {
		list.setObjects(getCurrentProject().getQualitys());
		super.onUpdate();
	}

	public boolean select(Quality quality) {
		if (!list.contains(quality)) update();
		return list.showObject(quality);
	}
}
