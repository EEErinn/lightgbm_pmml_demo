import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.TargetField;

public class WorkConditionRecognition {
  public static void main(String[] args) throws Exception {
        Map<String, Double>  map=new HashMap<String, Double>();
        map.put("0", 7098.696);map.put("1",7098.696 );
        map.put("2", 13.99792);map.put("3", 71.1143);
        map.put("4", 1724.456);map.put("5", 22.56);
        map.put("6", 2.695527);map.put("7", -999.25);
        map.put("8", 2.212459);map.put("9", 0.0736);
        map.put("10", 20.04099);map.put("11", 0.0);
        map.put("12", 0.0);map.put("13", 51.36);
        map.put("14", 0.0);map.put("15", 136.8243);
        map.put("16", 155.3404);map.put("17", 0.5145964);
        map.put("18", 0.788936);map.put("19", 0.5466411);
        map.put("20", 1.151263);map.put("21", 1.15);
        map.put("22", 49.29552);map.put("23", 53.36488);
        map.put("24", 11.2846);map.put("25", 0.0);
        map.put("26", 4.5644);map.put("27", 0.3519);
        map.put("28", 19.9775);map.put("29", 0.2054753);
        map.put("30", 11.92569);map.put("31", 1.219523);
        map.put("32", 0.00000000000000019);map.put("33", 12.34181);
        map.put("34", 0.0);map.put("35", 0.0);
        System.out.println(predictL(map));
    }

    public static String predictL(Map<String, Double> kxmap)throws Exception {
        PMML pmml;
        String pathxml = "lightgbm.pmml";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(pathxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = inputStream;
        try {
            pmml = org.jpmml.model.PMMLUtil.unmarshal(is);

            ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory
                    .newInstance();
            ModelEvaluator<?> modelEvaluator = modelEvaluatorFactory
                    .newModelEvaluator(pmml);
            Evaluator evaluator = (Evaluator) modelEvaluator;

            List<InputField> inputFields = evaluator.getInputFields();

            Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
            for (InputField inputField : inputFields) {
                FieldName inputFieldName = inputField.getName();
                Object rawValue = kxmap
                        .get(inputFieldName.getValue());
                FieldValue inputFieldValue = inputField.prepare(rawValue);
                arguments.put(inputFieldName, inputFieldValue);
            }
            Map<FieldName, ?> results = evaluator.evaluate(arguments);
            List<TargetField> targetFields = evaluator.getTargetFields();
            for (TargetField targetField : targetFields) {
                FieldName targetFieldName = targetField.getName();
                ProbabilityDistribution targetFieldValue = (ProbabilityDistribution) results.get(targetFieldName);
                System.out.println("target: " + targetFieldName.getValue()
                        + " value: " + targetFieldValue);
                Set<String> categories = targetFieldValue.getCategories();
                Double max = 0.0;
                String ca = null;
                for (String category : categories) {
                    if (targetFieldValue.getProbability(category) > max) {
                        max = targetFieldValue.getProbability(category);
                        ca = category;
                    }
                }
                return ca;
            }
        } catch (Exception e) {
            inputStream.close();
        }
        return "-1";
    }
}
