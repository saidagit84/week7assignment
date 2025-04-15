package projects;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.entity.Project;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();
	public Project addProject(Project project) {
		// TODO Auto-generated method stub
		return projectDao.insertProject(project);
	}
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}
	public Project fetchProjectById(Integer projectId) {
		// TODO Auto-generated method stub
		 Optional<Project> project = projectDao.fetchProjectById(projectId);
		    return project.orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
		}
	

}
