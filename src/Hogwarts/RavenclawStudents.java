package Hogwarts;

// Когтевран
public class RavenclawStudents extends HogwartsStudents {
    private int wisdom;
    private int witty;
    private int creativity;

    public RavenclawStudents(String name, int powerOfMagic, int transgressionDistance, int wisdom, int witty, int creativity) {
        super(name, powerOfMagic, transgressionDistance);
        this.wisdom = wisdom;
        this.witty = witty;
        this.creativity = creativity;
    }

    public RavenclawStudents(String name, int powerOfMagic, int transgressionDistance) {
        super(name, powerOfMagic, transgressionDistance);
    }

    public void compareWith(RavenclawStudents student) {

        int sumOfThisProperty = this.wisdom + this.witty + this.creativity;
        int sumOfThatProperty = student.wisdom + student.witty + student.creativity;

        if (sumOfThisProperty > sumOfThatProperty) {
            System.out.println(this.getName() + " лучший когтевранец, чем " + student.getName());
        } else if (sumOfThisProperty < sumOfThatProperty) {
            System.out.println(student.getName() + " лучший когтевранец, чем " + this.getName());
        } else {
            System.out.println("Студенты одинакого хороши");
        }
    }

    @Override
    public String toString() {
        return "Ravenclaw: " + super.toString() +
                ", wisdom=" + wisdom +
                ", witty=" + witty +
                ", creativity=" + creativity;
    }
}
