package io.github.porum.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension

/**
 * Created by panda on 2025/9/1 10:37
 */
class MavenCentralPublishPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.pluginManager.withPlugin("maven-publish") {

      val GROUP: String by project
      val VERSION: String by project
      val sonatypeUserName: String by project
      val sonatypePassword: String by project

      val publishing = project.extensions.getByType<PublishingExtension>()

      publishing.repositories {
        mavenLocal()
        maven {
          val url = if (VERSION.endsWith("-SNAPSHOT")) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
          } else {
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
          }
          setUrl(url)
          credentials {
            username = sonatypeUserName
            password = sonatypePassword
          }
        }
      }

      publishing.publications.whenObjectAdded {
        check(this is MavenPublication) {
          "unexpected publication $this"
        }

        groupId = GROUP
        version = VERSION

        pom {
          url.set("https://github.com/porum/JB")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
          developers {
            developer {
              id.set("porum")
              name.set("guobao.sun")
              email.set("sunguobao12@gmail.com")
            }
          }
          scm {
            url.set("https://github.com/porum/JB.git")
          }
        }
      }

      val signing = project.extensions.getByType<SigningExtension>()
      signing.sign(publishing.publications)
    }
  }
}