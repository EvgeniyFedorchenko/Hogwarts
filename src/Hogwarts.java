public class Hogwarts {
    private final String name;
    private int powerOfMagic;
    private int transgressionDistance;

    public Hogwarts(String name, int powerOfMagic, int transgressionDistance) {
        this.name = name;
        this.powerOfMagic = powerOfMagic;
        this.transgressionDistance = transgressionDistance;
    }

    public String getName() {
        return name;
    }

    public void interfacultyCompareWith(Hogwarts student) {
        int sumOfThisProperty = this.powerOfMagic + this.transgressionDistance;
        int sumOfThatProperty = student.powerOfMagic + student.transgressionDistance;

        if (sumOfThisProperty > sumOfThatProperty) {
            System.out.println(this.getName() + " обладает бОльшей мощностью магии, чем " + student.getName());
        } else if (sumOfThisProperty < sumOfThatProperty) {
            System.out.println(student.getName() + " обладает бОльшей мощностью магии, чем " + this.getName());
        } else {
            System.out.println("Студенты одинакого сильны");
        }
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", powerOfMagic=" + powerOfMagic +
                ", transgressionDistance=" + transgressionDistance;
    }
}
