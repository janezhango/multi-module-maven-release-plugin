package e2e;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import scaffolding.MvnRunner;
import scaffolding.TestProject;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static scaffolding.GitMatchers.hasTag;

public class SkippingUnchangedModulesTest {

    final TestProject testProject = TestProject.parentAsSibilngProject();

    @BeforeClass
    public static void installPluginToLocalRepo() throws MavenInvocationException {
        MvnRunner.installReleasePluginToLocalRepo();
    }

    @Test
    public void doesNotReReleaseAModuleThatHasNotChanged() throws Exception {
        testProject.mvnRelease("1");
        assertTagExists("parent-module-1.2.3.1");
        assertTagExists("core-utils-2.0.1");
        assertTagExists("console-app-3.2.1");

        testProject.commitRandomFile("console-app").pushIt();
        testProject.mvnRelease("2");
        assertTagDoesNotExist("parent-module-1.2.3.2");
        assertTagDoesNotExist("core-utils-2.0.2");
        assertTagExists("console-app-3.2.2");
    }

    private void assertTagExists(String tagName) {
        assertThat(testProject.local, hasTag(tagName));
        assertThat(testProject.origin, hasTag(tagName));
    }

    private void assertTagDoesNotExist(String tagName) {
        assertThat(testProject.local, not(hasTag(tagName)));
        assertThat(testProject.origin, not(hasTag(tagName)));
    }


}