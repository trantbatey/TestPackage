package com.trantbatey.test;

/*
 * Application to quiz the student on Chapter 1 of 
 * OCA Java SE 7 Programmer I Study Guide
 */

import java.util.*;
import java.io.*;
import static java.lang.System.*;


/**
 *
 * @author trant.w.batey
 */

class Question
{
	private	String m_Question;
	private boolean gotCorrect;
	public void setQuestion(String question) { m_Question = question; }
	public String getQuestion() { return m_Question; }
	public void setCorrect(boolean flag) { gotCorrect = flag; }
	public boolean isCorrect() { return gotCorrect; }
	final static String dilimiter = "///";

	private LinkedList <Answer> answerList;

	public static Question readQuestion(Scanner input)
	{
            String qTemp = "";
            String sTemp;
            sTemp = input.nextLine();
            String tString = sTemp.trim();
            if (tString.equals("")) return null;
            while (!sTemp.equals(dilimiter))
            {
                int index = sTemp.indexOf(')');
                if (index != -1)
                {
                    int index02 = sTemp.indexOf('(');
                    if ((index02 == -1) || (index02 > index))
                        sTemp = sTemp.substring(index+1);
                }
                qTemp += sTemp;
                qTemp += "\n";
                sTemp = input.nextLine();
            }
            Question question = new Question();
            question.setQuestion(qTemp);
            question.setCorrect(false);
            
            // read the answer
            question.answerList = new LinkedList <Answer>();
            while (true)
            {
                Answer tAnswer = Answer.readAnswer(input);
                if (tAnswer == null) break;
                question.answerList.add(tAnswer);
            }
            return question;
	}

	public void randomizeAnswers()
	{
		int j;
                int ansMax = answerList.size();
		int numAnswers = ansMax;
		Random rand = new Random();

                for (int i=0; i<numAnswers; i++)
		{
                        Answer answer = answerList.get(i);
                        if (answer.isReason())
                        {
                            answerList.add(numAnswers, answer);
                            answerList.remove(i);
                        } else {
                            j = rand.nextInt(ansMax)+i;
                            answerList.add(j, answer);
                            answerList.remove(i);
                        }
			ansMax--;
		}
	}

	public int numAnswers()	{ return answerList.size(); }

	public boolean testAnswer(String userAnswer)
	{
		char ch = 'A', letter;
		boolean correct = true;
		Iterator <Answer> aList = answerList.iterator();
		while (aList.hasNext())
		{
			Answer tAnswer = aList.next();
			boolean found = false;
			for (int k=0; k<userAnswer.length(); k++)
			{
				letter = userAnswer.charAt(k);
				if (ch == letter) found = true;
				if (found) break;
			}
			ch++;
			if (tAnswer.isTrue() && found) continue;
			else if (!tAnswer.isTrue() && !found) continue;
			else correct = false;
			break;
		}
		return correct;
	}

	public void printQuestion(int num, PrintWriter pw)
	{
		if (isCorrect()) pw.print("Answered Correctly.\n");
		else pw.print("Missed Question.\n");
		pw.println(num + ". "+ getQuestion());
		pw.println();
		char ch = 'A';
		Iterator <Answer> aList = answerList.iterator();
		while (aList.hasNext())
		{
			aList.next().printAnswer(ch, pw);
			ch++;
		}
	}

	public void printQuestion(int num, boolean report)
	{
		if (report)
		{
			if (isCorrect()) out.print("Answered Correctly.\n");
			else out.print("Missed Question.\n");
		}
		out.println(num + ". "+ getQuestion());
		out.println();
		char ch = 'A';
		Iterator <Answer> aList = answerList.iterator();
		while (aList.hasNext())
		{
			aList.next().printAnswer(ch, report);
			ch++;
		}
	}

}
