package com.textract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;


public class App {

    private static void DisplayAnalyzeExpenseSummaryInfo(ExpenseDocument expensedocument) {
        System.out.println("	ExpenseId : " + expensedocument.expenseIndex());
        System.out.println("    Expense Summary information:");
        if (expensedocument.hasSummaryFields()) {

            List<ExpenseField> summaryfields = expensedocument.summaryFields();

            for (ExpenseField summaryfield : summaryfields) {

                System.out.println("    Page: " + summaryfield.pageNumber());
                if (summaryfield.type() != null) {

                    System.out.println("    Expense Summary Field Type:" + summaryfield.type().text());

                }
                if (summaryfield.labelDetection() != null) {

                    System.out.println("    Expense Summary Field Label:" + summaryfield.labelDetection().text());
                    //Bỏ Geometry, Polygon, đưa boudingbox ra ngoài cùng cấp vs Text
                    //System.out.println("    Geometry");
                    System.out.println("        Bounding Box: "
                            + summaryfield.labelDetection().geometry().boundingBox().toString());
//                    System.out.println(
//                            "        Polygon: " + summaryfield.labelDetection().geometry().polygon().toString());

                }
                if (summaryfield.valueDetection() != null) {
                    System.out.println("    Expense Summary Field Value:" + summaryfield.valueDetection().text());
                    //Bỏ Geometry, đưa boudingbox ra ngoài cùng cấp vs Text
//                    System.out.println("    Geometry");
                    System.out.println("        Bounding Box: "
                            + summaryfield.valueDetection().geometry().boundingBox().toString());
//                    System.out.println(
//                            "        Polygon: " + summaryfield.valueDetection().geometry().polygon().toString());

                }

            }

        }

    }

    private static void DisplayAnalyzeExpenseLineItemGroupsInfo(ExpenseDocument expensedocument) {

        System.out.println("	ExpenseId : " + expensedocument.expenseIndex());
        System.out.println("    Expense LineItemGroups information:");

        if (expensedocument.hasLineItemGroups()) {

            List<LineItemGroup> lineitemgroups = expensedocument.lineItemGroups();

            for (LineItemGroup lineitemgroup : lineitemgroups) {

                System.out.println("    Expense LineItemGroupsIndexID :" + lineitemgroup.lineItemGroupIndex());

                if (lineitemgroup.hasLineItems()) {

                    List<LineItemFields> lineItems = lineitemgroup.lineItems();

                    for (LineItemFields lineitemfield : lineItems) {

                        if (lineitemfield.hasLineItemExpenseFields()) {

                            List<ExpenseField> expensefields = lineitemfield.lineItemExpenseFields();
                            for (ExpenseField expensefield : expensefields) {

                                if (expensefield.type() != null) {
                                    System.out.println("    Expense LineItem Field Type:" + expensefield.type().text());

                                }

                                if (expensefield.valueDetection() != null) {
                                    System.out.println(
                                            "    Expense Summary Field Value:" + expensefield.valueDetection().text());
                                    //Bỏ Geometry, đưa boudingbox ra ngoài cùng cấp vs Text
//                                    System.out.println("    Geometry");
                                    System.out.println("        Bounding Box: "
                                            + expensefield.valueDetection().geometry().boundingBox().toString());
//                                    System.out.println("        Polygon: "
//                                            + expensefield.valueDetection().geometry().polygon().toString());

                                }

                                if (expensefield.labelDetection() != null) {
                                    System.out.println(
                                            "    Expense LineItem Field Label:" + expensefield.labelDetection().text());
                                    //Bỏ Geometry, đưa boudingbox ra ngoài cùng cấp vs Text
//                                    System.out.println("    Geometry");
                                    System.out.println("        Bounding Box: "
                                            + expensefield.labelDetection().geometry().boundingBox().toString());
//                                    System.out.println("        Polygon: "
//                                            + expensefield.labelDetection().geometry().polygon().toString());
                                }

                            }
                        }

                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        File initialFile = new File("src/main/resources/fedex-receipt.jpg");
        InputStream fis = null;
        try {
            fis = new FileInputStream(initialFile);
            System.out.println(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SdkBytes bytes = SdkBytes.fromInputStream(fis);
//
        Document doc = Document.builder().bytes(bytes).build();

        List<FeatureType> list = new ArrayList<>();

        list.add(FeatureType.FORMS);

        TextractClient textractClient = TextractClient.builder().region(Region.US_EAST_1).build();

        AnalyzeExpenseRequest request = AnalyzeExpenseRequest.builder().document(doc).build();

        AnalyzeExpenseResponse response = textractClient.analyzeExpense(request);

        //ExpenseDocument test = response.expenseDocuments().get(0).summaryFields()
        List<ExpenseDocument> test = response.expenseDocuments();
        for (ExpenseDocument exd : test) {
            DisplayAnalyzeExpenseSummaryInfo(exd);
            DisplayAnalyzeExpenseLineItemGroupsInfo(exd);
        }
        textractClient.close();
    }
}
