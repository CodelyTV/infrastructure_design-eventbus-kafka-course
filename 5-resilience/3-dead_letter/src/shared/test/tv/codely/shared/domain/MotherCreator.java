package tv.codely.shared.domain;

import net.datafaker.Faker;

public final class MotherCreator {
    private final static Faker faker = new Faker();

    public static Faker random() {
        return faker;
    }
}
