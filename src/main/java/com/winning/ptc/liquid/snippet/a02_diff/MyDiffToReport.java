package com.winning.ptc.liquid.snippet.a02_diff;

import liquibase.database.Database;
import liquibase.diff.DiffResult;
import liquibase.diff.Difference;
import liquibase.diff.ObjectDifferences;
import liquibase.diff.StringDiff;
import liquibase.diff.compare.CompareControl;
import liquibase.exception.DatabaseException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.DatabaseObjectComparator;
import liquibase.structure.core.Schema;
import liquibase.util.StringUtils;

import java.io.PrintStream;
import java.util.*;

public class MyDiffToReport {

    private DiffResult diffResult;
    private PrintStream out;

    public MyDiffToReport(DiffResult diffResult, PrintStream out) {
        this.diffResult = diffResult;
        this.out = out;
    }

    public void print() throws DatabaseException {
        final DatabaseObjectComparator comparator = new DatabaseObjectComparator();
        out.println("参照快照: " + diffResult.getReferenceSnapshot().getDatabase() + "<br>");
        out.println("目标目标: " + diffResult.getComparisonSnapshot().getDatabase() + "<br>");

        CompareControl.SchemaComparison[] schemas = diffResult.getCompareControl().getSchemaComparisons();
        if ((schemas != null) && (schemas.length > 0)) {
            out.println("目标Schemas: " + StringUtils.join(Arrays.asList(schemas), ", ", new StringUtils.StringUtilsFormatter<CompareControl.SchemaComparison>() {
                @Override
                public String toString(CompareControl.SchemaComparison obj) {
                    String referenceName;
                    String comparisonName;

                    Database referenceDatabase = diffResult.getReferenceSnapshot().getDatabase();
                    Database comparisonDatabase = diffResult.getComparisonSnapshot().getDatabase();

                    if (referenceDatabase.supportsSchemas()) {
                        referenceName = obj.getReferenceSchema().getSchemaName();
                        if (referenceName == null) {
                            referenceName = referenceDatabase.getDefaultSchemaName();
                        }
                    } else if (referenceDatabase.supportsCatalogs()) {
                        referenceName = obj.getReferenceSchema().getCatalogName();
                        if (referenceName == null) {
                            referenceName = referenceDatabase.getDefaultCatalogName();
                        }
                    } else {
                        return "";
                    }

                    if (comparisonDatabase.supportsSchemas()) {
                        comparisonName = obj.getComparisonSchema().getSchemaName();
                        if (comparisonName == null) {
                            comparisonName = comparisonDatabase.getDefaultSchemaName();
                        }
                    } else if (comparisonDatabase.supportsCatalogs()) {
                        comparisonName = obj.getComparisonSchema().getCatalogName();
                        if (comparisonName == null) {
                            comparisonName = comparisonDatabase.getDefaultCatalogName();
                        }
                    } else {
                        return "";
                    }

                    if (referenceName == null) {
                        referenceName = StringUtils.trimToEmpty(referenceDatabase.getDefaultSchemaName());
                    }

                    if (comparisonName == null) {
                        comparisonName = StringUtils.trimToEmpty(comparisonDatabase.getDefaultSchemaName());
                    }

                    if (referenceName.equalsIgnoreCase(comparisonName)) {
                        return referenceName;
                    } else {
                        return referenceName + " -> " + comparisonName;
                    }
                }
            }, true) + "<br>");
        }

        printComparison("产品名称", diffResult.getProductNameDiff(), out);
        printComparison("产品版本", diffResult.getProductVersionDiff(), out);


        TreeSet<Class<? extends DatabaseObject>> types = new TreeSet<>(new Comparator<Class<? extends DatabaseObject>>() {
            @Override
            public int compare(Class<? extends DatabaseObject> o1, Class<? extends DatabaseObject> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });
        types.addAll(diffResult.getCompareControl().getComparedTypes());
        for (Class<? extends DatabaseObject> type : types) {
            if (type.equals(Schema.class) && !diffResult.getComparisonSnapshot().getDatabase().supportsSchemas()) {
                continue;
            }
            printSetComparison("缺失: " + getTypeName(type), diffResult.getMissingObjects(type, comparator), out);
            printSetComparison("新增: " + getTypeName(type), diffResult.getUnexpectedObjects(type, comparator), out);

            printChangedComparison("改变: " + getTypeName(type), diffResult.getChangedObjects(type, comparator), out);

        }

//        printColumnComparison(diffResult.getColumns().getChanged(), out);
    }

    protected String getTypeName(Class<? extends DatabaseObject> type) {
        return type.getSimpleName().replaceAll("([A-Z])", " $1").trim() + "(s)";
    }

    protected boolean getIncludeSchema() {
        return diffResult.getCompareControl().getSchemaComparisons().length > 1;
    }

    protected void printChangedComparison(String title, Map<? extends DatabaseObject, ObjectDifferences> objects, PrintStream out) {
        if (objects.isEmpty()) {
//            out.println("无");
        } else {
            out.print(title + ": ");
            out.println("<br>");
            for (Map.Entry<? extends DatabaseObject, ObjectDifferences> object : objects.entrySet()) {
                if (object.getValue().hasDifferences()) {
                    out.println("     " + object.getKey() + "<br>");
                    for (Difference difference : object.getValue().getDifferences()) {
                        out.println("          " + difference.toString() + "<br>");
                    }
                }
            }
        }
    }

    protected void printSetComparison(String title, Set<? extends DatabaseObject> objects, PrintStream out) {
        Schema lastSchema = null;
        if (objects.isEmpty()) {
//            out.println("无");
        } else {
            out.print(title + ": ");
            out.println("<br>");
            for (DatabaseObject object : objects) {
                if (getIncludeSchema() && (object.getSchema() != null) && ((lastSchema == null) || !lastSchema.equals
                        (object.getSchema()))) {
                    lastSchema = object.getSchema();
                    String schemaName = object.getSchema().getName();
                    if (schemaName == null) {
                        schemaName = object.getSchema().getCatalogName();
                    }
                    schemaName = includeSchemaComparison(schemaName);

                    out.println("  SCHEMA: " + schemaName + "<br>");
                }
                out.println("     " + object + "<br>");
            }
        }
    }

    protected String includeSchemaComparison(String schemaName) {
        String convertedSchemaName = CompareControl.SchemaComparison.convertSchema(schemaName, diffResult.getCompareControl().getSchemaComparisons());

        if ((convertedSchemaName != null) && !convertedSchemaName.equals(schemaName)) {
            schemaName = schemaName + " -> " + convertedSchemaName;
        }
        return schemaName;
    }

    protected void printComparison(String title, StringDiff string, PrintStream out) {
        out.print(title + ":");

        if (string == null) {
            out.print("NULL");
            return;
        }

        if (string.areEqual()) {
            out.println("相同: " + string.getReferenceVersion() + "<br>");
        } else {
            String referenceVersion = string.getReferenceVersion();
            if (referenceVersion == null) {
                referenceVersion = "NULL";
            } else {
                referenceVersion = "'" + referenceVersion + "'";
            }

            String targetVersion = string.getTargetVersion();
            if (targetVersion == null) {
                targetVersion = "NULL";
            } else {
                targetVersion = "'" + targetVersion + "'";
            }


            out.println("<br>");
            out.println("     参照:   " + referenceVersion + "<br>");
            out.println("     目标: " + targetVersion + "<br>");
        }

    }

}
