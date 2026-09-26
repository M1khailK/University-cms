package ua.foxminded.university.services.search.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LessonMaterialSearchPropertiesTest {

    @Test
    void constructor_shouldAcceptValidMaximumDistance() {
        LessonMaterialSearchProperties properties =
                new LessonMaterialSearchProperties(0.45);

        assertEquals(
                0.45,
                properties.maxDistance(),
                0.000001
        );
    }

    @Test
    void constructor_shouldRejectInvalidMaximumDistance() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new LessonMaterialSearchProperties(
                                -0.01
                        )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new LessonMaterialSearchProperties(
                                2.01
                        )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new LessonMaterialSearchProperties(
                                Double.NaN
                        )
                )
        );
    }
}