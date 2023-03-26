import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Tatec
{
    private static final int CORRECT_TOTAL_TOKEN_PER_STUDENT = 100;
    private static final String OUT_TATEC_UNHAPPY = "unhappyOutTATEC.txt";
    private static final String OUT_TATEC_ADMISSION = "admissionOutTATEC.txt";
    private static final String OUT_RAND_UNHAPPY = "unhappyOutRANDOM.txt";
    private static final String OUT_RAND_ADMISSION = "admissionOutRANDOM.txt";

    private static List<String> getUnhappiness(List<String> students, List<String> courses,
                                        List<List<String>> assignedStudents, List<List<Integer>> tokens,
                                        double h) {
        // Each student U for unhappiness
        final List<Double> studentU = IntStream.range(0,students.size()).boxed()
                .map(studentIndex -> IntStream.range(0,courses.size()).boxed()
                        .filter(courseIndex -> !assignedStudents.get(courseIndex).contains(students.get(studentIndex)))
                        .map(courseIndex ->
                                Math.min((-100/h) * Math.log(1 - (Double.valueOf(tokens.get(studentIndex).get(courseIndex))/100)),100.0)
                        ).reduce(0.0, Double::sum)
                ).collect(Collectors.toList());

        // Square U's for unassigned students
        final List<Boolean> squarePredicate = IntStream.range(0,students.size()).boxed()
                .map(studentIndex -> IntStream.range(0,courses.size()).boxed()
                        .anyMatch(courseIndex -> assignedStudents.get(courseIndex).contains(students.get(studentIndex)))
                ).collect(Collectors.toList());

        List<Double> squaredU = IntStream.range(0,studentU.size()).boxed()
                .map(studentIndex -> squarePredicate.get(studentIndex)
                        ? studentU.get(studentIndex)
                        : Math.pow(studentU.get(studentIndex),2)
                ).collect(Collectors.toList());

        // Calculate average U
        Double averageU = (1.0/students.size()) * squaredU.stream().reduce(0.0,Double::sum);

        // Convert string lines
        List<String> unhappinessLines = squaredU.stream().map(e -> e.toString()).collect(Collectors.toList());
        unhappinessLines.add(0,averageU.toString());

        return  unhappinessLines;
    }

    public static void main(String args[])
    {
        if(args.length < 4)
        {
            System.err.println("Not enough arguments!");
            return;
        }

        // File Paths
        String courseFilePath = args[0];
        String studentIdFilePath = args[1];
        String tokenFilePath = args[2];
        double h;

        try { h = Double.parseDouble(args[3]);}
        catch (NumberFormatException ex)
        {
            System.err.println("4th argument is not a double!");
            return;
        }

        // TODO: Rest is up to you
        List<String> studentList = new ArrayList<>();
        List<List<Integer>> tokenList = new ArrayList<>();
        List<String[]> courseList = new ArrayList<>();

        // Reading Files
        try {
            studentList = Files.lines(Paths.get(studentIdFilePath)).collect(Collectors.toList());
            tokenList = Files.lines(Paths.get(tokenFilePath)).map(line -> Arrays.stream(line.split(","))
                                                                    .map(Integer::valueOf).toList())
                                                                .collect(Collectors.toList());
            courseList = Files.lines(Paths.get(courseFilePath)).map( line -> line.split(",")).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
        }

        // Checking token input error
        List<Integer> tokenSums = tokenList.stream().map(e -> e.stream().reduce(0,Integer::sum))
                                        .collect(Collectors.toList());
        Boolean tokenError = tokenSums.stream().anyMatch(integer -> integer > 100);
        if( tokenError) {
            System.err.println("Token input error!");
            return;
        }

        final List<String> courses = courseList.stream().map(e -> e[0]).collect(Collectors.toList());
        final List<Integer> capacities = courseList.stream().map(e -> Integer.parseInt(e[1].trim())).collect(Collectors.toList());
        final List<List<Integer>> tokens = tokenList;
        final List<String> students = studentList;

        // TATEC
        // Sort tokens and find each courses minimum token
        List<List<Integer>> sortedTokens = IntStream.range(0,courses.size()).boxed().map(integer -> tokens.stream().map(e -> e.get(integer))
                                        .sorted(Comparator.reverseOrder()).limit(capacities.get(integer)).collect(Collectors.toList()))
                            .collect(Collectors.toList());

        List<Integer> courseMins = sortedTokens.stream().map(list -> list.get(list.size() - 1)).collect(Collectors.toList());

        // Assign students to courses with minimum tokens
        List<List<String>> assignedStudentsTatec = IntStream.range(0,courses.size()).boxed()
                .map(courseIndex -> IntStream.range(0,students.size()).boxed()
                                    .filter(studentIndex -> tokens.get(studentIndex).get(courseIndex) > 0 &&
                                            tokens.get(studentIndex).get(courseIndex) >= courseMins.get(courseIndex))
                                    .map(studentIndex -> students.get(studentIndex)).collect(Collectors.toList())
                ).collect(Collectors.toList());

        // Convert to string lines
        List<String> printLinesTatec = IntStream.range(0, courses.size()).
                mapToObj(index -> courses.get(index).concat(", ").concat(assignedStudentsTatec.get(index).stream().collect(Collectors.joining(", "))))
                .collect(Collectors.toList());

        // get unhappiness lines tatec
        List<String> unhappinessLinesTatec = getUnhappiness(students,courses,assignedStudentsTatec,tokens,h);

        // RANDOM
        var studentWithTokens = IntStream.range(0, students.size()).boxed()
                .collect(Collectors.toMap(students::get, tokens::get));

        // Shuffle keys to get random
        List<String> keys = new ArrayList(studentWithTokens.keySet());
        Collections.shuffle(keys);

        // Assign students random
        var assignedStudentsRandom = IntStream.range(0,courses.size())
                .mapToObj(courseIndex -> IntStream.range(0,keys.size()).boxed()
                        .filter(studentIndex -> studentWithTokens.get(keys.get(studentIndex)).get(courseIndex) != 0 )
                        .map(studentIndex -> keys.get(studentIndex))
                        .limit(capacities.get(courseIndex)).collect(Collectors.toList())
                ).collect(Collectors.toList());

        // Convert to string lines
        List<String> printLinesRandom = IntStream.range(0, courses.size()).
                mapToObj(index -> courses.get(index).concat(", ").concat(assignedStudentsRandom.get(index).stream().collect(Collectors.joining(", "))))
                .collect(Collectors.toList());

        // get unhappiness lines random
        List<String> unhappinessLinesRandom = getUnhappiness(students,courses,assignedStudentsRandom,tokens,h);

        // Write to files
        try {
            Files.write(Paths.get(OUT_TATEC_ADMISSION),printLinesTatec);
            Files.write(Paths.get(OUT_TATEC_UNHAPPY),unhappinessLinesTatec);
            Files.write(Paths.get(OUT_RAND_ADMISSION),printLinesRandom);
            Files.write(Paths.get(OUT_RAND_UNHAPPY),unhappinessLinesRandom);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
