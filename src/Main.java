import Hogwarts.*;

public class Main {
    public static void main(String[] args) {

        GryffindorStudents harryPotter = new GryffindorStudents("Harry Potter", 45, 23, 68, 76, 17);
        GryffindorStudents hermioneGranger = new GryffindorStudents("Hermione Granger", 43, 76, 25, 75, 52);
        GryffindorStudents ronWeasley = new GryffindorStudents("Ron Weasley", 87, 37, 86, 65, 94);

        SlytherinStudents dracoMalfoy = new SlytherinStudents("Draco Malfoy", 53, 35, 53, 76, 32, 84);
        SlytherinStudents grahamMontagu = new SlytherinStudents("Graham Montagu", 32, 74, 24, 85, 47, 84);
        SlytherinStudents gregoryGoyle = new SlytherinStudents("Gregory Goyle", 36, 75, 35, 87, 43, 43);

        HufflepuffStudents zacharySmith = new HufflepuffStudents("Zachary Smith", 34, 76, 34, 75, 34);
        HufflepuffStudents cedricDiggory = new HufflepuffStudents("Cedric Diggory", 54, 13, 78, 65, 34);
        HufflepuffStudents justinFinchFletchley = new HufflepuffStudents("Justin Finch-Fletchley", 23, 76, 43, 43, 22);

        RavenclawStudents zhouChang = new RavenclawStudents("Zhou Chang", 34, 56, 42, 65, 86);
        RavenclawStudents padmaPatil = new RavenclawStudents("Padma Patil", 23, 65, 33, 65, 44);
        RavenclawStudents marcusBelby = new RavenclawStudents("Marcus Belby", 43, 12, 68, 65, 32);

        System.out.println(ronWeasley);
        System.out.println(dracoMalfoy);
        System.out.println(zacharySmith);
        System.out.println(zhouChang);

        harryPotter.compareWith(hermioneGranger);
        grahamMontagu.compareWith(gregoryGoyle);
        cedricDiggory.compareWith(justinFinchFletchley);
        padmaPatil.compareWith(marcusBelby);

        harryPotter.compareWith(dracoMalfoy);
    }
}