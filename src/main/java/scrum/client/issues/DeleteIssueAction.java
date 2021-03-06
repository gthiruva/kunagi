package scrum.client.issues;

import scrum.client.common.TooltipBuilder;

public class DeleteIssueAction extends GDeleteIssueAction {

	public DeleteIssueAction(scrum.client.issues.Issue issue) {
		super(issue);
	}

	@Override
	public String getLabel() {
		return "Delete";
	}

	@Override
	public String getTooltip() {
		TooltipBuilder tb = new TooltipBuilder("Delete this Issue permanently.");
		if (!issue.getProject().isScrumTeamMember(getCurrentUser()))
			tb.addRemark(TooltipBuilder.NOT_SCRUMTEAM);
		if (!issue.isOpen()) tb.addRemark("Only issues from the inbox can be deleted.");
		return tb.getTooltip();
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	public boolean isPermitted() {
		if (!issue.isOpen()) return false;
		if (!issue.getProject().isScrumTeamMember(getCurrentUser())) return false;
		return true;
	}

	@Override
	protected void onExecute() {
		getCurrentProject().deleteIssue(issue);
		addUndo(new Undo());
	}

	class Undo extends ALocalUndo {

		@Override
		public String getLabel() {
			return "Undo Delete " + issue.getReference() + " " + issue.getLabel();
		}

		@Override
		protected void onUndo() {
			getDao().createIssue(issue);
		}

	}

}