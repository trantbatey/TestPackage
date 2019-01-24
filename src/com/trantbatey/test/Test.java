package com.trantbatey.test;

/*
 * This is a java class to help create multiple choice test.
 * 
 * The application is designed to be progressive, using a suite of related
 * test going from the beginning topics through the later topics while still
 * asking some questions from the earlier topics. It also has a mode for 
 * combining all topics.
 * 
 * This is designed to be used as a learning tool, reviewing old 
 * information as it introduces new topics.
 * 
 * The application allows for multiple correct answers per question so as 
 * to make the test non-trivial.
 * 
 */

import java.util.*;
import java.io.*;
import javax.mail.*;
import static java.lang.System.*;
import javax.mail.internet.*;


/**
 *
 * @author trant.w.batey
 */
public class Test {
    private int mAllTest;
    private int mTestNum=0;
    private int mSize = 0, mTrimSize = 0;
    private ArrayList <String> mFileList;
    private Scanner mScanner= null;
    private Scanner kb=null;
    private int mNumCorrect = 0;
    boolean emailGrade = false;
    LinkedList <Question> questions = new LinkedList<Question>();
    
    // Constructor for test with files past from main
    public Test (ArrayList<String> pFileList) 
    {
        // set list of files
        mFileList = pFileList;
        mAllTest = pFileList.size() + 1;
        
        // Set up keyboard entry
        kb = new Scanner(System.in);
    }

    /**
     * Get the number of test
     */
    public int getTestNum() { return mTestNum; }

    /**
     * Get the number of test
     */
    public void setEmailGrade(boolean setting) { emailGrade = setting; }
    
    /**
     * Set up the test
     *     Read each test file
     *     Unless all "Combine all Topics" is selected weight toward the last test
     *     Trim the test size
     *        
     */
    public void selectTest(int questionLimit)
    {
        
        // Select which chapter to use for the test
        while (true)
        {
            out.println("Select the latest topic you want included in the test?\n"+
                    "Select a number between 1 and " + mAllTest);
            
            // print test choices
            int num = 1;
            Iterator <String> tList = mFileList.iterator();
            while (tList.hasNext())
            {
                out.println("\t"+num+") "+tList.next());
                num++;
            }
            out.println("\t"+num+") Combine all Topics");
            String st = kb.nextLine();
            
            // Validate the response
            try {
                mTestNum = Integer.parseInt(st.trim());
            } catch (NumberFormatException NFE) {
                out.println("Invalid entry: Please enter an integer between 1 and " + mAllTest);
                continue;
            } catch (Exception e) {
                out.println("Unexpected data entry problem, the program is exiting");
                e.printStackTrace();
                exit(1);
            }
        
            if ((mTestNum > 0) && (mTestNum <= mAllTest)) break;
            out.println("Invalid entry: Please enter an integer between 1 and "
                    + mAllTest);
        }
        
        // loop to combine test
        int offset = 1;
        if (mTestNum != mAllTest) offset = 2;
        for (int i=0; i<mTestNum-offset; i++) 
            readTest(i);
        randomizeQuestions();
        
        // weight test
        if (mTestNum != mAllTest)
        {
            if (questionLimit > 0 ) trimTest(questionLimit/2);
            readTest(mTestNum-1);
            randomizeQuestions();
        }
        trimTest(questionLimit);
    }

    //Builds the test questions and answers
    void readTest(int pIndex)
    {
        //opens the data file
        String testFile = mFileList.get(pIndex);
        try
        {
            mScanner = new Scanner(new FileReader(testFile));
        }
        catch (IOException e)
        {
            System.out.println("PROBLEM :  the file "+testFile+" not found.");
            System.exit(0);
        }
            
        //read the data file
        while (mScanner.hasNext())
        {
            // read question
            Question tQuestion = Question.readQuestion(mScanner);
            if (tQuestion == null) continue;
            questions.add(tQuestion);
        }
        mSize = questions.size();
        mTrimSize = mSize;
        //mScanner.close();
    }

    public void trimTest(int newSize)
    {
        // intialize combined sizes
        if (newSize < 0) return;
        if (newSize > mSize) return;
        
        // remove unwanted nodes
        for(int i=mSize-1;i>=newSize;i--)
            questions.remove(i);
        mTrimSize = newSize;
        mSize = newSize;
    }

    public void appendTest(Test newTest)
    {
        // intialize combined sizes
	mSize += newTest.mSize;
	mTrimSize += newTest.mTrimSize;

	// copy first test
	for(int i=0;i<newTest.mSize;i++)
		questions.add(newTest.questions.get(i));
    }

