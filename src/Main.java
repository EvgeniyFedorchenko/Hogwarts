public class Main {
    public static void main(String[] args) {

        Gryffindor harryPotter = new Gryffindor("Harry Potter", 45, 23, 68, 76, 17);
        Gryffindor hermioneGranger = new Gryffindor("Hermione Granger", 43, 76, 25, 75, 52);
        Gryffindor ronWeasley = new Gryffindor("Ron Weasley", 87, 37, 86, 65, 94);

        Slytherin dracoMalfoy = new Slytherin("Draco Malfoy", 53, 35, 53, 76, 32, 84);
        Slytherin grahamMontagu = new Slytherin("Graham Montagu", 32, 74, 24, 85, 47, 84);
        Slytherin gregoryGoyle = new Slytherin("Gregory Goyle", 36, 75, 35, 87, 43, 43);

        Hufflepuff zacharySmith = new Hufflepuff("Zachary Smith", 34, 76, 34, 75, 34);
        Hufflepuff cedricDiggory = new Hufflepuff("Cedric Diggory", 54, 13, 78, 65, 34);
        Hufflepuff justinFinchFletchley = new Hufflepuff("Justin Finch-Fletchley", 23, 76, 43, 43, 22);

        Ravenclaw zhouChang = new Ravenclaw("Zhou Chang", 34, 56, 42, 65, 86);
        Ravenclaw padmaPatil = new Ravenclaw("Padma Patil", 23, 65, 33, 65, 44);
        Ravenclaw marcusBelby = new Ravenclaw("Marcus Belby", 43, 12, 68, 65, 32);

        System.out.println(ronWeasley);
        System.out.println(dracoMalfoy);
        System.out.println(zacharySmith);
        System.out.println(zhouChang);

        harryPotter.compareWith(hermioneGranger);
        grahamMontagu.compareWith(gregoryGoyle);
        cedricDiggory.compareWith(justinFinchFletchley);
        padmaPatil.compareWith(marcusBelby);

        harryPotter.interfacultyCompareWith(dracoMalfoy);

    }
}