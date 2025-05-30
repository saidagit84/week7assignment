package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.dao.DbConnection;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

@SuppressWarnings("unused")
public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) {
		// TODO Auto-generated method stub
		//@formatter:off
		 String sql = ""
				 + "INSERT INTO " + PROJECT_TABLE + ""
				 + "(project_name, estimated_hours, actual_hours, difficulty, notes)"
				 + "VALUES "
				 + "(?, ?, ?, ?, ?)";
		    //@formatter:on
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				setParameter(pstmt, 1, project.getProjectName(), String.class);
				setParameter(pstmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(pstmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(pstmt, 4, project.getDifficulty(), Integer.class);
				setParameter(pstmt, 5, project.getNotes(), String.class);

				pstmt.executeUpdate();
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				project.setProjectId(projectId);
				commitTransaction(conn);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM project ORDER BY project_id";
		List<Project> projects = new ArrayList<>();

		try (Connection conn = DbConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Project project = new Project();
				project.setActualHours(rs.getBigDecimal("actual_hours"));
				project.setDifficulty(rs.getObject("difficulty", Integer.class));
				project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
				project.setNotes(rs.getString("notes"));
				project.setProjectId(rs.getInt("project_id"));
				project.setProjectName(rs.getString("project_name"));
				projects.add(project);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		return projects;
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM project WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try {
				Project project = null;
				try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

					pstmt.setInt(1, projectId);
					try (ResultSet rs = pstmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);

						}
					}
				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				commitTransaction(conn);
				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);

			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		return Optional.empty();
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// TODO Auto-generated method stub
		// @formatter:off
	    String sql = ""
	        + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
	        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
	        + "WHERE project_id = ?;";
	    // @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();
				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}

		}

	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}

				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}

				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project updatedProject) {
		// TODO Auto-generated method stub
		String sql = "" + "UPDATE " + PROJECT_TABLE + " SET " + "project_name = ?, " + "estimated_hours = ?, "
				+ "actual_hours = ?, " + "difficulty = ?, " + "notes = ? " + "WHERE project_id = ?";
		// @formatter:on

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, updatedProject.getProjectName(), String.class);
				setParameter(stmt, 2, updatedProject.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, updatedProject.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, updatedProject.getDifficulty(), Integer.class);
				setParameter(stmt, 5, updatedProject.getNotes(), String.class);
				setParameter(stmt, 6, updatedProject.getProjectId(), Integer.class);

				boolean modified = stmt.executeUpdate() == 1;
				commitTransaction(conn);

				return modified;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		// TODO Auto-generated method stub

		String sql = "DELETE FROM project WHERE project_id= ?";
		try (Connection conn = DbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);
			stmt.setInt(1, projectId);
			int rowsAffected = stmt.executeUpdate();
			conn.commit();
			return rowsAffected == 1;
		} catch (SQLException e) {
			throw new DbException("Error deleting project", e);
		}
	}

}
