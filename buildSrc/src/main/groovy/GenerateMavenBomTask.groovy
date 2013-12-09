import groovy.xml.MarkupBuilder

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.*

public class GenerateMavenBomTask extends DefaultTask {

	Set<Project> projects

	Closure<MarkupBuilder> customizePom

	public GenerateMavenBomTask() {
		this.group = "Generate"
		this.description = "Generates a Maven Build of Materials (BOM)"
		this.projects = project.subprojects
	}

	@TaskAction
	public void configureBom() {
		project.configurations.archives.artifacts.clear()
		
		project.install {
			repositories.mavenInstaller {
				pom.whenConfigured {
					packaging = "pom"
					withXml {
						asNode().children().last() + {
							delegate.dependencyManagement {
								delegate.dependencies {
									projects.sort { "$it.name" }.each { p ->
										delegate.dependency {
											delegate.groupId(p.group)
											delegate.artifactId(p.name)
											delegate.version(p.version)
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		project.artifacts {
			// work around GRADLE-2406 by attaching text artifact
			archives(project.file("readme.txt"))
		}
	}
}
