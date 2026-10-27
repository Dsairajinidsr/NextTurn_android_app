package com.example.nextturn_project.utils;

public class DepartmentTokenManager {

    public static String getPrefix(String department) {

        switch (department) {

            case "General Medicine":
                return "GM";

            case "Cardiology":
                return "C";

            case "Gynecology":
                return "GY";

            case "Orthopedics":
                return "OR";

            case "Dermatology":
                return "DE";

            case "Neurology":
                return "NE";

            case "ENT":
                return "ENT";

            case "Pediatrics":
                return "PD";

            default:
                return "GEN";
        }
    }

    public static String generateToken(String department, int tokenNo) {

        String prefix = getPrefix(department);

        return String.format("%s%03d", prefix, tokenNo);
    }

}