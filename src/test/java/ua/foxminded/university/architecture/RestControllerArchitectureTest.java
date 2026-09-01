package ua.foxminded.university.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "ua.foxminded.university")
class RestControllerArchitectureTest {

    @ArchTest
    static final ArchRule rest_controllers_should_reside_in_api_package =
            classes()
                    .that()
                    .areAnnotatedWith(RestController.class)
                    .should()
                    .resideInAPackage("..api..")
                    .because(
                            "REST controllers belong to the API boundary"
                    );
}