    public void randomizeQuestions()
    {
        int i, j, max;

	max = mSize;
	Random rand = new Random();
	for(i=0;i<mSize;i++)
	{
            // randomize question
            j = rand.nextInt(max)+i;
            Question tQuestion = questions.get(j);
            tQuestion.randomizeAnswers();
            questions.add(i, tQuestion);
            questions.remove(j+1);
            max--;
	}
    }

    public void printTest()
    {
        // open output file
        PrintWriter pw = null;
        String homedir  = System.getProperty("user.home");
        String outputFile = homedir + "/TestResult.txt";

        // Verify file creation and open
        try
        {
            pw = new PrintWriter(new FileWriter(outputFile), true);
        }
        catch (IOException e)
        {
            System.err.println("Caught IOException: " +  e.getMessage());
        } 
            
        // print test result
        int num = 1;
        Iterator <Question> qList = questions.iterator();
        while (qList.hasNext())
        {
            qList.next().printQuestion(num, pw);
            pw.println();
            num++;
        }
    }

    public void printMissed()
    {
        
        // print test result
        int num = 1;
        Iterator <Question> qList = questions.iterator();
        while (qList.hasNext())
        {
            Question tQuestion = qList.next();
            if (!tQuestion.isCorrect())
            {
            	tQuestion.printQuestion(num, true);
                num++;
                out.println();
                System.out.println();
                System.out.print("Hit \"Enter\" to continue.\n");
                kb.nextLine();
                System.out.print("\n\n\n\n\n");
            }
        }
    }

    public void giveTest()
    {
    	String userAnswer, valid;
    	
    	System.out.println("\n\n\n\n\n");
    	int num = 1;
    	Iterator <Question> qList = questions.iterator();
    	while (qList.hasNext())
    	{
            Question tQuestion = qList.next();
            while (true)
            {
            	tQuestion.printQuestion(num, false);
            	
            	// set valid answer
            	valid = "[^A-";
            	valid += (char) ('A'+ tQuestion.numAnswers()-1);
            	valid += "]";

				// get answer
				System.out.print("Please enter your selection: ");
				userAnswer = kb.nextLine();
				userAnswer = userAnswer.toUpperCase();
				userAnswer = userAnswer.replaceAll(valid, "");
				System.out.println("\n\n\n\n\n");
				if (userAnswer.length() == 0) {
		                    out.println("*** INVALID ENTRY ***");
		                    continue;
				}
				break;
            }

            boolean correct = tQuestion.testAnswer(userAnswer);

            if (correct)
            {
				System.out.println("That is correct.\n");
				tQuestion.setCorrect(true);
				mNumCorrect++;
            }
            else System.out.println("Sorry, that was incorrect.\n");
            int score = (int) ((mNumCorrect/(double)(num))*100+0.5);
            System.out.println("Your current score is: " + score + "\n\n");
            num++;
    	}
    }

    public void printResult()
    {
		int grade;
	
		System.out.println("\n\n\n\n");
		grade = (int) (((double)mNumCorrect/mSize) * 100.0+0.5);
		System.out.println("You got "+mNumCorrect+" out of "+mSize+" right.");
		System.out.printf("That is a %d. \n", grade);
	
		//email grade
		if (emailGrade) 
		{
			try {
				this.emailGrade(grade);
	        } catch (Exception e) {
	        	System.out.println("PROBLEM :  could not send message ");
	        	System.out.println(e.getMessage());
	        }
	    }
		System.out.println("\n\n\n\n");
    }


    public void emailGrade(int grade)
    {
        String username = System.getProperty("user.name");
        String message = username + " " + Integer.toString(grade);
        String subject = username + " Test";
        String recipients[] = {"trant.w.batey.ctr@mail.mil"};

        final String from = "email@gmail.com";
        final String password = "password";
        Properties props = new Properties();
        props.put("mail.smtp.user", from); 
        props.put("mail.smtp.host","smtp.gmail.com");  
        props.put("mail.smtp.port", "465");    //465,587
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        
        Session session = 
                Session.getInstance(props, new javax.mail.Authenticator() 
                {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });
        
        try {

            // create the message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            
            // convert recipient list to address array
            InternetAddress[] addressTo = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                try {
                    addressTo[i] = new InternetAddress(recipients[i]);
                } catch (AddressException ae) {
                    System.out.println("PROBLEM :  addressTo: " + i);
                    ae.printStackTrace();
                }
            }
            
            // add the addresses to the recipient list
            try {
                msg.setRecipients(Message.RecipientType.TO, addressTo);
            } catch (MessagingException me) {
                System.out.println("PROBLEM :  setRecipients ");
                me.printStackTrace();
            }

            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);
            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

