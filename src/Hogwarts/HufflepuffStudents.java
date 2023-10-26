package Hogwarts;

// Пуффендуй
public class HufflepuffStudents extends HogwartsStudents {
    private int honesty;
    private int loyalty;
    private int hardWork;

    public HufflepuffStudents(String name, int powerOfMagic, int transgressionDistance, int honesty, int loyalty, int hardWork) {
        super(name, powerOfMagic, transgressionDistance);
        this.honesty = honesty;
        this.loyalty = loyalty;
        this.hardWork = hardWork;
    }

    public void compareWith(HufflepuffStudents student) {

        int sumOfThisProperty = this.honesty + this.loyalty + this.hardWork;
        int sumOfThatProperty = student.honesty + student.loyalty + student.hardWork;

        if (sumOfThisProperty > sumOfThatProperty) {
            System.out.println(this.getName() + " лучший пуффендуец, чем " + student.getName());
        } else if (sumOfThisProperty < sumOfThatProperty) {
            System.out.println(student.getName() + " лучший пуффендуец, чем " + this.getName());
        } else {
            System.out.println("Студенты одинакого хороши");
        }
    }

    @Override
    public String toString() {
        return "Hufflepuff: " + super.toString() +
                ", honesty=" + honesty +
                ", loyalty=" + loyalty +
                ", hardWork=" + hardWork;
    }
}

