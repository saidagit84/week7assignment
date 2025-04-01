package projects;

import projects.dao.DbConnection;

public class ProjectsApp {
    public static void main(String[] args) {
        try {
           
            DbConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
