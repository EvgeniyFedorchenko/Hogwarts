
// Гриффиндор
public class Gryffindor extends Hogwarts {
    private int nobility;
    private int honor;
    private int courage;

    public Gryffindor(String name, int powerOfMagic, int transgressionDistance, int nobility, int honor, int courage) {
        super(name, powerOfMagic, transgressionDistance);
        this.nobility = nobility;
        this.honor = honor;
        this.courage = courage;
    }

    public void compareWith(Gryffindor student) {

        int sumOfThisProperty = this.nobility + this.honor + this.courage;
        int sumOfThatProperty = student.nobility + student.honor + student.courage;

        if (sumOfThisProperty > sumOfThatProperty) {
            System.out.println(this.getName() + " лучший гриффендорец, чем " + student.getName());
        } else if (sumOfThisProperty < sumOfThatProperty) {
            System.out.println(student.getName() + " лучший гриффендорец, чем " + this.getName());
        } else {
            System.out.println("Студенты одинакого хороши");
        }
    }

    @Override
    public String toString() {
        return "Gryffindor: " + super.toString() +
                ", nobility=" + nobility +
                ", honor=" + honor +
                ", courage=" + courage;
    }
}
