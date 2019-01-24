/*
 * This is a class for an Answer object. It is used
 * by the Question Class 
 */
package com.trantbatey.test;

import java.io.PrintWriter;
import static java.lang.System.*;
import java.util.Scanner;

/**
 *
 * @author trant.w.batey
 */
public class Answer {

	private	String m_Answer;
	private boolean m_correct;
        private boolean m_Reason;
	final static String dilimiter = "///";
	final static String correctDilimiter = "///c";
	final static String answerDilimiter = "@@@";
        final static String reasonDilimiter = "///r";

	public void setAnswer(String answer)
	{
		m_Answer = answer;
	}
	public String getAnswer() { return m_Answer; }

	private void setTrue() { m_correct = true; }
	private void setFalse() { m_correct = false; }
	public boolean isTrue() { return m_correct; }
        private void setReason (boolean isReason) { m_Reason = isReason; }
        public boolean isReason() { return m_Reason; }

	public static Answer readAnswer(Scanner input)
	{
            Answer answer=null;
            String aTemp = "";
            while (input.hasNextLine())
            {
                String sTemp = input.nextLine();
		if (sTemp.equals(answerDilimiter)) return null;
                if (sTemp.equals(dilimiter) 
                        || sTemp.equals(correctDilimiter) 
                        || sTemp.equals(reasonDilimiter))
		{
                    answer = new Answer();
                    answer.setAnswer(aTemp);
                    if (sTemp.equals(reasonDilimiter)) answer.setReason(true);
                    else answer.setReason(false);
                    if (sTemp.equals(correctDilimiter)) answer.setTrue();
                    else answer.setFalse();
                    break;
                }
                aTemp += sTemp;
                aTemp += "\n";
            }
            return answer;
	}

	void printAnswer(char ch, PrintWriter pw)
	{
		pw.print(ch + ") ");
		pw.print(getAnswer());
		pw.println();
		if (isTrue()) pw.println("Correct Answer");
	}

	void printAnswer(char ch, boolean report)
	{
                if (this.isReason()) 
                {
                    if (!report) return;
                    out.print("\n\nExplination:\n");
                }
                else out.print(ch + ") ");
                if (report && isTrue()) out.println("Correct Answer");
		out.print(this.getAnswer());
		out.println();
	}
}

