package project.annotations;

import java.util.*; // Checkstyle violation: AvoidStarImport

public class statusCheckTest {
    int BadVariable; // Checkstyle violation: MemberName (must be lowercase, e.g., badVariable)

    public static void main(String[] args) {
        System.out.println("hello world") // Compilation error: Missing semicolon
    }
}
