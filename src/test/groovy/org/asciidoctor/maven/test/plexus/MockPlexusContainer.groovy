package org.asciidoctor.maven.test.plexus

import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.logging.SystemStreamLog
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.filtering.DefaultMavenFileFilter
import org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering
import org.asciidoctor.maven.AsciidoctorMojo
import org.sonatype.plexus.build.incremental.DefaultBuildContext

/**
 * Initializes a mocks components required to simulate a Plexus container for the tests.
 *
 * Note: This is a temporal solution. At some point 'proper' testing should be introduced, see:
 *  - https://vzurczak.wordpress.com/2014/07/23/write-unit-tests-for-a-maven-plug-in/
 *
 * @author abelsromero
 */
class MockPlexusContainer {

    class FakeMavenLogger {
        @Delegate
        Log logger = new SystemStreamLog()
    }

    private void initializeMojoContext(AsciidoctorMojo mojo, Map<String, String> properties) {

        mojo.@project = [
                getBasedir   : {
                    return new File('.')
                },
                getProperties: properties as Properties
        ] as MavenProject

        def buildContext = new DefaultBuildContext()
        def logger = new FakeMavenLogger() as org.codehaus.plexus.logging.Logger

        DefaultMavenFileFilter mavenFileFilter = new DefaultMavenFileFilter()
        mavenFileFilter.@buildContext = buildContext
        mavenFileFilter.enableLogging(logger)

        DefaultMavenResourcesFiltering resourceFilter = new DefaultMavenResourcesFiltering()
        resourceFilter.@mavenFileFilter = mavenFileFilter
        resourceFilter.@buildContext = buildContext
        resourceFilter.initialize()
        resourceFilter.enableLogging(logger)
        mojo.encoding = "UTF-8"
        mojo.@outputResourcesFiltering = resourceFilter
    }

    /**
     * Intercept Asciidoctor mojo constructor to mock and inject required plexus objects.
     */
    static MockPlexusContainer initializeMockContext(Class<?> clazz) {
        initializeMockContext(clazz, Collections.emptyMap())
    }

    static MockPlexusContainer initializeMockContext(Class<?> clazz,  Map<String, String> mavenProperties) {
        final MockPlexusContainer mockPlexusContainer = new MockPlexusContainer()
        def oldConstructor = clazz.constructors[0]

        clazz.metaClass.constructor = {
            def mojo = oldConstructor.newInstance()
            mockPlexusContainer.initializeMojoContext(mojo, mavenProperties)
            return mojo
        }
        mockPlexusContainer
    }

}
