package StepByStep;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static String NBA_example = "C:\\Users\\University\\IdeaProjects\\SafraConstruction\\input\\NBA_example.txt";
    private static String NBA_example2 = absolutePath("\\ProjectFiles\\NBA_example2.txt");
    private static String DNA_example = absolutePath("\\ProjectFiles\\DNA_example.txt");
    private static String DNA_example2 = absolutePath("\\ProjectFiles\\DNA_example2.txt");
    static String DNA_result2 = "DNA_result2.png";


    private static String absolutePath(String relPath){
        return new File("").getAbsolutePath().concat(relPath);
    }

    public static void main(String[] args){
//        midProjectIO(args);
//        test1();
//        test2();
//        test4();
//        test5();
//        test6();
//        test7();
//        test8();
        test9();
    }

    private static void midProjectIO(String[] args){
        if (args.length<1){
            System.out.println("not enough arguments!");
            return;
        }
        String input=args[0];
        String outputText=args[1];
//        String outputPic=args[2];
        NBA nba=new NBA(input);
        Construction c=new Construction(nba);
        DNA d=c.construct();
        d.saveText(outputText);
//        d.savePicture(outputPic);
    }


    //testing NBA parsing, and graphviz
    private static void test2(){
        NBA n = new NBA(NBA_example);
        System.out.println(n.toString());
//        n.savePicture("test2.png");
    }

    //label and tree representation tests
    private static void test4(){
        String s="Q2 [label=\"[0],[0 -2]\"]";
        System.out.println(s.replaceAll("-2","@").replaceAll("\\@","-2"));
        Tree tree=new Tree(new ArrayList<>(Arrays.asList(0,-2)));
        System.out.println(tree.toString());
    }

    //Safraconstruction for shefi example
    private static void test5(){
        NBA nba=new NBA(new File(NBA_example));
        Construction c=new Construction(nba);
        DNA d=c.construct();
        System.out.println("and final DNA is:\n"+d.toString());
        //d.savePicture(DNA_result2);
    }

    //parsing all examples (NBAs and DNAs)
    private static void test6(){
        NBA nba=new NBA(new File(NBA_example));
        System.out.println("the first nba is :\n");
        System.out.println(nba);
        System.out.println("---------------------------------------------------------\n");
//        nba.savePicture("nba1test6.png");

        NBA nba2=new NBA(new File(NBA_example2));
        System.out.println("the second nba is :\n");
        System.out.println(nba2);
        System.out.println("---------------------------------------------------------\n");
//        nba2.savePicture("nba2test6.png");

        DNA dna=new DNA(new File(DNA_example));
        System.out.println("the first dna is :\n");
        System.out.println(dna);
        System.out.println("---------------------------------------------------------\n");
//        dna.savePicture("dna1test6.png");

//        DNA dna2=new DNA(DNA_example2);
//        System.out.println("the second dna is :\n");
//        System.out.println(dna2);
//        System.out.println("---------------------------------------------------------\n");
//        dna2.savePicture("dna2test6.png");
    }

    //testing a specific state
    private static void test7(){
        List<Integer> Treelst=new ArrayList<>();
        Treelst.add(0);Treelst.add(0);Treelst.add(1);
        Integer [] Labelarr= new Integer[4];
        Labelarr[0]=2;Labelarr[1]=3;Labelarr[2]=0;Labelarr[3]=1;
        Label l=new Label(Labelarr);
        Tree t=new Tree(Treelst);
        DnaState dnastate=new DnaState(t,l);
        testState(dnastate);
    }

    /*checking DNAs states structure and preliminaries*/
    public static void test8(){
        DNA dna=new DNA(new File(DNA_example));
        System.out.println("the first dna is :\n");
        System.out.println(dna);
        System.out.println("---------------------------------------------------------\n");
        for (State state :dna.getQ().keySet()){
            testState((DnaState)state);
        }

        DNA dna2=new DNA(new File(DNA_example2));
        System.out.println("the second dna is :\n");
        System.out.println(dna2);
        System.out.println("---------------------------------------------------------\n");
        for (State state :dna2.getQ().keySet()){
            testState((DnaState)state);
        }
    }

    public static void test9(){
        String n="";
        try{
            Scanner s=new Scanner(new File(NBA_example));
            while(s.hasNextLine()){
                n+=s.nextLine()+System.lineSeparator();
            }
        }catch (Exception e){
        	e.printStackTrace();
            System.out.println(n);
        }
        NBA nba=new NBA(n);
        NbaSteps c=new NbaSteps(nba);
        int i=0;
        
            while(c.makeNextStep()){
            	System.out.println(c.tempD);
            }
            System.out.println(c.tempD);
        
    }
    
    /*checks Dnastate for structure and preliminaries*/
    private static void testState(DnaState dnastate){
        System.out.println("testing state "+dnastate);
        stateDetails(dnastate);
    }

    /*prints Dnastate's descendant, and children siblings*/
    private static void stateDetails(DnaState dnastate){
        Tree t=dnastate.getT();
        int n=dnastate.getT().size();
        for (int i=0;i<n;i++){
            System.out.println(String.format("children of %d=%s",i,t.children(i)));
            System.out.println(String.format("siblings of %d=%s",i,t.siblings(i)));
            System.out.println(String.format("descendants of %d=%s",i,t.descendants(i)));
            //System.out.println(String.format("S(%d)=%s",i,dnastate.S(i)));
        }
    }



}
