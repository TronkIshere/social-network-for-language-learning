package com.private_project.social_network_for_language_learning.tools.codeGenerate.utils;

public class RequiredImports {
    public static String getRequiredImports(String fields) {
        StringBuilder imports = new StringBuilder();

        // Date and time
        if (fields.contains("LocalDateTime")) {
            imports.append("import java.time.LocalDateTime;\n");
        } else if (fields.contains("LocalDate")) {
            imports.append("import java.time.LocalDate;\n");
        } else if (fields.contains("LocalTime")) {
            imports.append("import java.time.LocalTime;\n");
        }
        if (fields.contains("Instant")) {
            imports.append("import java.time.Instant;\n");
        }
        if (fields.contains("Timestamp")) {
            imports.append("import java.sql.Timestamp;\n");
        }
        if (fields.contains("Date")) {
            imports.append("import java.util.Date;\n");
        }
        if (fields.contains("Calendar")) {
            imports.append("import java.util.Calendar;\n");
        }
        if (fields.contains("GregorianCalendar")) {
            imports.append("import java.util.GregorianCalendar;\n");
        }
        if (fields.contains("ZonedDateTime")) {
            imports.append("import java.time.ZonedDateTime;\n");
        }
        if (fields.contains("OffsetDateTime")) {
            imports.append("import java.time.OffsetDateTime;\n");
        }
        if (fields.contains("OffsetTime")) {
            imports.append("import java.time.OffsetTime;\n");
        }
        if (fields.contains("Year")) {
            imports.append("import java.time.Year;\n");
        }
        if (fields.contains("YearMonth")) {
            imports.append("import java.time.YearMonth;\n");
        }
        if (fields.contains("MonthDay")) {
            imports.append("import java.time.MonthDay;\n");
        }

        // Number
        if (fields.contains("BigDecimal")) {
            imports.append("import java.math.BigDecimal;\n");
        }
        if (fields.contains("Double")) {
            imports.append("import java.lang.Double;\n");
        }
        if (fields.contains("Float")) {
            imports.append("import java.lang.Float;\n");
        }
        if (fields.contains("Integer") || fields.contains("int")) {
            imports.append("import java.lang.Integer;\n");
        }

        // collections
        if (fields.contains("List")) {
            imports.append("import java.util.List;\n");
        }
        if (fields.contains("Set")) {
            imports.append("import java.util.Set;\n");
        }
        if (fields.contains("Map")) {
            imports.append("import java.util.Map;\n");
        }

        // special
        if (fields.contains("UUID")) {
            imports.append("import java.util.UUID;\n");
        }
        if (fields.contains("Boolean")) {
            imports.append("import java.lang.Boolean;\n");
        }

        imports.append("\n");

        return imports.toString();
    }
}
