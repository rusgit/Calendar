package com.diosoft.calendar.common;

public class Person implements Comparable<Person> {

    private final String name;
    private final String lastName;
    private final String email;

    public String getLastName() {
        return lastName;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

    private Person(PersonBuilder builder) {
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Person)) return false;
        if (this == obj) return true;

        Person person = (Person) obj;

        if (!name.equals(person.name)) return false;
        if (!lastName.equals(person.lastName)) return false;
        if (!email.equals(person.email)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person { ");
        sb.append(name).append(", ")
                .append(lastName).append(", ")
                .append(email).append(" } \n");

        return sb.toString();
    }

    @Override
    public int compareTo(Person obj) {
        if (obj == null) return 1;
        Person person = (Person) obj;
        int result = name.compareTo(person.name);
        if (result != 0) return (int) (result / Math.abs(result));
        result = lastName.compareTo(person.lastName);
        if (result != 0) return (int) (result / Math.abs(result));
        result = email.compareTo(person.email);
        return (result != 0) ? (int) (result / Math.abs(result)) : 0;
    }

    public static class PersonBuilder {
        private String name;
        private String lastName;
        private String email;

        public PersonBuilder() {}

        public PersonBuilder(Person original) {
            this.name = original.name;
            this.lastName = original.lastName;
            this.email = original.email;
        }

        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder lastName(String secondName) {
            this.lastName = secondName;
            return this;
        }

        public PersonBuilder email(String email) {
            this.email = email;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}