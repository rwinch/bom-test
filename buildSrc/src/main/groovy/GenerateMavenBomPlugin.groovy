import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.TaskAction

public class GenerateMavenBomPlugin implements Plugin<Project> {
	static String MAVEN_BOM_TASK_NAME = "mavenBom"

	public void apply(Project project) {
		project.plugins.apply(MavenPlugin)
		project.plugins.apply(JavaPlugin)
		project.task(MAVEN_BOM_TASK_NAME, type: GenerateMavenBomTask, group: 'Generate', description: 'Generates a Maven Build of Materials (BOM)')
		project.install.dependsOn project.mavenBom
	}
}