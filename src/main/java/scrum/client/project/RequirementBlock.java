package scrum.client.project;

import ilarkesto.gwt.client.Date;
import scrum.client.collaboration.EmoticonsWidget;
import scrum.client.common.ABlockWidget;
import scrum.client.common.AScrumAction;
import scrum.client.common.BlockHeaderWidget;
import scrum.client.common.BlockWidgetFactory;
import scrum.client.dnd.TrashSupport;
import scrum.client.img.Img;
import scrum.client.journal.ActivateChangeHistoryAction;
import scrum.client.sprint.Sprint;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RequirementBlock extends ABlockWidget<Requirement> implements TrashSupport {

	private SimplePanel statusIcon;
	private Anchor estimationLabel;
	private SprintSwitchIndicatorWidget sprintBorderIndicator;

	@Override
	protected void onInitializationHeader(BlockHeaderWidget header) {
		Requirement requirement = getObject();
		statusIcon = header.insertPrefixIcon();
		estimationLabel = header.appendCenterSuffix(null);

		header.appendCell(new EmoticonsWidget(requirement), null, true, true, null);
		header.appendCell(new EstimationBarWidget(requirement), "150px", false, true, null);

		header.addMenuAction(new AddRequirementToCurrentSprintAction(requirement));
		header.addMenuAction(new RemoveRequirementFromSprintAction(requirement));
		header.addMenuAction(new SetRequirementDirtyAction(requirement));
		header.addMenuAction(new SetRequirementCleanAction(requirement));
		header.addMenuAction(new StartRequirementEstimationVotingAction(requirement));
		header.addMenuAction(new ActivateChangeHistoryAction(requirement));
		header.addMenuAction(new DeleteRequirementAction(requirement));
	}

	@Override
	protected void onUpdateHeader(BlockHeaderWidget header) {
		Requirement requirement = getObject();
		header.setDragHandle(requirement.getReference());
		Image statusImage = null;
		if (requirement.isWorkEstimationVotingActive()) {
			statusImage = Img.bundle.reqPoker().createImage();
			statusImage.setTitle("Estimation game \"Planning Poker\" active.");
		} else if (requirement.isInCurrentSprint()) {
			statusImage = Img.bundle.reqInSprint().createImage();
			statusImage.setTitle("In current sprint.");
		} else if (requirement.isClosed()) {
			statusImage = Img.bundle.reqClosed().createImage();
			statusImage.setTitle("Closed.");
		} else if (!requirement.isEstimatedWorkValid()) {
			statusImage = Img.bundle.reqDirty().createImage();
			statusImage.setTitle("Needs estimation.");
		}
		estimationLabel.setText(requirement.getEstimatedWorkWithUnit());
		statusIcon.setWidget(statusImage);
		header.setCenter(requirement.getLabel());

		boolean sprintBorder = false;
		Requirement previous = getList().getPrevious(requirement);
		if (previous != null) {
			EstimationBar previousEstimationBar = previous.getEstimationBar();
			sprintBorder = !requirement.getEstimationBar().isCompetedOnSameSprint(previousEstimationBar);
		}

		if (previous != null && sprintBorder) {
			if (sprintBorderIndicator == null) {
				sprintBorderIndicator = new SprintSwitchIndicatorWidget();
				Sprint sprint = getCurrentProject().getCurrentSprint();
				int sprints = previous.getEstimationBar().getEndSprintOffset() + 1;
				sprintBorderIndicator.updateLabel(sprints,
					sprint.getLength().multiplyBy(sprints).subtract(sprint.getBegin().getPeriodTo(Date.today()).abs()));
				getPreHeaderPanel().add(sprintBorderIndicator);
				requirement.updateLocalModificationTime();
			}
		} else {
			if (sprintBorderIndicator != null) {
				getPreHeaderPanel().remove(sprintBorderIndicator);
				sprintBorderIndicator = null;
				requirement.updateLocalModificationTime();
			}
		}
	}

	@Override
	protected Widget onExtendedInitialization() {
		return new RequirementWidget(getObject(), true, true, false, true, true, true, false);
	}

	@Override
	public AScrumAction getTrashAction() {
		return new DeleteRequirementAction(getObject());
	}

	public static final BlockWidgetFactory<Requirement> FACTORY = new BlockWidgetFactory<Requirement>() {

		@Override
		public RequirementBlock createBlock() {
			return new RequirementBlock();
		}
	};

}
