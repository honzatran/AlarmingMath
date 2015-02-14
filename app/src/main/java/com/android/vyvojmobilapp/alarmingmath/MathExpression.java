package com.android.vyvojmobilapp.alarmingmath;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


/**
 * Created by marketa on 10.2.15.
 * pocita se pouze s celymi cisly (uzivatel asi po probuzeni nic jineho nezvladne)
 */
public class MathExpression {

    public ArrayList<String> expr;      // vyraz v postfixu
    public String exprInfix;
    public int difficulty;
    public String result;

    private char[] operators = {'+', '-', '*', '/'};

    public void setDifficulty(int d) { difficulty = d; }

    /**
     * Prevede expr do infixove notace
     */
    public String toInfixString() {
        Stack<String> s = new Stack();      // zasobnik operandu
        String e;
        String x, y, z;
        for (int i = 0; i < expr.size(); i++) {     // ctu postupne postfix
            e = expr.get(i);
            if (!isOperator(e)) {   // je to operand -> na zasobnik
                s.push(e);
            }
            else {
                x = s.pop();
                y = s.pop();
                z = y + " " + e + " " + x;
                if (s.size() > 0) {
                    z = "( " + z + " )";
                }
                s.push(z);
            }
        }
        return s.pop();
    }

    /**
     * Otestuje, jestli je to operator
     * @param op - operator
     * @return true - je, false - neni
     */
    private boolean isOperator(String op) {
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/"))
            return true;
        else
            return false;
    }

    /**
     * Vyhodnoti vyraz v postfixu.
     * @return  vysledek vyrazu
     */
    public String evaluate(ArrayList<String> eAL) {
        String e;
        Stack<String> s = new Stack<>();
        String x, y;

        for (int i = 0; i < eAL.size(); i++) {        // postupne ctu
            e = eAL.get(i);
            if (!isOperator(e)) {   // hodnota -> na zasobnik
                s.push(e);
            }
            else {  // operator, jdu operovat a vysledek vratim na zasobnik
                y = s.pop();
                x = s.pop();
                s.push(Integer.toString(eval(x, y, e)));
            }
        }

        return s.pop();
    }

    private int eval(String xS, String yS, String op) {
        int x,y;
        int r = 0;
        x = Integer.parseInt(xS);
        y = Integer.parseInt(yS);
        if (op.equals("+"))
            r = x + y;
        else if (op.equals("-"))
            r = x - y;
        else if (op.equals("*"))
            r = x * y;
        else if (op.equals("/"))
            r = x / y;
        return r;
    }

    /**
     * Vygeneruje priklad podle obtiznosti.
     */
    public void generateExpression() {
        char op;
        ArrayList<String> pom;

        switch (difficulty) {
            case 0:     // easy - mala nasobilka, "male" +, -, /
                op = randomOperator();
                expr = randomEasyEpxression(op, -1);
                break;
            case 1:     // medium - male operace se 3 operandy
            case 2:     // hard - 2 operace s vetsimi cisly
                op = randomOperator();
                pom = randomEasyEpxression(op, -1);
                op = randomOperator();
                expr = randomEasyEpxression2(op, pom);
                break;
        }

        // infix
        exprInfix = toInfixString();
        // vysledek
        result = evaluate(expr);
    }

    /**
     * Vybere z pole operatoru nahodne nejaky jeden.
     * @return jeden ze ctyr operandu
     */
    private char randomOperator() {
        Random rnd = new Random();
        return operators[rnd.nextInt(4)];
    }

    /**
     * K danemu operatoru a operandu vybere druhy operand, aby to bylo hezky spocitatelne.
     * @param op - operator
     * @param x - prvni operand, pokud x == -1, pak se nahodne generuje
     * @return vraci triprvkovy arrayList (dva operandy a operator)
     */
    private ArrayList<String> randomEasyEpxression(char op, int x) {
        int y;
        int x1, y1; // pomocne
        Random rnd = new Random();
        ArrayList<String> e = new ArrayList<>();

        int boundPlus = 11;
        int boundMinus = 11;
        int boundMulti = 11;
        int boundDiv = 11;

        // nastaveni maximalni velikosti operandu
        if (difficulty == 0 || difficulty == 1) {
            boundPlus = 21;
            boundMinus = 31;
            boundMulti = 11;
            boundDiv = 11;
        }
//		else if (difficulty == 1) {
//			boundPlus = 51;
//			boundMinus = 101;
//			boundMulti = 21;
//			boundDiv = 21;
//		}
        else if (difficulty == 2) {      // tezke
            boundPlus = 101;
            boundMinus = 201;
            boundMulti = 31;
            boundDiv = 31;
        }

        if (op == '*') {
            if (x < 0) x = rnd.nextInt(boundMulti);
            y = rnd.nextInt(boundMulti);
        }
        else if (op == '+') {
            if (x < 0) x = rnd.nextInt(boundPlus);
            y = rnd.nextInt(boundPlus);
        }
        else if (op == '/') {       // toto mi zajisti delitelnost
            x1 = (x < 0) ? rnd.nextInt(boundDiv) : x;
            y = rnd.nextInt(boundDiv);
            if (y == 0) { y = 1; }      // deleni nulou
            x = x1*y;
        }
        else {       // vzdy odcitam mensi od vetsiho
            x1 = (x < 0) ? rnd.nextInt(boundMinus) : x;
            y1 = rnd.nextInt(boundMinus);
            x = (x1 > y1) ? x1 : y1;
            y = (x1 > y1) ? y1 : x1;
        }

        e.add(Integer.toString(x));
        e.add(Integer.toString(y));
        e.add(Character.toString(op));

        return e;
    }

    /**
     * Dostane jednoduchy vyraz (arrayList.size == 3) a k nemu prida do postfixu jeste jeden operand a operator.
     * Je to napraseny, nelze zobecnit ani moc dobre rozsirovat.
     * @param op - operator
     * @param xAL - postfixovy arrayList, ke kteremu vygeneruju jeste kousek prikaldu
     * @return finalni vyraz
     */
    private ArrayList<String> randomEasyEpxression2(char op, ArrayList<String> xAL) {
        int x = Integer.parseInt(evaluate(xAL));
        ArrayList<String> pom = randomEasyEpxression(op, x);
        ArrayList<String> e = new ArrayList<>();

        String first = pom.get(0);      // prvni prvek  - cislo

        if (first.equals(Integer.toString(x))) {       // substituuju za nej
            pom.remove(0);
            for (int i = 0; i < xAL.size(); i++) {
                e.add(xAL.get(i));
            }
            for (int i = 0; i < pom.size(); i++) {
                e.add(pom.get(i));
            }
        }
        else {
            e.add(pom.get(0));

            for (int i = 0; i < xAL.size(); i++) {
                e.add(xAL.get(i));
            }
            e.add(pom.get(2));
        }

        return e;
    }
}