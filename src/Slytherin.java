
// Слизерин
public class Slytherin extends Hogwarts {
    private int resourcefulness;
    private int ambitious;
    private int determination;
    private int trick;

    public Slytherin(String name, int powerOfMagic, int transgressionDistance, int resourcefulness, int ambitious,
                     int determination, int trick) {
        super(name, powerOfMagic, transgressionDistance);
        this.resourcefulness = resourcefulness;
        this.ambitious = ambitious;
        this.determination = determination;
        this.trick = trick;
    }

    public void compareWith(Slytherin student) {

        int sumOfThisProperty = this.resourcefulness + this.ambitious + this.determination + this.ambitious;
        int sumOfThatProperty = student.resourcefulness + student.ambitious + student.determination + student.ambitious;

        if (sumOfThisProperty > sumOfThatProperty) {
            System.out.println(this.getName() + " лучший слизеринец, чем " + student.getName());
        } else if (sumOfThisProperty < sumOfThatProperty) {
            System.out.println(student.getName() + " лучший слизеринец, чем " + this.getName());
        } else {
            System.out.println("Студенты одинакого хороши");
        }
    }

    @Override
    public String toString() {
        return "Slytherin: " + super.toString() +
                " resourcefulness=" + resourcefulness +
                ", ambitious=" + ambitious +
                ", determination=" + determination +
                ", trick=" + trick;
    }
}
