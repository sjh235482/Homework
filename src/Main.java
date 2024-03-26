import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static Map<String, Integer> globalMap = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("请输入一行字符串 (输入exit退出程序): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("程序已退出。");
                break;
            }

            // 检查输入的字符串是否以 "->" 开头
            if (!input.startsWith("->")) {
                System.out.println("输入的字符串不以 \"->\" 开头，请重新输入。");
                continue;
            }

            // 去除 "->"
            input = input.substring(2);

            int if_contain_input = input.toLowerCase().indexOf("input");
            int if_contain_output = input.toLowerCase().indexOf("output");
            if (if_contain_input >= 0) {
                String tips=input.substring(input.indexOf("\"")+1,input.lastIndexOf("\""));
                input=input.substring(input.lastIndexOf("\"")+1,input.length()-1);
                input=input.trim();
                System.out.println(tips);
                List<String> list = Arrays.asList(input.split("\\s+"));
                Scanner scannerInput = new Scanner(System.in);


                for (String item : list) {
                    int count = scannerInput.nextInt();
                    globalMap.put(item,count);
                }
                System.out.println("值输入完成！");

            } else if (if_contain_output >= 0) {
                input=input.substring(input.toLowerCase().indexOf("output")+6,input.length()-1);
                input=input.trim();
                if (!input.contains("(")){
                    List<String> list = Arrays.asList(input.split("\\s+"));
                    for (String item : list) {
                        System.out.print(item+":"+globalMap.get(item)+"   ");
                    }
                    System.out.println();
                }
                else {
                    List<String> parts = new ArrayList<>();

                    Pattern pattern = Pattern.compile("\\([^\\(\\)]*\\)");
                    Matcher matcher = pattern.matcher(input);

                    while (matcher.find()) {
                        String part = matcher.group();
                        parts.add(part);
                    }
                    for (String item:parts){
                        System.out.print(calculate(extractOperator(item),extractOperands(item))+"   ");
                    }
                    System.out.println();
                }


            }
            // 正常运算
            else {
                input = deleteAfterLastClosingParenthesis(input);
                int index = input.indexOf('(') + 1;
                String op = (input.length() >= 2 ? input.substring(1, 3) : input);

                op = op.replaceAll("\\s+", "");
                input = input.substring(2);

                if (op.equals(":=")) {


                    assign(input);
                } else {
                    List<String> childList = splitString(input);
                    int result = process(op, childList);
                    System.out.println(result);
                }
            }
        }
    }

    public static int process(String op, List<String> childList) {
        List<String> result = new ArrayList<>();
        for (String element : childList) {
            if (element.contains("(")){
                String operator = extractOperator(element);
                List<String> operands = extractOperands(element);
                result.add(String.valueOf(calculate(operator, operands)));
            }
          else {
              result.add(element);
            }
        }

        return calculate(op, result);
    }

    public static int calculate(String op, List<String> elements) {
        int result = 0;

        switch (op) {
            case "+":
                for (String numStr : elements) {
                    int num = parseNumber(numStr);
                    result += num;
                }
                break;
            case "-":
                result = parseNumber(elements.get(0));
                for (int i = 1; i < elements.size(); i++) {
                    result -= parseNumber(elements.get(i));
                }
                break;
            case "*":
                result = 1;
                for (String numStr : elements) {
                    int num = parseNumber(numStr);
                    result *= num;
                }
                break;
            case "/":
                result = parseNumber(elements.get(0));
                for (int i = 1; i < elements.size(); i++) {
                    int num = parseNumber(elements.get(i));
                    if (num != 0) {
                        result /= num;
                    } else {
                        System.out.println("错误：除数为零");
                        return 0;
                    }
                }
                break;
            case ">":
                result = parseNumber(elements.get(0)) > parseNumber(elements.get(1)) ? 1 : 0;
                break;
            case ">=":
                result = parseNumber(elements.get(0)) >= parseNumber(elements.get(1)) ? 1 : 0;
                break;
            case "<":
                result = parseNumber(elements.get(0)) < parseNumber(elements.get(1)) ? 1 : 0;
                break;
            case "<=":
                result = parseNumber(elements.get(0)) <= parseNumber(elements.get(1)) ? 1 : 0;
                break;
            case "!=":
                result = parseNumber(elements.get(0)) != parseNumber(elements.get(1)) ? 1 : 0;
                break;
            default:
                System.out.println("错误：未知的运算符");
                break;
        }

        return result;
    }

    public static void assign(String expression) {
        if (expression.contains("(")) {
            expression=expression.replaceAll("=\\s*(?=a)|\\s*(?=\\))", "");
            expression=expression.substring(0, expression.length() - 1);

            Pattern pattern = Pattern.compile("^(\\w+)\\s+(.+)$");


            Matcher matcher = pattern.matcher(expression);


            if (matcher.find()) {

                String firstString = matcher.group(1);

                String secondString = matcher.group(2);
                secondString=secondString.trim();

                secondString=secondString.substring(1, secondString.length() );
                // 使用空格分割字符串
                String[] parts = secondString.split("\\s+");


                String first = parts[0];


                List<String> restStrings = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    restStrings.add(parts[i]);
                }

                int result = calculate(first,restStrings);
                globalMap.put(firstString, result);
                System.out.println(result);

            } else {
                // 如果没有找到匹配的子串，则输出错误信息
                System.out.println("无法提取字符串");
            }
        }
        else {
            expression=expression.replace("=", "");
            expression=expression.trim();
            // 使用空格分割字符串
            String[] parts = expression.split("\\s+");
            String first = parts[0];
            String second=parts[1];
            int result=parseNumber(second);
            globalMap.put(first, result);
            System.out.println(result);

        }

    }



    public static int parseNumber(String numStr) {
        try {
            return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            Integer value = globalMap.get(numStr);
            if (value != null) {
                return value;

            } else if (globalMap.containsKey(numStr)) {
                return globalMap.get(numStr);

            } else {
                System.out.println("错误：无法解析的运算数 " + numStr);
                return 0;
            }
        }
    }

    public static String extractOperator(String element) {
        if (element.contains("(")) {
            Pattern pattern = Pattern.compile("\\(([^\\s]+)\\s*([\\w]+)\\s*([\\w]+)\\s*\\)");

            Matcher matcher = pattern.matcher(element);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        else {
            return element;
        }
        return null;
    }

    public static List<String> extractOperands(String element) {
        List<String> operands = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\(([^\\s]+)\\s*([\\w]+)\\s*([\\w]+)\\s*\\)");

        Matcher matcher = pattern.matcher(element);
        if (matcher.find()) {
            operands.add(matcher.group(2));
            operands.add(matcher.group(3));
        }
        return operands;
    }

    public static List<String> splitString(String input) {


        Pattern pattern = Pattern.compile("\\([^()]*\\)|\\S+");

        Matcher matcher = pattern.matcher(input);


        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {

            tokens.add(matcher.group());
        }
        List<String> resultList = new ArrayList<>();

        for (String temp : tokens){
            if (!temp.contains("(")){

                resultList.add(temp);

            }
            else {
                Pattern pattern1 = Pattern.compile("\\([^()]*\\)");
                Matcher matcher1 = pattern.matcher(temp);

                while (matcher1.find()) {
                    String match = matcher1.group();
                    resultList.add(match);
                }
            }
        }




        return resultList;
    }

    public static String deleteAfterLastClosingParenthesis(String input) {
        int lastIndex = -1;
        for (int i = input.length() - 1; i >= 0; i--) {
            if (input.charAt(i) == ')') {
                lastIndex = i;
                break;
            }
        }
        if (lastIndex != -1) {
            return input.substring(0, lastIndex);
        }
        return input;
    }
}
