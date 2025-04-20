package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;

import java.util.Objects;

public class ProjectsApp {
	private Project curProject;
	private ProjectService projectService = new ProjectService();
	 // @formatter:off
    private List<String> operations = List.of(
        "1) Add a project",
    	"2) List projects",
    	"3) Select a project",
    	"4) Update project details",
    	"5) Delete a project"
    ); // @formatter:on
    
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
        	 ProjectsApp app = new ProjectsApp();
             app.processUserSelections();
           
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

	private void processUserSelections() {
		// TODO Auto-generated method stub
		 boolean done = false;

	        while (!done) {
	        	 try {
	                 int selection = getUserSelection();
	                 switch (selection) {
	                 case 1:
	                	    createProject();
	                	    break;
	                 case 2: 
	                     listProjects(); 
	                     break;
	                 case 3: 
	                	    selectProject();
	                	    break;
	                 case 4:
	                	    	updateProjectDetails();
	                	    	break;
	                 case 5:
	                	 deleteProject();
	                	 break;
	                     case -1:
	                         done = exitMenu();
	                         break;
	                     default:
	                         System.out.println("\n" + selection + " is not a valid selection. Try again.");
	                 }
	             }
	        	 catch (Exception e) {
	                 System.out.println("Error: " + e.toString());
	             }
	        }
	}

	private void deleteProject() {
		// TODO Auto-generated method stub
		
		    listProjects();

		    Integer projectId = getIntInput("Enter the ID of the project to delete");

		    projectService.deleteProject(projectId);
		    System.out.println("Project " + projectId + " was deleted successfully.");

		    if (Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
		        curProject = null;
		    }
		
	}

	private void updateProjectDetails() {
		// TODO Auto-generated method stub
		
		    if (curProject == null) {
		        System.out.println("\nPlease select a project.");
		        return;
		    }

		    Scanner scanner = new Scanner(System.in);
		    Project updatedProject = new Project();

		   	   

		    System.out.print("Enter new project name : ");
		    String newName = scanner.nextLine();
		    updatedProject.setProjectName(newName.isEmpty() ? curProject.getProjectName() : newName);

		    System.out.print("Enter new estimated hours : ");
		    String newEstimatedHours = scanner.nextLine();
		    updatedProject.setEstimatedHours(newEstimatedHours.isEmpty() ? curProject.getEstimatedHours() : new BigDecimal(newEstimatedHours));
		    
		    System.out.print("Enter new actual hours : ");
		    String newActualHours = scanner.nextLine();
		    updatedProject.setActualHours(newActualHours.isEmpty() ? curProject.getActualHours() : new BigDecimal(newActualHours));

		    System.out.print("Enter the project diffucilty (1-5) : ");
		    String newDifficullty = scanner.nextLine();
		    updatedProject.setDifficulty(newDifficullty.isEmpty() ? curProject.getDifficulty() : new Integer(newDifficullty));

		    System.out.print("Enter new project notes : ");
		    String newNotes = scanner.nextLine();
		    updatedProject.setNotes(newNotes.isEmpty() ? curProject.getNotes():newNotes);

		    // Repeat for other fields...

		    updatedProject.setProjectId(curProject.getProjectId());

		    projectService.modifyProjectDetails(updatedProject);
		    curProject = projectService.fetchProjectById(curProject.getProjectId());
		
		
	}

	private void selectProject() {
		// TODO Auto-generated method stub
		 curProject = null;
		 listProjects();
		    Integer projectId = getIntInput("Enter a project ID to select a project");
		   

		    try {
		        curProject = projectService.fetchProjectById(projectId); 
		        if (curProject != null) {
		            System.out.println("Selected Project: " + curProject);
		           
		        }
		    }
		    catch (NoSuchElementException e) {
		        System.out.println("Invalid project ID selected.");
		    }
	}

	private void listProjects() {
		// TODO Auto-generated method stub
		List<Project> projects = projectService.fetchAllProjects(); 
	   
	    System.out.println("\nProjects:");

	    
	    projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName())); 
	    
		
	}

	private void createProject() {
		// TODO Auto-generated method stub
		 String projectName = getStringInput("Enter the project name");
		    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		    String notes = getStringInput("Enter the project notes");

		    Project project = new Project();
		    project.setProjectName(projectName);
		    project.setEstimatedHours(estimatedHours);
		    project.setActualHours(actualHours);
		    project.setDifficulty(difficulty);
		    project.setNotes(notes);

		    Project dbProject = projectService.addProject(project);
		    System.out.println("You have successfully created project: " + dbProject);
		
	}

	private BigDecimal getDecimalInput(String prompt) {
		// TODO Auto-generated method stub
		  BigDecimal bd = null;
		    try {
		        String input = getStringInput(prompt);
		        bd = new BigDecimal(input).setScale(2);
		    } catch (NumberFormatException e) {
		        throw new DbException("Invalid decimal input: " + e.getMessage());
		    }
		    return bd;
	}

	private boolean exitMenu() {
		// TODO Auto-generated method stub
		  System.out.println("Exiting the application.");
          return true;
	}

	private int getUserSelection() {
		// TODO Auto-generated method stub
		printOperations();
        Integer input = getIntInput("Enter a menu selection");
        return (input == null) ? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		// TODO Auto-generated method stub
		String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
            }
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. Try again.");
        }
	}

	private String getStringInput(String prompt) {
		// TODO Auto-generated method stub
		 System.out.print(prompt + ": ");
	        String input = scanner.nextLine();
	        return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		// TODO Auto-generated method stub
		 System.out.println("\nThese are the available selections. Press Enter key to quit:");
	        operations.forEach(line -> System.out.println("  " + line));
	        if (curProject == null) {
	            System.out.println("\nYou are not working with a project.");
	        } else {
	            System.out.println("\nYou are working with project: " + curProject.getProjectName());
	        }
	}
}
