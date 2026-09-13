package tv.codely.mooc.courses_counter.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tv.codely.mooc.courses.domain.CourseId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public final class CourseIdListConverter implements AttributeConverter<List<CourseId>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CourseId> courses) {
        if (null == courses) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(courses.stream().map(CourseId::value).toList());
        } catch (IOException error) {
            throw new IllegalStateException("Cannot serialize the courses identifiers", error);
        }
    }

    @Override
    public List<CourseId> convertToEntityAttribute(String value) {
        if (null == value || value.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<String> ids = MAPPER.readValue(value, new TypeReference<List<String>>() {});

            return new ArrayList<>(ids.stream().map(CourseId::new).toList());
        } catch (IOException error) {
            throw new IllegalStateException("Cannot deserialize the courses identifiers", error);
        }
    }
}
