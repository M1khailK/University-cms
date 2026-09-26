package ua.foxminded.university.services.search.config;

import org.springframework.boot.context.properties
        .ConfigurationProperties;

@ConfigurationProperties(
        prefix = "app.search.lesson-material"
)
public record LessonMaterialSearchProperties(
        double maxDistance
) {

    public LessonMaterialSearchProperties {
        if (!Double.isFinite(maxDistance)
                || maxDistance < 0.0
                || maxDistance > 2.0) {
            throw new IllegalArgumentException(
                    "Lesson material search max distance "
                            + "must be between 0.0 and 2.0."
            );
        }
    }
}