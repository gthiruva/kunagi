package scrum.server.files;

import ilarkesto.base.time.DateAndTime;
import ilarkesto.fp.Predicate;
import scrum.server.project.Project;

public class FileDao extends GFileDao {

	public File getFilesByName(final String filename, final Project project) {
		return getEntity(new Predicate<File>() {

			public boolean test(File e) {
				return e.isProject(project) && e.isFilename(filename);
			}
		});
	}

	public File getFileByNumber(final int number, final Project project) {
		return getEntity(new Predicate<File>() {

			public boolean test(File t) {
				return t.isNumber(number) && t.isProject(project);
			}
		});
	}

	public File postFile(java.io.File f, Project project) {
		File file = newEntityInstance();
		file.setProject(project);
		file.setFilename(f.getName());
		file.setLabel(createLabel(f));
		file.setUploadTime(DateAndTime.now());
		file.updateNumber();
		saveEntity(file);
		return file;
	}

	private String createLabel(java.io.File file) {
		String label = file.getName();
		int idx = label.lastIndexOf('.');
		if (idx > 0) {
			label = label.substring(0, idx);
		}
		return label;
	}

}