package Hogwarts;

// Гриффиндор
public class GryffindorStudents extends HogwartsStudents {
    private int nobility;
    private int honor;
    private int courage;

    public GryffindorStudents(String name, int powerOfMagic, int transgressionDistance, int nobility, int honor, int courage) {
        super(name, powerOfMagic, transgressionDistance);
        this.nobility = nobility;
        this.honor = honor;
        this.courage = courage;
    }

    public void compareWith(GryffindorStudents student) {

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
        return "GryffindorStudents: " + super.toString() +
                ", nobility=" + nobility +
                ", honor=" + honor +
                ", courage=" + courage;
    }
}
