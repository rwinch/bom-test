import groovy.xml.MarkupBuilder

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.*

public class GenerateMavenBomTask extends DefaultTask {

	Set<Project> projects

	String artifactId

	String groupId

	Closure<MarkupBuilder> customizePom

	File pomFile

	public GenerateMavenBomTask() {
		this.group = "Generate"
		this.description = "Generates a Maven Build of Materials (BOM)"
		this.artifactId = "${->project.name}-bom"
		this.projects = project.subprojects
		this.pomFile = project.file("${->project.buildDir}/maven-bom/${->artifactId}-${->project.version}.xml")
	}

	@TaskAction
	public void generateMavenBom() {
		if(!groupId) {
			this.groupId = project.group
		}
		pomFile.parentFile.mkdirs()
		def xml = new MarkupBuilder(new OutputStreamWriter(new FileOutputStream(pomFile)))
		xml.project(xmlns: 'http://maven.apache.org/POM/4.0.0',
				'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
				'xsi:schemaLocation' : 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd') {
			modelVersion('4.0.0')
			delegate.groupId(groupId)
			delegate.artifactId(artifactId)
			delegate.version(project.version)
			packaging('pom')
			name(artifactId)
			description("Bill of Materials (BOM)")
			if(customizePom != null) {
				customizePom.call(xml)
			}
			dependencyManagement {
				projects.sort { dep -> "$dep.group:$dep.name" }.each { p ->
					dependency {
						delegate.groupId(p.group)
						delegate.artifactId(p.name)
						delegate.version(p.version)
					}
				}
			}
		}
	}
}
